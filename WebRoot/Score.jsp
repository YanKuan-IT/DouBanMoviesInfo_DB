<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'index.jsp' starting page</title>
  </head>
  
  <body>
    <a href="MovieServlet?method=listAll"><button>查看所有的数据</button></a>
    <a href="ScoreServlet?method=barChart"><button>柱状图</button></a>
    <a href="ScoreServlet?method=pieChart"><button>饼状图</button></a>
    <a href="ScoreServlet?method=lineChart"><button>折线图</button></a><br>
    
  	<img alt="" src="${pageContext.request.contextPath }/barChart.jpg" />
  	<img alt="" src="${pageContext.request.contextPath }/pieChart.jpg" />
  	<img alt="" src="${pageContext.request.contextPath }/lineChart.jpg" />
  	
  </body>
</html>
