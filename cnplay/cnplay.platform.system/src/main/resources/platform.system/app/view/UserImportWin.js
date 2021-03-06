/*
 * File: app/view/UserImportWin.js
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

Ext.define('platform.system.view.UserImportWin', {
    extend: 'Ext.window.Window',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.column.Column',
        'Ext.grid.View',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 400,
    width: 862,
    resizable: false,
    layout: 'border',
    title: '未导入数据',
    modal: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'gridpanel',
                    region: 'west',
                    maxHeight: 338,
                    width: 850,
                    autoScroll: true,
                    title: '',
                    columnLines: true,
                    forceFit: true,
                    columns: [
                        {
                            xtype: 'gridcolumn',
                            dataIndex: 'username',
                            text: '用户编号'
                        },
                        {
                            xtype: 'gridcolumn',
                            dataIndex: 'name',
                            text: '用户名称'
                        },
                        {
                            xtype: 'gridcolumn',
                            dataIndex: 'orgId',
                            text: '所属机构编号'
                        },
                        {
                            xtype: 'gridcolumn',
                            dataIndex: 'jobName',
                            text: '角色名称'
                        },
                        {
                            xtype: 'gridcolumn',
                            dataIndex: 'mobile',
                            text: '手机号'
                        },
                        {
                            xtype: 'gridcolumn',
                            dataIndex: 'memo',
                            text: '备注'
                        },
                        {
                            xtype: 'gridcolumn',
                            width: 250,
                            weight: 200,
                            dataIndex: 'createUsername',
                            text: '错误信息'
                        }
                    ],
                    listeners: {
                        afterrender: {
                            fn: me.onUserImportGridPanelAfterRender,
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
                            padding: '5 20 5 5',
                            iconCls: 'icon-cancel',
                            text: '确定',
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

    onUserImportGridPanelAfterRender: function(component, eOpts) {
        this.userImportGridPanel = component;
    },

    onCancelClick: function(button, e, eOpts) {
        var me = this;
        me.close();
    },

    loadForm: function(result) {

        var me = this;
        //me.userImportGridPanel.loadData(result);
        //Ext.Msg.alert('操作提示', result.msg);
        var UserStore = Ext.create('Ext.data.Store', {
            storeId:'UserStore',
            fields: ['username','name','orgId','jobName','mobile','memo','createUsername'],
            data : result,
            proxy:
            {
                type: 'memory',
                reader:{
                    type: 'json'
                }
            }
        });
        me.userImportGridPanel.bindStore(UserStore);

    }

});