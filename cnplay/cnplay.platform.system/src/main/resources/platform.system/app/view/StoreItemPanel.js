/*
 * File: app/view/StoreItemPanel.js
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

Ext.define('platform.system.view.StoreItemPanel', {
    extend: 'Ext.grid.Panel',

    requires: [
        'platform.system.view.DateTegion',
        'Ext.form.Panel',
        'Ext.button.Button',
        'Ext.grid.column.Column',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Paging',
        'Ext.form.field.ComboBox'
    ],

    title: '入库管理',
    forceFit: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            dockedItems: [
                {
                    xtype: 'form',
                    dock: 'top',
                    border: false,
                    layout: 'column',
                    bodyBorder: false,
                    header: false,
                    title: '查询表单',
                    items: [
                        {
                            xtype: 'textfield',
                            margin: 5,
                            width: 270,
                            fieldLabel: '所有人',
                            labelAlign: 'right',
                            labelWidth: 60,
                            name: 'dywOwner'
                        },
                        {
                            xtype: 'DateTegion',
                            margin: 5
                        }
                    ],
                    listeners: {
                        afterrender: {
                            fn: me.onFormAfterRender,
                            scope: me
                        }
                    },
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            height: '',
                            items: [
                                {
                                    xtype: 'button',
                                    iconCls: 'icon-search',
                                    text: '查询',
                                    listeners: {
                                        afterrender: {
                                            fn: me.onBtnSearchAfterRender,
                                            scope: me
                                        },
                                        click: {
                                            fn: me.onBtnSearchClick,
                                            scope: me
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    disabled: true,
                                    iconCls: 'icon-del',
                                    text: '出库',
                                    listeners: {
                                        afterrender: {
                                            fn: me.onBtnInAfterRender,
                                            scope: me
                                        },
                                        click: {
                                            fn: me.onBtnInClick,
                                            scope: me
                                        }
                                    }
                                }
                            ]
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
                                    fn: me.onpageSizeAfterRender,
                                    single: true,
                                    scope: me
                                },
                                change: {
                                    fn: me.onComboboxChange,
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
                    width: 150,
                    dataIndex: 'areaName',
                    text: '存放区域'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'rfid',
                    text: '标签号'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'sn',
                    text: '物品编号'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'name',
                    text: '物品名称'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'storeman',
                    text: '保管员'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'dywOwner',
                    text: '所有人'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'dywOwnerId',
                    text: '身份证号'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'dywId',
                    text: '抵押物证号码'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'htId',
                    text: '贷款合同号'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'registerDate',
                    text: '登记日期'
                },
                {
                    xtype: 'gridcolumn',
                    width: 300,
                    dataIndex: 'pgje',
                    text: '评估金额'
                },
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'memo',
                    text: '备注'
                }
            ],
            viewConfig: {
                listeners: {
                    beforeitemdblclick: {
                        fn: me.onViewBeforeItemDblClick,
                        scope: me
                    }
                }
            },
            selModel: Ext.create('Ext.selection.CheckboxModel', {

            }),
            listeners: {
                afterrender: {
                    fn: me.onGridpanelAfterRender,
                    scope: me
                },
                selectionchange: {
                    fn: me.onGridpanelSelectionChange,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onFormAfterRender: function(component, eOpts) {
        this.form = component;
    },

    onBtnSearchAfterRender: function(component, eOpts) {
        Common.hidden({params : {url:'/store/item/list'},component:component});
    },

    onBtnSearchClick: function(button, e, eOpts) {

        this.loadGrid();
    },

    onBtnInAfterRender: function(component, eOpts) {
        Common.hidden({params : {url:'/store/out/save'},component:component});
        this.btnDel = component;
    },

    onBtnInClick: function(button, e, eOpts) {

        try
        {
            var selected  = this.getSelectionModel().selected.items;
            if(selected.length!=1){
                Ext.Msg.alert('提示','请选择一行!');
                return;
            }
            var me = this;
            me.showForm(selected[0].data.id);
        }
        catch(error)
        {
            Common.show({title:'信息提示',html:error.toString()});
        }
    },

    onViewBeforeItemDblClick: function(dataview, record, item, index, e, eOpts) {
        this.showForm(record.data.id);
    },

    onGridpanelAfterRender: function(component, eOpts) {

        this.loadGrid();
    },

    onGridpanelSelectionChange: function(model, selected, eOpts) {
        if(selected.length === 1){
            this.btnDel.setDisabled(false);
        }else{
            this.btnDel.setDisabled(true);
        }
    },

    onPagingtoolbarBeforeRender: function(component, eOpts) {
        this.pagingToolbar = component;
    },

    onpageSizeAfterRender: function(component, eOpts) {
        this.pageSize = component;
        Common.bindPageSize(component);
    },

    onComboboxChange: function(field, newValue, oldValue, eOpts) {
        this.loadGrid();
    },

    showForm: function(id) {
        try
        {
            var me = this;
            var formwin = Ext.create('platform.system.view.StoreOutWindow');
            formwin.addListener('close', function(panel,opts)
                                {
                                    me.loadGrid();
                                });
            formwin.show();
            formwin.loadForm(id);
        }
        catch(error)
        {
            Common.show({title:'信息提示',html:error.toString()});
        }
    },

    loadGrid: function() {
        try{
            var me = this;
            var params = me.form.getForm().getValues();
            Common.loadStore({
                component:this,
                url:ctxp + '/store/item/list',
                pageSize:this.pageSize.getValue(),
                fields: ['id', 'sn','rfid','status','name','areaId','areaName','memo', 'orgId', 'storeman','dywOwner','dywOwnerId','dywId','registerDate',
                         'jkrsfz','jkrxm','jkje','pgje','htEndDate','htStartDate','operator','updateCheckUsername','htId'],
                params:params
            });
        }catch(ex){}
    }

});