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
 * 用户实体类
 * 
 * @author zzy
 */
public class User {
	
	//用户编号
	private int id; 
	
	// 手机
	private String mobile;
	
	// 昵称
	private String name;
	
	// 用户密码
	private String password; 

	/**
	 * 无参构造函数
	 */
	public User() {
		this.id = 0;
		this.mobile = "";
		this.password = "";
	}

	
	// --------------setter & getter--------------------
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

