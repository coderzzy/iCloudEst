/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.core.predicates.Predicate;

/**
 * This class represents a simulation entity. An entity handles events and can send events to other
 * entities. When this class is extended, there are a few methods that need to be implemented:
 * 这个类代表了一个仿真实体。
 * 一个实体处理事件并且可以发送事件给其他的实体。
 * 当这个类被继承的时候，以下几个方法必须被实现
 * 
 * <ul>
 * <li> {@link #startEntity()} is invoked by the {@link Simulation} class when the simulation is
 * started. This method should be responsible for starting the entity up.
 * 		startEntity()方法:当仿真开始的时候，被Simulation类引用。这个方法应当用来启动实体
 * <li> {@link #processEvent(SimEvent)} is invoked by the {@link Simulation} class whenever there is
 * an event in the deferred queue, which needs to be processed by the entity.
 * 		processEvent(SimEvent)方法:任何时候只要有延迟消息队列需要被实体推进，就会被Simulation调用
 * <li> {@link #shutdownEntity()} is invoked by the {@link Simulation} before the simulation
 * finishes. If you want to save data in log files this is the method in which the corresponding
 * code would be placed.
 * 		shutdownEntity()方法:在仿真结束之前被Simulation类引用。如果你想要保存数据在日志文件中，这是可以放置相关代码的方法。
 * </ul>
 * 
 * @todo the list above is redundant once all mentioned methods are abstract.
 * The documentation duplication may lead to have some of them
 * out-of-date and future confusion.
 * 一旦所有提到的方法是抽象的，那么上面的列表就是多余的。
 * 文件重复可能会导致使他们过时和等待的混乱。
 * 
 * 方法总结：
 *  1、发送事件，有cloudsim组建成simevent，调用futureQueue方法加入future queue队列中
 *  2、队列中发往本实体满足谓词p的事件个数，谓词p用来对事件限制
 *  3、队列中等待发往本实体的事件个数
 *  4、选择第一个满足src（此目的地为实体本身）和谓词p条件的事件，并把它从队列中移走
 *  5、在future队列中寻找第一个满足条件的事件，从中移走即取消此事件
 *  6、选择下一个事件
 *  7、三个核心方法，分别是实体开始，处理事件，实体销毁
 *  8、实体线程，一直查找事件并处理事件
 *  9、clone对象
 *  10、向目标实体发送事件delay差brite矩阵获得。
 *  11、获得两实体之间传送事件延迟，从birte矩阵中获得
 * 
 * 
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public abstract class SimEntity implements Cloneable {

	/** The entity name. 实体名称*/
	private String name;

	/** The entity id. 实体编号*/
	private int id;

	/** The buffer for selected incoming events. 所选将要进行事件的缓存区*/
	private SimEvent evbuf;

	/** The entity's current state. 实体现在的状态*/
	private int state;
	
	// The entity states 实体状态
    //@todo The states should be an enum.
	/** The Constant RUNNABLE. 常量——运行状态*/
	public static final int RUNNABLE = 0;

	/** The Constant WAITING. 常量——等待状态*/
	public static final int WAITING = 1;

	/** The Constant HOLDING. 常量——挂起状态*/
	public static final int HOLDING = 2;

	/** The Constant FINISHED. 常量——结束状态*/
	public static final int FINISHED = 3;

	/**
	 * Creates a new entity.
	 * 构造函数
	 * 
	 * @param name the name to be associated with the entity
	 */
	public SimEntity(String name) {
		if (name.indexOf(" ") != -1) {
			throw new IllegalArgumentException("Entity names can't contain spaces.");
		}
		this.name = name;
		id = -1;
		state = RUNNABLE;
		CloudSim.addEntity(this);
	}

	// The schedule functions 调度函数，重载

	/**
	 * Sends an event to another entity by id number, with data. Note that the tag <code>9999</code>
	 * is reserved.
	 * 通过ID编号，有数据，发送一个事件给另一个实体。
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void schedule(int dest, double delay, int tag, Object data) {
		if (!CloudSim.running()) {
			return;
		}
		CloudSim.send(id, dest, delay, tag, data);
	}

	/**
	 * Sends an event to another entity by id number and with <b>no</b> data. Note that the tag
	 * <code>9999</code> is reserved.
	 * 通过ID编号，无数据，发送一个事件给另一个实体。
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 */
	public void schedule(int dest, double delay, int tag) {
		schedule(dest, delay, tag, null);
	}

	/**
	 * Sends an event to another entity through a port with a given name, with data. Note that the
	 * tag <code>9999</code> is reserved.
	 * 通过名字，有数据，发送一个事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void schedule(String dest, double delay, int tag, Object data) {
		schedule(CloudSim.getEntityId(dest), delay, tag, data);
	}

	/**
	 * Sends an event to another entity through a port with a given name, with <b>no</b> data. Note
	 * that the tag <code>9999</code> is reserved.
	 * 通过名字，没有数据，发送一个事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 */
	public void schedule(String dest, double delay, int tag) {
		schedule(dest, delay, tag, null);
	}

	/**
	 * Sends an event to another entity by id number, with data
         * but no delay. Note that the tag <code>9999</code> is reserved.
         * 通过编号ID、有数据、但是没有延迟，发送一个事件给另一个实体
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void scheduleNow(int dest, int tag, Object data) {
		schedule(dest, 0, tag, data);
	}

	/**
	 * Sends an event to another entity by id number and with <b>no</b> data
         * and no delay. Note that the tag <code>9999</code> is reserved.
         * 通过编号ID、没有数据、没有延迟，发送一个事件给另一个实体
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param tag An user-defined number representing the type of event.
	 */
	public void scheduleNow(int dest, int tag) {
		schedule(dest, 0, tag, null);
	}

	/**
	 * Sends an event to another entity through a port with a given name, with data
         * but no delay. Note that the tag <code>9999</code> is reserved.
         * 通过名字、有数据、没有延迟，发送一个事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void scheduleNow(String dest, int tag, Object data) {
		schedule(CloudSim.getEntityId(dest), 0, tag, data);
	}

	/**
	 * Send an event to another entity through a port with a given name, with <b>no</b> data
         * and no delay. 
         * Note that the tag <code>9999</code> is reserved.
         * 通过名字、没有数据、没有延迟，发送一个事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param tag An user-defined number representing the type of event.
	 */
	public void scheduleNow(String dest, int tag) {
		schedule(dest, 0, tag, null);
	}

	/**
	 * Sends a high priority event to another entity by id number, with data. 
         * Note that the tag <code>9999</code> is reserved.
         * 通过编号ID、有数据，发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void scheduleFirst(int dest, double delay, int tag, Object data) {
		if (!CloudSim.running()) {
			return;
		}
		CloudSim.sendFirst(id, dest, delay, tag, data);
	}

	/**
	 * Sends a high priority event to another entity by id number and with <b>no</b> data. 
         * Note that the tag <code>9999</code> is reserved.
         * 通过编号ID、没有数据，发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 */
	public void scheduleFirst(int dest, double delay, int tag) {
		scheduleFirst(dest, delay, tag, null);
	}

	/**
	 * Sends a high priority event to another entity through a port with a given name, with data.
	 * Note that the tag <code>9999</code> is reserved.
	 * 通过名字、有数据，发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void scheduleFirst(String dest, double delay, int tag, Object data) {
		scheduleFirst(CloudSim.getEntityId(dest), delay, tag, data);
	}

	/**
	 * Sends a high priority event to another entity through a port with a given name, with <b>no</b>
	 * data. Note that the tag <code>9999</code> is reserved.
	 * 通过名字、没有数据，发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param delay How long from the current simulation time the event should be sent
	 * @param tag An user-defined number representing the type of event.
	 */
	public void scheduleFirst(String dest, double delay, int tag) {
		scheduleFirst(dest, delay, tag, null);
	}

	/**
	 * Sends a high priority event to another entity by id number, with data
         * and no delay. 
         * Note that the tag <code>9999</code> is reserved.
         * 通过编号ID、有数据、没有延迟发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void scheduleFirstNow(int dest, int tag, Object data) {
		scheduleFirst(dest, 0, tag, data);
	}

	/**
	 * Sends a high priority event to another entity by id number and with <b>no</b> data
         * and no delay. 
         * Note that the tag <code>9999</code> is reserved.
         * 通过编号ID、没有数据、没有延迟发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The unique id number of the destination entity
	 * @param tag An user-defined number representing the type of event.
	 */
	public void scheduleFirstNow(int dest, int tag) {
		scheduleFirst(dest, 0, tag, null);
	}

	/**
	 * Sends a high priority event to another entity through a port with a given name, with data
         * and no delay.
	 * Note that the tag <code>9999</code> is reserved.
	 * 通过名字、有数据、没有延迟发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param tag An user-defined number representing the type of event.
	 * @param data The data to be sent with the event.
	 */
	public void scheduleFirstNow(String dest, int tag, Object data) {
		scheduleFirst(CloudSim.getEntityId(dest), 0, tag, data);
	}

	/**
	 * Sends a high priority event to another entity through a port with a given name, with <b>no</b>
	 * data and no delay. Note that the tag <code>9999</code> is reserved.
	 * 通过名字、没有数据、没有延迟发送一个高优先级事件给另一个实体
	 * 
	 * @param dest The name of the port to send the event through
	 * @param tag An user-defined number representing the type of event.
	 */
	public void scheduleFirstNow(String dest, int tag) {
		scheduleFirst(dest, 0, tag, null);
	}

	/**
	 * Sets the entity to be inactive for a time period.
	 * 设置实体在一段时间内处于非活动状态
	 * 
	 * @param delay the time period for which the entity will be inactive
	 */
	public void pause(double delay) {
		if (delay < 0) {
			throw new IllegalArgumentException("Negative delay supplied.");
		}
		if (!CloudSim.running()) {
			return;
		}
		CloudSim.pause(id, delay);
	}

	/**
	 * Counts how many events matching a predicate are waiting in the entity's deferred queue.
	 * 统计多少个符合指定谓语的事件正在延迟队列中等待
	 * 
	 * @param p The event selection predicate
	 * @return The count of matching events
	 */
	public int numEventsWaiting(Predicate p) {
		return CloudSim.waiting(id, p);
	}

	/**
	 * Counts how many events are waiting in the entity's deferred queue.
	 * 统计多少个事件正在延迟队列中等待
	 * 
	 * @return The count of events
	 */
	public int numEventsWaiting() {
		return CloudSim.waiting(id, CloudSim.SIM_ANY);
	}

	/**
	 * Extracts the first event matching a predicate waiting in the entity's deferred queue.
	 * 提取与实体延迟队列中等待的谓词匹配的第一个事件
	 * 
	 * @param p The event selection predicate
	 * @return the simulation event
	 */
	public SimEvent selectEvent(Predicate p) {
		if (!CloudSim.running()) {
			return null;
		}

		return CloudSim.select(id, p);
	}

	/**
	 * Cancels the first event matching a predicate waiting in the entity's future queue.
	 * 取消与实体就绪队列中等待的谓词匹配的第一个事件
	 * 
	 * @param p The event selection predicate
	 * @return The number of events cancelled (0 or 1)
	 */
	public SimEvent cancelEvent(Predicate p) {
		if (!CloudSim.running()) {
			return null;
		}

		return CloudSim.cancel(id, p);
	}

	/**
	 * Gets the first event matching a predicate from the deferred queue, or if none match, wait for
	 * a matching event to arrive.
	 * 获取与延迟队列匹配谓词的第一个事件，或者如果没有匹配，等待一个匹配的事件到达。
	 * 
	 * @param p The predicate to match
	 * @return the simulation event
	 */
	public SimEvent getNextEvent(Predicate p) {
		if (!CloudSim.running()) {
			return null;
		}
		if (numEventsWaiting(p) > 0) {
			return selectEvent(p);
		}
		return null;
	}

	/**
	 * Waits for an event matching a specific predicate. This method does not check the entity's
	 * deferred queue.
	 * 等待与特定谓词匹配的事件。 此方法不检查实体的延期队列
	 * 
	 * @param p The predicate to match
	 */
	public void waitForEvent(Predicate p) {
		if (!CloudSim.running()) {
			return;
		}

		CloudSim.wait(id, p);
		state = WAITING;
	}

	/**
	 * Gets the first event waiting in the entity's deferred queue, or if there are none, wait for an
	 * event to arrive.
	 * 获取延迟队列的第一个事件，或者如果没有匹配，等待一个事件到达。
	 * 
	 * @return the simulation event
	 */
	public SimEvent getNextEvent() {
		return getNextEvent(CloudSim.SIM_ANY);
	}

	// --------------------------ABSTRACT FUNCTION--------------------------------
	/**
	 * This method is invoked by the {@link CloudSim} class when the simulation is started. 
	 * It should be responsible for starting the entity up.
	 */
	public abstract void startEntity();

	/**
	 * Processes events or services that are available for the entity.
	 * This method is invoked by the {@link CloudSim} class whenever there is an event in the
	 * deferred queue, which needs to be processed by the entity.
	 * 
	 * @param ev information about the event just happened
         * 
	 * @pre ev != null
	 * @post $none
	 */
	public abstract void processEvent(SimEvent ev);

	/**
         * Shuts down the entity.
	 * This method is invoked by the {@link CloudSim} before the simulation finishes. If you want
	 * to save data in log files this is the method in which the corresponding code would be placed.
	 */
	public abstract void shutdownEntity();
	

        /**
         * The run loop to process events fired during the simulation.
         * 运行循环来处理在仿真期间触发的事件。
         * 
         * The events that will be processed are defined
         * in the {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)} method.
         * 
         * @see #processEvent(org.cloudbus.cloudsim.core.SimEvent) 
         */
	public void run() {
		SimEvent ev = null;
		if(evbuf != null){
			ev = evbuf;
		}else{
			ev = getNextEvent();
		}

		while (ev != null) {
			processEvent(ev);
			if (state != RUNNABLE) {
				break;
			}

			ev = getNextEvent();
		}

		evbuf = null;
	}

	/**
	 * Gets a clone of the entity. This is used when independent replications have been specified as
	 * an output analysis method. Clones or backups of the entities are made in the beginning of the
	 * simulation in order to reset the entities for each subsequent replication. This method should
	 * not be called by the user.
	 * Clone一个实体
	 * 
	 * @return A clone of the entity
	 * @throws CloneNotSupportedException when the entity doesn't support cloning
	 */
	@Override
	protected final Object clone() throws CloneNotSupportedException {
		SimEntity copy = (SimEntity) super.clone();
		copy.setName(name);
		copy.setEvbuf(null);
		return copy;
	}


	// --------------- EVENT / MESSAGE SEND WITH NETWORK DELAY METHODS ------------------

	/**
	 * Sends an event/message to another entity by <tt>delaying</tt> the simulation time from the
	 * current time, with a tag representing the event type.
	 * 有数据，通过从当前时间延迟模拟时间向另一个实体发送事件/消息
	 * 
	 * @param entityId the id number of the destination entity
	 * @param delay how long from the current simulation time the event should be sent. If delay is
	 *            a negative number, then it will be changed to 0
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @param data A reference to data to be sent with the event
	 * @pre entityID > 0
	 * @pre delay >= 0.0
	 * @pre data != null
	 * @post $none
	 */
	protected void send(int entityId, double delay, int cloudSimTag, Object data) {
		if (entityId < 0) {
			return;
		}

		// if delay is -ve, then it doesn't make sense. So resets to 0.0
		if (delay < 0) {
			delay = 0;
		}

		if (Double.isInfinite(delay)) {
			throw new IllegalArgumentException("The specified delay is infinite value");
		}

		if (entityId < 0) {
			Log.printConcatLine(getName(), ".send(): Error - " + "invalid entity id ", entityId);
			return;
		}

		int srcId = getId();
		if (entityId != srcId) {// only delay messages between different entities
			delay += getNetworkDelay(srcId, entityId);
		}

		schedule(entityId, delay, cloudSimTag, data);
	}

	/**
	 * Sends an event/message to another entity by <tt>delaying</tt> the simulation time from the
	 * current time, with a tag representing the event type.
	 * 没有数据，通过从当前时间延迟模拟时间向另一个实体发送事件/消息
	 * 
	 * @param entityId the id number of the destination entity
	 * @param delay how long from the current simulation time the event should be sent. If delay is
	 *            a negative number, then it will be changed to 0
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @pre entityID > 0
	 * @pre delay >= 0.0
	 * @post $none
	 */
	protected void send(int entityId, double delay, int cloudSimTag) {
		send(entityId, delay, cloudSimTag, null);
	}

	/**
	 * Sends an event/message to another entity by <tt>delaying</tt> the simulation time from the
	 * current time, with a tag representing the event type.
	 * 有数据，通过事件名称，通过从当前时间延迟模拟时间向另一个实体发送事件/消息
	 * 
	 * 
	 * @param entityName the name of the destination entity
	 * @param delay how long from the current simulation time the event should be sent. If delay is
	 *            a negative number, then it will be changed to 0
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @param data A reference to data to be sent with the event
	 * @pre entityName != null
	 * @pre delay >= 0.0
	 * @pre data != null
	 * @post $none
	 */
	protected void send(String entityName, double delay, int cloudSimTag, Object data) {
		send(CloudSim.getEntityId(entityName), delay, cloudSimTag, data);
	}

	/**
	 * Sends an event/message to another entity by <tt>delaying</tt> the simulation time from the
	 * current time, with a tag representing the event type.
	 * 没有数据、通过事件名称，通过从当前时间延迟模拟时间向另一个实体发送事件/消息
	 * 
	 * @param entityName the name of the destination entity
	 * @param delay how long from the current simulation time the event should be sent. If delay is
	 *            a negative number, then it will be changed to 0
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @pre entityName != null
	 * @pre delay >= 0.0
	 * @post $none
	 */
	protected void send(String entityName, double delay, int cloudSimTag) {
		send(entityName, delay, cloudSimTag, null);
	}

	/**
	 * Sends an event/message to another entity, with a tag representing the event type.
	 * 有数据，没有延迟发送事件
	 * 
	 * @param entityId the id number of the destination entity
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @param data A reference to data to be sent with the event
	 * @pre entityID > 0
	 * @pre delay >= 0.0
	 * @pre data != null
	 * @post $none
	 */
	protected void sendNow(int entityId, int cloudSimTag, Object data) {
		send(entityId, 0, cloudSimTag, data);
	}

	/**
	 * Sends an event/message to another entity, with a tag representing the event type.
	 * 没有数据，没有延迟发送事件
	 * 
	 * @param entityId the id number of the destination entity
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @pre entityID > 0
	 * @pre delay >= 0.0
	 * @post $none
	 */
	protected void sendNow(int entityId, int cloudSimTag) {
		send(entityId, 0, cloudSimTag, null);
	}

	/**
	 * Sends an event/message to another entity, with a tag representing the event type.
	 * 通过名称，有数据没有延迟发送事件
	 * 
	 * @param entityName the name of the destination entity
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @param data A reference to data to be sent with the event
	 * @pre entityName != null
	 * @pre delay >= 0.0
	 * @pre data != null
	 * @post $none
	 */
	protected void sendNow(String entityName, int cloudSimTag, Object data) {
		send(CloudSim.getEntityId(entityName), 0, cloudSimTag, data);
	}

	/**
	 * Sends an event/message to another entity, with a tag representing the event type.
	 * 通过名称、没有数据、没有延迟发送事件
	 * 
	 * @param entityName the name of the destination entity
	 * @param cloudSimTag an user-defined number representing the type of an event/message
	 * @pre entityName != null
	 * @pre delay >= 0.0
	 * @post $none
	 */
	protected void sendNow(String entityName, int cloudSimTag) {
		send(entityName, 0, cloudSimTag, null);
	}

	/**
	 * Gets the network delay associated to the sent of a message from a given source to a given
	 * destination.
	 * 获取与从给定的源到给定的目的地的消息相关联的网络延迟。
	 * 
	 * @param src source of the message
	 * @param dst destination of the message
	 * @return delay to send a message from src to dst
	 * @pre src >= 0
	 * @pre dst >= 0
	 */
	private double getNetworkDelay(int src, int dst) {
		if (NetworkTopology.isNetworkEnabled()) {
			return NetworkTopology.getDelay(src, dst);
		}
		return 0.0;
	}

	// --------------setter & getter--------------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SimEvent getEvbuf() {
		return evbuf;
	}

	public void setEvbuf(SimEvent evbuf) {
		this.evbuf = evbuf;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
