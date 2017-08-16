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

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import cn.edu.njau.zzy.db.UserDB;
import cn.edu.njau.zzy.util.AppException;
import net.sf.json.JSONObject;

/**
 * 注册前Ajax数据确认类
 * 
 * @author zzy
 *
 */
public class AjaxRegisterAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	
	private String mobile;  
    private String result;  

    // ajax请求参数赋值  
    public void setMobile(String mobile) {  
        this.mobile = mobile;  
    }  

    // ajax返回结果  
    public String getResult() {  
        return result;  
    } 
	
	/**
	 * 操作执行
	 */
	@Override
	public String execute(){
			UserDB userDB = new UserDB();
			//将数据存储在map里，再转换成json类型数据，也可以自己手动构造json类型数据
			Map<String,Object> map = new HashMap<String,Object>();
			boolean isExistMobile = false;	
			try {
				if(userDB.isExist(mobile)){
					isExistMobile = true;
				}else{
					isExistMobile = false;
				}
				map.put("isExistMobile", isExistMobile);
				JSONObject json = JSONObject.fromObject(map);//将对象转换成json类型数据
				result = json.toString();//给result赋值，传递给页面
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return SUCCESS;
	}
}
