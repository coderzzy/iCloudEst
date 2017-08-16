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

import java.util.List;

import cn.edu.njau.zzy.model.Cloud_DCHost;

/**
 * 数据中心类
 * 
 * @author zzy
 *
 */
public class Cloud_DC {
	
	// 数据中心名字
	private String dcName;
	
	// 数据中心编号
	private int dcId;
	
	// 数据中心区域
	private double dcRagion;
	
	// 数据中心框架
	private String dcArch;
	
	// 数据中心操作系统
	private String dcOs;
	
	// 虚拟化方案
	private String dcVmm;
	
	// 内核单价
	private double dcPeCost;
	
	// 内存单价
	private double dcRamCost;
	
	// 存储单价
	private double dcStorageCost;
	
	// 带宽单价
	private double dcBwCost;
	
	// 主机数量
	private int dcHostNum;
	
	// 主机实体列表
	private List<Cloud_DCHost> dchost;

	public Cloud_DC(String dcName, int dcId, double dcRagion, String dcArch, String dcOs, String dcVmm, double dcPeCost,
			double dcRamCost, double dcStorageCost, double dcBwCost, int dcHostNum, List<Cloud_DCHost> dchost) {
		super();
		this.dcName = dcName;
		this.dcId = dcId;
		this.dcRagion = dcRagion;
		this.dcArch = dcArch;
		this.dcOs = dcOs;
		this.dcVmm = dcVmm;
		this.dcPeCost = dcPeCost;
		this.dcRamCost = dcRamCost;
		this.dcStorageCost = dcStorageCost;
		this.dcBwCost = dcBwCost;
		this.dcHostNum = dcHostNum;
		this.dchost = dchost;
	}

	
	// --------------setter & getter--------------------
	public String getDcName() {
		return dcName;
	}

	public void setDcName(String dcName) {
		this.dcName = dcName;
	}

	public int getDcId() {
		return dcId;
	}

	public void setDcId(int dcId) {
		this.dcId = dcId;
	}

	public double getDcRagion() {
		return dcRagion;
	}

	public void setDcRagion(double dcRagion) {
		this.dcRagion = dcRagion;
	}

	public String getDcArch() {
		return dcArch;
	}

	public void setDcArch(String dcArch) {
		this.dcArch = dcArch;
	}

	public String getDcOs() {
		return dcOs;
	}

	public void setDcOs(String dcOs) {
		this.dcOs = dcOs;
	}

	public String getDcVmm() {
		return dcVmm;
	}

	public void setDcVmm(String dcVmm) {
		this.dcVmm = dcVmm;
	}

	public double getDcPeCost() {
		return dcPeCost;
	}

	public void setDcPeCost(double dcPeCost) {
		this.dcPeCost = dcPeCost;
	}

	public double getDcRamCost() {
		return dcRamCost;
	}

	public void setDcRamCost(double dcRamCost) {
		this.dcRamCost = dcRamCost;
	}

	public double getDcStorageCost() {
		return dcStorageCost;
	}

	public void setDcStorageCost(double dcStorageCost) {
		this.dcStorageCost = dcStorageCost;
	}

	public double getDcBwCost() {
		return dcBwCost;
	}

	public void setDcBwCost(double dcBwCost) {
		this.dcBwCost = dcBwCost;
	}

	public int getDcHostNum() {
		return dcHostNum;
	}

	public void setDcHostNum(int dcHostNum) {
		this.dcHostNum = dcHostNum;
	}

	public List<Cloud_DCHost> getDchost() {
		return dchost;
	}

	public void setDchost(List<Cloud_DCHost> dchost) {
		this.dchost = dchost;
	}
}
