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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * IO操作方法类
 * 
 * @author zzy
 *
 */
public class IO {
	public static void write(String name,StringBuffer str){
		 FileWriter fw=null;
	     	try {
	         	//在工程的根目录下创建一个FileWriter对象，可以续写
	            fw=new FileWriter("e:\\"+name+".txt",true);
	            //要被写入的字符串
	            fw.write(str.toString());
	            //将数据刷新到目的文件中
	            fw.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally{
	        	if(fw!=null){
	        		try {
	        			//关闭数据流
	        			fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        }
	}
	
	public static StringBuffer read(String name){
		FileReader fr=null;
		StringBuffer result = new StringBuffer();
        try {
             fr=new FileReader("e:\\"+name+".txt");
             int count=0;
             char buf[]=new char[1024];
             while((count=fr.read(buf))!=-1){
                result.append(new String(buf,0,count));
             }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("文件不存在...");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fr!=null){
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
	}
}
