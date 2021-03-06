/*
 * File: app/view/StoreAreaWindow.js
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

Ext.define('platform.system.view.StoreAreaWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.form.Panel',
        'Ext.form.field.Hidden',
        'Ext.form.Label',
        'Ext.form.field.TextArea',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 269,
    width: 432,
    resizable: false,
    layout: 'border',
    title: '编辑区域',
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
                            xtype: 'textfield',
                            padding: 5,
                            width: 330,
                            fieldLabel: '区域编码',
                            labelAlign: 'right',
                            name: 'code',
                            invalidText: '机构编码不能为空！',
                            allowBlank: false,
                            maxLength: 10
                        },
                        {
                            xtype: 'label',
                            html: '<font color=red>*</font>',
                            padding: '10 5 5 5 '
                        },
                        {
                            xtype: 'textfield',
                            padding: 5,
                            width: 330,
                            fieldLabel: '区域名称',
                            labelAlign: 'right',
                            name: 'name',
                            invalidText: '机构名称不能为空！',
                            allowBlank: false,
                            enforceMaxLength: true,
                            maxLength: 50,
                            maxLengthText: '机构名称最大长度不超过50个字符！'
                        },
                        {
                            xtype: 'label',
                            html: '<font color=red>*</font>',
                            padding: '10 5 5 5 '
                        },
                        {
                            xtype: 'hiddenfield',
                            padding: 5,
                            width: 330,
                            fieldLabel: '上级机构',
                            labelAlign: 'right',
                            name: 'parentId'
                        },
                        {
                            xtype: 'textfield',
                            padding: 5,
                            width: 330,
                            fieldLabel: '上级区域',
                            labelAlign: 'right',
                            name: 'parentName',
                            readOnly: true,
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
                            xtype: 'label',
                            html: '<font color=red>*</font>',
                            padding: '10 5 5 5 '
                        },
                        {
                            xtype: 'textareafield',
                            padding: 5,
                            width: 330,
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
                            width: 53,
                            iconCls: 'icon-save',
                            text: '保存',
                            listeners: {
                                click: {
                                    fn: me.onSaveClick,
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
            ]
        });

        me.callParent(arguments);
    },

    onParentNameTextfieldFocus: function(component, e, eOpts) {
        try{
            var me = this;
            var parentId = me.form.getForm().findField('parentId');
            var myId = me.form.getForm().findField('id').getValue();
            Common.showTreeSelect({
                multiple:false,
                url:ctxp + '/store/area/loadTree',
                params:{orgId: parentId.getValue(),myId:myId},
                callback:function(item)
                {
                    parentId.setValue(item.id);
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
                url : ctxp+'/store/area/save',
                form:me.form,
                callback : function(result)
                {
                    Ext.Msg.alert('操作提示', '机构保存成功！');
                    me.close();
                }
            });
        }catch(error){
            Common.show({title:'操作提示',html:error.toString()});
        }
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
                url : ctxp+'/store/area/load?id='+id,
                callback : function(result)
                {
                    me.form.getForm().reset();
                    me.form.getForm().setValues(result.rows);
                    if(!Ext.isEmpty(id)){
                        me.form.getForm().findField('code').setReadOnly(result.rows.checked);
                    }
                }
            });
        }
        catch(error)
        {
            Common.show({title:'操作提示',html:error.toString()});
        }
    }

});