/*
 * 标题:          基于Cloudsim4.0的云平台评估系统
 * 
 * 描述:  		  以开源项目Cloudsim4.0为核心，进行封装、优化的云平台仿真和运行分析评价系统
 * 
 * 备注:			  数据中心、主机、虚拟机、云任务ID均从1开始
 *
 * Copyright (c) 2016-2017, Nanjing Agricultural University, China
 */
package cn.edu.njau.zzy.model;

/**
 * 数据中心主机类
 * 
 * @author zzy
 *
 */
public class Cloud_DCHost {
	
	// 主机名字
	private String hostName;
	
	// 主机编号
	private int hostId;
	
	// 主机内存大小
	private int hostRam;
	
	// 主机存储大小
	private int hostStorge;
	
	// 主机带宽
	private int hostBw;
	
	// 主机内核数
	private int hostPe;
	
	// 主机计算速度
	private int hostMips;
	
	// 主机调度机制
	private String hostMechanism;

	/**
	 * 构造函数
	 * 
	 * @param hostName   主机名字
	 * @param hostId     主机编号
	 * @param hostRam    主机内存大小
	 * @param hostStorge 主机存储大小
	 * @param hostBw     主机带宽大小
	 * @param hostPe     主机内核数
	 * @param hostMips	   主机计算量
	 */
	public Cloud_DCHost(String hostName, int hostId, int hostRam, int hostStorge, int hostBw, int hostPe,
			int hostMips,String hostMechanism) {
		super();
		this.hostName = hostName;
		this.hostId = hostId;
		this.hostRam = hostRam;
		this.hostStorge = hostStorge;
		this.hostBw = hostBw;
		this.hostPe = hostPe;
		this.hostMips = hostMips;
		this.hostMechanism = hostMechanism;
	}

	
	// --------------setter & getter--------------------
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public int getHostRam() {
		return hostRam;
	}

	public void setHostRam(int hostRam) {
		this.hostRam = hostRam;
	}

	public int getHostStorge() {
		return hostStorge;
	}

	public void setHostStorge(int hostStorge) {
		this.hostStorge = hostStorge;
	}

	public int getHostBw() {
		return hostBw;
	}

	public void setHostBw(int hostBw) {
		this.hostBw = hostBw;
	}

	public int getHostPe() {
		return hostPe;
	}

	public void setHostPe(int hostPe) {
		this.hostPe = hostPe;
	}

	public int getHostMips() {
		return hostMips;
	}

	public void setHostMips(int hostMips) {
		this.hostMips = hostMips;
	}


	public String getHostMechanism() {
		return hostMechanism;
	}


	public void setHostMechanism(String hostMechanism) {
		this.hostMechanism = hostMechanism;
	}
}
