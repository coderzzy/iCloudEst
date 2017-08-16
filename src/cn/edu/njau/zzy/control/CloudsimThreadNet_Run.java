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
import cn.edu.njau.zzy.model.User;

/**
 * 利用方法接口和仿真父类实现的一个云仿真子类(networkExample3&4,动态部署)
 * 
 * @author zzy
 *
 */
public class CloudsimThreadNet_Run extends CloudSimulatedControl implements CloudsimulatedFunction{
		// 运行结果存储
		StringBuffer result = null;
		
		// 运行数据存储
		StringBuffer result_data = null;
		String filename = null;
		
		// 构造函数
		public CloudsimThreadNet_Run(Map<String, Object> session){
			result = new StringBuffer();
			result_data = new StringBuffer();
			result_data.append("0"); //识别到0表示开始，0后表示算法，第二个0表示结束
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
			return false;
		}

		@Override
		public Datacenter createDatacenter(Cloud_DC dc) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DatacenterBroker createBroker() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void printCloudletList(List<Cloudlet> list, Map<String, Object> session) throws IOException {
			// TODO Auto-generated method stub
			
		}
}