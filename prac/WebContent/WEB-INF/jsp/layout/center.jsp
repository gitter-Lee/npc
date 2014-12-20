<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<script type="text/javascript">
	$(function() {
		$('#layout_center_tabsMenu').menu({
			onClick : function(item) {
				var curTabTitle = $(this).data('tabTitle');
				var type = $(item.target).attr('type');

				if (type === 'refresh') {
					layout_center_refreshTab(curTabTitle);
					return;
				}

				if (type === 'close') {
					var t = $('#layout_center_tabs').tabs('getTab', curTabTitle);
					if (t.panel('options').closable) {
						$('#layout_center_tabs').tabs('close', curTabTitle);
					}
					return;
				}

				var allTabs = $('#layout_center_tabs').tabs('tabs');
				var closeTabsTitle = [];

				$.each(allTabs, function() {
					var opt = $(this).panel('options');
					if (opt.closable && opt.title != curTabTitle && type === 'closeOther') {
						closeTabsTitle.push(opt.title);
					} else if (opt.closable && type === 'closeAll') {
						closeTabsTitle.push(opt.title);
					}
				});

				for ( var i = 0; i < closeTabsTitle.length; i++) {
					$('#layout_center_tabs').tabs('close', closeTabsTitle[i]);
				}
			}
		});

		$('#layout_center_tabs').tabs({
			fit : true,
			border : false,
			onContextMenu : function(e, title) {
				e.preventDefault();
				$('#layout_center_tabsMenu').menu('show', {
					left : e.pageX,
					top : e.pageY
				}).data('tabTitle', title);
			},
			tools : [ {
				iconCls : 'icon-reload',
				handler : function() {
					var href = $('#layout_center_tabs').tabs('getSelected').panel('options').href;
					if (href) {/*说明tab是以href方式引入的目标页面*/
						var index = $('#layout_center_tabs').tabs('getTabIndex', $('#layout_center_tabs').tabs('getSelected'));
						$('#layout_center_tabs').tabs('getTab', index).panel('refresh');
					} else {/*说明tab是以content方式引入的目标页面*/
						var panel = $('#layout_center_tabs').tabs('getSelected').panel('panel');
						var frame = panel.find('iframe');
						try {
							if (frame.length > 0) {
								for ( var i = 0; i < frame.length; i++) {
									frame[i].contentWindow.document.write('');
									frame[i].contentWindow.close();
									frame[i].src = frame[i].src;
								}
								if ($.browser.msie) {
									CollectGarbage();
								}
							}
						} catch (e) {
						}
					}
				}
			}, {
				iconCls : 'icon-cancel',
				handler : function() {
					var index = $('#layout_center_tabs').tabs('getTabIndex', $('#layout_center_tabs').tabs('getSelected'));
					var tab = $('#layout_center_tabs').tabs('getTab', index);
					if (tab.panel('options').closable) {
						$('#layout_center_tabs').tabs('close', index);
					} else {
						$.messager.alert('Tip', '[' + tab.panel('options').title + '] Cannot be closed', 'info');
					}
				}
			} ]
		});
	});

	function layout_center_refreshTab(title) {
		$('#layout_center_tabs').tabs('getTab', title).panel('refresh');
	}

	function layout_center_addTabFun(opts) {
		var t = $('#layout_center_tabs');
		if (t.tabs('exists', opts.title)) {
			t.tabs('select', opts.title);
		} else {
			t.tabs('add', opts);
		}
	}
</script>
<div id="layout_center_tabs" style="overflow: hidden;">
	<div title='<s:message code="p_center.title"/>' data-options="href:'${pageContext.request.contextPath}/dashboard.action'"></div>
</div>
<div id="layout_center_tabsMenu" style="width: 120px;display:none;">
	<div type="refresh">Refresh</div>
	<div class="menu-sep"></div>
	<div type="close">Close</div>
	<div type="closeOther">Close Others</div>
	<div type="closeAll">Close All</div>
</div>