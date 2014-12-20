<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML>
<html>
<head>
<title><s:message code="p_main.title" /></title>
<meta http-equiv="content-type" content="text/html;charset=UTF-8">
<script type="text/javascript">
	var session_userId = '${user.id}';
	if (session_userId == '') {
		window.location.href = "${pageContext.request.contextPath}/";
	}
</script>
<jsp:include page="inc.jsp"></jsp:include>
</head>
<body class="easyui-layout">
	<div
		data-options="region:'north',href:'${pageContext.request.contextPath}/north.action'"
		style="height: 50px; overflow: hidden; background-image: url('${pageContext.request.contextPath}/images/logo.png'); background-repeat: no-repeat;"></div>
	<div
		data-options="region:'west',split:true,title:'<s:message code="p_main.navigator" />',href:'${pageContext.request.contextPath}/west.action'"
		style="width: 200px; overflow: hidden;"></div>
	<div
		data-options="region:'center',href:'${pageContext.request.contextPath}/center.action'"
		style="overflow: hidden;"></div>
	<div
		data-options="region:'south',href:'${pageContext.request.contextPath}/south.action'"
		style="height: 27px; overflow: hidden;"></div>
</body>
</html>