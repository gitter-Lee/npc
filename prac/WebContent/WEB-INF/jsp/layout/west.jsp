<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<script type="text/javascript">
	
</script>
<div class="easyui-accordion" data-options="fit:true,border:false">
	<div title="<s:message code="p_west.mainmenu"/>" data-options="isonCls:'icon-reload',tools : [ {
				iconCls : 'icon-reload',
				handler : function() {
					$('#layout_mainmenu_tree').tree('reload');
				}
			}, {
				iconCls : 'icon-add',
				handler : function() {
					var node = $('#layout_mainmenu_tree').tree('getSelected');
					if (node) {
						$('#layout_mainmenu_tree').tree('expandAll', node.target);
					} else {
						$('#layout_mainmenu_tree').tree('expandAll');
					}
				}
			}, {
				iconCls : 'icon-remove',
				handler : function() {
					var node = $('#layout_mainmenu_tree').tree('getSelected');
					if (node) {
						$('#layout_mainmenu_tree').tree('collapseAll', node.target);
					} else {
						$('#layout_mainmenu_tree').tree('collapseAll');
					}
				}
			} ]">
		<ul id="layout_mainmenu_tree"></ul>
	</div>
	<div title="<s:message code="p_west.workgroup"/>">
		<ul id="layout_workgroup_tree"></ul>
	</div>
	<div title="<s:message code="p_west.security"/>">
		<ul id="layout_security_tree"></ul>
	</div>
</div>