/**
 * Created by robertk on 25.7.2016.
 */
Ext.define('HtrGui.view.strategy.window.IbOrderEventsWindow', {
    extend: 'Ext.window.Window',
    xtype: 'htr-strategy-iborderevents-window',

    title: 'IB Order Events',
    width: 410,
    scrollable: true,
    maximizable: false,
    modal: false,
    closeAction: 'hide'
});