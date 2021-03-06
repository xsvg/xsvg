/*
 * File: app/view/OrgImportGridPanel.js
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

Ext.define('platform.system.view.OrgImportGridPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.orgimportgridpanel',

    requires: [
        'Ext.grid.column.Column',
        'Ext.grid.View'
    ],

    maxHeight: 350,
    width: 650,
    autoScroll: true,
    bodyBorder: false,
    title: '',
    columnLines: true,
    forceFit: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'code',
                    text: '机构编码'
                },
                {
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'name',
                    text: '机构名称'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'parentName',
                    text: '上级机构编码'
                },
                {
                    xtype: 'gridcolumn',
                    defaultWidth: 40,
                    dataIndex: 'memo',
                    text: '备注'
                },
                {
                    xtype: 'gridcolumn',
                    width: 250,
                    dataIndex: 'createUsername',
                    text: '错误信息'
                }
            ],
            viewConfig: {
                listeners: {
                    afterrender: {
                        fn: me.onOrgImportGridViewAfterRender,
                        scope: me
                    }
                }
            }
        });

        me.callParent(arguments);
    },

    onOrgImportGridViewAfterRender: function(component, eOpts) {
        this.orgImportGridView=component;
    },

    loadData: function(result) {
        var me = this;
        var OrgStore = Ext.create('Ext.data.Store', {
                    storeId:'OrgStore',
                    fields: ['code','name','parentName','memo','createUsername'],
                    data : result,
                    proxy:
                    {
                        type: 'memory',
                        reader:{
                            type: 'json'
                        }
                    }
                });
          me.bindStore(OrgStore);


    }

});