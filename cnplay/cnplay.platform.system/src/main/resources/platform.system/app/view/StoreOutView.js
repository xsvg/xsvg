/*
 * File: app/view/StoreOutView.js
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

Ext.define('platform.system.view.StoreOutView', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Hidden',
        'Ext.form.field.Number',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.Label',
        'Ext.form.field.Date',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 459,
    width: 768,
    resizable: false,
    layout: 'border',
    title: '抵押物明细',
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
                            xtype: 'hiddenfield',
                            fieldLabel: 'Label',
                            name: 'itemId'
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            height: 32,
                            width: 677,
                            layout: 'absolute',
                            header: false,
                            title: 'rfid',
                            items: [
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 670,
                                    fieldLabel: '标签号',
                                    labelAlign: 'right',
                                    name: 'rfid',
                                    invalidText: '机构编码不能为空！',
                                    readOnly: true,
                                    allowBlank: false
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            height: 260,
                            width: 340,
                            items: [
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '物品编号',
                                    labelAlign: 'right',
                                    name: 'sn',
                                    invalidText: '机构编码不能为空！',
                                    readOnly: true,
                                    allowBlank: false,
                                    maxLength: 10
                                },
                                {
                                    xtype: 'textfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '物品名称',
                                    labelAlign: 'right',
                                    name: 'name',
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
                                    width: 330,
                                    fieldLabel: '抵质押物证号码',
                                    labelAlign: 'right',
                                    name: 'dywId',
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
                                    width: 330,
                                    fieldLabel: '抵质押物所有人',
                                    labelAlign: 'right',
                                    name: 'dywOwner',
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
                                    width: 330,
                                    fieldLabel: '所有人证件号码',
                                    labelAlign: 'right',
                                    name: 'dywOwnerId',
                                    invalidText: '机构名称不能为空！',
                                    readOnly: true,
                                    allowBlank: false,
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'numberfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '抵质押物评估价值',
                                    labelAlign: 'right',
                                    name: 'pgje',
                                    invalidText: '机构名称不能为空！',
                                    readOnly: true,
                                    allowBlank: false,
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'radiogroup',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '身份证原件确认',
                                    labelAlign: 'right',
                                    items: [
                                        {
                                            xtype: 'radiofield',
                                            name: 'confirmId',
                                            boxLabel: '已确认',
                                            inputValue: 'true'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            name: 'confirmId',
                                            boxLabel: '未确认',
                                            checked: true,
                                            inputValue: 'false'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'radiogroup',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '贷款是否还清',
                                    labelAlign: 'right',
                                    items: [
                                        {
                                            xtype: 'radiofield',
                                            name: 'paidOff',
                                            boxLabel: '已还清',
                                            inputValue: 'true'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            name: 'paidOff',
                                            boxLabel: '未还清',
                                            checked: true,
                                            inputValue: 'false'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            height: 260,
                            width: 340,
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
                                    readOnly: true,
                                    allowBlank: false,
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
                                            readOnly: true,
                                            vtype: 'startDate',
                                            editable: false,
                                            format: 'Y年m月d日'
                                        },
                                        {
                                            xtype: 'datefield',
                                            width: 120,
                                            fieldLabel: '-',
                                            labelAlign: 'right',
                                            labelSeparator: ' ',
                                            labelWidth: 10,
                                            name: 'htEndDate',
                                            readOnly: true,
                                            vtype: 'endDate',
                                            editable: false,
                                            format: 'Y年m月d日'
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
                                    readOnly: true,
                                    allowBlank: false,
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！',
                                    editable: false,
                                    format: 'Y年m月d日'
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
                                    maxLength: 50
                                },
                                {
                                    xtype: 'numberfield',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '实际借款金额',
                                    labelAlign: 'right',
                                    name: 'jkje',
                                    invalidText: '机构名称不能为空！',
                                    readOnly: true,
                                    allowBlank: false,
                                    enforceMaxLength: true,
                                    maxLength: 50,
                                    maxLengthText: '机构名称最大长度不超过50个字符！'
                                },
                                {
                                    xtype: 'radiogroup',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '抵押凭证客户联',
                                    labelAlign: 'right',
                                    items: [
                                        {
                                            xtype: 'radiofield',
                                            name: 'takeBackCertificate',
                                            boxLabel: '已回收',
                                            inputValue: 'true'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            name: 'takeBackCertificate',
                                            boxLabel: '未回收',
                                            checked: true,
                                            inputValue: 'false'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'radiogroup',
                                    padding: 5,
                                    width: 330,
                                    fieldLabel: '是否本人签名',
                                    labelAlign: 'right',
                                    items: [
                                        {
                                            xtype: 'radiofield',
                                            name: 'selfSign',
                                            boxLabel: '已签名',
                                            inputValue: 'true'
                                        },
                                        {
                                            xtype: 'radiofield',
                                            name: 'selfSign',
                                            boxLabel: '未签名',
                                            checked: true,
                                            inputValue: 'false'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'textareafield',
                            padding: 5,
                            width: 670,
                            fieldLabel: '备注',
                            labelAlign: 'right',
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
            ]
        });

        me.callParent(arguments);
    },

    onFormAfterRender: function(component, eOpts) {
        this.form=component;
    },

    onCancelClick: function(button, e, eOpts) {
        var me = this;
        me.close();
    },

    loadForm: function(id) {
        var me = this;
        try{
            Common.ajax({
                component : me.form,
                message : '加载信息...',
                url : ctxp+'/store/out/vo?id='+id,
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
    }

});