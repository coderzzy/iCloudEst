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
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import cn.edu.njau.zzy.control.UserControl;
import cn.edu.njau.zzy.model.User;
import cn.edu.njau.zzy.util.AppException;

/**
 * 登陆表单处理类
 * 
 * @author zzy
 *
 */
public class LoginAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	
	//声明并初始化用户信息服务层实现类
	private UserControl userControl = new UserControl();
		
	//获取form表单内容
	HttpServletRequest req = ServletActionContext.getRequest();
	String mobile = req.getParameter("mobile");
	String password = req.getParameter("password");
	
	/**
	 * 用户登陆处理,返回跳转信息
	 */
	@Override
	public String execute(){
		User user = new User();
		try {
			//登录查询
			user = userControl.login(mobile,password);
			if(user != null){
				//初始化一个session对象
				Map<String, Object> session = ActionContext.getContext().getSession();
				// 查询到匹配用户，保存至session中，跳转到首页
				session.put("user", user);
				return "index";
			}
			else{
				//登录失败，返回错误页面，提示错误信息
				return "login_error";
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//异常返回404页面
			return "404";
		}
	}
}
