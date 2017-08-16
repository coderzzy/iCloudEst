/*
 * 标题:          基于Cloudsim4.0的云平台评估系统
 * 
 * 描述:  		  以开源项目Cloudsim4.0为核心，进行封装、优化的云平台仿真和运行分析评价系统
 * 
 * 备注:			  数据中心、主机、虚拟机、云任务ID均从1开始
 *
 * Copyright (c) 2016-2017, Nanjing Agricultural University, China
 */
package cn.edu.njau.zzy.control;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import cn.edu.njau.zzy.model.Cloud_DC;
import cn.edu.njau.zzy.model.Cloud_DCHost;
import cn.edu.njau.zzy.model.User;
import cn.edu.njau.zzy.util.Cloud_ArrayList;
import cn.edu.njau.zzy.util.IO;

/**
 * 利用方法接口和仿真父类实现的一个云仿真子类（example7-8,动态部署）
 * 
 * @author zzy
 *
 */
public class CloudsimThread_Run extends CloudSimulatedControl implements CloudsimulatedFunction{
	
	// 运行结果存储
	StringBuffer result = null;
	
	// 运行数据存储
	StringBuffer result_data = null;
	String filename = null;
	
	int brokerCount = 0;
	int vm_temp=0;
	int cloudlet_temp=0;
	
	// 云任务编号,从1开始
	int id = 1;
	
	// 构造函数
	public CloudsimThread_Run(Map<String, Object> session){
		result = new StringBuffer();
		result_data = new StringBuffer();
		result_data.append("0"); //识别到0表示开始，第二个0表示结束
		User user = (User)session.get("user");
		if(user != null){
			filename = new String(user.getName());
		}else{
			filename = new String("zzy");
		}
	}
	
