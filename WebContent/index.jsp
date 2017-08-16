<%@ page language="java" import="java.util.*,cn.edu.njau.zzy.model.User" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Cloudsim NJAU</title>
    
    <!-- 清除浏览器缓存，重新进入时强行从服务器刷新 -->
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	
	<!-- 搜索引擎搜索关键词和主要内容 -->
	<meta http-equiv="keywords" content="cloudsim,njau">
	<meta http-equiv="description" content="cloudsim improved by njau">
	
	<!-- 引用css样式表 -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" >
	<link rel="stylesheet" type="text/css" href="css/style.css"/><!-- top和footer的css -->
	<link rel="stylesheet" type="text/css" href="css/style_login.css"/>
	<link rel="stylesheet" type="text/css" href="css/left.css">
	<style type="text/css">
		.theme-popover-mask {
			z-index: 9998;
			position:fixed;
			top:0;
			left:0;
			width:100%;
			height:100%;
			background:#000;
			opacity:0.4;
			filter:alpha(opacity=40);
			display:none
		}
		.theme-popover {
			z-index:9999;
			position:fixed;
			top:50%;
			left:50%;
			width:660px;
			height:360px;
			margin:-180px 0 0 -330px;
			border-radius:5px;
			border:solid 2px #666;
			background-color:#fff;
			display:none;
			box-shadow: 0 0 10px #666;
		}
		.theme-poptit {
			border-bottom:1px solid #ddd;
			padding:12px;
			position: relative;
		}
		.theme-popbod {
			padding:60px 15px;
			height: 148px;
		}
		.theme-poptit .close {
			float:right;
			color:#999;
			padding:5px;
			margin:-2px -5px -5px;
			font:bold 14px/14px simsun;
			text-shadow:0 1px 0 #ddd
		}
		.theme-poptit .close:hover {
			color:#444;
		}
	</style>
</head>
 
<body>
	<!-- 登录块 -->
	<div class="theme-popover">
	     <div class="theme-poptit">
	          <a href="javascript:;" title="关闭" class="close">×</a>
	          <h3>登录</h3>
	     </div>
	     <!-- 
	     <div class="theme-popbod dform">
	           <form class="theme-signin" name="loginform" action="" method="post">
	                <ol>
	                     <li><h4>你必须先登录！</h4></li>
	                     <li><strong>手机号:</strong><input class="ipt" type="text" name="log" value="输入手机号" size="20" /></li>
	                     <li><strong>密码:</strong><input class="ipt" type="password" name="pwd" value="输入密码" size="20" /></li>
	                     <li><input class="btn btn-primary" type="submit" name="submit" value="登 录" /></li>
	                </ol>
	           </form>
	     </div>
	     -->
		 <div class="theme-popbod">
		 	<form class="form-signin" role="form" action="loginAction.do" method="post">
	       		<center><h2 class="form-signin-heading">欢迎登录</h2></center>
	        	<input type="text" name="mobile" class="form-control" placeholder="请输入手机号码" required autofocus>
	        	<input type="password" name="password" class="form-control" placeholder="请输入密码" required>
	        	<center><button class="btn btn-lg btn-primary btn-block" type="submit" id="login">登录</button></center>
	      	</form>
	    </div>
	</div>
	<div class="theme-popover-mask"></div>
	
 	<div class="rows">
 		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="background-color:#F0F0F0;">
    		<div class="topleft">
    			<a class="cloudsim_show" href="index.jsp"><img src="images/logo.png" title="系统首页" /></a>
    		</div>    
    		<div class="topright">    
    			<ul>
    				<li><span><img src="images/help.png" title="帮助"  class="helpimg"/></span><a href="help/overview-summary.html"  target="_blank">帮助</a></li>
    				<%
    				User user = (User)session.getAttribute("user"); 
    				if(user == null){
    				%>
    				<li><a href="javascript:;" title="登录" class="theme-login">登录</a></li>
    				<li><a href="register.html"  target="_parent">注册</a></li>
    				<%
    				}else{
    				%>
    				<li><%= user.getName() %></li>
    				<li><a href="logoutAction.do"  target="_parent">注销</a></li>
    				<%
    				}
    				%>
    			</ul>
    		</div>
 		</div>
 	</div>
 	<div class="rows">
 		<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
  			<dl class="leftmenu">
  				<div class="title">
		   			<span><img src="images/leftico01.png" />通用仿真部署</span>
				</div>
				<dd>
	  				<ul>
	  					<li><cite></cite><a href="content.html" target="_parent">静态部署</a><i></i></li>
	  					<li><cite></cite><a href="content_thread.html" target="_parent">动态部署</a><i></i></li>
	  				</ul>
	  			</dd>
	  			<div class="title">
		   			<span><img src="images/leftico01.png" />网络拓扑仿真</span>
				</div>
				<dd>
	  				<ul>
	  					<li><cite></cite><a href="contentNet.html" target="_parent">静态仿真</a><i></i></li>
	  					<li><cite></cite><a href="content_threadNet.html" target="_parent">动态仿真</a><i></i></li>
	  				</ul>
	  			</dd>
	  			<div class="title">
		    		<span><img src="images/leftico01.png" />demo介绍</span>
				</div>
				<dd>
		  			<ul>
		  				<li><cite></cite><a href="javascript:;">examples</a><i></i></li>
		  				<li><cite></cite><a href="javascript:;">network</a><i></i></li>
		  				<li><cite></cite><a href="javascript:;">power</a><i></i></li>
		  			</ul>	
				</dd>
				<div class="title">
		    		<span><img src="images/leftico01.png" />账户信息</span>
				</div>
				<dd>
		  			<ul>
		  				<li><cite></cite><a href="javascript:;" class="myinfo_show">我的信息</a><i></i></li>
		  				<li><cite></cite><a href="javascript:;" class="myhistory_show">我的记录</a><i></i></li>
		  			</ul>	
				</dd>
  			</dl>
 		</div>
 		<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">&nbsp;</div>
 		<div id="cloudsim_show">
	  		<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
	  			<div style="text-align:center">
	  			<h1>基于CloudSim的云平台评估系统</h1>
	  			</div>
				<br>
				<h4>介绍:</h4><br>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本系统基于云平台仿真软件——CloudSim,针对云平台的性能评估问题，研究待评估的云平台的平台参数特征、
				资源调度策略、所执行负载的类型以及云平台的性能评估数学模型,从而给出一个现有或即将搭建的云平台的性能综合评估结果，或根据用户的计算能力需求初步给出其搭建的云平台的各项配置指数等相关信息的建议，
				从而避免不必要的浪费，减少云平台搭建的人力和资金浪费，提高云平台搭建的效率。
				<br><br><h4>CloudSim:</h4><br>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; CloudSim是在GridSim模型基础上发展而来，提供了云计算的特性，支持云计算的资源管理和调度模拟。
				云计算与网格计算的一个显著区别是云计算采用了成熟的虚拟化技术，将数据中心的资源虚拟化为资源池，打包对外向用户提供服务，
				CloudSim体现了此特点，扩展部分实现了一系列接口，提供基于数据中心的虚拟化技术、虚拟化云的建模和仿真功能。
				通常，数据中心的一台主机的资源可以根据用户的需求映射到多台虚拟机上，因此，虚拟机之间存在对主机资源的竞争关系。
				CloudSim提供了资源的监测、主机到虚拟机的映射功能。
				CloudSim的CIS（Cloud Information Service）和DataCenterBroker实现资源发现和信息交互，是模拟调度的核心。
				用户自行开发的调度算法可在DataCenterBroker的方法中实现，从而实现调度算法的模拟。
				<br><br><h4>主要特点:</h4><br>
				<ul>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持大型云计算数据中心的建模和仿真</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持虚拟化服务器主机的建模和仿真，以及可自定义的策略来为虚拟机提供主机资源</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持应用程序容器的建模和仿真</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持能源感知计算资源的建模和仿真</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持数据中心网络拓扑和消息传递应用程序的建模和仿真</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持联合云的建模和仿真</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持动态插入模拟元素，停止和恢复模拟</li>
					<li> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支持将主机分配给虚拟机的用户定义策略和将主机资源分配给虚拟机的策略</li>
				</ul>
				<br>
	  		</div>
		</div>
		<div id="myinfo_show" style="display:none">
			<%
			if(user == null){
			%>
			
			<% 
			}
			%>	
			
		</div>
		<div id="myhistory_show" style="display:none">
	
		</div>
	</div>
	
	<div class="rows">
		<iframe src="footer.html" width="100%" height="100%"></iframe>
	</div>
