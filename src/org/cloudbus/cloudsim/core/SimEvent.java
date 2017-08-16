/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core;

/**
 * This class represents a simulation event which is passed between the entities in the simulation.
 * 这个类表示仿真中在两个实体之间传递的模拟事件
 * 
 * @author Costas Simatos
 * @see Simulation
 * @see SimEntity
 */
public class SimEvent implements Cloneable, Comparable<SimEvent> {

	/** Internal event type. 内部事件属性**/
	private final int etype;

	/** The time that this event was scheduled, at which it should occur. 事件计划发生的时间**/
	private final double time;

	/** Time that the event was removed from the queue to start service. 事件从队列中移除，开始服务的时间**/
	private double endWaitingTime;

	/** Id of entity who scheduled the event. 发送事件的实体编号**/
	private int entSrc;

	/** Id of entity that the event will be sent to. 接受事件的实体编号**/
	private int entDst;

	/** The user defined type of the event. 用户定义的事件类型**/
	private final int tag;

	/** 
         * Any data the event is carrying. 
         * 事件承载的任何数据
         * 
         * @todo I would be used generics to define the type of the event data.
         * But this modification would incur several changes in the simulator core
         * that has to be assessed first.
         * 我本想使用泛型来定义事件数据的类型。
         *  但是这种修改会引起模拟器核心的几个变化，必须首先进行评估。
         *  
         **/
	private final Object data;

        /**
         * An attribute to help CloudSim to identify the order of received events
         * when multiple events are generated at the same time.
         * If two events have the same {@link #time}, to know
         * what event is greater than other (i.e. that happens after other),
         * the {@link #compareTo(org.cloudbus.cloudsim.core.SimEvent)}
         * makes use of this field.
         * 一个属性，用来在同时生成多个事件的时候，帮助Cloudsim识别收到的事件的顺序。
         * 如果两个事件有同样的计划发生时间，要知道什么事件比另一个事件更重要(换句话说，发生在另一个之后)。比较函数为compareTo(org.cloudbus.cloudsim.core.SimEvent)
         * 利用这个领域
         * 
         * 
         */
	private long serial = -1;

	// Internal event types 内部事件属性

	public static final int ENULL = 0;

	public static final int SEND = 1;

	public static final int HOLD_DONE = 2;

	public static final int CREATE = 3;

	/**
	 * Creates a blank event.
	 * 创建一个空白事件
	 */
	public SimEvent() {
		etype = ENULL;
		time = -1L;
		endWaitingTime = -1.0;
		entSrc = -1;
		entDst = -1;
		tag = -1;
		data = null;
	}

	// ------------------- PACKAGE LEVEL METHODS -------------------------- 包等级的方法
	SimEvent(int evtype, double time, int src, int dest, int tag, Object edata) {
		etype = evtype;
		this.time = time;
		entSrc = src;
		entDst = dest;
		this.tag = tag;
		data = edata;
	}

	SimEvent(int evtype, double time, int src) {
		etype = evtype;
		this.time = time;
		entSrc = src;
		entDst = -1;
		tag = -1;
		data = null;
	}
        
	// ------------------- PUBLIC METHODS --------------------------  公共方法    
	
	/**
	 * 重写toString说明对事件的描述
	 */
	@Override 
	public String toString() {
		return "Event tag = " + tag + " source = " + CloudSim.getEntity(entSrc).getName() + " destination = "
				+ CloudSim.getEntity(entDst).getName();
	}

	/**
	 * 重写直接比较函数，比较事件之间的优先级
	 * 
	 * @param event 另一个事件
	 * @return 1,自身优先级高;0,一样高;-1,自身优先级低
	 */
	@Override
	public int compareTo(SimEvent event) {
		if (event == null) {
			return 1;
		} else if (time < event.time) {
			return -1;
		} else if (time > event.time) {
			return 1;
		} else if (serial < event.serial) {
			return -1;
		} else if (this == event) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 重写复制函数
	 * 
	 * @return 复制的对象
	 */
	@Override
	public Object clone() {
		return new SimEvent(etype, time, entSrc, entDst, tag, data);
	}

	// --------------setter & getter--------------------
	public double getEndWaitingTime() {
		return endWaitingTime;
	}

	public void setEndWaitingTime(double endWaitingTime) {
		this.endWaitingTime = endWaitingTime;
	}

	public int getEntSrc() {
		return entSrc;
	}

	public void setEntSrc(int entSrc) {
		this.entSrc = entSrc;
	}

	public int getEntDst() {
		return entDst;
	}

	public void setEntDst(int entDst) {
		this.entDst = entDst;
	}

	public long getSerial() {
		return serial;
	}

	public void setSerial(long serial) {
		this.serial = serial;
	}

	public int getEtype() {
		return etype;
	}

	public double getTime() {
		return time;
	}

	public int getTag() {
		return tag;
	}

	public Object getData() {
		return data;
	}
	
	
}
