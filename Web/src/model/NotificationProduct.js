var Notification = require("../model/Notification");
var Product = require("../model/Product");

var NotificationProduct = project.db.define('NotificationProduct', {
  id: {type: 'serial'}
});

NotificationProduct.hasOne('Notification', Notification, {field: 'notificationId'});
NotificationProduct.hasOne('Product', Product, {field: 'productId', autoFetch: true});

module.exports = NotificationProduct;