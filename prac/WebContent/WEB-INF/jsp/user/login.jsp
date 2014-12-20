<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML >
<html>
<head>
<title><s:message code="p_login.title"/></title>
<meta http-equiv="content-type" content="text/html;charset=UTF-8">
<jsp:include page="../inc.jsp"></jsp:include>
<script type="text/javascript" charset="utf-8">
$(function() {
	var formParam = {
		url : '${pageContext.request.contextPath}/userController/login.action',
		success : function(result) {
			var r = $.parseJSON(result);
			if (r.success) {
				window.location.reload();
			} else {
				alert(r.msg);
			}
		}
	};

	$('#ff').form(formParam);
});
</script>
</head>
<body>
	<div class="easyui-panel" title="<s:message code="p_login.title"/>" style="width: 400px">
		<div style="padding: 10px 0 10px 60px">
			<form id="ff" method="post">
				<table>
					<tr>
						<td><s:message code="p_login.username"/></td>
						<td><input class="easyui-validatebox" type="text" style="width:200px" name="uid" data-options="required:true"></input></td>
					</tr>
					<tr>
						<td><s:message code="p_login.password"/></td>
						<td><input class="easyui-validatebox" type="password" style="width:200px" name="pwd" data-options="required:true"></input></td>
					</tr>
				</table>
			</form>
		</div>
		<div style="text-align: center; padding: 5px">
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()"><s:message code="p_login.submit"/></a> <a href="javascript:void(0)" class="easyui-linkbutton"
				onclick="resetForm()"><s:message code="p_login.reset"/></a>
		</div>
	</div>
	<script>
		function submitForm() {
			$('#ff').submit();//.form('submit');
		}
		function resetForm() {
			$('#ff').form('clear');
		}
	</script>
</body>
</html>