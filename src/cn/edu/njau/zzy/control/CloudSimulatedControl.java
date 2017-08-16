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

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * 云仿真运行总类，限制了基础的数据结构
 * 
 * @author zzy
 *
 */
public class CloudSimulatedControl {
	// The cloudlet list 云任务列表
	protected List<Cloudlet> cloudletList;

	// The vmlist 虚拟机列表
	protected List<Vm> vmlist;
}
