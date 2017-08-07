<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'index.jsp' starting page</title>
    <style type="text/css">
		table{
			width:80%;
			border:1px solid blue;
			border-collapse: collapse;
			margin: 30px auto;
		}
		td{
			border: 1px solid red;
			height: 30px;
			text-align: center;
		}
		tr:NTH-CHILD(odd) {
			background: #e6e6e6;
		}
		tr:NTH-CHILD(even) {
			background: #99ccff;
		}
		.box_one{
			text-align: center;
			margin-top:30px; 
		}
	</style>
  </head>
  
  <body>
    <!-- 只用于获取数据 -->
    <a href="MovieServlet?method=getData"><button>抓取数据</button></a>
    
    <a href="MovieServlet?method=listAll"><button>查看所有的数据</button></a>
  	  
    <c:if test="${fn:length(allMovies)>0 }">
    	<table>
   			<tr>
   				<td>序号</td>
   				<td>名称</td>
   				<td>类型</td>
   				<td>发布时间</td>
   				<td>评分</td>
   				<td>是否可以在线播放</td>
   			</tr>
   			<c:forEach items="${allMovies}" var="list" varStatus="vs">
			    <tr>
			    	<td>${vs.count }</td>
			        <td>
			        	<a href="${list.movieUrl }">${list.name }</a>
			        </td>
			        <td>${list.types }</td>
			        <td>${list.release_date }</td>
			        <td>${list.score }</td>
			        <td>${list.is_playable }</td>
			    </tr>
    		</c:forEach>
   		</table>
    </c:if>
  </body>
</html>
