<!DOCTYPE html>
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>403</title>
<meta http-equiv='refresh' content='3;url=<c:url value="/index.html"/>'>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<c:url value="/favicon.ico"/>" type="image/x-icon" rel="icon" />
<link href="<c:url value="/favicon.ico"/>" type="image/x-icon" rel="Shortcut Icon" />
<link rel="stylesheet" type="text/css" href="<c:url value="/static/portal.css"/>" />
</head>
<body>
	<div id="container">
		<div class="dualbrand" style="position: absolute; top: 50%; left: 50%; margin-left: -53px; margin-top: -49px; border: 0px;">${json.msg}</div>
	</div>
</body>
</html>
