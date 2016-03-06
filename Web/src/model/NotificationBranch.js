var Notification = require("../model/Notification");
var Branch = require("../model/Branch");

var NotificationBranch = project.db.define('NotificationBranch', {
  id: {type: 'serial'}
});

NotificationBranch.hasOne('Notification', Notification, {field: 'notificationId'});
NotificationBranch.hasOne('Branch', Branch, {field: 'branchId'});

module.exports = NotificationBranch;