	@Override
	public boolean execute(HttpServletRequest req, Map<String, Object> session) {
		// TODO Auto-generated method stub
		try {
			/* 
			 * 第一步:初始化Cloudsim包,应当在创建实体前被调用
			 */
			// 云使用者数量
			int num_user = 1;
			// 日期
			Calendar calendar = Calendar.getInstance();
			// 任务跟踪标志
			boolean trace_flag = false;
			// 初始化Cloudsim库
			CloudSim.init(num_user, calendar, trace_flag);
			
			
			/*
			 * 第二步:获取前端参数配置等数据(与分析)
			 */
			// 获得主机有关的前台数据
			String[] host_dcid = req.getParameterValues("host_dcid");				// 主机所在的数据中心编号
			String[] host_id = req.getParameterValues("host_id");					// 主机编号
			String[] host_ram = req.getParameterValues("host_ram");					// 主机内存
			String[] host_storage = req.getParameterValues("host_storage"); 		// 主机存储
			String[] host_bw = req.getParameterValues("host_bw");					// 主机带宽
			String[] host_mips = req.getParameterValues("host_mips");				// 主机计算能力
			String[] host_pe = req.getParameterValues("host_pe");					// 主机内核数
			String[] host_mechanism = req.getParameterValues("host_mechanism");		// 主机调度机制
			
			// 获得数据中心有关的前台数据
			String[] dc_id = req.getParameterValues("dc_id");						// 数据中心编号
			String[] dc_ragion = req.getParameterValues("dc_ragion");				// 数据中心区域？？time_zone
			String[] dc_arch = req.getParameterValues("dc_arch");					// 数据中心架构
			String[] dc_os = req.getParameterValues("dc_os");						// 数据中心操作系统
			String[] dc_vmtechnology = req.getParameterValues("dc_vmtechnology");	// 数据中心虚拟化技术
			String[] dc_pecost = req.getParameterValues("dc_pecost");				// 数据中心内核单价
			String[] dc_ramcost = req.getParameterValues("dc_ramcost");				// 数据中心内存单价
			String[] dc_storagecost = req.getParameterValues("dc_storagecost");		// 数据中心存储单价
			String[] dc_bwcost = req.getParameterValues("dc_bwcost");				// 数据中心带宽单价
			String[] dc_hostnum = req.getParameterValues("dc_hostnum");				// 数据中心主机数量
			
			// 获得虚拟机有关的前台数据
			String[] vm_id = req.getParameterValues("vm_id");						// 虚拟机编号
			int vm_num = vm_id.length;
			String[] vm_ram = req.getParameterValues("vm_ram");						// 虚拟机内存
			String[] vm_storage = req.getParameterValues("vm_storage");				// 虚拟机存储
			String[] vm_bw = req.getParameterValues("vm_bw");						// 虚拟机带宽
			String[] vm_mips = req.getParameterValues("vm_mips");					// 虚拟机计算能力
			String[] vm_pe = req.getParameterValues("vm_pe");						// 虚拟机内核数
			String[] vm_technology = req.getParameterValues("vm_technology");		// 虚拟机虚拟化技术
			String[] vm_mechanism = req.getParameterValues("vm_mechanism");			// 虚拟机调度机制
			
			// 获得云任务有关的前台数据
			int CloudletNum = Integer.parseInt(req.getParameter("cloudlet_num"));
			long CloudletMin = Long.parseLong(req.getParameter("cloudlet_minlength"));
			long CloudletMax = Long.parseLong(req.getParameter("cloudlet_maxlength"));
			long CloudletInputsize = Long.parseLong(req.getParameter("cloudlet_inputsize"));
			long CloudletOutputsize = Long.parseLong(req.getParameter("cloudlet_outputsize"));
			int CloudletChoose = Integer.parseInt(req.getParameter("cloudlet"));	// 云任务方式选择,选择值下标从0开始
			
			// 获得动态策略有关的前台数据
			long sleep_time = Long.parseLong(req.getParameter("sleep_time"));
			int vm_snum = Integer.parseInt(req.getParameter("vm_snum"));
			int cloudlet_snum = Integer.parseInt(req.getParameter("cloudlet_snum"));
			
			
			System.out.println("program-test");
			
			/*
			 * 第三步，创建数据中心:数据中心是Cloudsim中的资源提供者，我们至少需要它们中的一个来运行一个云仿真
			 */
			
			// datacenterlist 数据中心列表
			List<Datacenter> datacenterlist = new ArrayList<Datacenter>();
			
			// 添加数据到datacenterlist中
			for(int i=0;i < dc_id.length;i++){
				// hostlist 主机列表 临时使用
				List<Cloud_DCHost> hostlist = new ArrayList<Cloud_DCHost>();
				
				for (int j = 0; j < host_id.length; j++) {
					// 如果属于这个dc,就放入hostlist里面
					// 区分10以内为0x格式					
					if (host_dcid[j].equals("dc0" + (i + 1)) || host_dcid[j].equals("dc" + (i + 1))) {
						hostlist.add(new Cloud_DCHost(
								host_dcid[j], 
								Integer.parseInt(host_id[j]), 
								Integer.parseInt(host_ram[j]), 
								Integer.parseInt(host_storage[j]),
								Integer.parseInt(host_bw[j]),
								Integer.parseInt(host_pe[j]),
								Integer.parseInt(host_mips[j]), 
								host_mechanism[j]));
					}
				}
				
				// 创建临时dc数据存放
				Cloud_DC dc = new Cloud_DC(
						"datacenter_" + (i+1), 
						i+1, 
						Double.parseDouble(dc_ragion[i]),
						dc_arch[i], 
						dc_os[i], 
						dc_vmtechnology[i],
						Double.parseDouble(dc_pecost[i]), 
						Double.parseDouble(dc_ramcost[i]), 
						Double.parseDouble(dc_storagecost[i]),
						Double.parseDouble(dc_bwcost[i]), 
						Integer.parseInt(dc_hostnum[i]), 
						hostlist);
				
				// 先创建数据中心对象，然后添加到数据中心
				datacenterlist.add(this.createDatacenter(dc));
			}
			session.put("dc_length", datacenterlist.size());
			result.append("data:\r\n");
			result.append("数据中心数:"+ datacenterlist.size() +"\r\n");
			
			/*
			 * 第四步，给每一个数据中心设置代理Broker
			 */
			DatacenterBroker broker[] =new DatacenterBroker
					[vm_num/vm_snum <= CloudletNum/cloudlet_snum ? vm_num/vm_snum : CloudletNum/cloudlet_snum];
			broker[brokerCount] = createBroker();
			int brokerId = broker[brokerCount-1].getId();
			
			
			/*
			 * 第五步，创建虚拟机列表，并由代理进行登录
			 */
			vmlist = new ArrayList<Vm>();
			for (int i = vm_temp; i < vm_temp + vm_snum; i++) {
				if (vm_mechanism[i].equals("1")){
					vmlist.add(new Vm(Integer.parseInt(vm_id[i]), brokerId, Double.parseDouble(vm_mips[i]),
							Integer.parseInt(vm_pe[i]), Integer.parseInt(vm_ram[i]), Long.parseLong(vm_bw[i]),
							Long.parseLong(vm_storage[i]), vm_technology[i], new CloudletSchedulerTimeShared()));
				}else{
					vmlist.add(new Vm(Integer.parseInt(vm_id[i]), brokerId, Double.parseDouble(vm_mips[i]),
							Integer.parseInt(vm_pe[i]), Integer.parseInt(vm_ram[i]), Long.parseLong(vm_bw[i]),
							Long.parseLong(vm_storage[i]), vm_technology[i], new CloudletSchedulerSpaceShared()));
				}
			}
			broker[brokerCount-1].submitVmList(vmlist);
			vm_temp += vm_snum;
			
			/*
			 * 第六步，创建云任务列表，并由代理进行登录
			 */
			cloudletList = new ArrayList<Cloudlet>();
			
			// 使用模型
			UtilizationModel utilizationModel = new UtilizationModelFull();
			
			
			if (CloudletChoose == 0) {
				// 表示选择从min-max的等差递增数组
				// 任务等差数组
				long[] cloudletlength = Cloud_ArrayList.ArithmeticArrayLong(CloudletNum, CloudletMin, CloudletMax);
				for (int i = cloudlet_temp; i < cloudlet_temp+cloudlet_snum; i++) {
					Cloudlet cloudlet = new Cloudlet(id, cloudletlength[i], 1, CloudletInputsize, CloudletOutputsize,
							utilizationModel, utilizationModel, utilizationModel);
					cloudlet.setUserId(brokerId);
					cloudletList.add(cloudlet);
					id++;
				}
			} else if (CloudletChoose == 1) {
				// 表示选择从max-min的等差递减数组
				// 任务等差数组
				long[] cloudletlength = Cloud_ArrayList.ArithmeticArrayLong(CloudletNum, CloudletMax, CloudletMin);
				for (int i = cloudlet_temp; i < cloudlet_temp+cloudlet_snum; i++) {
					Cloudlet cloudlet = new Cloudlet(id, cloudletlength[i], 1, CloudletInputsize, CloudletOutputsize,
							utilizationModel, utilizationModel, utilizationModel);
					cloudlet.setUserId(brokerId);
					cloudletList.add(cloudlet);
					id++;
				}
			} else if (CloudletChoose == 2) {
				// 表示选择从min-max之间的随机数组
				// 任务随机数组
				long[] cloudletlength = Cloud_ArrayList.RandomArrayLong(CloudletNum, CloudletMin, CloudletMax);
				for (int i = cloudlet_temp; i < cloudlet_temp+cloudlet_snum; i++) {
					Cloudlet cloudlet = new Cloudlet(id, cloudletlength[i], 1, CloudletInputsize,CloudletOutputsize, 
							utilizationModel, utilizationModel, utilizationModel);
					cloudlet.setUserId(brokerId);
					cloudletList.add(cloudlet);
					id++;
				}
			}
			broker[brokerCount-1].submitCloudletList(cloudletList);
			cloudlet_temp += cloudlet_snum;
			
			/* 
			 * 第七步，根据动态策略，设置线程
			 */
			Runnable monitor = new Runnable() {
				@Override
				public void run() {
					// System.out.println("run-test");
					CloudSim.pauseSimulation(sleep_time);
					while (true) {
						if (CloudSim.isPaused()) {
							// System.out.println("run-test");
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// Log.printLine("\n\n\n" + CloudSim.clock() + ": The simulation is paused for 1 sec \n\n");

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					broker[brokerCount] = createBroker();
					int otherbrokerId = broker[brokerCount-1].getId();
							
					vmlist.clear();
					for (int i = vm_temp; i < vm_temp + vm_snum; i++) {
						if (vm_mechanism[i].equals("1")){
							vmlist.add(new Vm(Integer.parseInt(vm_id[i]), otherbrokerId, Double.parseDouble(vm_mips[i]),
											Integer.parseInt(vm_pe[i]), Integer.parseInt(vm_ram[i]), Long.parseLong(vm_bw[i]),
											Long.parseLong(vm_storage[i]), vm_technology[i], new CloudletSchedulerTimeShared()));
						}else{
							vmlist.add(new Vm(Integer.parseInt(vm_id[i]), otherbrokerId, Double.parseDouble(vm_mips[i]),
											Integer.parseInt(vm_pe[i]), Integer.parseInt(vm_ram[i]), Long.parseLong(vm_bw[i]),
											Long.parseLong(vm_storage[i]), vm_technology[i], new CloudletSchedulerSpaceShared()));
							}
					}
					broker[brokerCount-1].submitVmList(vmlist);
					vm_temp += vm_snum;
							
					cloudletList.clear();
					// 云任务编号,从1开始
					// int id = 1;
					// 使用模型
					UtilizationModel utilizationModel = new UtilizationModelFull();
							
					if (CloudletChoose == 0) {
						// 表示选择从min-max的等差递增数组
						// 任务等差数组
						long[] cloudletlength = Cloud_ArrayList.ArithmeticArrayLong(CloudletNum, CloudletMin, CloudletMax);
						for (int i = cloudlet_temp; i < cloudlet_temp+cloudlet_snum; i++) {
							Cloudlet cloudlet = new Cloudlet(id, cloudletlength[i], 1, CloudletInputsize, CloudletOutputsize,
											utilizationModel, utilizationModel, utilizationModel);
							cloudlet.setUserId(otherbrokerId);
							cloudletList.add(cloudlet);
							id++;
						}
					} else if (CloudletChoose == 1) {
						// 表示选择从max-min的等差递减数组
						// 任务等差数组
						long[] cloudletlength = Cloud_ArrayList.ArithmeticArrayLong(CloudletNum, CloudletMax, CloudletMin);
						for (int i = cloudlet_temp; i < cloudlet_temp+cloudlet_snum; i++) {
							Cloudlet cloudlet = new Cloudlet(id, cloudletlength[i], 1, CloudletInputsize, CloudletOutputsize,
											utilizationModel, utilizationModel, utilizationModel);
							cloudlet.setUserId(otherbrokerId);
							cloudletList.add(cloudlet);
							id++;
						}
					} else if (CloudletChoose == 2) {
						// 表示选择从min-max之间的随机数组
						// 任务随机数组
						long[] cloudletlength = Cloud_ArrayList.RandomArrayLong(CloudletNum, CloudletMin, CloudletMax);
						for (int i = cloudlet_temp; i < cloudlet_temp+cloudlet_snum; i++) {
							Cloudlet cloudlet = new Cloudlet(id, cloudletlength[i], 1, CloudletInputsize,CloudletOutputsize, 
											utilizationModel, utilizationModel, utilizationModel);
							cloudlet.setUserId(otherbrokerId);
							cloudletList.add(cloudlet);
							id++;
						}
					}
					broker[brokerCount-1].submitCloudletList(cloudletList);
					cloudlet_temp += cloudlet_snum;
							
					CloudSim.resumeSimulation();
				}
			};

			Thread t = new Thread(monitor);
			t.start();
			Thread.sleep(2000);
			
			/*
			 * 第八步，开始仿真
			 */
			CloudSim.startSimulation();
			
			/*
			 * 第九步，获取结果数据
			 */
			List<Cloudlet> resultlist = broker[0].getCloudletReceivedList();
			for(int i=1;i<brokerCount;i++){
				resultlist.addAll(broker[i].getCloudletReceivedList());
			}
			
			/*
			 * 第十步，停止仿真
			 */
			// t.destroy();
			CloudSim.stopSimulation();
			
			/*
			 * 第十一步，输出结果
			 */
			this.printCloudletList(resultlist, session);
			
			for (int i = 0; i < datacenterlist.size(); i++) {
				//datacenterlist.get(i).printDebts()
				Set<Integer> keys = datacenterlist.get(i).getDebts().keySet();
				Iterator<Integer> iter = keys.iterator();
				DecimalFormat df = new DecimalFormat("#.##");
				while (iter.hasNext()) {
					int key = iter.next();
					double value = datacenterlist.get(i).getDebts().get(key);
					//session.put("data"+Integer.toString(i), df.format(value));
					Log.printLine(key + "\t\t" + df.format(value));
				}
			}
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public Datacenter createDatacenter(Cloud_DC dc) {
		// TODO Auto-generated method stub
		// 根据提供的数据中心的数据，创建一个数据中心实体
		/*
		 * 第一步，创建一个主机列表,以备存储主机配置
		 */
		List<Host> hostlist = new ArrayList<Host>();
		for(int i=0;i<dc.getDchost().size();i++){
			/*
			 * 第二步，一个主机可能会含有多个内核，建立内核列表，并且通过循环来存储配置
			 */
			List<Pe> pelist = new ArrayList<Pe>();
			for(int j=0;j<dc.getDchost().get(i).getHostPe();j++){
				pelist.add(new Pe(j, new PeProvisionerSimple(dc.getDchost().get(i).getHostMips())));
			}
	
			
			/*
			 * 第三步，在每次循环中，将相应的数据存储在主机列表中
			 */
			// 选择值为1,表示时分复用机制;选择值为2，表示空分复用机制
			if(dc.getDchost().get(i).getHostMechanism().equals("1")){
				hostlist.add(new Host(
						dc.getDchost().get(i).getHostId(),
						new RamProvisionerSimple(dc.getDchost().get(i).getHostRam()),
						new BwProvisionerSimple(dc.getDchost().get(i).getHostBw()),
						dc.getDchost().get(i).getHostStorge(),
						pelist,
						new VmSchedulerTimeShared(pelist)));
			}else{
				hostlist.add(new Host(
						dc.getDchost().get(i).getHostId(),
						new RamProvisionerSimple(dc.getDchost().get(i).getHostRam()),
						new BwProvisionerSimple(dc.getDchost().get(i).getHostBw()),
						dc.getDchost().get(i).getHostStorge(),
						pelist,
						new VmSchedulerSpaceShared(pelist)));
			}
		}
		
		/*
		 * 第四步，创建一个DatacenterCharacteristics对象来存储数据中心的有关数据
		 */
		DatacenterCharacteristics dcCharacteristics = new DatacenterCharacteristics(
				dc.getDcArch(),
				dc.getDcOs(),
				dc.getDcVmm(),
				hostlist,
				dc.getDcRagion(),
				dc.getDcPeCost(),
				dc.getDcRamCost(),
				dc.getDcStorageCost(),
				dc.getDcBwCost());
		
		
		/*
		 * 第五步，SANStorage的配置，本次暂时用不到
		 */
		LinkedList<Storage> storagelist = new LinkedList<Storage>();
		
		
		/*
		 * 第六步，创建一个Datacenter对象
		 */
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(
					dc.getDcName(),
					dcCharacteristics,
					new VmAllocationPolicySimple(hostlist),
					storagelist,
					0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return datacenter;
	}

	@Override
	public DatacenterBroker createBroker() {
		// TODO Auto-generated method stub
		DatacenterBroker broker = null;
        try {
		broker = new DatacenterBroker("Broker_"+String.valueOf(brokerCount++));
        } catch (Exception e) {
		e.printStackTrace();
		return null;
        }
    	return broker;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void printCloudletList(List<Cloudlet> list, Map<String, Object> session) throws IOException{
		// TODO Auto-generated method stub
		
		// 获取结果列表长度
		int size = list.size();
		// 云任务临时实体
		Cloudlet cloudlet;
		// 格式化输出实体
		DecimalFormat dft = new DecimalFormat("###.##");

		/*
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");
		*/
		
		session.put("list_size", size);
		
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			//Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			session.put("cloudlet_getCloudletId"+Integer.toString(i), cloudlet.getCloudletId());
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				session.put("flag"+Integer.toString(i), 1);
				/*
				Log.print("SUCCESS");
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
				*/
				session.put("cloudlet_getResourceId"+Integer.toString(i), cloudlet.getResourceId()-1);
				result.append((cloudlet.getResourceId()-1)+"\t");
				session.put("cloudlet_getvmId"+Integer.toString(i), cloudlet.getVmId());
				result.append(cloudlet.getVmId()+"\t");
				session.put("cloudlet_ActualCPUTime"+Integer.toString(i), dft.format(cloudlet.getActualCPUTime()));
				result.append(dft.format(cloudlet.getActualCPUTime())+"\t");
				result_data.append(dft.format(cloudlet.getActualCPUTime())+"\r\n");
				session.put("cloudlet_getExecStartTime"+Integer.toString(i), dft.format(cloudlet.getExecStartTime()));
				result.append(dft.format(cloudlet.getExecStartTime())+"\t");
				session.put("cloudlet_getFinishTime"+Integer.toString(i), dft.format(cloudlet.getFinishTime()));
				result.append(dft.format(cloudlet.getFinishTime())+"\r\n");
			}
			else{
				session.put("flag"+Integer.toString(i), 0);
			}
		}
		result_data.append("0\r\n");
		
		IO.write(filename, result);
		IO.write(filename+"_data", result_data);
	}
}
