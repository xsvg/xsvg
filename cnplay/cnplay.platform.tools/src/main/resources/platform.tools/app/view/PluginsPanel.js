/*
 * File: app/view/PluginsPanel.js
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

Ext.define('platform.tools.view.PluginsPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.PluginsPanel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.grid.column.Column',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel'
    ],

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
                            disabled: true,
                            icon: 'static\icons\fam\yes.png',
                            iconCls: 'icon-yes',
                            text: '启用',
                            listeners: {
                                click: {
                                    fn: me.onButtonEnableClick,
                                    scope: me
                                },
                                afterrender: {
                                    fn: me.onButtonEnableAfterRender,
                                    single: true,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            disabled: true,
                            iconCls: 'icon-cancel',
                            text: '停用',
                            listeners: {
                                click: {
                                    fn: me.onButtonDisableClick,
                                    scope: me
                                },
                                afterrender: {
                                    fn: me.onButtonDisableAfterRender,
                                    single: true,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            disabled: true,
                            iconCls: 'icon-del',
                            text: '删除',
                            listeners: {
                                click: {
                                    fn: me.onDelButtonClick,
                                    scope: me
                                },
                                afterrender: {
                                    fn: me.onDelButtonDisableAfterRender,
                                    single: true,
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
                    dataIndex: 'name',
                    text: '插件名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'fileName',
                    text: '文件名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'versionNum',
                    text: '版本号'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'statusStr',
                    text: '状态'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'createTimeStr',
                    text: '创建时间'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'updateTimeStr',
                    text: '最后更新'
                }
            ],
            selModel: Ext.create('Ext.selection.CheckboxModel', {
                mode: 'SINGLE'
            }),
            listeners: {
                selectionchange: {
                    fn: me.onGridpanelSelectionChange,
                    scope: me
                },
                afterrender: {
                    fn: me.onGridpanelAfterRender,
                    single: true,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onButtonEnableClick: function(button, e, eOpts) {
        var me = this;
        var selected = me.getSelectionModel().selected.items;
        if(selected.length==1){
            Ext.Msg.confirm('确认','确定要启用此插件吗?',function(yes){
                if(yes){
                    Common.ajax({
                        message:'正在提交...',
                        url:ctxp+'/ptools/plugins/enable',
                        params:{id:selected[0].data.id},
                        callback:function(json,options,success,response){
                            try{
                                if(json.success){
                                    button.setDisabled(true);
                                    me.loadGrid();
                                }
                            }catch(e){}
                        }
                    });
                }
            });
        }
    },

    onButtonEnableAfterRender: function(component, eOpts) {
        this.btnEnable = component;
        Common.hidden({params : {url:'/ptools/plugins/enable'},component:component});
    },

    onButtonDisableClick: function(button, e, eOpts) {
        var me = this;
        var selected = this.getSelectionModel().selected.items;
        if(selected.length==1){
            Ext.Msg.confirm('确认','确定要停用此插件吗?',function(yes){
                if(yes){
                    Common.ajax({
                        message:'正在提交...',
                        url:ctxp+'/ptools/plugins/disable',
                        params:{id:selected[0].data.id},
                        callback:function(json,options,success,response){
                            try{
                                if(json.success){
                                    button.setDisabled(true);
                                    me.loadGrid();
                                }
                            }catch(e){}
                        }
                    });
                }
            });
        }
    },

    onButtonDisableAfterRender: function(component, eOpts) {
        this.btnDisable=component;
        Common.hidden({params : {url:'/ptools/plugins/disable'},component:component});
    },

    onDelButtonClick: function(button, e, eOpts) {
        Common.deleteSelectionIds(this,ctxp+'/ptools/plugins/remove');
    },

    onDelButtonDisableAfterRender: function(component, eOpts) {
        this.btnDel=component;
        Common.hidden({params : {url:'/ptools/plugins/remove'},component:component});
    },

    onGridpanelSelectionChange: function(model, selected, eOpts) {
        this.btnDel.setDisabled(selected.length === 0);
        if(selected.length === 0){
            return;
        }
        if( selected[0].data.status=='Normal'){
            this.btnEnable.setDisabled(true);
            this.btnDisable.setDisabled(false);
        }else{
            this.btnEnable.setDisabled(false);
            this.btnDisable.setDisabled(true);

        }
    },

    onGridpanelAfterRender: function(component, eOpts) {
            this.loadGrid();
    },

    loadGrid: function() {
        Common.loadStore({
            component:this,
            url:ctxp + '/ptools/plugins/list',
            fields: ['id', 'name', 'fileName','status','statusStr', 'versionNum','createTimeStr','updateTimeStr'],
            params:''
        });
    }

});