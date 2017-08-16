/*
 * 标题:          基于Cloudsim4.0的云平台评估系统
 * 
 * 描述:  		  以开源项目Cloudsim4.0为核心，进行封装、优化的云平台仿真和运行分析评价系统
 * 
 * 备注:			  数据中心、主机、虚拟机、云任务ID均从1开始
 *
 * Copyright (c) 2016-2017, Nanjing Agricultural University, China
 */
package cn.edu.njau.zzy.util;

/**
 * 自定义异常类
 * 
 * @author zzy
 *
 */
public class AppException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	//异常编号
	private int exceptionCode; 
	
	//异常消息
	private String message; 
	
	/**
	 * 构造方法，设置异常消息
	 * 
	 * @param message 异常消息
	 */
	public AppException(String message){
		this.message = message;
	}
	
	/**
	 * 构造方法，设置异常消息和异常编号
	 * 
	 * @param message 异常消息
	 * @param exceptionCode 异常编号
	 */
	public AppException(String message,int exceptionCode){
		this.message = message;
		this.exceptionCode = exceptionCode;
	}
	
	/**
	 * 获取异常编号
	 * 
	 * @return 异常编号
	 */
	public int getExceptionCode(){
		return exceptionCode;
	}
	
	/**
	 * 获取详细的异常消息
	 * 
	 * @return detailMessage
	 */
	public String getMessage(){
		String detailMessage = "Detail message:"+exceptionCode+" "+message;
		return detailMessage;
	}
}

