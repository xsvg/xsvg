/*
 * File: app/view/CheckLogPanel.js
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

Ext.define('platform.system.view.CheckLogPanel', {
    extend: 'Ext.grid.Panel',

    requires: [
        'platform.system.view.DateTegion',
        'Ext.button.Button',
        'Ext.form.Panel',
        'Ext.form.field.Hidden',
        'Ext.grid.column.Column',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Paging',
        'Ext.form.field.ComboBox'
    ],

    title: '复核记录',
    forceFit: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            iconCls: 'icon-search',
                            text: '查询',
                            listeners: {
                                click: {
                                    fn: me.onButtonSoClick,
                                    scope: me
                                },
                                afterrender: {
                                    fn: me.onButtonSoAfterRender,
                                    scope: me
                                }
                            }
                        }
                    ]
                },
                {
                    xtype: 'form',
                    dock: 'top',
                    border: false,
                    layout: 'column',
                    header: false,
                    title: '查询表单',
                    listeners: {
                        afterrender: {
                            fn: me.onFormAfterRender,
                            scope: me
                        }
                    },
                    items: [
                        {
                            xtype: 'hiddenfield',
                            fieldLabel: '组织机构',
                            labelAlign: 'right',
                            labelWidth: 80,
                            name: 'orgId',
                            listeners: {
                                beforerender: {
                                    fn: me.onHiddenfieldBeforeRender,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'textfield',
                            margin: 5,
                            width: 270,
                            fieldLabel: '组织机构',
                            labelAlign: 'right',
                            labelWidth: 60,
                            name: 'orgName',
                            readOnly: true,
                            listeners: {
                                focus: {
                                    fn: me.onTextfieldFocus,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'DateTegion',
                            label: '复核时间',
                            margin: 5
                        }
                    ]
                },
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    afterPageText: '页，共 {0} 页',
                    beforePageText: '第',
                    displayInfo: true,
                    displayMsg: '第 {0} - {1} 行，共 {2} 行',
                    listeners: {
                        beforerender: {
                            fn: me.onPagingtoolbarBeforeRender,
                            single: true,
                            scope: me
                        }
                    },
                    items: [
                        {
                            xtype: 'combobox',
                            width: 120,
                            fieldLabel: '每页行数',
                            labelAlign: 'right',
                            labelWidth: 60,
                            name: 'pageSize',
                            listeners: {
                                beforerender: {
                                    fn: me.onPageSizeAfterRender,
                                    single: true,
                                    scope: me
                                },
                                change: {
                                    fn: me.onPageSizeChange,
                                    single: false,
                                    scope: me
                                }
                            }
                        }
                    ]
                }
            ],
            columns: [
                {
                    xtype: 'gridcolumn',
                    defaultWidth: 200,
                    dataIndex: 'memo',
                    text: '复核内容'
                },
                {
                    xtype: 'gridcolumn',
                    hidden: true,
                    width: 150,
                    defaultWidth: 150,
                    dataIndex: 'url',
                    text: '复核地址'
                },
                {
                    xtype: 'gridcolumn',
                    defaultWidth: 120,
                    dataIndex: 'orgName',
                    text: '所属机构'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'createUsername',
                    text: '申请人'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'createTimeStr',
                    text: '申请时间'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'createCheckUsername',
                    text: '复核人'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'checkTimeStr',
                    text: '复核时间'
                }
            ],
            viewConfig: {
                enableTextSelection: true
            },
            selModel: Ext.create('Ext.selection.CheckboxModel', {

            }),
            listeners: {
                afterrender: {
                    fn: me.onGridpanelAfterRender,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onButtonSoClick: function(button, e, eOpts) {
        var me = this;
        me.loadGrid();
    },

    onButtonSoAfterRender: function(component, eOpts) {
        Common.hidden({params : {url:'/system/checkLog/list'},component:component});
    },

    onFormAfterRender: function(component, eOpts) {
        this.form = component;
    },

    onHiddenfieldBeforeRender: function(component, eOpts) {
        this.orgId = component;
    },

    onTextfieldFocus: function(component, e, eOpts) {
        try{
            var me = this;
            var orgId = me.form.getForm().findField('orgId');
            Common.showTreeSelect({
                multiple:false,
                url:ctxp + '/home/loadUserOrgTree',
                params:{orgId: orgId.getValue()},
                callback:function(item)
                {
                    orgId.setValue(item.id);
                    component.setValue(item.text);
                }
            });
        }catch(ex){}
    },

    onPagingtoolbarBeforeRender: function(component, eOpts) {
        this.pagingToolbar = component;
    },

    onPageSizeAfterRender: function(component, eOpts) {
        this.pageSize = component;
        Common.bindPageSize(component);
    },

    onPageSizeChange: function(field, newValue, oldValue, eOpts) {
        try{
            if(!Ext.isEmpty(this.pagingToolbar))
            {
                this.loadGrid();
            }
        }
        catch(error)
        {
        }
    },

    onGridpanelAfterRender: function(component, eOpts) {
        this.loadGrid();
    },

    loadGrid: function() {
        var me = this;
        var params = me.form.getForm().getValues();
        Common.loadStore({
            component:this,
            url:ctxp + '/system/checkLog/list',
            pageSize:this.pageSize.getValue(),
            fields: ['id', 'url', 'memo','createUsername', 'createTime','createTimeStr','createCheckUsername', 'checkTime','checkTimeStr','checkJsonByte','orgName'],
            params:params
        });
    }

});