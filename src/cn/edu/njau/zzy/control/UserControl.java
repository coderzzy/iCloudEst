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

import cn.edu.njau.zzy.db.UserDB;
import cn.edu.njau.zzy.model.User;
import cn.edu.njau.zzy.util.AppException;

/**
 * 用户信息服务类
 * 
 * @author zzy
 */
public class UserControl {
	//初始化用户数据库访问层实现类
	private UserDB userDB = new UserDB();
	
	/**
	 * 进行用户注册，注册成功则返回true，注册失败则返回false，
	 * 不区分是因为存在同名用户还是注册操作失败，统一为注册失败
	 * 
	 * @param user 用户对象
	 * @return 注册成功返回true，失败则返回false
	 * @throws AppException
	 */
	public boolean register(User user) throws AppException{
		boolean flag = false;
		try {
			//1.验证不存在同名用户
			if(! userDB.isExist(user.getMobile())){
				//2.不存在同名用户则进行保存操作
				flag = userDB.saveUser(user);
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.control.UserControl.register");
		}
		
		return flag;
	}
	
	/**
	 * 进行用户登录，如果查询到相应用户则返回存有信息的用户实体，否则实体为null
	 * 
	 * @param mobile 手机号
	 * @param password 密码
	 * @return 用户实体
	 * @throws AppException
	 */
	public User login(String mobile,String password) throws AppException{
		User user = null;
		int id = 0;
		try {
			id = userDB.searchID(mobile,password); //查询得到用户编号
			if(id > 0){
				//编号存在则返回用户实体
				user = new User();
				user = userDB.getUser(id);
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.control.UserControl.login");
		}
		
		return user;
	}
}

