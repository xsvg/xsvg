/**
 * Extjs 与后台交互统一类
 * 
 * @author peishaoguo@sgsg.cc
 * @version 2015-06-17
 */
var Common = {

	show : function(conf)
	{
		var p = Ext.getCmp('app-viewport');
		if (!Ext.isEmpty(p))
		{
			p.showNotice(conf);
		}
	},

	addTabURL : function(conf)
	{
		var p = Ext.getCmp('app-viewport');
		if (!Ext.isEmpty(p))
		{
			var panel = '';
			var tabPanel = p.tabPanel;
			for (var i = 0; i < tabPanel.items.length; i++)
			{
				if (tabPanel.items.get(i).id === conf.id)
				{
					panel = tabPanel.items.get(i);
					break;
				}
			}
			if (panel === '')
			{
				panel = Ext.create('Ext.panel.Panel', {
					id : conf.id,
					title : conf.title,
					closable : true,
					// iconCls : 'icon-activity',
					html : '<iframe width="100%" height="100%" frameborder="0" src="' + conf.url + '"></iframe>'
				});
				tabPanel.add(panel);
			}
			tabPanel.setActiveTab(panel);
		}
	},

	addTabPanel : function(conf)
	{
		var p = Ext.getCmp('app-viewport');
		if (!Ext.isEmpty(p))
		{
			p.addTabPanel(conf.component, conf.id, conf.title);
		}
		else
		{
			Common.alert({
				title : '操作提示',
				msg : '操作失败，请重试！',
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.ERROR
			});
		}
	},

	removeTab : function(component)
	{
		var p = Ext.getCmp('app-viewport');
		if (!Ext.isEmpty(p))
		{
			p.removeTab(component);
		}
	},

	onException : function(response, component)
	{
		if (!Ext.isEmpty(component))
		{
			if (response.status === 403)
			{
				component.close();
			}
		}
		if (response.status === 0)
		{
			Common.setLoading({
				title : '连接服务器失败',
				msg : '连接服务器失败'
			});
			return;
		}
		else if (response.status === 200)
		{
			var json = Common.JSONdecode(response.responseText);
			var msg = json.msg;
			if (json.rows)
			{
				msg = msg + '<br>' + json.rows;
			}
			/*
			 * Common.show({ title : '操作提示', html : msg });
			 */
			Common.alert({
				title : '操作提示',
				msg : msg,
				buttons : Ext.Msg.OK,
				icon : Ext.Msg.WARNING
			});
		}
		else if (response.status === 401)
		{
			window.location.href = ctxp + '/index.html?' + Math.random();
		}
		else if (response.status === 403)
		{
			if (!Ext.isEmpty(response.responseText))
			{
				Common.alert({
					title : '无操作权限',
					buttons : Ext.Msg.OK,
					msg : '你的访问受限！'
				});
			}
		}
		else
		{
			if (!Ext.isEmpty(response.responseText))
			{
				Common.show({
					title : '操作提示',
					html : response.responseText
				});
			}
		}
	},

	bindPageSize : function(component)
	{
		var store = Ext.create('Ext.data.Store', {
			fields : [ 'text' ],
			data : [ {
				"text" : 5
			}, {
				"text" : 10
			}, {
				"text" : 20
			}, {
				"text" : 25
			}, {
				"text" : 50
			}, {
				"text" : 100
			}, {
				"text" : 500
			}, {
				"text" : 1000
			} ]
		});
		component.setValue(25);
		component.bindStore(store);
	},

	loadStore : function(conf)
	{
		try
		{
			var comp = conf.component;
			var model = conf.model;
			var fields = conf.fields;
			var url = conf.url;
			var pageSize = conf.pageSize;
			var params = conf.params;
			var root = 'rows';
			if (!Ext.isEmpty(conf.root))
			{
				root = conf.root;
			}
			if (Ext.isEmpty(pageSize))
			{
				pageSize = 20;
			}
			if (Ext.isEmpty(model))
			{
				model = '';
			}
			if (Ext.isEmpty(fields))
			{
				fields = [];
			}
			var myStore = Ext.create('Ext.data.JsonStore', {
				model : model,
				fields : fields,
				pageSize : pageSize,
				autoLoad : true,
				proxy : {
					type : 'ajax',
					actionMethods : 'post',
					url : url,
					reader : {
						type : 'json',
						root : root
					},
					listeners : {
						exception : function(proxy, response, operation, eOpts)
						{
							try
							{
								loadMask.hide();
								var json = Common.JSONdecode(response.responseText);
								if (json.needCheck)
								{
									Common.showCheckWindow({
										component : conf.component,
										callback : function(result)
										{
											Common.loadStore(conf);
										}
									});
								}
								else
								{
									Common.onException(response, comp);
								}
							}
							catch (err)
							{
								// alert(err);
							}
						}
					}
				},
				listeners : {
					load : {
						fn : function()
						{
							if (!Ext.isEmpty(comp.pagingToolbar))
							{
								var pageData = comp.pagingToolbar.getPageData();
								if (pageData.currentPage > pageData.pageCount && pageData.pageCount > 0)
								{
									comp.pagingToolbar.moveLast();
								}
							}
							try
							{
								conf.callback();
							}
							catch (ex)
							{
							}
						}
					},
					beforeload : {
						fn : function()
						{
							loadMask.show();
						}
					}
				}
			});
			var loadMask = new Ext.LoadMask(comp, {
				msg : '正在加载中...',
				store : myStore
			});
			if (!Ext.isEmpty(params))
			{
				Ext.apply(myStore.proxy.extraParams, params);
			}
			if (!Ext.isEmpty(comp.pagingToolbar))
			{
				comp.pagingToolbar.bindStore(myStore);
			}
			comp.bindStore(myStore);
		}
		catch (error)
		{
			// alert(error.toString());
		}
	},

	loadLocalStore : function(conf)
	{
		try
		{
			var comp = conf.component;
			var fields = conf.fields;
			if (Ext.isEmpty(fields))
			{
				fields = [];
			}
			var jsondata = conf.data;
			if (Ext.isEmpty(jsondata))
			{
				jsondata = [];
			}
			var myStore = new Ext.data.JsonStore({
				data : jsondata,
				autoLoad : true, // 自动 加载(不能用store.load())
				fields : fields
			});

			comp.bindStore(myStore);
		}
		catch (error)
		{
			// alert(error.toString());
		}
	},

	getSelectionIds : function(gridPanel)
	{
		var ids = [];
		try
		{
			var selecteditems = gridPanel.getSelectionModel().selected.items;
			if (selecteditems.length > 0)
			{
				Ext.each(selecteditems, function()
				{
					var nd = this;
					ids.push(nd.data.id);
				});
			}
		}
		catch (error)
		{
			// alert(error.toString());
		}
		return ids;
	},

	ajaxSelectionIds : function(conf)
	{
		try
		{
			if (Ext.isEmpty(conf.confirm))
			{
				conf.confirm = '确定要执行选中的数据吗？';
			}
			if (Ext.isEmpty(conf.msg))
			{
				conf.msg = '操作成功';
			}
			var ids = Common.getSelectionIds(conf.component);
			if (ids.length === 0)
			{
				Common.alert({
					title : "操作提示",
					msg : "请选择要操作的节点!",
					buttons : Ext.Msg.OK,
					icon : Ext.Msg.WARNING
				});
				return;
			}
			Ext.Msg.confirm('确认提示', conf.confirm, function(button)
			{
				if (button == 'yes')
				{
					try
					{
						Common.ajax({
							component : conf.component,
							params : {
								'id' : ids.join(',')
							},
							message : '正执行...',
							url : conf.url,
							callback : function(result)
							{
								if (!Ext.isEmpty(conf.callback))
								{
									conf.callback(result);
								}
								else if (!Ext.isEmpty(conf.component))
								{
									if (!Ext.isEmpty(conf.component.pagingToolbar))
									{
										conf.component.pagingToolbar.getStore().reload();
									}
									else
									{
										conf.component.getStore().reload();
									}
								}
								if (result.rows === true)
								{
									result.rows = conf.msg;
								}
								var msg = result.msg + result.rows;
								if (Ext.isEmpty(msg))
									msg = conf.msg;
								if (!Ext.isEmpty(msg))
								{
									Common.alert({
										title : '操作提示',
										msg : msg,
										buttons : Ext.Msg.OK,
										icon : Ext.Msg.INFO
									});
								}
							}
						});
					}
					catch (error)
					{
						Common.alert({
							title : '操作提示',
							msg : error,
							buttons : Ext.Msg.OK,
							icon : Ext.Msg.WARNING
						});
					}
				}
			});
		}
		catch (error)
		{
			Common.alert({
				title : '操作提示',
				msg : error,
				buttons : Ext.Msg.OK,
				icon : Ext.Msg.WARNING
			});
		}
	},
	deleteSelectionIds : function(gridPanel, url)
	{
		Common.ajaxSelectionIds({
			component : gridPanel,
			url : url,
			confirm : '确定要删除选中的数据吗？',
			msg : '删除成功'
		});
	},

	redirect : function(conf)
	{
		conf.callback = function(result)
		{
			if (!result.rows)
			{
				location.href = conf.href;
			}
		};
		Common.hidden(conf);
	},

	hidden : function(conf)
	{
		if (Ext.isEmpty(conf.url))
		{
			conf.url = 'home/disabled';
		}
		Common.ajax({
			url : conf.url,
			params : conf.params,
			callback : function(result)
			{
				try
				{
					if (!Ext.isEmpty(conf.component))
					{
						if (result.rows)
						{
							conf.component.hide();
						}
						else
						{
							conf.component.show();
						}
					}
					if (!Ext.isEmpty(conf.callback))
					{
						conf.callback(result);
					}
				}
				catch (error)
				{
					Common.show({
						title : '信息提示',
						html : error.toString()
					});
				}
			}
		});

	},

	ajax : function(config)
	{
		if (config.component)
		{
			if (config.msg)
			{
				config.message = config.msg;
			}
			if (config.message)
			{
				config.component.setLoading(config.message);
			}
			else
			{
				var lock = true;
				if (!Ext.isEmpty(config.lock))
				{
					lock = config.lock;
				}
				if (lock)
					config.component.setLoading('正在下载...');
			}
		}
		Ext.Ajax.request({
			url : config.url,
			params : config.params,
			method : 'post',
			callback : function(options, success, response)
			{
				try
				{
					if (config.component)
					{
						config.component.setLoading(false);
					}
					if (success)
					{
						var json = Common.JSONdecode(response.responseText);
						if (!Ext.isEmpty(json.success))
						{
							if (json.success)
							{
								config.callback(json, options, success, response);
							}
							else if (json.needCheck)
							{
								Common.showCheckWindow({
									component : config.component,
									callback : function(result)
									{
										Common.ajax(config);
									}
								});
							}
							else
							{
								Common.onException(response, config.component);
							}
						}
						else
						{
							config.callback(json, options, success, response);
						}

					}
					else
					{
						Common.onException(response, config.component);
					}
				}
				catch (err)
				{
					Common.log(err);
				}
			}
		});
	},

	isWindow : function(component)
	{
		try
		{
			while (!Ext.isEmpty(component))
			{
				if (component.isWindow)
				{
					return component;
				}
				component = component.ownerCt;
			}
		}
		catch (error)
		{
			// alert(error);
			return false;
		}
		return false;
	},

	showCheckWindow : function(conf)
	{
		var component = conf.component;
		var checkWin = Ext.create('Gotom.view.CheckWindow');
		var win = Common.isWindow(component);
		if (win)
		{
			var x = win.getX();
			var y = win.getY() + win.getHeight();
			checkWin.setPosition(x, y);
			checkWin.width = win.width;
		}
		if (!Ext.isEmpty(conf.title))
		{
			checkWin.title = conf.title;
		}
		if (!Ext.isEmpty(conf.url))
		{
			checkWin.url = conf.url;
		}
		checkWin.show();
		if (!Ext.isEmpty(conf.username))
		{
			checkWin.setUsername(conf.username);
		}
		checkWin.addListener('close', function(panel, opts)
		{
			try
			{
				if (component)
				{
					component.setLoading(false);
				}
				if (checkWin.result.success)
				{
					conf.callback(checkWin.result);
				}
				else if (!Ext.isEmpty(component) && component.isWindow)
				{
					component.close();
				}
			}
			catch (ee)
			{
				Common.setLoading({
					comp : conf.component,
					msg : '异常提示:' + ee
				});
			}
		});
	},

	showTreeSelect : function(conf)
	{
		var treeSelect = Ext.create('Gotom.view.TreeSelect');
		treeSelect.showTree(conf);
	},

	formSubmit : function(conf)
	{
		if (!conf.form.isValid())
		{
			return false;
		}
		var isValid = false;
		var msg = '确认要提交数据吗？';
		if (!Ext.isEmpty(conf.msg))
		{
			msg = conf.msg;
		}
		Ext.Msg.confirm("确认提示", msg, function(button)
		{
			if (button == "yes")
			{
				isValid = Common.submit(conf);
			}
		});
		return isValid;
	},

	submit : function(conf)
	{
		if (conf.form.isValid())
		{
			var msg = '正在保存数据，稍后...';
			if (conf.msg)
			{
				msg = conf.msg;
			}
			conf.form.submit({
				url : conf.url,
				method : 'POST',
				waitMsg : msg,
				success : function(f, action)
				{
					if (action.result.success)
					{
						conf.callback(action.result);// 调用回调函数
					}
					else if (action.result.needCheck)
					{
						Common.showCheckWindow({
							component : conf.form,
							callback : function(result)
							{
								Common.submit(conf);
							}
						});
					}
					else
					{
						Common.onException(action.response);
					}
				},
				failure : function(f, action)
				{
					if (action.response.status === 200 && action.result.needCheck)
					{
						Common.showCheckWindow({
							component : conf.form,
							callback : function(result)
							{
								Common.submit(conf);
							}
						});
					}
					else if (action.response.status === 200)
					{
						var msg = action.result.msg;
						if (!Ext.isEmpty(action.result.rows))
						{
							if (Ext.isEmpty(msg))
							{
								msg = action.result.rows;
							}
							else
							{
								msg = msg + '<br>' + action.result.rows;
							}
						}
						Common.setLoading({
							comp : conf.form,
							msg : msg
						});
					}
					else
					{
						Common.onException(action.response);
					}
				}
			});
			return true;
		}
		else
		{
			return false;
		}
	},

	bindTree : function(conf)
	{
		try
		{
			var comp = conf.component;
			var store = Ext.create("Ext.data.TreeStore", {
				defaultRootId : conf.pid,
				clearOnLoad : true,
				nodeParam : 'id',
				fields : conf.fields,
				proxy : {
					type : 'ajax',
					actionMethods : 'post',
					url : conf.url,
					reader : {
						type : 'json',
						root : 'rows'
					},
					listeners : {
						exception : function(proxy, response, operation, eOpts)
						{
							Common.onException(response, comp);
						}
					}
				},
				listeners : {
					load : {
						fn : function(treestore, node, records, successful, eOpts)
						{
							try
							{
								if (conf.onload)
									conf.onload(treestore, node, records, successful, eOpts);
							}
							catch (ex)
							{
								Common.log(ex);
							}
						}
					}
				}
			});
			if (!Ext.isEmpty(conf.params))
			{
				Ext.apply(store.proxy.extraParams, conf.params);
			}
			conf.treePanel.bindStore(store);
			conf.treePanel.getStore().reload();
		}
		catch (error)
		{
			// alert(error.toString());
		}
	},

	createMenuTreeStore : function(URL, pid)
	{
		try
		{
			var store = Ext.create("Ext.data.TreeStore", {
				defaultRootId : pid,
				clearOnLoad : true,
				nodeParam : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'sort',
					type : 'int'
				}, {
					name : 'text'
				}, {
					name : 'icon'
				}, {
					name : 'iconCls'
				}, {
					name : 'leaf',
					type : 'boolean'
				}, {
					name : 'type'
				}, {
					name : 'url'
				}, {
					name : 'resource'
				}, {
					name : 'component'
				}, {
					name : 'parentId'
				} ],
				proxy : {
					type : 'ajax',
					actionMethods : 'post',
					url : URL,
					reader : {
						type : 'json',
						root : 'rows'
					},
					listeners : {
						exception : function(proxy, response, operation, eOpts)
						{
							Common.onException(response);
						}
					}
				}
			});
			return store;
		}
		catch (error)
		{
			// alert(error.toString());
		}
	},

	storeToJson : function(jsondata)
	{
		try
		{
			var listRecord;
			if (jsondata instanceof Ext.data.Store)
			{
				listRecord = new Array();
				jsondata.each(function(record)
				{
					listRecord.push(record.data);
				});
			}
			else if (jsondata instanceof Array)
			{
				listRecord = new Array();
				Ext.each(jsondata, function(record)
				{
					listRecord.push(record.data);
				});
			}
			return Ext.encode(listRecord);
		}
		catch (error)
		{
			// alert(error.toString());
		}
	},

	getQueryParam : function(name)
	{
		var regex = RegExp('[?&]' + name + '=([^&]*)');
		var scriptEls = document.getElementsByTagName('script');
		var path = scriptEls[scriptEls.length - 1].src;
		var match = regex.exec(location.search) || regex.exec(path);
		return match && decodeURIComponent(match[1]);
	},

	addQueryParam : function(url, name, value)
	{
		var path = url;
		if (value !== null && value.length > 0)
		{
			if (url.indexOf('?') >= 0)
			{
				path = url + '&' + name + '=' + value;
			}
			else
			{
				path = url + '?' + name + '=' + value;
			}
		}
		return path;
	},

	onTreeParentNodeChecked : function(node, checked)
	{
		if (node.parentNode !== null)
		{
			var childNodes = node.parentNode.childNodes;
			var parentCheck = false;
			if (childNodes.length > 0)
			{
				Ext.each(childNodes, function(childNode)
				{
					if (childNode.data.checked)
					{
						parentCheck = true;
					}
				});
			}
			node.parentNode.set('checked', parentCheck);
			Common.onTreeParentNodeChecked(node.parentNode, checked);
		}
	},

	setTreeParentNodeChecked : function(node, checked)
	{
		if (node.parentNode !== null)
		{
			node.parentNode.set('checked', checked);
			Common.onTreeParentNodeChecked(node.parentNode, checked);
		}
	},

	onTreeChildNodesChecked : function(node, checked)
	{
		Ext.each(node.childNodes, function(childNode)
		{
			childNode.set('checked', checked);
			if (childNode.childNodes.length > 0)
			{
				Common.onTreeChildNodesChecked(childNode, checked);
			}
		});
	},

	onTreePanelSingleChange : function(node)
	{
		var parentNode = node;
		while (parentNode.parentNode !== null)
		{
			parentNode = parentNode.parentNode;
		}
		Common.onTreeChildNodesChecked(parentNode, false);
	},

	onTreePanelCheckChange : function(node, checked)
	{
		if (!checked)
			Common.onTreeChildNodesChecked(node, checked);
		if (checked)
			Common.onTreeParentNodeChecked(node, checked);
	},

	commRemoveCallback : function()
	{
		if (!Ext.isEmpty(Common.commCallback))
		{
			ocxComm.remove(Common.commCallback);
		}
	},

	comm : function(conf)
	{
		var initiative = true;
		if (!Ext.isEmpty(conf.initiative))
		{
			initiative = conf.initiative;
		}
		if (Ext.isEmpty(conf.params))
		{
			conf.params = {};
		}
		Common.commRemoveCallback(Common.commCallback);
		Common.commCallback = {
			id : 'commCallback',
			callback : function(hexString)
			{
				conf.params.hexString = hexString;
				Common.ajax({
					component : conf.component,
					msg : conf.msg,
					url : conf.url,
					params : conf.params,
					callback : function(result)
					{
						try
						{
							if (!Ext.isEmpty(result.rows) && !Ext.isEmpty(result.rows.hexString))
							{
								if (!ocxComm.send(result.rows.hexString))
								{
									ocxComm.remove(Common.commCallback);
									Common.alert({
										title : '操作提示',
										msg : '发送数据失败，请重试！',
										buttons : Ext.Msg.OK,
										icon : Ext.MessageBox.ERROR
									});
									if (!Ext.isEmpty(conf.callback))
									{
										conf.callback(result.rows);
									}
								}
							}
							Common.setLoading({
								comp : conf.component,
								msg : '正在通信...'// result.msg
							});
							if (result.code !== 0)
							{
								ocxComm.remove(Common.commCallback);
								if (!Ext.isEmpty(conf.callback))
								{
									conf.callback(result.rows);
								}
								if (!Ext.isEmpty(result.msg))
								{
									Common.alert({
										msg : result.msg
									});
								}
							}
							if (!Ext.isEmpty(result.rows) && !Ext.isEmpty(result.rows.promptMessage))
							{
								Common.show({
									title : '提示信息',
									html : result.rows.promptMessage
								});
							}
						}
						catch (ex)
						{
							if (!Ext.isEmpty(conf.component) && conf.component.isWindow)
							{
								conf.component.close();
							}
							Common.alert({
								title : '操作提示',
								msg : '操作错误，请重新操作！',
								buttons : Ext.Msg.OK,
								icon : Ext.MessageBox.ERROR
							});
						}
					}
				});
			}
		};
		ocxComm.add(Common.commCallback);
		if (initiative)
		{// 主动触发
			Common.ajax({
				component : conf.component,
				msg : conf.msg,
				url : conf.url,
				params : conf.params,
				callback : function(result)
				{
					if (Ext.isEmpty(result.rows))
					{
						ocxComm.remove(Common.commCallback);
						if (result.code !== 0)
						{
							if (!Ext.isEmpty(result.msg))
							{
								Common.alert({
									msg : result.msg
								});
							}
						}
					}
					else if (!ocxComm.send(result.rows.hexString))
					{
						Common.alert({
							title : '操作提示',
							msg : '发送数据失败，请检查设备是否已连接！',
							buttons : Ext.Msg.OK,
							icon : Ext.MessageBox.ERROR
						});
						ocxComm.remove(Common.commCallback);
						if (!Ext.isEmpty(conf.callback))
						{
							conf.callback(result.rows);
						}
					}
					if (!Ext.isEmpty(result.rows) && !Ext.isEmpty(result.rows.promptMessage))
					{
						Common.show({
							title : '提示信息',
							html : result.rows.promptMessage
						});
					}
				}
			});
		}
	},

	/*
	 * 文件上传,params:{suffix:'',callback:function(result){}}
	 * suffix:允许的文件类型，值为文件后缀名(不包含点),多类型可用半角, ; | 分隔, callback:上传完成的回调方法
	 */
	upload : function(params)
	{
		if (Ext.isEmpty(params.title))
		{
			params.title = '文件上传';
		}
		var form = new Ext.form.Panel({
			xtype : 'form',
			region : 'center',
			border : false,
			hideLabel : true,
			bodyPadding : 10,
			header : false,
			title : params.title,
			items : [ {
				xtype : 'filefield',
				anchor : '100%',
				fieldLabel : '文件',
				labelAlign : 'right',
				labelWidth : 50,
				buttonText : '选择文件'
			} ]
		});
		var win = new Ext.Window({
			width : 300,
			height : 100,
			resizable : false,
			draggable : false,
			modal : true,
			layout : 'border',
			title : params.title,
			items : [ form ],
			dockedItems : [ {
				xtype : 'toolbar',
				dock : 'bottom',
				layout : {
					type : 'hbox',
					align : 'middle',
					pack : 'end'
				},
				items : [ {
					text : '确 定',
					xtype : 'button',
					iconCls : 'icon-save',
					handler : function()
					{
						if (form.form.isValid())
						{
							if (params.suffix)
							{
								var allow = false;
								var suffix = params.suffix;
								var file = form.items.items[0];
								var name = file.getValue();
								name = name.toUpperCase();
								var array = params.suffix.split(/,|;|\|/);
								for (var i = 0; i < array.length; i++)
								{
									var val = array[i].toUpperCase();
									var regExp = new RegExp("\\." + val + "$", 'gi');
									var match = regExp.test(name);
									if (match)
									{
										allow = true;
										break;
									}
								}
								if (!allow)
								{
									Ext.Msg.alert({
										title : '提示',
										msg : "只允许上传如下文件类型:" + params.suffix,
										icon : Ext.Msg.WARNING
									});
									file.setValue("");
									return;
								}
							}
							if (Ext.isEmpty(params.url))
							{
								params.url = ctxp + '/home/upload';
							}
							Common.submit({
								form : form,
								url : params.url,
								msg : '正在上传,请稍候...',
								callback : function(result)
								{
									if (result.success)
									{
										win.close();
										params.callback(result);
									}
									else
									{
										Common.alert({
											title : '操作提示',
											msg : result.msg,
											icon : Ext.Msg.WARNING
										});
									}
								}
							});
						}
					}
				}, {
					text : '取 消',
					xtype : 'button',
					iconCls : 'icon-cancel',
					handler : function()
					{
						win.close();
					}
				} ]
			} ]
		});
		win.show();
	},
	// 文件下载
	getDownloadURL : function(id)
	{
		return 'home/download?id=' + id;
	},

	alert : function(conf)
	{
		if (Ext.isEmpty(conf.msg))
		{
			return;
		}
		if (!conf.millis)
		{
			conf.millis = 5000;
		}
		if (!conf.buttons)
		{
			conf.buttons = Ext.MessageBox.OK;
		}
		if (!conf.icon)
		{
			conf.icon = Ext.MessageBox.INFO;
		}
		if (!conf.title)
		{
			conf.title = '操作提示';
		}
		var box = Ext.Msg.show({
			title : conf.title,
			msg : conf.msg,
			buttons : conf.buttons,
			icon : conf.icon
		});
		Ext.defer(function()
		{
			box.close();
		}, conf.millis);
	},

	info : function(message)
	{
		try
		{
			console.info(message);
		}
		catch (ex)
		{
		}
	},
	debug : function(message)
	{
		try
		{
			console.debug(message);
		}
		catch (ex)
		{
		}
	},
	warn : function(message)
	{
		try
		{
			console.warn(message);
		}
		catch (ex)
		{
		}
	},
	error : function(message)
	{
		try
		{
			console.error(message);
		}
		catch (ex)
		{
		}
	},
	log : function(message)
	{
		try
		{
			console.log(message);
		}
		catch (ex)
		{
		}
	},

	isIE : function()
	{
		var userAgent = window.navigator.userAgent;
		if (userAgent.indexOf("MSIE") >= 0)
		{
			return true;
		}
		else
		{
			false;
		}
	},

	paramStr : function(jsonobj)
	{
		var rst = '';
		for ( var key in jsonobj)
		{
			var vv = jsonobj[key];
			if ("undefined" == typeof (vv))
			{
				vv = '';
			}
			rst = rst + '&' + key + '=' + vv;
		}
		if (rst.length > 0 && rst.substring(0, 1) == '&')
		{
			return rst.substring(1);
		}
		else
		{
			return rst;
		}
	},

	JSONdecode : function(value)
	{
		try
		{
			return Ext.JSON.decode(value);
		}
		catch (err)
		{
			return {
				needCheck : false,
				success : false,
				rows : ''
			};
		}
	},

	setLoading : function(conf)
	{
		try
		{
			if (Ext.isEmpty(conf.comp))
			{
				conf.comp = Ext.getCmp('app-viewport');
			}
			if (!Ext.isEmpty(conf.comp))
			{
				if (Ext.isEmpty(conf.msg))
				{
					conf.msg = "正在加载...";
				}
				if (Ext.isEmpty(conf.ms))
				{
					conf.ms = 5000;
				}
				if (Ext.isEmpty(conf.color))
				{
					conf.color = '#ff3300';
				}
				conf.comp.setLoading('<font color=' + conf.color + ' onclick=Common.hideCmpLoading(\'' + conf.comp.id + '\');>' + conf.msg + '<font>');
				Ext.defer(function()
				{
					conf.comp.setLoading(false);
				}, conf.ms);
			}
		}
		catch (ex)
		{
		}
	},

	hideCmpLoading : function(id)
	{
		var cmp = Ext.getCmp(id);
		cmp.hideLoading = true;
		cmp.setLoading(false);
	},

	waitLoadingCancel : function(id)
	{
		Ext.Msg.confirm('确认提示', '确认取消等待？', function(button)
		{
			if (button == 'yes')
			{
				var cmp = Ext.getCmp(id);
				cmp.hideLoading = true;
				cmp.setLoading(false);
				if (!Ext.isEmpty(Common.waitLoadingConf) && !Ext.isEmpty(Common.waitLoadingConf.callback))
					Common.waitLoadingConf.callback('取消操作');
			}
		});
	},

	waitLoading : function(conf)
	{
		try
		{
			if (Ext.isEmpty(conf.comp))
			{
				conf.comp = Ext.getCmp('app-viewport');
			}
			if (!Ext.isEmpty(conf.comp))
			{
				conf.comp.hideLoading = false;
				if (Ext.isEmpty(conf.msg))
				{
					conf.msg = "正在加载...";
				}
				if (Ext.isEmpty(conf.ms))
				{
					conf.ms = 1000;
				}
				if (Ext.isEmpty(conf.color))
				{
					conf.color = '#ff3300';
				}
				Common.waitLoadingConf = conf;
				Common.waitLoadingCallback(conf);
			}
		}
		catch (ex)
		{
			Common.log(ex.toString());
		}
	},

	waitLoadingCallback : function(conf)
	{
		try
		{
			if (conf.comp.hideLoading)
			{
				return;
			}
			conf.comp.setLoading('<font color=' + conf.color + ' onclick=Common.waitLoadingCancel(\'' + conf.comp.id + '\');>' + conf.msg + '<font>');
			Common.ajax({
				component : conf.comp,
				message : conf.msg,
				url : conf.url,
				callback : function(result)
				{
					try
					{
						conf.msg = result.msg + result.rows;
						conf.comp.setLoading('<font color=' + conf.color + ' onclick=Common.waitLoadingCancel(\'' + conf.comp.id + '\');>' + conf.msg + '<font>');
						if (result.code === 0)
						{
							Ext.defer(function()
							{
								Common.waitLoadingCallback(conf);
							}, conf.ms);
						}
						else
						{
							conf.comp.setLoading(false);
							if (!Ext.isEmpty(conf.callback))
								conf.callback(conf.msg);
						}
					}
					catch (ex)
					{
						Common.log(ex.toString());
					}
				}
			});
		}
		catch (ex)
		{
			Common.log(ex.toString());
		}
	}
};