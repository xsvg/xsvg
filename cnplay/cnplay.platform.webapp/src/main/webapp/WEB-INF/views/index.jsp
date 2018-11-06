<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
<title>${message}</title>
<meta http-equiv='refresh' content='3;url=<c:url value="/index.html?${p}"/>'>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<link href="<c:url value="/favicon.ico"/>" type="image/x-icon" rel="icon" />
<link href="<c:url value="/favicon.ico"/>" type="image/x-icon" rel="Shortcut Icon" />
<link rel="stylesheet" type="text/css" href="<c:url value="/static/portal.css"/>" />
</head>
<script type="text/javascript">
	if ('${message}' !== '')
	{
		//alert('${message}');	
	}
	//location.href = '<c:url value="/index.html${p}"/>';
</script>
<body>
	<div id="container">
<%-- 		<img alt="加载中..." id="loadimageid" style="position: absolute; top: 50%; left: 50%; margin-left: -53px; margin-top: -49px; border: 0px;" src="<c:url value="/static/icons/load.gif"/>" /> --%>
		<div class="dualbrand" style="position: absolute; top: 50%; left: 50%; margin-left: -53px; margin-top: -49px; border: 0px;">${message}</div>
	</div>
</body>
</html>