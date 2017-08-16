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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import cn.edu.njau.zzy.control.UserControl;
import cn.edu.njau.zzy.model.User;
import cn.edu.njau.zzy.util.AppException;

/**
 * 注册表单处理类
 * 
 * @author zzy
 *
 */
public class RegisterAction extends ActionSupport{

	private static final long serialVersionUID = 1L;

	//声明并初始化用户信息服务层实现类
	private UserControl userControl = new UserControl();
		
		//获取form表单内容
		private HttpServletRequest req = ServletActionContext.getRequest();
		private String mobile = req.getParameter("mobile");
		private String name = req.getParameter("name");
		private String password = req.getParameter("password");
		
		/**
		 * 注册处理
		 */
		@Override
		public String execute(){
			//声明并初始化User对象，接受注册信息
			User user = new User();
			user.setName(name);
			user.setPassword(password);
			user.setMobile(mobile);
			
			try {
				//调用用户信息服务层处理注册请求，注册成功则跳转至登陆页
				if(userControl.register(user)){
					return "login";
				} else{//注册失败，则返回注册页面
					return "register";
				}
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//系统异常，则跳转到404报错页面
				return "404";
			}
		}
}
