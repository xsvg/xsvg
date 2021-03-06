/*
 * File: app/view/SysConfigLogPanel.js
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

Ext.define('platform.tools.view.SysConfigLogPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.SysConfigLogPanel',

    requires: [
        'Ext.toolbar.Toolbar',
        'Ext.button.Button',
        'Ext.grid.View',
        'Ext.grid.column.Column'
    ],

    autoScroll: true,
    title: '重要参数配置',
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
                            iconCls: 'icon-edit',
                            text: '设置',
                            listeners: {
                                click: {
                                    fn: me.onBtnSetClick,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            iconCls: 'icon-reset',
                            text: '还原',
                            listeners: {
                                click: {
                                    fn: me.onBtnRestoreClick,
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
                    minWidth: 150,
                    width: 150,
                    dataIndex: 'createTimeStr',
                    text: '设置时间'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'pwdParameter',
                    text: '参数1'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'bitConfByPWD',
                    text: '参数2'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'bitConfByFinger',
                    text: '参数3'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'lockModel',
                    text: '参数4'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'openLockNum',
                    text: '参数5'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'openLockDate',
                    text: '参数6'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'mutiOpenLock',
                    text: '参数7'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'lockWaveAlarm',
                    text: '参数8'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'lockLockedState',
                    text: '参数9'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'lockHighTempAlarm',
                    text: '参数10'
                }
            ],
            listeners: {
                afterrender: {
                    fn: me.onGridpanelAfterRender,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onBtnSetClick: function(button, e, eOpts) {
        try
        {
            var me = this;
            var formwin = Ext.create('platform.tools.view.SysConfigLogWindow');
            formwin.addListener('close', function(panel,opts)
                                {
                                    me.loadGrid();
                                });
            formwin.show();
        }
        catch(error)
        {
            Common.show({title:'信息提示',html:error.toString()});
        }
    },

    onBtnRestoreClick: function(button, e, eOpts) {
        var  me =  this.grid;
        var selected = me.getSelectionModel().selected;

        if(selected.items.length <1){
            Common.alert({msg:'请选择一条记录!'});
            return;
        }

        var data = selected.items[0].data;
        Ext.Msg.confirm("确认提示", '确认要恢复到此配置值吗?', function(button)
                        {
                            if (button == "yes")
                            {
                                var formwin = Ext.create('platform.tools.view.SysConfigLogWindow');

                                formwin.addListener('close', function(panel,opts)
                                                    {
                                                        me.loadGrid();
                                                    });
                                formwin.show();
                                formwin.loadForm(data);
                            }
                        });
    },

    onGridpanelAfterRender: function(component, eOpts) {
        this.grid = component;
        this.loadGrid();
    },

    loadGrid: function() {
        try{
            Common.loadStore({
                component:this,
                url:ctxp + '/system/config/log/list',
                fields: ['pwdParameter', 'lockModel', 'bitConfByPWD','bitConfByFinger','mutiOpenLock','openLockNum','openLockDate','createTimeStr','lockWaveAlarm','lockLockedState','lockHighTempAlarm'],
                params:''
            });
        }catch(error){
            alert(error.toString());
        }
    }

});