Ext.define('Gotom.view.CheckWindow', {
	extend : 'Ext.window.Window',

	requires : [ 'Ext.form.Panel', 'Ext.toolbar.Toolbar', 'Ext.button.Button', 'Ext.form.field.Text' ],

	result : ' ',
	height : 160,
	width : 400,
	layout : 'border',
	title : '复核窗口',
	modal : true,
	url : '/loginCheck',
	initComponent : function()
	{
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				xtype : 'form',
				region : 'center',
				border : false,
				bodyPadding : 10,
				header : false,
				title : '表单',
				dockedItems : [ {
					xtype : 'toolbar',
					dock : 'bottom',
					layout : {
						type : 'hbox',
						align : 'middle',
						pack : 'end'
					},
					items : [ {
						xtype : 'button',
						iconCls : 'icon-save',
						text : '确认',
						listeners : {
							click : {
								fn : me.onSubmitClick,
								scope : me
							}
						}
					}, {
						xtype : 'button',
						iconCls : 'icon-cancel',
						text : '取消',
						listeners : {
							click : {
								fn : me.onCancelClick,
								scope : me
							}
						}
					} ]
				} ],
				items : [ {
					xtype : 'textfield',
					anchor : '100%',
					fieldLabel : '用户帐号',
					labelAlign : 'right',
					labelWidth : 60,
					name : 'username',
					listeners : {
						change : {
							fn : me.onUsernameChange,
							scope : me
						},
						specialkey : {
							fn : me.onUserSpecialkey,
							scope : me
						}
					}
				}, {
					xtype : 'textfield',
					anchor : '100%',
					fieldLabel : '用户密码',
					labelAlign : 'right',
					labelWidth : 60,
					name : 'password',
					inputType : 'password',
					listeners : {
						specialkey : {
							fn : me.onPwdSpecialkey,
							scope : me
						}
					}
				}, {
					xtype : 'tbtext',
					text : '',
					listeners : {
						beforerender : {
							fn : me.onTbtextBeforeRender,
							scope : me
						}
					}
				} ],
				listeners : {
					afterrender : {
						fn : me.onFormAfterRender,
						single : true,
						scope : me
					}
				}
			} ]
		});

		me.processCheckWindow(me);
		me.callParent(arguments);
	},
	onTbtextBeforeRender : function(component, eOpts)
	{
		this.textTip = component;
	},
	textTipUpdate : function(msg)
	{
		this.textTip.update('<span style="color:red">' + msg + '</span>');
	},
	processCheckWindow : function(config)
	{
		var me = this;
		me.on('specialkey', function()
		{
			Ext.Msg.alert("提示", "键盘回车事件测试成功");
		});
	},
	onUserSpecialkey : function(field, e, eOpts)
	{
		if (e.getKey() === e.ENTER)
		{
			var me = this;
			var form = me.form;
			form.getForm().findField('password').focus();
			me.onUsernameChange(field);
		}
	},
	onPwdSpecialkey : function(field, e, eOpts)
	{
		if (e.getKey() === e.ENTER)
		{
			var me = this;
			me.onSubmit();
		}
	},

	onSubmitClick : function(button, e, eOpts)
	{
		this.onSubmit();
	},

	onCancelClick : function(button, e, eOpts)
	{
		this.result = {
			success : false
		};
		this.close();
	},

	onFormAfterRender : function(component, eOpts)
	{
		this.form = component;
		this.fingerData = '';
		this.fingerDev = '';
	},

	setCheckForm : function(form)
	{
		this.checkForm = form;
	},

	readFingerData : function(dev)
	{
		var me = this;
		try
		{
			me.fingerDev = dev;
			me.fingerData = finger.getFingerFeature(dev);
			if (me.fingerData === '-1')
			{
				Common.setLoading({
					comp : me,
					msg : '指纹获取失败！'
				});
			}
		}
		catch (error)
		{
			Common.setLoading({
				comp : me,
				msg : '异常提示:' + error
			});
		}
	},

	setUsername : function(username)
	{
		var me = this;
		try
		{
			me.setLoading('正在加载数据...');
			var field = me.form.getForm().findField('username');
			field.setValue(username);
			field.setReadOnly(true);
		}
		catch (error)
		{
			Common.setLoading({
				comp : me,
				msg : '异常提示:' + error
			});
		}
	},

	onUsernameChange : function(field, newValue, oldValue, eOpts)
	{
		var me = this;
		var form = me.form.getForm();
		var usernameF = form.findField('username');
		form.findField('password').setReadOnly(false);
		Common.ajax({
			url : ctxp + '/loginConf',
			params : {
				username : usernameF.getValue()
			},
			callback : function(result)
			{
				if (!Ext.isEmpty(result.rows))
				{
					var row = result.rows;
					me.textTipUpdate(row.msg);
					me.fingerDev = row.fingerDev;
					me.verifyFinger = row.verifyFinger;
					me.verifyPwd = row.verifyPwd;
					if (me.verifyPwd)
					{
						form.findField('password').setReadOnly(false);
					}
					else if (me.verifyFinger)
					{
						form.findField('password').setReadOnly(true);
					}
				}
			}
		});
	},
	onSubmit : function()
	{
		var me = this;
		try
		{
			me.setLoading('正在登录...');
			var form = me.form;
			var username = form.getForm().findField('username').getValue();
			var password = form.getForm().findField('password').getValue();
			var pwdEn = pwdEncoding(password);
			// password = pwdEncoding(password);
			var encoder = pwdEn != password;
			if(encoder === false)
			{
				//pwdEn = window.BASE64.encoder(password);
			}			
			if (me.verifyFinger)
			{
				me.readFingerData(me.fingerDev);
			}
			Ext.Ajax.request({
				url : ctxp + me.url,
				params : {
					username : username,
					password : pwdEn,
					passwordEncoder : encoder,
					fingerData : me.fingerData,
					fingerDev : me.fingerDev
				},
				method : 'post',
				callback : function(options, success, response)
				{
					try
					{
						me.fingerData = '';
						if (success)
						{
							var json = Ext.JSON.decode(response.responseText);
							Common.setLoading({
								comp : me,
								msg : json.msg
							});
							me.textTipUpdate(json.msg);
							if (json.success)
							{
								me.result = json;
								me.close();
							}
						}
						else
						{
							me.textTipUpdate('登录失败');
						}
					}
					catch (EX)
					{
						Common.setLoading({
							comp : me,
							msg : '异常提示:' + EX
						});
					}
				}
			});
		}
		catch (error)
		{
			Common.setLoading({
				comp : me,
				msg : '异常提示:' + error
			});
		}
	}

});