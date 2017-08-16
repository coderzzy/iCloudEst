/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Log;

/**
 * A Cloud Information Service (CIS) is an entity that provides cloud resource registration,
 * indexing and discovery services. The Cloud hostList tell their readiness to process Cloudlets by
 * registering themselves with this entity. Other entities such as the resource broker can contact
 * this class for resource discovery service, which returns a list of registered resource IDs. In
 * summary, it acts like a yellow page service. This class will be created by CloudSim upon
 * initialisation of the simulation. Hence, do not need to worry about creating an object of this
 * class.
 * 一个云信息服务是一个提供云资源注册、索引和发现服务的实体。
 * 云主机列表通过使用这个实体注册他们自己来说明准备好了实行云任务。
 * 其他实体，例如资源代理者可以联系这个类来追求资源发现服务，资源发现服务返回一个已经注册的资源ID列表。
 * 总之，它的行为像一个黄页服务。
 * 这个类将由Cloudsim创建，用于初始化仿真。
 * 因此，不需要担心创建这个类的对象。
 * 
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class CloudInformationService extends SimEntity {

	/** A list containing the id of all entities that are registered at the 
         * Cloud Information Service (CIS). 
         * @todo It is not clear if this list is a list of host id's or datacenter id's.
         * The previous attribute documentation just said "For all types of hostList".
         * It can be seen at the method {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)}
         * that the list is updated when a CloudSimTags.REGISTER_RESOURCE event
         * is received. However, only the Datacenter class sends and event
         * of this type, including its id as parameter.
         * 一个包含所有在CIS中注册实体ID的列表。
         * 不清楚这个列表是否是主机ID或者数据中心ID的。
         * 以前的属性文档只说是"对于所有种类的主机列表"。
         * 它可以在方法SimEvent中看到。
         * 当一个CloudSimTags.REGISTER_RESOURCE事件被接受的时候，这个列表被更新。
         * 但是，只有数据中心类发送和事件，包括其id作为参数。
         * 
         */
	private final List<Integer> resList;

	/** A list containing only the id of entities with Advanced Reservation feature
         * that are registered at the CIS. 
         * 一个只包含在CIS中注册的高级预留功能的实体编号的列表
         * */
	private final List<Integer> arList;

	/** List of all regional CIS. 所有区域的CIS列表*/
	private final List<Integer> gisList;

	/**
	 * Instantiates a new CloudInformationService object.
	 * 构造函数
	 * 
	 * @param name the name to be associated with this entity (as required by {@link SimEntity} class)
	 * @throws Exception when creating this entity before initialising CloudSim package
	 *             or this entity name is <tt>null</tt> or empty
	 * @pre name != null
	 * @post $none
         * 
         * @todo The use of Exception is not recommended. Specific exceptions
         * would be thrown (such as {@link IllegalArgumentException})
         * or {@link RuntimeException}
	 */
	public CloudInformationService(String name) throws Exception {
		super(name);
		resList = new LinkedList<Integer>();
		arList = new LinkedList<Integer>();
		gisList = new LinkedList<Integer>();
	}

        /**
         * The method has no effect at the current class. 
         * 在当前类中这个方法没有影响
         */
	@Override
	public void startEntity() {
	}

	@Override
	public void processEvent(SimEvent ev) {
		int id = -1;  // requester id
		switch (ev.getTag()) {
		// storing regional CIS id
			case CloudSimTags.REGISTER_REGIONAL_GIS:
				gisList.add((Integer) ev.getData());
				break;

			// request for all regional CIS list
			case CloudSimTags.REQUEST_REGIONAL_GIS:

				// Get ID of an entity that send this event
				id = ((Integer) ev.getData()).intValue();

				// Send the regional GIS list back to sender
				super.send(id, 0L, ev.getTag(), gisList);
				break;

			// A resource is requesting to register.
			case CloudSimTags.REGISTER_RESOURCE:
				resList.add((Integer) ev.getData());
				break;

			// A resource that can support Advance Reservation
			case CloudSimTags.REGISTER_RESOURCE_AR:
				resList.add((Integer) ev.getData());
				arList.add((Integer) ev.getData());
				break;

			// A Broker is requesting for a list of all hostList.
			case CloudSimTags.RESOURCE_LIST:

				// Get ID of an entity that send this event
				id = ((Integer) ev.getData()).intValue();

				// Send the resource list back to the sender
				super.send(id, 0L, ev.getTag(), resList);
				break;

			// A Broker is requesting for a list of all hostList.
			case CloudSimTags.RESOURCE_AR_LIST:

				// Get ID of an entity that send this event
				id = ((Integer) ev.getData()).intValue();

				// Send the resource AR list back to the sender
				super.send(id, 0L, ev.getTag(), arList);
				break;

			default:
				processOtherEvent(ev);
				break;
		}
	}

	@Override
	public void shutdownEntity() {
		notifyAllEntity();
	}

	/**
	 * Gets the list of all CloudResource IDs, including hostList that support Advance Reservation.
	 * 
	 * @return list containing resource IDs. Each ID is represented by an Integer object.
	 * @pre $none
	 * @post $none
	 */
	public List<Integer> getList() {
		return resList;
	}

	/**
	 * Gets the list of CloudResource IDs that <b>only</b> support Advanced Reservation.
	 * 
	 * @return list containing resource IDs. Each ID is represented by an Integer object.
	 * @pre $none
	 * @post $none
	 */
	public List<Integer> getAdvReservList() {
		return arList;
	}

	/**
	 * Checks whether a given resource ID supports Advanced Reservations or not.
	 * 
	 * @param id a resource ID
	 * @return <tt>true</tt> if this resource supports Advanced Reservations, <tt>false</tt>
	 *         otherwise
	 * @pre id != null
	 * @post $none
	 */
	public boolean resourceSupportAR(Integer id) {
		if (id == null) {
			return false;
		}

		return resourceSupportAR(id.intValue());
	}

	/**
	 * Checks whether a given resource ID supports Advanced Reservations or not.
	 * 
	 * @param id a resource ID
	 * @return <tt>true</tt> if this resource supports Advanced Reservations, <tt>false</tt>
	 *         otherwise
	 * @pre id >= 0
	 * @post $none
	 */
	public boolean resourceSupportAR(int id) {
		boolean flag = false;
		if (id < 0) {
			flag = false;
		} else {
			flag = checkResource(arList, id);
		}

		return flag;
	}

	/**
	 * Checks whether the given CloudResource ID exists or not.
	 * 
	 * @param id a CloudResource id
	 * @return <tt>true</tt> if the given ID exists, <tt>false</tt> otherwise
	 * @pre id >= 0
	 * @post $none
	 */
	public boolean resourceExist(int id) {
		boolean flag = false;
		if (id < 0) {
			flag = false;
		} else {
			flag = checkResource(resList, id);
		}

		return flag;
	}

	/**
	 * Checks whether the given CloudResource ID exists or not.
	 * 
	 * @param id a CloudResource id
	 * @return <tt>true</tt> if the given ID exists, <tt>false</tt> otherwise
	 * @pre id != null
	 * @post $none
	 */
	public boolean resourceExist(Integer id) {
		if (id == null) {
			return false;
		}
		return resourceExist(id.intValue());
	}

	// //////////////////////// PROTECTED METHODS ////////////////////////////

	/**
	 * Process non-default received events that aren't processed by
         * the {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)} method.
         * This method should be overridden by subclasses in other to process
         * new defined events.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printConcatLine("CloudInformationService.processOtherEvent(): ",
					"Unable to handle a request since the event is null.");
			return;
		}

		Log.printLine("CloudInformationSevice.processOtherEvent(): " + "Unable to handle a request from "
				+ CloudSim.getEntityName(ev.getEntSrc()) + " with event tag = " + ev.getTag());
	}

	/**
	 * Notifies the registered entities about the end of simulation. This method should be
	 * overridden by child classes.
	 */
	protected void processEndSimulation() {
		// this should be overridden by the child class
	}

	// ////////////////// End of PROTECTED METHODS ///////////////////////////

	/**
	 * Checks whether a list contains a particular resource id.
	 * 
	 * @param list list of resource id
	 * @param id a resource ID to find
	 * @return true if a resource is in the list, otherwise false
	 * @pre list != null
	 * @pre id > 0
	 * @post $none
	 */
	private boolean checkResource(Collection<Integer> list, int id) {
		boolean flag = false;
		if (list == null || id < 0) {
			return flag;
		}

		Integer obj = null;
		Iterator<Integer> it = list.iterator();

		// a loop to find the match the resource id in a list
		while (it.hasNext()) {
			obj = it.next();
			if (obj.intValue() == id) {
				flag = true;
				break;
			}
		}

		return flag;
	}

	/**
	 * Tells all registered entities about the end of simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	private void notifyAllEntity() {
		Log.printConcatLine(super.getName(), ": Notify all CloudSim entities for shutting down.");

		signalShutdown(resList);
		signalShutdown(gisList);

		// reset the values
		resList.clear();
		gisList.clear();
	}

	/**
	 * Sends a {@link CloudSimTags#END_OF_SIMULATION} signal to all entity IDs 
         * mentioned in the given list.
	 * 
	 * @param list List storing entity IDs
	 * @pre list != null
	 * @post $none
	 */
	protected void signalShutdown(Collection<Integer> list) {
		// checks whether a list is empty or not
		if (list == null) {
			return;
		}

		Iterator<Integer> it = list.iterator();
		Integer obj = null;
		int id = 0;     // entity ID

		// Send END_OF_SIMULATION event to all entities in the list
		while (it.hasNext()) {
			obj = it.next();
			id = obj.intValue();
			super.send(id, 0L, CloudSimTags.END_OF_SIMULATION);
		}
	}

}
