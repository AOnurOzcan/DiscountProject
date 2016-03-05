var User = require("../model/User");
var Notification = require("../model/Notification");

var UserNotification = project.db.define('UserNotification', {
  id: {type: 'serial'},
  isRead: Boolean
});

UserNotification.hasOne('User', User, {field: 'userId'});
UserNotification.hasOne('Notification', Notification, {field: 'notificationId'});

module.exports = UserNotification;

