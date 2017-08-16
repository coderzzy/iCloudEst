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
 * 测试类
 * 
 * @author zzy
 *
 */
public class Test {
	
	static int total = 0;
	
	public static void main(String[] args){
		int array[] = new int[10];
		array[total] = function();
		for(int i=0;i<array.length;i++){
			System.out.println("array["+i+"] = "+array[i]);
		}
	}
	
	public static int function(){
		total++;
		return 1;
	}
}
