/*
 * File: classes/view/Portal.js
 *
 * This file was generated by Sencha Architect version 3.2.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('Gotom.view.Portal', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.Portal',

	requires : [ 'Ext.panel.Tool', 'Ext.tab.Panel', 'Ext.tab.Tab' ],

	passWin : '',
	border : false,
	id : 'app-viewport',

	layout : {
		type : 'border',
		padding : '0 5 5 5'
	},

	initComponent : function()
	{
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				xtype : 'panel',
				region : 'north',
				border : false,
				height : 60,
				id : 'app-header',
				animCollapse : false,
				header : false,
				title : '',
				layout : {
					type : 'accordion',
					animate : false
				}
			}, {
				xtype : 'container',
				region : 'center',
				border : false,
				id : 'portal-container',
				layout : 'border',
				items : [ {
					xtype : 'panel',
					region : 'west',
					split : true,
					id : 'app-options',
					maxWidth : 450,
					minWidth : 100,
					width : 180,
					animCollapse : true,
					collapsible : true,
					title : '系统菜单',
					layout : {
						type : 'accordion',
						animate : true
					},
					tools : [ {
						xtype : 'tool',
						type : 'refresh',
						listeners : {
							click : {
								fn : me.onToolClick,
								scope : me
							}
						}
					} ]
				}, me.processTabPanel({
					xtype : 'tabpanel',
					region : 'center',
					split : true,
					listeners : {
						afterrender : {
							fn : me.onTabPanelAfterRender,
							scope : me
						}
					},
					items : [ {
						xtype : 'panel',
						border : false,
						layout : 'border',
						title : '我的桌面',
						listeners : {
							afterrender : {
								fn : me.onHomePanelAfterRender,
								scope : me
							}
						}
					} ]
				}) ]
			}, {
				xtype : 'panel',
				region : 'south',
				split : false,
				border : false,
				height : 1,
				id : 'app-footer',
				shrinkWrap : 0,
				layout : 'border'
			} ],
			listeners : {
				afterlayout : {
					fn : me.onPortalVIewPanelAfterLayout,
					single : true,
					scope : me
				}
			}
		});

		me.callParent(arguments);
	},

	processTabPanel : function(config)
	{
		config.plugins = Ext.create('Gotom.view.TabCloseMenu');
		return config;
	},

	onToolClick : function(tool, e, eOpts)
	{
		this.onOptionsToolClick(tool, e, eOpts);
	},

	onTabPanelAfterRender : function(component, eOpts)
	{
		this.tabPanel = component;
	},

	onHomePanelAfterRender : function(component, eOpts)
	{
		this.homePanel = component;
	},

	onPortalVIewPanelAfterLayout : function(container, layout, eOpts)
	{
		this.onLoad();
	},

	createTools : function()
	{
		return [ {
			xtype : 'tool',
			type : 'gear',
			handler : function(e, target, header, tool)
			{
				var portlet = header.ownerCt;
				portlet.setLoading(portlet.id + 'Loading...');
				Ext.defer(function()
				{
					portlet.setLoading(false);
				}, 1000);
			}
		} ];
	},

	onPortletClose : function(portlet)
	{
		alert('"' + portlet.title + '" was removed');
	},

	onLoad : function()
	{
		var me = this;
		me.seconds = 0;
		me.headerPanel = Ext.getCmp('app-header');
		me.footerPanel = Ext.getCmp('app-footer');
		me.options = Ext.getCmp('app-options');
		// me.tabPanel = Ext.getCmp('tabPanel');
		me.loadHeader();
		me.loadOptions();
		me.footerPanel.setHeight(0);
		me.showNotice({
			html : '欢迎光临',
			title : '登录提示'
		});
	},

	loadDesktop : function(name)
	{
		var me = this;
		try
		{
			if (Ext.isEmpty(name))
			{
				this.loadIndex();
			}
			else
			{
				var desktop = Ext.create(name, {
					region : 'center',
					title : '我的桌面',
					border : false,
					header : false

				});
				me.homePanel.removeAll();
				me.homePanel.add(desktop);
				me.tabPanel.setActiveTab(me.homePanel);
			}
		}
		catch (error)
		{

		}
	},

	loadHeader : function()
	{
		var me = this;
		try
		{
			Common.ajax({
				component : me,
				lock : false,
				url : ctxp + '/home/info',
				callback : function(result)
				{
					me.setHeader(result);
					if (result.success)
					{
						var user = result.rows;
						if (user.needModifyPswd)
						{
							Ext.Msg.alert('提示', '为了您的账户安全请先修改密码！', function()
							{
								var cmp = Ext.getCmp('app-viewport');
								var formwin = Ext.create('Gotom.view.SettingPassowrd', {
									logout : true
								});
								formwin.addListener('close', function(panel,opts)
                                {
									if(formwin.logout)
									{
										location.href = ctxp + '/logout';
									}
                                });								
								formwin.show();
							});
						}
					}
				}
			});
		}
		catch (error)
		{
			me.showNotice({
				html : error,
				title : '异常提示'
			});
		}
		// Ext.defer(function(){me.loadHeader();}, 3000);
	},

	setHeader : function(result)
	{
		var me = this;
		try
		{
			var header = me.headerPanel;
			var data = result.rows;
			me.desktopPanel = data.desktopPanel;
			me.loadDesktop(me.desktopPanel);
			header.setLoading(false);
			me.setLoading(false);
			var image = ctxp + '/static/icons/logo.jpg';
			if (!Ext.isEmpty(data.topbgImg))
			{
				image = data.topbgImg;
			}
			header.setBodyStyle('background-image', 'url(' + image + ')');
			var imlogo = ctxp + '/static/icons/logo.png';
			if (!Ext.isEmpty(data.logoImg))
			{
				imlogo = data.logoImg;
			}
			document.title = data.title;
			if (Ext.isEmpty(data.title))
			{
				document.title = '抵质押品库房管理系统';
			}
			var style = 'color: red;';
			if (!Ext.isEmpty(data.fontStyle))
			{
				style = data.fontStyle;
			}
			var htmlStr = '';
			htmlStr += '<div class="logoPanel"><img onclick="Ext.defer(function(){Ext.getCmp(\'app-viewport\').loadHeader();}, 100);" src="' + imlogo + '" border="0"/></div>';
			htmlStr += '<div class="titlePanel"><font style="' + style + '">' + data.name + '</font></div>';
			htmlStr += '<div class="logoutPanel">';
			htmlStr += '<a style="' + style + '" href="javascript:Ext.getCmp(\'app-viewport\').settingPassword();">修改密码</a>　';
			htmlStr += '<a style="' + style + '" href="' + ctxp + data.logoutUrl + '">' + data.logoutText + '</a>';
			htmlStr += '</div>';
			htmlStr += '<div class="userPanel">';
			htmlStr += '<font style="' + style + '">欢迎您：</font><a href="#" style="' + style + '">' + data.userFullname + '</a>　';
			htmlStr += '</div>';
			
			if (me.headerHtml != htmlStr)
				header.update(htmlStr);
			me.headerHtml = htmlStr;
		}
		catch (error)
		{
			me.showNotice({
				html : error,
				title : '异常提示'
			});
		}
	},

	loadOptions : function()
	{
		var me = this;
		try
		{
			Common.ajax({
				component : me.options,
				message : '加载菜单...',
				url : ctxp + '/home/menu?id=',
				callback : function(result)
				{
					me.setOptions(result.rows);
				}
			});
		}
		catch (error)
		{
			me.showNotice({
				html : error,
				title : '异常提示'
			});
		}
	},

	setOptions : function(data)
	{
		var me = this;
		try
		{
			var options = me.options;
			options.removeAll();
			var URL = ctxp + '/home/menu';
			for (var i = 0; i < data.length; i++)
			{
				var treeStore = Common.createMenuTreeStore(URL, data[i].id);
				var tree = Ext.create("Ext.tree.Panel", {
					id : data[i].id,
					title : data[i].text,
					icon : data[i].icon,
					iconCls : data[i].iconCls,
					// useArrows: true,
					autoScroll : true,
					rootVisible : false,
					viewConfig : {
						loadingText : "正在加载..."
					},
					store : treeStore,
					listeners : {
						itemclick : {
							fn : me.onOptionsItemClick,
							scope : me
						}
					}
				});
				options.add(tree);
			}
			options.doLayout();
		}
		catch (error)
		{
			me.showNotice({
				html : error,
				title : '异常提示'
			});
		}
	},

	onOptionsItemClick : function(view, node)
	{
		try
		{
			var me = this;	
			var milliseconds = Ext.Date.now();
			if(milliseconds - me.milliseconds < 1500)
			{
				return;
			}
			me.milliseconds = milliseconds;
			me.setLoading('加载中...');
			var tabPanel = me.tabPanel;
			var has = false;
			for (var i = 0; i < tabPanel.items.length; i++)
			{
				if (tabPanel.items.get(i).id == node.data.id)
				{
					has = true;
					tabPanel.setActiveTab(tabPanel.items.get(i));
					break;
				}
				else if (i > 0)
				{
					tabPanel.remove(i);
				}
			}
			if (!has)
			{
				if (node.isLeaf())
				{// 判断是否是根节点
					if (node.data.type === 'URL')
					{// 判断资源类型
						var theme = Common.getQueryParam('theme');
						var url = Common.addQueryParam(node.data.component, 'theme', theme);
						var panel = Ext.create('Ext.panel.Panel', {
							id : node.data.id,
							icon : node.data.icon,
							title : node.data.text,
							closable : false,
							// iconCls : 'icon-activity',
							html : '<iframe width="100%" height="100%" frameborder="0" src="' + url + '"></iframe>'
						});
						tabPanel.add(panel);
						tabPanel.setActiveTab(panel);
					}
					else if (node.data.type === 'COMPONENT')
					{
						try
						{
							var component = Ext.create(node.data.component, {
								id : node.data.id,
								icon : node.data.icon,
								title : node.data.text,
								closable : false
							// iconCls : 'icon-activity'
							});
							tabPanel.add(component);
							tabPanel.setActiveTab(component);
						}
						catch (ex)
						{
							Ext.MessageBox.show({
								title : '加载提示',
								msg : '控件加载失败，服务器无响应！',
								buttons : Ext.Msg.OK,
								icon : Ext.MessageBox.WARNING
							});
						}
					}
				}
			}
			me.setLoading(false);
		}
		catch (err)
		{
			this.setLoading(false);
			Ext.MessageBox.show({
				title : '错误提示',
				msg : err.message,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.ERROR
			});
		}
	},

	onOptionsToolClick : function(tool, e, eOpts)
	{
		this.loadOptions();
	},

	settingPassword : function()
	{
		// Ext.Msg.alert('修改密码');
		if (!this.passWin)
		{
			this.passWin = Ext.create('Gotom.view.SettingPassowrd');
		}
		this.passWin.show();
	},

	loadIndex : function()
	{
		return;
		var me = this;
		try
		{
			var portalPanel = Ext.create("Gotom.view.PortalPanel", {
				id : 'app-portal',
				region : 'center',
				title : "我的桌面",
				border : false,
				header : false,
				layout : 'column'
			});
			var portalcolumn = Ext.create('Gotom.view.PortalColumn', {
				columnWidth : 0.7,
				items : [ {
					title : '最新通知',
					height : 150,
					tools : me.createTools(),
					listeners : {
						'close' : Ext.bind(me.onPortletClose, this)
					}
				}, {
					title : '业绩报表',
					height : 250,
					tools : me.createTools(),
					items : Ext.create('Gotom.view.ChartPortlet'),
					listeners : {
						'close' : Ext.bind(me.onPortletClose, this)
					}
				} ]
			});
			portalPanel.add(portalcolumn);
			var portalcolumn2 = Ext.create('Gotom.view.PortalColumn', {
				columnWidth : 0.3,
				items : [ {
					title : '功能链接',
					height : 150,
					tools : me.createTools(),
					listeners : {
						'close' : Ext.bind(me.onPortletClose, this)
					}
				}, {
					title : '待办事项',
					height : 150,
					tools : me.createTools(),
					listeners : {
						'close' : Ext.bind(me.onPortletClose, this)
					}
				}, {
					title : '业绩报表',
					height : 250,
					tools : me.createTools(),
					items : Ext.create('Gotom.view.ChartPortlet'),
					listeners : {
						'close' : Ext.bind(me.onPortletClose, this)
					}
				} ]
			});
			portalPanel.add(portalcolumn2);
			me.homePanel.removeAll();
			me.homePanel.add(portalPanel);
			me.tabPanel.setActiveTab(me.homePanel);
		}
		catch (error)
		{
			Ext.Msg.alert('操作提示', error);
		}
	},

	addTabPanel : function(xtype, id, title)
	{
		try
		{
			var me = this;
			var panel = '';
			var panelId = id;
			var tabPanel = me.tabPanel;
			for (var i = 0; i < tabPanel.items.length; i++)
			{
				if (tabPanel.items.get(i).id === panelId)
				{
					panel = tabPanel.items.get(i);
					break;
				}
			}
			if (panel === '')
			{
				panel = Ext.create(xtype, {
					id : panelId,
					title : title,
					closable : true
				});
				tabPanel.add(panel);
			}
			tabPanel.setActiveTab(panel);
			panel.loadData(id);
		}
		catch (error)
		{
			Ext.Msg.alert('信息提示', error);
		}
	},

	removeTab : function(tabPanel)
	{
		try
		{
			me.tabPanel.remove(tabPanel);
		}
		catch (error)
		{
			Ext.Msg.alert('信息提示', error);
		}
	},

	showNotice : function(conf)
	{
		if (Ext.isEmpty(conf.html))
		{
			return;
		}
		if (!Ext.isEmpty(this.noticeWindow))
		{
			this.noticeWindow.close();
		}
		this.noticeWindow = Ext.create('Gotom.view.NoticeWindow');
		this.noticeWindow.parentPanel = this;
		this.noticeWindow.loadData(conf.html, conf.title);
	}

});