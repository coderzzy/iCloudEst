/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * This class implements the deferred event queue used by {@link CloudSim}. 
 * The event queue uses a linked list to store the events.
 * 此类实现{@link CloudSim}使用的延迟事件队列
  *事件队列使用链表来存储事件。
 * 
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 * @see CloudSim
 * @see SimEvent
 */
public class DeferredQueue {

	/** The list of events. 事件链表*/
	private final List<SimEvent> list = new LinkedList<SimEvent>();

	/** The max time that an added event is scheduled. 一个事件被调度的最长时间*/
	private double maxTime = -1;

	/**
	 * Adds a new event to the queue. Adding a new event to the queue preserves the temporal order
	 * of the events.
	 * 添加一个新的事件到队列中
	 * 添加一个新的事件到队列中的时候保存事件的时间顺序
	 * 
	 * @param newEvent The event to be added to the queue.
	 */
	public void addEvent(SimEvent newEvent) {
		// The event has to be inserted as the last of all events
		// with the same event_time(). Yes, this matters.
		double eventTime = newEvent.getTime();
		// 如果比最大的时间大，则直接插入
		if (eventTime >= maxTime) {
			list.add(newEvent);
			maxTime = eventTime;
			return;
		}

		// 否则，使用列表迭代器遍历链表，等价于按照时间顺序插入
		ListIterator<SimEvent> iterator = list.listIterator();
		SimEvent event;
		while (iterator.hasNext()) {
			event = iterator.next();
			if (event.getTime() > eventTime) {
				iterator.previous();
				iterator.add(newEvent);
				return;
			}
		}

		list.add(newEvent);
	}

	/**
	 * Returns an iterator to the events in the queue.
	 * 
	 * @return the iterator
	 */
	public Iterator<SimEvent> iterator() {
		return list.iterator();
	}

	/**
	 * Returns the size of this event queue.
	 * 
	 * @return the number of events in the queue.
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Clears the queue.
	 */
	public void clear() {
		list.clear();
	}

}