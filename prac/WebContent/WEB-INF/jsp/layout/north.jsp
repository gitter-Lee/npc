<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<script type="text/javascript" charset="utf-8">
	function logoutFun() {
		$.getJSON('${pageContext.request.contextPath}/userController/logout.action', function(result) {
			location.reload();
		});
	}
	function userInfoFun() {
		$('<div/>').dialog({
			href : '${pageContext.request.contextPath}/userController/userInfo.action',
			width : 250,
			height : 160,
			modal : true,
			title : '<s:message code="p_north.dialog.userinfo.title"/>',
			buttons : [ {
				text : '<s:message code="p_north.dialog.userinfo.button.save"/>',
				iconCls : 'icon-edit',
				handler : function() {
					var d = $(this).closest('.window-body');
					$('#user_userInfo_form').form('submit', {
						url : '${pageContext.request.contextPath}/userController/updateMyProfile.action',
						success : function(result) {
							try {
								var r = $.parseJSON(result);
								if (r.success) {
									d.dialog('destroy');
								}
								$.messager.show({
									title : '<s:message code="common.dialog.title.tip"/>',
									msg : r.msg
								});
							} catch (e) {
								$.messager.alert('<s:message code="common.dialog.title.tip"/>', result);
							}
						}
					});
				}
			} ],
			onClose : function() {
				$(this).dialog('destroy');
			},
			onLoad : function() {
			}
		});
	}
</script>
<div style="position: absolute; right: 5px; top: 5px; ">
	<c:if test="${user.id != null}"><s:message code="p_north.welcome" arguments="${user.name}" /></c:if>
</div>
<div style="position: absolute; right: 0px; bottom: 0px; ">
	<a href="javascript:void(0);" class="easyui-menubutton" data-options="menu:'#layout_north_zxMenu',iconCls:'icon-help'"><s:message code="p_north.menu.setting"/></a>
</div>
<div id="layout_north_zxMenu" style="width: 100px; display: none;">
	<div onclick="userInfoFun();"><s:message code="p_north.menu.profile"/></div>
	<div class="menu-sep"></div>
	<div onclick="logoutFun();"><s:message code="p_north.menu.logout"/></div>
</div>
