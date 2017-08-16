/*
 * 标题:          基于Cloudsim4.0的云平台评估系统
 * 
 * 描述:  		  以开源项目Cloudsim4.0为核心，进行封装、优化的云平台仿真和运行分析评价系统
 * 
 * 备注:			  数据中心、主机、虚拟机、云任务ID均从1开始
 *
 * Copyright (c) 2016-2017, Nanjing Agricultural University, China
 */
package cn.edu.njau.zzy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.edu.njau.zzy.model.User;
import cn.edu.njau.zzy.util.AppException;
import cn.edu.njau.zzy.util.DBUtil;

/**
 * 用户数据访问类
 * 
 * @author zzy
 */
public class UserDB {

	/**
	 * 查询是否用户已经存在
	 * 
	 * @param mobile 手机号
	 * @retrun 有同名用户返回true，否则返回false
	 * @throws AppException
	 */
	public boolean isExist(String mobile) throws AppException {
		// 函数返回值标志位
		boolean flag = false; 
		
		// 声明数据库对象、预编译对象和结果集对象
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			// 创建数据库连接
			conn = DBUtil.getConnection();
			// 声明操作语句：根据用户名查询用户编号，"?"为占位符
			String sql = "select id from user where mobile = ?";
			// 预编译sql
			psmt = conn.prepareStatement(sql);
			// 为占位符设置值
			psmt.setString(1, mobile);
			// 执行此查询操作
			rs = psmt.executeQuery();
			// 查询到记录，则该账户名的用户存在，flag为true
			if (rs.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.isExist");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.isExist");
		} finally {
			// 关闭数据库查询结果集
			DBUtil.closeResultSet(rs);

			// 关闭数据库查询指令
			DBUtil.closeStatement(psmt);

			// 关闭数据库连接
			DBUtil.closeConnection(conn);
		}
		return flag;
	}

	/**
	 * 将用户实体存放进数据库
	 * 
	 * @param user 用户实体
	 * @retrun 成功返回true，否则返回false
	 * @throws AppException
	 */
	public boolean saveUser(User user) throws AppException {
		// 函数返回值标志位
		boolean flag = false; 
		// 判断用户对象若为空，则不进行保存操作
		if (user == null) {
			return flag;
		}
		// 声明数据库连接对象，预编译对象
		Connection conn = null;
		PreparedStatement psmt = null;

		try {
			// 创建数据库连接
			conn = DBUtil.getConnection();
			// 声明操作语句：将用户信息保存到数据库中，"?"为占位符
			String sql = "insert into user (mobile,name,password)"
					+ " values(?,?,?)";
			// 预编译sql
			psmt = conn.prepareStatement(sql);
			// 为占位符设置值
			psmt.setString(1, user.getMobile());
			psmt.setString(2, user.getName());
			psmt.setString(3, user.getPassword());
			// 执行更新操作，返回受影响行数
			int count = psmt.executeUpdate();
			// 如果受影响行数大于0，则操作成功
			if (count > 0) {
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.saveUser");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.saveUser");
		} finally {
			// 关闭数据库查询指令
			DBUtil.closeStatement(psmt);

			// 关闭数据库连接
			DBUtil.closeConnection(conn);
		}

		return flag;
	}

	/**
	 * 根据手机号查询用户编号
	 * 
	 * @param mobile 手机号
	 * @param password 密码        
	 * @return 用户编号
	 * @throws AppException
	 */
	public int searchID(String mobile,String password) throws AppException {
		// 用户编号
		int id = 0; 
		// 声明数据库对象、预编译对象和结果集对象
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			// 创建数据库连接
			conn = DBUtil.getConnection();
			// 声明操作语句：根据用户名和密码查询用户编号，"?"为占位符
			String sql = "select id from user where mobile = ? and password = ?";
			// 预编译sql
			psmt = conn.prepareStatement(sql);
			// 为占位符设置值
			psmt.setString(1, mobile);
			psmt.setString(2, password);
			// 执行此查询操作
			rs = psmt.executeQuery();
			// 查询到记录，提取用户编号
			if (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.search");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.search");
		} finally {
			// 关闭数据库查询结果集
			DBUtil.closeResultSet(rs);

			// 关闭数据库查询指令
			DBUtil.closeStatement(psmt);

			// 关闭数据库连接
			DBUtil.closeConnection(conn);
		}
		return id;
	}

	/**
	 * 根据用户编号查询用户对象
	 * 
	 * @param id 用户编号
	 * @return 用户对象
	 * @throws AppException
	 */
	public User getUser(int id) throws AppException {
		// 声明用户对象
		User user = null; 
		// 声明数据库对象、预编译对象和结果集对象
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			// 创建数据库连接
			conn = DBUtil.getConnection();
			// 声明操作语句：根据用户编号查询用户对象，"?"为占位符
			String sql = "select * from user where id = ?";
			// 预编译sql
			psmt = conn.prepareStatement(sql);
			// 为占位符设置值
			psmt.setInt(1, id);
			// 执行此查询操作
			rs = psmt.executeQuery();
			// 查询到记录，用用户实体保存信息
			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt("id"));
				user.setMobile(rs.getString("mobile"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.getUser");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("cn.edu.njau.zzy.db.UserDB.getUser");
		} finally {
			// 关闭数据库查询结果集
			DBUtil.closeResultSet(rs);

			// 关闭数据库查询指令
			DBUtil.closeStatement(psmt);

			// 关闭数据库连接
			DBUtil.closeConnection(conn);
		}
		return user;
	}
}
