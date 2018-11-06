/*
 * File: classes/view/NoticeWindow.js
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

Ext.define('Gotom.view.NoticeWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.NoticeWindow',

    requires: [
        'Ext.panel.Panel'
    ],

    draggable: false,
    height: 200,
    padding: 2,
    width: 300,
    shadow: false,
    layout: 'border',
    animCollapse: true,
    closeAction: 'hide',
    collapseDirection: 'bottom',
    collapsible: true,
    title: '消息通知',
    titleAlign: 'center',
    titleCollapse: false,
    plain: true,
	maximizable: true,
    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'panel',
                    region: 'center',
                    border: false,
                    margin: 0,
                    padding: 0,
                    autoScroll: true,
                    header: false,
                    title: '内容',
                    listeners: {
                        afterrender: {
                            fn: me.onPanelAfterRender,
                            scope: me
                        }
                    }
                }
            ],
            listeners: {
                afterrender: {
                    fn: me.onWindowAfterRender,
                    single: true,
                    scope: me
                },
                render: {
                    fn: me.onWindowRender,
                    scope: me
                },				
                maximize: {
                    fn: me.onWindowMaximize,
                    scope: me
                },                
                collapse: {
                    fn: me.onWindowCollapse,
                    scope: me
                },
                expand: {
                    fn: me.onWindowExpand,
                    scope: me
                }				
            }
        });

        me.callParent(arguments);
    },

    onPanelAfterRender: function(component, eOpts) {
        this.noticePanel = component;
    },
    
    onWindowRender: function(window, eOpts) {
		var me = this;
    	window.body.on('click', function() 
			{ 
				me.autoClose = false;
			}
		);
    },
	
    onWindowMaximize: function(window, eOpts) {
		var me = this;
    	me.autoClose = false;
    },
    
    onWindowAfterRender: function(component, eOpts) {
    	var me = this;
        this.resetPosition();
        Ext.EventManager.onWindowResize(this.resetPosition, this);
        me.autoClose = true;
        Ext.defer(function(){me.onWindowClose();}, 10000);
    },
    
    onWindowClose: function() {
		var me = this;
    	if(me.autoClose){
    		this.close();
    	}
    },
    
    onWindowCollapse: function(p, eOpts) {
        this.resetPosition();
    },

    onWindowExpand: function(p, eOpts) {
        this.resetPosition();
    },

    resetPosition: function() {
        try{
            var width = this.width;
            var height = this.height;
            if(this.collapsed === 'bottom' || this.collapsed === 'top')
            {
                height = 25;
            }
            if(this.collapsed === 'right' || this.collapsed === 'left')
            {
                width = 25;
            }
            var body = Ext.getBody();
            var left = body.getWidth()-4-width;
            var top = body.getHeight()-4-height;
            this.setPosition(left, top);
        }catch(e){
            Ext.Msg.alert('title',e);
        }
    },

    loadData: function(html, title) {
        var me = this;
        if(this.collapsed !== false)
        {
            me.expand();
        }
        if(me.hidden)
        {
            me.show();
        }
        me.resetPosition();
        me.setTitle(title);
        me.noticePanel.update(html);
    }

});