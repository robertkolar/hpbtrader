/**
 * Created by robertk on 4.4.2016.
 */
Ext.define('MktData.model.mktdata.Base', {
    extend: 'Ext.data.Model',

    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'}
    ],
    schema: {
        id: 'mktdataSchema',
        namespace: 'MktData.model.mktdata',  // generate auto entityName,
        proxy: {
            type: 'ajax',
            actionMethods: {
                read: 'GET',
                update: 'PUT'
            },
            reader: {
                type: 'json',
                rootProperty: 'items',
                totalProperty: 'total'
            },
            writer: {
                type: 'json',
                writeAllFields: true,
                writeRecordId: true
            },
            listeners: {
                exception: function(proxy, response, operation) {
                    //MktData.common.Util.showErrorMsg(response.responseText);
                }
            }
        }
    }
});