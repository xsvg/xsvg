/*
 * File: app/view/ComboBoxTree.js
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

Ext.define('platform.tools.view.ComboBoxTree', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.ComboBoxTree',

    selected: '',
    anchor: '100%',
    fieldLabel: 'Label',
    editable: false,
    queryMode: 'local',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            fields: [
                'id',
                'text',
                'checked'
            ]
        });

        me.processComboBoxTree(me);
        me.callParent(arguments);
    },

    processComboBoxTree: function(config) {
        var me = this;
        me.treeid = Ext.String.format('tree-combobox-{0}', Ext.id());
        me.tpl = Ext.String.format('<div id="{0}"></div>', me.treeid);
        if (!Ext.isEmpty(me.url)) {
            me.tree = Ext.create('Ext.tree.Panel', {
                rootVisible : false,
                border : false,
                autoScroll : true,
                height : 200,
                anchor: '100%',
                displayField:me.displayField
            });
            Common.bindTree({treePanel:me.tree,url:me.url, pid:'',fields:me.fields,params:me.params});
            me.tree.on('itemclick', function(view, record) {
                me.selected = record.data;
                me.setValue(record.get(me.displayField));
                me.collapse();
            });
            me.on('expand', function() {
                me.renderTree();
            });
            me.tree.on('itemexpand',function(){
                var record = me.tree.getStore().getNodeById(me.getValue());
                if(record){
                    me.selected = record.data;
                    me.setValue(record.get(me.displayField));
                    me.tree.getSelectionModel().select(record);
                }
            });
            Ext.defer(function(){me.tree.expandAll();}, 300);
        }
    },

    getValue: function() {
        if(Ext.isEmpty(this.selected)){
            return this.value;
        }
        return this.selected.id;
    },

    renderTree: function() {
                try{
                    var me = this;
                    if (!me.tree.rendered) {
                        me.tree.render(me.treeid);
                        me.tree.expandAll();
                    }
                    var record = me.tree.getStore().getNodeById(me.getValue());
                    if(record){
                        me.selected = record.data;
                        me.setValue(record.get(me.displayField));
                        me.tree.getSelectionModel().select(record);
                    }
                }catch(error){}
    }

});