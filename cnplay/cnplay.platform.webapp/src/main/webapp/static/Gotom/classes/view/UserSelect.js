Ext.define('Gotom.view.UserSelect', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.UserSelect',

    requires: [
        'Ext.grid.column.Column',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel'
    ],

    height: 250,
    width: 400,
    title: '',
    forceFit: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    width: 150,
                    dataIndex: 'name',
                    text: '姓名'
                },
                {
                    xtype: 'gridcolumn',
                    width: 200,
                    dataIndex: 'orgName',
                    text: '组织名称'
                }
            ],
            selModel: Ext.create('Ext.selection.CheckboxModel', {
                mode: 'SINGLE'
            })
        });

        me.callParent(arguments);
    },

    loadData: function(username) {

        var me = this;
        Common.ajax({
            component : me,
            params:{'username':username},
            message : '正在加载......',
            url : ctxp+'/manage/authority/findUserByName',
            callback : function(result)
            {
                var UserStore = Ext.create('Ext.data.Store', {
                    storeId:'UserStore',
                    fields: ['id','name','orgName'],
                    data : result.rows,
                    proxy:
                    {
                        type: 'memory',
                        reader:{
                            type: 'json'
                        }
                    },
        					listeners:{
        						load:{
        							fn:function(gridStore, records, successful, eOpts){

        								if(me.toUserId){
        									for(var i=0;i<gridStore.getCount();i++){
        										var record = gridStore.getAt(i);
        										if(record.data.id ===me.toUserId){
        											me.getSelectionModel().select(record);
        										}
        									}
        								}

        							}
        						}
        					}
                });
                me.bindStore(UserStore);
            }
        });

    }

});