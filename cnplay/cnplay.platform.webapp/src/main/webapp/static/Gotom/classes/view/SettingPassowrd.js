Ext.define('Gotom.view.SettingPassowrd', {
    extend: 'Ext.window.Window',
    alias: 'widget.SettingPassowrd',

    requires: [
        'Ext.form.Panel',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.form.field.Text',
        'Ext.form.field.Hidden'
    ],

    height: 164,
    width: 400,
    layout: 'border',
    closeAction: 'hide',
    title: '修改密码',
    modal: true,
	logout: false,
	
    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'form',
                    region: 'center',
                    border: false,
                    bodyPadding: 10,
                    header: false,
                    title: 'PasswordForm',
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            layout: {
                                type: 'hbox',
                                pack: 'end',
                                padding: 3
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    iconCls: 'icon-save',
                                    text: '保存',
                                    listeners: {
                                        click: {
                                            fn: me.onButtonClick,
                                            scope: me
                                        }
                                    }
                                }
                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            fieldLabel: '原 密 码',
                            msgTarget: 'side',
                            name: 'password',
                            inputType: 'password',
                            allowBlank: false,
                            enforceMaxLength: true,
                            emptyText: '请输入原密码！',
                            maxLength: 12,
                            minLength: 1,
                            listeners: {
                                specialkey: {
                                    fn: me.onPasswordSpecialkey,
                                    scope: me
                                }
                            }							
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            fieldLabel: '新 密 码',
                            msgTarget: 'side',
                            name: 'newpass',
                            inputType: 'password',
                            invalidText: '请输入6-12位字符的密码！',
							vtype: 'newpass',
                            allowBlank: false,
                            enforceMaxLength: true,
                            blankText: '请输入6-12位字符的密码！',
                            emptyText: '请输入6-12位字符的密码！',
                            maxLength: 12,
                            maxLengthText: '请输入6-12位字符的密码！',
                            minLength: 6,
                            minLengthText: '请输入6-12位字符的密码！',
                            listeners: {
                                change: {
                                    fn: me.onNewpassChange,
                                    scope: me
                                },								
                                specialkey: {
                                    fn: me.onNewpassSpecialkey,
                                    scope: me
                                }
                            }							
                        },
                        {
                            xtype: 'textfield',
                            confirmPwd: 'newpass',
                            anchor: '100%',
                            fieldLabel: '确认密码',
                            msgTarget: 'side',
                            name: 'newpassCheck',
                            inputType: 'password',
                            allowBlank: false,
                            blankText: '请输入6-12位字符的密码！',
                            emptyText: '请输入6-12位字符的密码！',
                            vtype: 'password',
                            listeners: {
                                change: {
                                    fn: me.onnewpassCheckChange,
                                    scope: me
                                },
                                specialkey: {
                                    fn: me.onNewpassCheckSpecialkey,
                                    scope: me
                                }								
                            }
                        },
                        {
                            xtype: 'hiddenfield',
                            anchor: '100%',
                            fieldLabel: 'Label',
                            name: 'passwordEncoder'
                        }
                    ],
                    listeners: {
                        afterrender: {
                            fn: me.onFormAfterRender,
                            scope: me
                        }				
                    }
                }
            ]
        });

        me.callParent(arguments);
    },
    onPasswordSpecialkey: function(field, e, eOpts) {
		if (e.getKey() === e.ENTER)
        {
			var me = this;
			var form = me.form;
			form.getForm().findField('newpass').focus();	
		}		
    },  
    onNewpassSpecialkey: function(field, e, eOpts) {
		if (e.getKey() === e.ENTER)
        {		
			var me = this;
			var form = me.form;
			form.getForm().findField('newpassCheck').focus();	
		}			
    },  	
	onNewpassCheckSpecialkey: function(field, e, eOpts) {
		if (e.getKey() === e.ENTER)
        {		
			var me = this;
			me.onSubmit();	
		}
    }, 	
    onSubmit: function() {
        var me = this;
        try{
            var form = me.form;
    		if (!form.isValid())
    		{
    			return false;
    		}            
            var password = form.getForm().findField('password').getValue();
            var pwdEn = pwdEncoding(password);
            var newpass = form.getForm().findField('newpass').getValue();
            var newpassEn = pwdEncoding(newpass);
            var newpassCheck = form.getForm().findField('newpassCheck').getValue();
            var newpassCheckEn = pwdEncoding(newpassCheck);
            var encoder = pwdEn != password;
            Common.ajax(
                {
                    component : me,
                    lock:true,
                    url : ctxp+'/home/settingPassword',
                    params:{'password':pwdEn,'newpass':newpassEn,'newpassCheck':newpassCheckEn,'passwordEncoder':encoder},
                    callback : function(result){
                        me.result = result;
                        if(result.success)
                        {
							me.form.getForm().reset();
							me.logout = false;
                            me.close();
							Common.setLoading({msg:'保存成功'});					
                        }
                        else
                        {
							Common.setLoading({msg:result.msg});
                        }
                    }
                });  
        }catch(error)
        {
           alert(error);
        }
    },
    
    onButtonClick: function(button, e, eOpts) {
		var me = this;
		me.onSubmit();
    },

    onnewpassCheckChange: function(field, newValue, oldValue, eOpts) {
        var form = this.form;
        Ext.apply(Ext.form.VTypes, {
            password : function(val, field) {
                if (field.confirmPwd) {
                    var pwd = form.getForm().findField(field.confirmPwd);//Ext.getCmp(field.confirmPwd);
                    return (val == pwd.getValue());
                }
                return true;
            },
            passwordText : '两次输入的密码不一致!'
        });
    },

    onNewpassChange: function(field, newValue, oldValue, eOpts) {
        var form = this.form;
		var me = this;
        Ext.apply(Ext.form.VTypes, {
            newpass : function(val, field) {
                var pwd = field.getValue();
				var success = false;
				if(pwd.length >= 6 && pwd.length <= 12)
				{
					success = true;
				}
				if(success){
					success = false;
					for(var i = 0;i<pwd.length-1;i++)
					{//ABCDEF
						if((pwd.charCodeAt(i)+1) != pwd.charCodeAt(i+1))
						{
							success = true;
						}		
					}					
				}
				if(success){
					success = false;
					for(var i = 0;i<pwd.length-1;i++)
					{//FEDCBA
						if((pwd.charCodeAt(i)-1) != pwd.charCodeAt(i+1))
						{
							success = true;
						}		
					}					
				}
				if(success){
					success = false;
					for(var i = 0;i<pwd.length-1;i++)
					{//AAAAAA
						if(pwd.charCodeAt(i) != pwd.charCodeAt(i+1))
						{
							success = true;
						}		
					}					
				}				
				if(success){
					success = me.checkPwd(pwd,2);	
				}
				if(success){
					success = me.checkPwd(pwd,3);					
				}
				if(success){
					success = me.checkPwd(pwd,4);					
				}	
				if(success){
					success = me.checkPwd(pwd,5);					
				}
				if(success){
					success = me.checkPwd(pwd,6);					
				}				
				return success;
            },
            newpassText : '密码复杂度不够，请重新设置!'
        });
    },
	checkPwd: function(pwd, len) {
		var success = true;
		if(pwd.length % len == 0 && pwd.length/len > 1)
		{
			success = false;
			var tmp = pwd.substr(0,len);
			for(var i = 1;i<pwd.length/len;i++)
			{//不允许ABABAB、BABABA类型
				var tmp2 = pwd.substr(i*len,len);
				if(tmp2 !== tmp)
				{
					success = true;
				}else{
					tmp = tmp2;
				}
			}
		}
		return success;
    },	
    onFormAfterRender: function(component, eOpts) {
        this.form = component;
    }

});