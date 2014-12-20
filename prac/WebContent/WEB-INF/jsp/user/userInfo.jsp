<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<script type="text/javascript" charset="utf-8">

</script>
<div class="easyui-panel" style="overflow: hidden;padding: 5px;border: 0" align="center">
	<form id="user_userInfo_form" method="post">
		<input name="uid" type="hidden" value="${user.id}" />
		<table class="tableForm">
			<tr>
				<th style="width: 55px;"><s:message code="p_userinfo.name"/></th>
				<td><input readonly="readonly" value="${user.name}" /></td>
			</tr>
			<tr>
				<th><s:message code="p_userinfo.email"/></th>
				<td><input name="email" value="${user.email}" /></td>
			</tr>
		</table>
	</form>
</div>
