/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.exec.ExecController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HtrGui.common.Definitions',
        'HtrGui.view.exec.grid.IbOrdersEventsGrid'
    ],

    alias: 'controller.htr-exec',

    init: function () {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            accountsGrid = me.lookupReference('accountsGrid');

        Ext.Ajax.request({
            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/texts',
            success: function(response, opts) {
                me.ibOrderStatusTexts = Ext.decode(response.responseText);
                Ext.Ajax.request({
                    url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/iborderstatus/colors',
                    success: function(response, opts) {
                        me.ibOrderStatusColors = Ext.decode(response.responseText);
                        Ext.Ajax.request({
                            url: HtrGui.common.Definitions.urlPrefixExec + '/codemap/strategymode/colors',
                            success: function(response, opts) {
                                me.strategyModeColors = Ext.decode(response.responseText);
                                if (ibAccounts) {
                                    ibAccounts.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixExec + '/ibaccounts');
                                    ibAccounts.load(function (records, operation, success) {
                                        if (success) {
                                            console.log('loaded ibAccounts')
                                            accountsGrid.setSelection(ibAccounts.first());
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    },

    onAccountSelect: function(grid, record, index, eOpts) {
        var me = this,
            ibOrders = me.getStore('ibOrders'),
            ordersPaging = me.lookupReference('ordersPaging');

        me.ibAccountId = record.data.accountId;
        ibOrders.getProxy().setUrl(HtrGui.common.Definitions.urlPrefixExec + '/ibaccounts/' + me.ibAccountId  + '/iborders');

        if (ordersPaging.getStore().isLoaded()) {
            ordersPaging.moveFirst();
        } else {
            ibOrders.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded ibOrders for ibAccountId=' + me.ibAccountId)
                }
            });
        }
    },

    showEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.eventsGrid) {
            me.eventsGrid =  Ext.create('HanGui.view.exec.grid.IbOrderEventsGrid');
            me.eventsWindow = Ext.create('widget.htr-exec-events-window');
            me.eventsWindow.add(me.eventsGrid);
        }
        var permId = record.get(record.getFields()[1].getName());
        me.eventsGrid.setStore(record.ibOrderEvents());
        me.eventsWindow.setTitle("IB Order Events, permId=" + permId);
        me.eventsWindow.show();
    },

    ibOrderStatusRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'cursor: pointer; background-color: ' + me.ibOrderStatusColors[val] + '; color: white;';
        return me.ibOrderStatusTexts[val];
    },

    strategyRenderer: function(val, metadata, record) {
        var me = this;

        metadata.style = 'color: ' + me.strategyModeColors[val];
        return record.data['strategyId'] + '/' + val;
    },

    connectStatusRenderer: function(val, metadata, record) {
        if (metadata) {
            metadata.style = 'background-color: ' + (val ? 'green' : 'red') + '; color: white;';
        }
        return (val ? 'conn' : 'disconn');
    },

    connectIb: function(grid, rowIndex, colIndex) {
        this.connect(grid, rowIndex, colIndex, true);
    },

    disconnectIb: function(grid, rowIndex, colIndex) {
        this.connect(grid, rowIndex, colIndex, false);
    },

    connect: function(grid, rowIndex, colIndex, con) {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            accountId = grid.getStore().getAt(rowIndex).get('accountId'),
        box = Ext.MessageBox.wait(((con ? 'Connecting' : 'Disconnecting') + ' IB account ' + accountId), 'Action in progress');

        Ext.Ajax.request({
            method: 'PUT',
            url: HtrGui.common.Definitions.urlPrefixExec + '/ibaccounts/' + accountId + '/connect/' + (con ? 'true' : 'false'),
            success: function(response) {
                box.hide();
                grid.getStore().reload();
            },
            failure: function() {
                box.hide();
            }
        });
    }
});