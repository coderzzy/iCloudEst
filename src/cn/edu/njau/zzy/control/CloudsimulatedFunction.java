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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;

import cn.edu.njau.zzy.model.Cloud_DC;

/**
 * 云仿真运行控制接口，限制了使用的方法
 * 
 * @author zzy
 *
 */
public interface CloudsimulatedFunction {
	/**
	 * 最上层云仿真执行函数
	 * 
	 * @param req 		获取前端数据
	 * @param session	存储后端数据
	 * @return true表示仿真成功，否则失败
	 */
	public boolean execute(HttpServletRequest req,Map<String,Object> session);
	
	/**
	 * 配置数据中心函数
	 * 
	 * @param dc 数据中心数据实体
	 * @return 配置好的数据中心
	 */
	Datacenter createDatacenter(Cloud_DC dc);
	
	/**
	 * 设置数据中心代理
	 * 
	 * @return 数据中心代理实体
	 */
	DatacenterBroker createBroker();
	
	/**
	 * 输出任务结果，把结果数据存储
	 * 
	 * @param list 		云任务列表
	 * @param session	后端数据存储实体
	 */
	void printCloudletList(List<Cloudlet> list,Map<String,Object> session) throws IOException;
}