</body>


	<!-- 引用JQuery框架 -->
	<script type="text/javascript" src="js/jquery-3.2.1.min.js"></script>
	
	<script type="text/javascript">
   		$(document).ready(function() {
   			// 登陆弹出框事件
   			$('.theme-login').click(function(){
   				$('.theme-popover-mask').fadeIn(100);
   				$('.theme-popover').slideDown(200);
   			})
   			$('.theme-poptit .close').click(function(){
   				$('.theme-popover-mask').fadeOut(100);
   				$('.theme-popover').slideUp(200);
   			})
   			
   			// 显示CloudSim介绍
   			$('.cloudsim_show').click(function(){
   				$('#myinfo_show').hide();
   				$('#myhistory_show').hide();
   				$('#cloudsim_show').show();
   			})
			
   			// 显示个人信息
   			$('.myinfo_show').click(function(){
   				$('#cloudsim_show').hide();
   				$('#myhistory_show').hide();
   				$('#myinfo_show').show();
   			})
   			
   			// 显示个人记录
   			$('.myhistory_show').click(function(){
   				$('#cloudsim_show').hide();
   				$('#myinfo_show').hide();
   				$('#myhistory_show').show();
   			})
   		})
	</script>
 	<!-- 传统frame布局
  	<frameset rows="90,*" frameborder="no" border="0" framespacing="0">
    	<frame src="top.jsp" name="topFrame" scrolling="No" noresize="noresize" id="topFrame" title="topFrame" />
    	<frameset cols="20%,80%" frameborder="no" border="0" framespacing="0">
      		<frame src="left.jsp" name="leftFrame" scrolling="No" noresize="noresize" id="leftFrame" title="leftFrame" />
      		<frame src="content.jsp" name="rightFrame" scrolling="No" id="rightFrame" title="rightFrame" />
   		</frameset>
   	</frameset>
  	<noframes>
  	<body>
  	</body>
  	</noframes>
  	-->
</html>
