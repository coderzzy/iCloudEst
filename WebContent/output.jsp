<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
<head>
<title>静态部署结果输出</title>
	<link rel="stylesheet" type="text/css" href="css/content.css"/>
	
</head>
<body>
	<div class="result">
		<h4>静态部署结果输出</h4>
		<h5>数据中心数：<%=session.getAttribute("dc_length") %></h5>
		<h5>您选择的算法：<%=session.getAttribute("suanfa") %></h5>
		
		<div class="result" style= "width:700px">
			<table style= "width: 100%">
			<thead>
				<tr>
					<td>任务ID</td>
					<td>状态</td>
					<td>数据中心id</td>
					<td>虚拟机ID</td>
					<td>用时</td>
					<td>开始时间</td>
					<td>结束时间</td>
				</tr>
			</thead>
			<tbody>
		<% 
		int size = (int)session.getAttribute("list_size");
		for (int i = 0; i < size; i++) {
		%>
				<tr>
					<td><%=session.getAttribute("cloudlet_getCloudletId"+Integer.toString(i)) %></td>
		<% 
		if ((int)session.getAttribute("flag"+Integer.toString(i)) == 1){
		%>
					<td>SUCCESS</td>
					<td><%=session.getAttribute("cloudlet_getResourceId"+Integer.toString(i)) %></td>
					<td><%=session.getAttribute("cloudlet_getvmId"+Integer.toString(i)) %></td>
					<td><%=session.getAttribute("cloudlet_ActualCPUTime"+Integer.toString(i)) %></td>
					<td><%=session.getAttribute("cloudlet_getExecStartTime"+Integer.toString(i)) %></td>
					<td><%=session.getAttribute("cloudlet_getFinishTime"+Integer.toString(i)) %></td>
		<%
		}else{
		%>
					<td>ERROR</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
		<%
		}
		}
		%>
			</tbody>
			</table>
	
	</div>
	</div>
</body>
</html>