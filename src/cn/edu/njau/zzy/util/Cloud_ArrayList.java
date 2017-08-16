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
 * 云任务数组生成类，提供静态方法
 * 
 * @author zzy
 *
 */
public class Cloud_ArrayList {
	
	/**
	 * long类型的等差数组，静态方法
	 * 
	 * @param num			数组总项数
	 * @param minlength		最小值
	 * @param maxlength		最大值
	 * @return  等差数组
	 */
	public static long[]  ArithmeticArrayLong(int num,long minlength,long maxlength){
		// 创建返回值数组
		long[] result = new long[num];
		// 单位增量
		long dx = (maxlength-minlength)/(num-1);
		// 循环对数组赋值
		for(int i=0;i<num;i++){
			result[i] = minlength + i * dx;
		}
		return result;
	}
	
	/**
	 * long类型的随机数组，静态方法
	 * 
	 * @param num
	 * @param minlength
	 * @param maxlength
	 * @return
	 */
	public static long[] RandomArrayLong(int num,long minlength,long maxlength){
		// 创建返回值数组
		long[] result = new long[num];
		// 循环对数组赋值
		for(int i=0;i<num;i++){
			result[i] = (long) (minlength + Math.random() * (maxlength - minlength));
		}
		return result;
	}
}
