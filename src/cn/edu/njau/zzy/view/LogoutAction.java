/*
 * 标题:          基于Cloudsim4.0的云平台评估系统
 * 
 * 描述:  		  以开源项目Cloudsim4.0为核心，进行封装、优化的云平台仿真和运行分析评价系统
 * 
 * 备注:			  数据中心、主机、虚拟机、云任务ID均从1开始
 *
 * Copyright (c) 2016-2017, Nanjing Agricultural University, China
 */
package cn.edu.njau.zzy.view;

import java.util.Map;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 注销表单处理类
 * 
 * @author zzy
 *
 */
public class LogoutAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户注销处理,返回跳转信息
	 */
	@Override
	public String execute(){
		// 初始化一个session对象
		Map<String,Object> session = ActionContext.getContext().getSession();
		// 移除对象类
		session.remove("user");
		
		return "index";
	}
}
