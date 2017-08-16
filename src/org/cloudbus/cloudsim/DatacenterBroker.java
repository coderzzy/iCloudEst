/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * DatacentreBroker represents a broker acting on behalf of a user. It hides VM management, as vm
 * creation, sumbission of cloudlets to this VMs and destruction of VMs.
 * 数据中心代理者代表用户执行操作。
 * 它封装了虚拟机管理的操作，包括虚拟机创建、云任务中运行和虚拟机的销毁
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBroker extends SimEntity {

	/** The vm list. */
	protected List<? extends Vm> vmList;

	/** The vms created list. */
	protected List<? extends Vm> vmsCreatedList;

	/** The cloudlet list. */
	protected List<? extends Cloudlet> cloudletList;

	/** The cloudlet submitted list. */
	protected List<? extends Cloudlet> cloudletSubmittedList;

	/** The cloudlet received list. */
	protected List<? extends Cloudlet> cloudletReceivedList;

	/** The cloudlets submitted. */
	protected int cloudletsSubmitted;

	/** The vms requested. */
	protected int vmsRequested;

	/** The vms acks. */
	protected int vmsAcks;

	/** The vms destroyed. */
	protected int vmsDestroyed;

	/** The datacenter ids list. */
	protected List<Integer> datacenterIdsList;

	/** The datacenter requested ids list. */
	protected List<Integer> datacenterRequestedIdsList;

	/** The vms to datacenters map. */
	protected Map<Integer, Integer> vmsToDatacentersMap;

	/** The datacenter characteristics list. */
	protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;

	/**
	 * Created a new DatacenterBroker object.
	 * 
	 * @param name name to be associated with this entity (as required by Sim_entity class from
	 *            simjava package)
	 * @throws Exception the exception
	 * @pre name != null
	 * @post $none
	 */
	public DatacenterBroker(String name) throws Exception {
		super(name);

		setVmList(new ArrayList<Vm>());
		setVmsCreatedList(new ArrayList<Vm>());
		setCloudletList(new ArrayList<Cloudlet>());
		setCloudletSubmittedList(new ArrayList<Cloudlet>());
		setCloudletReceivedList(new ArrayList<Cloudlet>());

		cloudletsSubmitted = 0;
		setVmsRequested(0);
		setVmsAcks(0);
		setVmsDestroyed(0);

		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
	}

	/**
	 * This method is used to send to the broker the list with virtual machines that must be
	 * created.
	 * 
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends Vm> list) {
		getVmList().addAll(list);
	}

	/**
	 * This method is used to send to the broker the list of cloudlets.
	 * 
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitCloudletList(List<? extends Cloudlet> list) {
		getCloudletList().addAll(list);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 * 
	 * @param cloudletId ID of the cloudlet being bount to a vm
	 * @param vmId the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId) {
		CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
	}

	/////////////////////////Scheduling Algorithm////////////////////////////////////
	/**
	 * Extended function for allocating cloudlets to vms in order.
	 * RR轮询算法，按作业顺序分配的算法
	 * 当云任务数量大于虚拟机数量的时候，编号为0的虚拟机继续分配云任务
	 */
	public void bindCloudletsToVmsSimple(){
		int cloudletNum=cloudletList.size();
		int vmNum=vmList.size();
		int idx=0;
		for(int i=0;i<cloudletNum;i++){
			cloudletList.get(i).setVmId(vmList.get(idx).getId());
			// 当云任务数量大于调度的虚拟机数量时候，可能会有两个或以上云任务执行同一个虚拟机节点
			idx = (idx + 1) % vmNum;
		}
	}
	
	/**
	 * 时间优先的贪心算法
	 */
	public void bindCloudletsToVmsTimeAwared(){
		int cloudletNum=cloudletList.size();
		int vmNum=vmList.size();
		
		//time[i][j] represents the execution time of cloudlet i runing on vm j
		double[][] time=new double[cloudletNum][vmNum];
		
		// Sort cloudletList in dsc, vm in asc
		// 均升序排序
		Collections.sort(cloudletList,new CloudletComparator());
		Collections.sort(vmList,new VmComparator());
		
		/*
		////////////////////////////For test//////////////////////////////////
		System.out.println("///////////For test///////////////");
		for(int i=0;i<cloudletNum;i++){
			System.out.print(cloudletList.get(i).getCloudletId()+":"+cloudletList.get(i).getCloudletLength()+" ");
		}
		System.out.println();
		for(int i=0;i<vmNum;i++){
			System.out.print(vmList.get(i).getId()+":"+vmList.get(i).getMips()+" ");
		}
		System.out.println();
		System.out.println("//////////////////////////////////");
		//////////////////////////////////////////////////////////////////////
		*/
		
		for(int i=0;i<cloudletNum;i++){
			for(int j=0;j<vmNum;j++){
				time[i][j]=
					(double)cloudletList.get(i).getCloudletLength()/vmList.get(j).getMips();
				
				//System.out.print(time[i][j]+" ");   //For test
			}
			//System.out.println();   //For test
		}
		
		double[] vmLoad=new double[vmNum];
		int[] vmTasks=new int[vmNum]; //The number of tasks running on the specific vm
		double minLoad=0;
		int idx=0;
		
		//Allocate the first cloudlet to the fastest vm
		vmLoad[vmNum-1]=time[0][vmNum-1];
		vmTasks[vmNum-1]=1;
		cloudletList.get(0).setVmId(vmList.get(vmNum-1).getId());
		
		for(int i=1;i<cloudletNum;i++){
			minLoad=vmLoad[vmNum-1]+time[i][vmNum-1];
			idx=vmNum-1;
			
			for(int j=vmNum-2;j>=0;j--){
				if(vmLoad[j]==0){
					if(minLoad>=time[i][j])idx=j;
					break;
				}
				
				if(minLoad>vmLoad[j]+time[i][j]){
					minLoad=vmLoad[j]+time[i][j];
					idx=j;
				}
				//Load balance
				else if(minLoad==vmLoad[j]+time[i][j]&&vmTasks[j]<vmTasks[idx])
					idx=j;
			}
			vmLoad[idx]+=time[i][idx];
			vmTasks[idx]++;
			cloudletList.get(i).setVmId(vmList.get(idx).getId());
		}
	}
	
	/**
	 * Min-min(极小算法):
	 * Min-Min算法是一种实现起来很简单的算法，算法的执行时间也很快。
	 * 算法的思想是首先映射小的任务，并且映射到执行快的机器上。
	 * 执行过程为：计算要参与映射事件的每个任务在各个机器上的期望完成时间，找到每个任务的最早完成时间及其对应的机器；
	 	* 从中找出具有最小最早完成时间的任务，将该任务指派给获得它的机器；
	 	* 指派完成后，更新机器期望就绪时间并将已完成映射的任务从任务集合中删除。
	 	* 重复上面的过程，直到所有的任务都被映射完。
	 * 该算法形式化描述如下：
	 	* M为所有未调度的任务的集合
	 	* （1）判断任务集合M是否为空，不为空，执行（2）；否则跳到步骤（6）。
	 	* （2）对于任务集合中的所有任务，求出它们映射到所有可用机器上的最早完成时间cij。
	 	* （3）根据（2）的结果，找出最早完成时间最小的那个任务mi和所对应的机器hj。
	 	* （4）将任务mi映射到机器hj上；并将该任务从任务集合中删除。
	 	* （5）更新其它任务在机器hj上的最早完成时间；回到（1）。
	 	* （6）此次映射事件结束，退出程序。
	 */
	public void bindCloudletsToVmsTimeMin_min(){
		int cloudletNum=cloudletList.size();
		int vmNum=vmList.size();
		
		// time[i][j] represents the execution time of cloudlet i runing on vm j
		// time[i][j],第i个云任务执行第j个虚拟机所需要的时间
		double[][] time=new double[cloudletNum][vmNum];
		
		// 最小加载时间
		double minLoad=9999;
		
		// 任务集合M
		List<Cloudlet> cloudletSet = new ArrayList<Cloudlet>(cloudletList);
		// 计算time数组
		
		for(int i=0;i<cloudletNum;i++){
			for(int j=0;j<vmNum;j++){
				time[i][j]=
					(double)cloudletList.get(i).getCloudletLength()/vmList.get(j).getMips();
			}
					//System.out.println();   //For test
		}
		
		// 第一步，判断任务集合是否为空，否则映射事件结束
		while(cloudletSet.size() > 0){
			// System.out.println("min-min.test:"+cloudletSet.size());
			// 第二步，求出最早完成时间，即为time[][]数组
			// 提高程序效率，这一步不在循环中进行
			
			// 第三步，找出最小time[][],以及i和j
			minLoad = 9999;
			int min_i = 0;
			int min_j = 0;
			for(int i=0;i<cloudletNum;i++){
				for(int j=0;j<vmNum;j++){
					if(time[i][j] < minLoad){
						minLoad = time[i][j];
						min_i = i;
						min_j = j;
					}
				}
			}
			//System.out.println("min-min:"+cloudletList.get(min_i).getCloudletId()+"\t"+vmList.get(min_j).getId());
			
			// 第四步，映射任务，同时任务从任务集合删除
			cloudletList.get(min_i).setVmId(vmList.get(min_j).getId());
			cloudletSet.remove(cloudletList.get(min_i));
			for(int j=0;j<vmNum;j++){
				time[min_i][j] = 9999;
			}
			
			// 第五步,更新time[][min_j]最早完成时间
			for(int i=0;i<cloudletNum;i++){
				time[i][min_j] += minLoad; // time[min_i][j] = 9999,加上minLoad也不会有影响
			}
		}
	}
	
	

	/**
	 *  针对Min-min贪心算法的饥饿现象，Max-min算法更加注重公平
	 *  算法描述：
	 	*（1）对云任务和虚拟机节点进行升序排序
	 	*（2）对于任务集合中的所有任务，求出它们映射到所有可用机器上的最早完成时间cij。
	 	*（3）判断任务集合M是否为空，不为空，执行（4）；否则跳到步骤（6）。
	 	*（4）找出虚拟机已运行时间中的最大值下标为k，将Mi映射到Vk上
	 	*（5）更新数据，删除任务，跳转（3）
	 	*（6）事件映射结束
	 *  
	 */
	public void bindCloudletsToVmsTimeMax_min(){
		int cloudletNum=cloudletList.size();
		int vmNum=vmList.size();
		
		//time[i][j] represents the execution time of cloudlet i runing on vm j
		double[][] time=new double[cloudletNum][vmNum];
				
		// Sort cloudletList in dsc, vm in asc
		// 均升序排序
		Collections.sort(cloudletList,new CloudletComparator());
		Collections.sort(vmList,new VmComparator());
		
		for(int i=0;i<cloudletNum;i++){
			for(int j=0;j<vmNum;j++){
				time[i][j]=
					(double)cloudletList.get(i).getCloudletLength()/vmList.get(j).getMips();
			}
					//System.out.println();   //For test
		}
		
		double[] vmLoad=new double[vmNum];
		int k = 0;
		for(int i=0;i<cloudletNum;i++){
			
			for(int j=1;j<vmNum;j++){
				if(vmLoad[k] > vmLoad[j]){
					k = j;
				}
			}
			
			vmLoad[k] += time[i][k];
			cloudletList.get(i).setVmId(vmList.get(k).getId());
		}
	}
	
	/**
	 * Custom class for sorting cloudletList according to their MIs
	 */
	private class CloudletComparator implements Comparator<Cloudlet>{
		public int compare(Cloudlet cl1,Cloudlet cl2){
			return (int)(cl1.getCloudletLength()-cl2.getCloudletLength());
		}
	}
	
	/**
	 * Custom class for sorting vmList according to their MIPSs
	 */
	private class VmComparator implements Comparator<Vm>{
		public int compare(Vm vm1,Vm vm2){
			return (int)(vm1.getMips()-vm2.getMips());
		}
	}
	////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Processes events available for this Broker.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		// Resource characteristics request
			case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
				processResourceCharacteristicsRequest(ev);
				break;
			// Resource characteristics answer
			case CloudSimTags.RESOURCE_CHARACTERISTICS:
				processResourceCharacteristics(ev);
				break;
			// VM Creation answer
			case CloudSimTags.VM_CREATE_ACK:
				processVmCreate(ev);
				break;
			// A finished cloudlet returned
			case CloudSimTags.CLOUDLET_RETURN:
				processCloudletReturn(ev);
				break;
			// if the simulation finishes
			case CloudSimTags.END_OF_SIMULATION:
				shutdownEntity();
				break;
			// other unknown tags are processed by this method
			default:
				processOtherEvent(ev);
				break;
		}
	}

	/**
	 * Process the return of a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
			createVmsInDatacenter(getDatacenterIdsList().get(0));
		}
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloud Resource List received with "
				+ getDatacenterIdsList().size() + " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId
					+ " has been created in Datacenter #" + datacenterId + ", Host #"
					+ VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId
					+ " failed in Datacenter #" + datacenterId);
		}

		incrementVmsAcks();

		// all the requested VMs have been created
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			submitCloudlets();
		} else {
			// all the acks received, but some VMs were not created
			if (getVmsRequested() == getVmsAcks()) {
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) {
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}

				// all datacenters already queried
				if (getVmsCreatedList().size() > 0) { // if some vm were created
					submitCloudlets();
				} else { // no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
		}
	}

	/**
	 * Process a cloudlet return event.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
				+ " received");
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}

	/**
	 * Overrides this method when making a new and different type of Broker. This method is called
	 * by {@link #body()} for incoming unknown tags.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null.");
			return;
		}

		Log.printLine(getName() + ".processOtherEvent(): "
				+ "Error - event unknown by this DatacenterBroker.");
	}

	/**
	 * Create the virtual machines in a datacenter.
	 * 
	 * @param datacenterId Id of the chosen PowerDatacenter
	 * @pre $none
	 * @post $none
	 */
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId()
						+ " in " + datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}

	/**
	 * Submit cloudlets to the created VMs.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void submitCloudlets() {
		int vmIndex = 0;
		for (Cloudlet cloudlet : getCloudletList()) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // vm was not created
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId() + ": bount VM not available");
					continue;
				}
			}

			Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
					+ cloudlet.getCloudletId() + " to VM #" + vm.getId());
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			getCloudletSubmittedList().add(cloudlet);
		}

		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}

	/**
	 * Destroy the virtual machines running in datacenters.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		for (Vm vm : getVmsCreatedList()) {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Destroying VM #" + vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();
	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T> the generic type
	 * @param vmList the new vm list
	 */
	protected <T extends Vm> void setVmList(List<T> vmList) {
		this.vmList = vmList;
	}

	/**
	 * Gets the cloudlet list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>) cloudletList;
	}

	/**
	 * Sets the cloudlet list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletList the new cloudlet list
	 */
	protected <T extends Cloudlet> void setCloudletList(List<T> cloudletList) {
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletSubmittedList() {
		return (List<T>) cloudletSubmittedList;
	}

	/**
	 * Sets the cloudlet submitted list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletSubmittedList the new cloudlet submitted list
	 */
	protected <T extends Cloudlet> void setCloudletSubmittedList(List<T> cloudletSubmittedList) {
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletReceivedList() {
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletReceivedList the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmsCreatedList() {
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T> the generic type
	 * @param vmsCreatedList the vms created list
	 */
	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 * 
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 * 
	 * @param vmsRequested the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 * 
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 * 
	 * @param vmsAcks the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		vmsAcks++;
	}

	/**
	 * Gets the vms destroyed.
	 * 
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 * 
	 * @param vmsDestroyed the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 * 
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 * 
	 * @param datacenterIdsList the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 * 
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 * 
	 * @param vmsToDatacentersMap the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 * 
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 * 
	 * @param datacenterCharacteristicsList the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 * 
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 * 
	 * @param datacenterRequestedIdsList the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

}
