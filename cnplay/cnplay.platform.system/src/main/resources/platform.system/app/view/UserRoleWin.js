/*
 * File: app/view/UserRoleWin.js
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

Ext.define('platform.system.view.UserRoleWin', {
    extend: 'Ext.window.Window',
    alias: 'widget.UserRoleWin',

    requires: [
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    height: 321,
    width: 432,
    layout: 'border',
    title: '设置用户角色',
    modal: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            listeners: {
                afterrender: {
                    fn: me.onWindowAfterRender,
                    scope: me
                }
            },
            items: [
                {
                    xtype: 'treepanel',
                    region: 'center',
                    split: true,
                    forceFit: true,
                    rootVisible: false,
                    viewConfig: {
                        autoScroll: true
                    },
                    columns: [
                        {
                            xtype: 'treecolumn',
                            width: 185,
                            dataIndex: 'name',
                            text: '角色名称'
                        }
                    ],
                    listeners: {
                        afterrender: {
                            fn: me.onRoleAfterRender,
                            scope: me
                        },
                        itemclick: {
                            fn: me.onTreepanelItemClick,
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

    onWindowAfterRender: function(component, eOpts) {
        //this.gridPanel = component;
    },

    onRoleAfterRender: function(component, eOpts) {
        this.roleTree = component;
        this.loadRole();
    },

    onTreepanelItemClick: function(dataview, record, item, index, e, eOpts) {
        record.set('checked', !record.data.checked);
    },

    onSaveClick: function(button, e, eOpts) {
        var me = this;
        //var form = Ext.getCmp('RoleForm');
        try{

            var roles = [];
            var rights = [];
            var items = me.roleTree.getSelectionModel().store.data.items;
            Ext.each(items, function()
                     {
                         var nd = this;
                         if(nd.data.checked)
                         {
                             roles.push(nd.data.id);
                         }
                     });

            //if(roles.length<1){
            //   Ext.Msg.alert('信息提示', '角色不能为空！');
            //    return;
            // }

            //var rightItems = rightTree.getSelectionModel().store.data.items;
            // Ext.each(rightItems, function()
            //   {
            //  var nd = this;
            //  if(nd.data.checked)
            //  {
            //    rights.push(nd.data.id);
            // }
            // });
            Ext.Msg.confirm('确认','确定修改吗?',function(button){
                if (button == 'yes'){
                    Common.ajax({
                        component:me,
                        message:'正在提交...',
                        url:ctxp+'/system/user/updateRoleRight',
                        params:{userid:me.userid,
                                roleids:roles},//,
                        //rightids:rights},
                        callback:function(json,options,success,response){
                            try{
                                if(json.success){
                                    Ext.Msg.alert('信息提示', '用户角色设置成功！');
                                    me.close();
                                }else{
                                    Ext.Msg.alert('错误',json.msg);
                                }
                            }catch(e){}
                        }
                    });
                }
            });


        }catch(error){
            alert(error);
        }
    },

    onCancelClick: function(button, e, eOpts) {
        var me = this;
        me.close();
    },

    loadData: function(userid) {

        var me = this;
        me.userid=userid;

    },

    loadRole: function() {
        /*
        var fields = [{name:'name'}];
        var treeStore = Common.createTreeStore(ctxp + '/system/user/findRoleByUser','',fields);
        this.roleTree.bindStore(treeStore);
        this.roleTree.getStore().reload();
        */
        Common.bindTree({
                       pid:'',
                       fields:['id','name'],
                       params:{'userid':this.userid},
                       url : ctxp+'/system/user/findRoleByUser',
                       treePanel:this.roleTree
        });
    },

    loadRight: function() {
        /*
        var fields = [{name:'text'}];
        var treeStore = Common.createTreeStore(ctxp + '/system/menu/list','',fields);
        this.rightTree.bindStore(treeStore);
        this.rightTree.getStore().reload();
        */
        Common.bindTree({
               pid:'',
               fields:['id','text'],
               url : ctxp+'/system/user/findRightByUser?userid='+this.userid,
               treePanel:this.rightTree
        });
    }

});