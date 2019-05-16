/*
 * File: app/view/StoreModityWindow.js
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

Ext.define('platform.system.view.StoreModityWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Hidden',
        'Ext.button.Button',
        'Ext.form.Label',
        'Ext.form.field.Date',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar'
    ],

    height: 390,
    width: 768,
    resizable: false,
    layout: 'border',
    title: '修改',
    modal: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'form',
                    region: 'center',
                    border: false,
                    layout: 'column',
                    bodyPadding: 10,
                    header: false,
                    title: '编辑机构表单',
                    items: [
                        {
                            xtype: 'hiddenfield',
                            fieldLabel: 'Label',
                            name: 'id'
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            height: 32,
                            width: 710,
                            layout: 'absolute',
                            header: false,
                            title: 'rfid',
                            items: [
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 690,
                                    fieldLabel: '标签号',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'rfid',
                                    invalidText: '机构编码不能为空！',
                                    readOnly: true,
                                    allowBlank: false,
                                    listeners: {
                                        afterrender: {
                                            fn: me.onTextfieldAfterRender,
                                            single: true,
                                            scope: me
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    x: 635,
                                    y: 5,
                                    disabled: true,
                                    hidden: true,
                                    width: 60,
                                    text: '读标签号',
                                    listeners: {
                                        afterrender: {
                                            fn: me.onButtonAfterRender,
                                            scope: me
                                        },
                                        click: {
                                            fn: me.onButtonClick,
                                            scope: me
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            height: 196,
                            width: 360,
                            items: [
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '物品编号',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'sn',
                                    invalidText: '机构编码不能为空！',
                                    maxLength: 50
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '物品名称',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'name',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '抵质押物证号码',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'dywId',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '抵质押物所有人',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'dywOwner',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '所有人证件号码',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'dywOwnerId',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '抵质押物评估金额',
                                    labelAlign: 'right',
                                    labelWidth: 120,
                                    name: 'pgje',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            height: 196,
                            width: 360,
                            items: [
                                {
                                    xtype: 'hiddenfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '上级机构',
                                    labelAlign: 'right',
                                    name: 'areaId'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '贷款合同号',
                                    labelAlign: 'right',
                                    name: 'htId',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'panel',
                                    label: '时间间隔',
                                    startDateName: 'startDate',
                                    endDateName: 'endDate',
                                    border: false,
                                    height: 26,
                                    width: 399,
                                    layout: 'column',
                                    header: false,
                                    title: '时间段',
                                    items: [
                                        {
                                            xtype: 'label',
                                            padding: '3 4 0 30',
                                            text: '合同起止日期:'
                                        },
                                        {
                                            xtype: 'datefield',
                                            width: 105,
                                            fieldLabel: '',
                                            labelAlign: 'right',
                                            name: 'htStartDate',
                                            vtype: 'startDate',
                                            format: 'Ymd'
                                        },
                                        {
                                            xtype: 'datefield',
                                            width: 120,
                                            fieldLabel: '-',
                                            labelAlign: 'right',
                                            labelSeparator: ' ',
                                            labelWidth: 10,
                                            name: 'htEndDate',
                                            vtype: 'endDate',
                                            format: 'Ymd'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'datefield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '表外登记日期',
                                    labelAlign: 'right',
                                    name: 'registerDate',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！',
                                    format: 'Ymd'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '保管人',
                                    labelAlign: 'right',
                                    name: 'storeman',
                                    invalidText: '机构名称不能为空！',
                                    readOnly: true,
                                    allowBlank: false,
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 329,
                                    fieldLabel: '保存区域',
                                    labelAlign: 'right',
                                    name: 'areaName',
                                    invalidText: '请选择保存区域',
                                    readOnly: true,
                                    allowBlank: false,
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    listeners: {
                                        focus: {
                                            fn: me.onParentNameTextfieldFocus,
                                            scope: me
                                        }
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '抵质押金额',
                                    labelAlign: 'right',
                                    name: 'jkje',
                                    invalidText: '机构名称不能为空！',
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                }
                            ]
                        },
                        {
                            xtype: 'textareafield',
                            padding: 5,
                            width: 692,
                            fieldLabel: '备注',
                            labelAlign: 'right',
                            labelWidth: 120,
                            name: 'memo',
                            enforceMaxLength: true,
                            maxLength: 100,
                            maxLengthText: '备注内容最大长度不超过100个字符！'
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
                        align: 'middle',
                        pack: 'end'
                    },
                    items: [
                        {
                            xtype: 'button',
                            width: 53,
                            iconCls: 'icon-save',
                            text: '保存',
                            listeners: {
                                click: {
                                    fn: me.onSaveClick,
                                    scope: me
                                },
                                render: {
                                    fn: me.onButtonRender,
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
                                    fn: me.onCancelClick,
                                    scope: me
                                }
                            }
                        }
                    ]
                }
            ],
            listeners: {
                destroy: {
                    fn: me.onWindowDestroy,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onTextfieldAfterRender: function(component, eOpts) {
        var me = this;
        me.rfidComp = component;
    },

    onButtonAfterRender: function(component, eOpts) {
        var me = this;
        me.rfidBtn = component;
    },

    onButtonClick: function(button, e, eOpts) {
        var me = this;
        me.rfidBtn = button;
        me.rfidBtn.setDisabled(false);
        window.rfidRead = function(){
            me.rfidRead();
        };
        setTimeout(window.rfidRead,1000);
    },

    onParentNameTextfieldFocus: function(component, e, eOpts) {
        try{
            var me = this;
            var areaId = me.form.getForm().findField('areaId');
            Common.showTreeSelect({
                multiple:false,
                url:ctxp + '/store/area/tree',
                params:{myId:areaId},
                callback:function(item)
                {
                    areaId.setValue(item.id);
                    component.setValue(item.text);
                }
            });
        }catch(ex){}
    },

    onFormAfterRender: function(component, eOpts) {
        this.form=component;
    },

    onSaveClick: function(button, e, eOpts) {

        var me = this;
        try{
            Common.formSubmit({
                url : ctxp+'/store/in/modify',
                form:me.form,
                callback : function(result)
                {
                    Ext.Msg.alert('操作提示', '保存成功！');
                    me.close();
                }
            });
        }catch(error){
            Common.show({title:'操作提示',html:error.toString()});
        }
    },

    onButtonRender: function(component, eOpts) {
        this.btnSave = component;
    },

    onCancelClick: function(button, e, eOpts) {
        var me = this;
        me.close();
    },

    onWindowDestroy: function(component, eOpts) {
        setTimeout(window.rfidRead,1000);
        window.rfidRead = null;
    },

    loadForm: function(id) {
        var me = this;
        try{
            if(id === ''){
                me.btnShow();
            }
            Common.ajax({
                component : me.form,
                message : '加载信息...',
                url : ctxp+'/store/out/load?id='+id,
                callback : function(result)
                {
                    me.form.getForm().reset();
                    me.form.getForm().setValues(result.rows);
                }
            });
        }
        catch(error)
        {
            Common.show({title:'操作提示',html:error.toString()});
        }
    },

    rfidRead: function() {
        var me = this;
        try{
            me.rfidBtn.setDisabled(false);
            Common.ajax({
                component : me.form,
                lock:false,
                url : ctxp+'/home/store/getTag',
                callback : function(result)
                {
                    me.rfidBtn.setDisabled(true);
                    if(result.rows === ''){
                        setTimeout(window.rfidRead,2000);
                    }else{
                        me.rfidComp.setValue(result.rows);
                        me.rfidBtn.setDisabled(false);
                    }
                }
            });
        }
        catch(error)
        {
            me.rfidBtn.setDisabled(false);
            Common.show({title:'操作提示',html:error.toString()});
        }
    },

    btnShow: function() {
        var me = this;
        try{
            me.btnSave.show();
            me.rfidBtn.setDisabled(false);
        }
        catch(error)
        {
            Common.show({title:'操作提示',html:error.toString()});
        }
    }

});