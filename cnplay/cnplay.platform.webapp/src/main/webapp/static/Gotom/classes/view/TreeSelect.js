Ext.define('Gotom.view.TreeSelect', {
	extend : 'Ext.window.Window',

	requires : [ 'Ext.tree.Panel', 'Ext.tree.View' ],

	height : 400,
	width : 400,
	resizable : true,
	layout : 'border',
	title : '选择树',
	modal : true,

	initComponent : function()
	{
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				xtype : 'treepanel',
				region : 'center',
				stateful : true,
				border : false,
				autoScroll : true,
				header : false,
				title : '权限配置',
				animate : true,
				rootVisible : false,
				viewConfig : {

				},
				columns : [ {
					xtype : 'treecolumn',
					dataIndex : 'name',
					width : 300,
					text : '名称'
				}, {
					xtype : 'gridcolumn',
					dataIndex : 'code',
					text : '编码'
				} ],
				listeners : {
					afterrender : {
						fn : me.onTreepanelAfterRender,
						scope : me
					},
					itemclick : {
						fn : me.onTreePanelItemClick,
						scope : me
					}
				}
			} ]
		});

		me.callParent(arguments);
	},

	onTreepanelAfterRender : function(component, eOpts)
	{
		this.treePanel = component;
	},

	onTreePanelItemClick : function(dataview, record, item, index, e, eOpts)
	{
		try
		{
			var me = this;
			if (me.conf.multiple)
			{
				if (record.data.checked)
				{
					record.set('checked', false);
					Common.onTreePanelCheckChange(record, false);
				}
				else
				{
					record.set('checked', true);
					Common.onTreePanelCheckChange(record, true);
				}
				var items = me.treePanel.getSelectionModel().store.data.items;
				var selectedIds = [];
				var selectedTexts = [];
				Ext.each(items, function()
				{
					var nd = this;
					if (nd.data.checked)
					{
						if (Ext.isEmpty(nd.data.text))
						{
							nd.data.text = nd.data.name;
						}
						selectedIds.push(nd.data.id);
						selectedTexts.push(nd.data.text);
					}
				});
				me.conf.callback({
						id : selectedIds,
						text : selectedTexts
					});
			}
			else
			{
				if (!record.data.checked)
				{
					if (Ext.isEmpty(record.data.text))
					{
						record.data.text = record.data.name;
					}
					me.conf.callback(record.data);
					me.close();
				}
				else
				{
					record.set('checked', false);
					me.conf.callback({
							id : '',
							text : ''
						});
				}
			}
		}
		catch (ex)
		{
			Common.alert({
				msg : '错误信息：' + ex,
				icon : Ext.Msg.WARNING
			});
		}
	},

	showTree : function(conf)
	{
		var me = this;
		if (Ext.isEmpty(conf.fields))
		{
			conf.fields = [ {
				name : 'id'
			}, {
				name : 'text'
			} ];
		}
		if (Ext.isEmpty(conf.multiple))
		{
			conf.multiple = false;
		}
		if (!Ext.isEmpty(conf.component))
		{
			var x = conf.component.getX();
			var y = component.getY() + conf.component.getHeight();
			me.setPosition(x, y);
		}
		me.conf = conf;
		me.show();
		me.expandNode = true;
		Common.bindTree({
			treePanel : me.treePanel,
			message : '正在加载树......',
			url : conf.url,
			params : conf.params,
			fields : [ 'id', 'code', 'name', 'text', 'checked' ],
			onload : function onload(treestore, node, records, successful, eOpts)
			{
				if (records.length > 0 && me.expandNode)
				{
					me.expandNode = false;
					Ext.defer(function()
					{
						me.treePanel.expandNode(records[0]);
					}, 100);
				}
			}
		});
	}

});