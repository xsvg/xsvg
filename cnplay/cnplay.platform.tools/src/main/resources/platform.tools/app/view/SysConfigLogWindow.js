/*
 * File: app/view/SysConfigLogWindow.js
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

Ext.define('platform.tools.view.SysConfigLogWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.SysConfigLogWindow',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Text',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 406,
    width: 467,
    resizable: false,
    layout: 'border',
    title: '运行7参设置',
    modal: true,

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
                    title: '运行7参',
                    items: [
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数1',
                            labelWidth: 50,
                            name: 'pwdParameter'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数2',
                            labelWidth: 50,
                            name: 'bitConfByPWD'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数3',
                            labelWidth: 50,
                            name: 'bitConfByFinger'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数4',
                            labelWidth: 50,
                            name: 'lockModel'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数5',
                            labelWidth: 50,
                            name: 'openLockNum'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数6',
                            labelWidth: 50,
                            name: 'openLockDate'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数7',
                            labelWidth: 50,
                            name: 'mutiOpenLock'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数8',
                            labelWidth: 50,
                            name: 'lockWaveAlarm'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数9',
                            labelWidth: 50,
                            name: 'lockLockedState'
                        },
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            padding: 5,
                            fieldLabel: '参数10',
                            labelWidth: 50,
                            name: 'lockHighTempAlarm'
                        }
                    ],
                    listeners: {
                        afterrender: {
                            fn: me.onFormAfterRender,
                            scope: me
                        }
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    layout: {
                        type: 'hbox',
                        pack: 'end'
                    },
                    items: [
                        {
                            xtype: 'button',
                            iconCls: 'icon-ok',
                            text: '确定',
                            listeners: {
                                click: {
                                    fn: me.onBtnOkClick,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            iconCls: 'icon-cancel',
                            text: '取消',
                            listeners: {
                                click: {
                                    fn: me.onBtnCloseClick,
                                    scope: me
                                }
                            }
                        }
                    ]
                }
            ]
        });

        me.callParent(arguments);
    },

    onFormAfterRender: function(component, eOpts) {
        this.form = component;
        this.loadForm();
    },

    onBtnOkClick: function(button, e, eOpts) {
        var me  =this;
        var params = me.form.getForm().getValues();
        if(this.validateValues(params)){
            try{
                Ext.Msg.confirm("确认提示", '保存后要进行锁具激活，请确认是否保存？', function(btn)
                                {
                                    if (btn == "yes")
                                    {
                                        Common.formSubmit({
                                            url : ctxp+'/system/config/log/save',
                                            form:me.form,
                                            callback : function(result)
                                            {
                                                Ext.Msg.alert('信息提示', '设置保存成功！');
                                                me.close();
                                            }
                                        });
                                    }
                                });
            }catch(error){
                Common.show({title:'信息提示',html:error.toString()});
            }
        }
    },

    onBtnCloseClick: function(button, e, eOpts) {
        this.close();
    },

    loadForm: function(data) {
        var me = this;
        try{
            me.data = data;
            if(Ext.isEmpty(me.data)){
                Common.ajax({
                    message : '加载信息...',
                    url : ctxp+'/system/config/log/load',
                    callback : function(result)
                    {
                        if(Ext.isEmpty(me.data)){
                            me.form.getForm().reset();
                            me.form.getForm().setValues(result.rows);
                        }

                    }
                });
            }else{
                me.form.getForm().reset();
                me.form.getForm().setValues(me.data);
            }
        }
        catch(error)
        {
            Common.show({title:'信息提示',html:error.toString()});
        }
    },

    validateValues: function(values) {
        for(var key in values){
            var value = values[key];
            switch(key){
                case 'pwdParameter':
                    if(!/[1357]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数1的值只能为1、3、5、7" });
                        return false;
                    }
                    break;
                case 'bitConfByPWD':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数2的值只能为0或1" });
                        return false;
                    }
                    break;
                case 'bitConfByFinger':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数3的值只能为0或1" });
                        return false;
                    }
                    break;
                case 'lockModel':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数4的值只能为0或1" });
                        return false;
                    }
                    break;
                case 'openLockNum':
                    if(!/[0-4]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数5的值只能为0-4的数字" });
                        return false;
                    }
                    break;
                case 'openLockDate':
                    if(!/[0-2]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数6的值只能为0-2的数字" });
                        return false;
                    }
                    break;
                case 'mutiOpenLock':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数7的值只能为0或1" });
                        console.info(key,value);
                        return false;
                    }
                    break;
                case 'lockWaveAlarm':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数8的值只能为0或1" });
                        console.info(key,value);
                        return false;
                    }
                    break;
                case 'lockLockedState':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数9的值只能为0或1" });
                        console.info(key,value);
                        return false;
                    }
                    break;
                case 'lockHighTempAlarm':
                    if(!/[01]$/.test(value) || value.length >1){
                        Ext.Msg.alert({title : '提示',msg : "参数10的值只能为0或1" });
                        console.info(key,value);
                        return false;
                    }
                    break;
                default: break;
            }
        }
        return true;
    }

});