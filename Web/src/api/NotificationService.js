/**
 * Created by Onur Kuru on 21.3.2016.
 */

var GCM = require('node-gcm');
var User = require('../model/User');
var Notification = require('../model/Notification');
var NotificationProduct = require('../model/NotificationProduct');
var NotificationBranch = require('../model/NotificationBranch');
var UserNotification = require('../model/UserNotification');

project.app.get("/notification/send", function (req, res) {

  User.find({}, function (err, users) {
    var userRegistrationIds = [];
    var message = new GCM.Message({
      data: {
        key1: 'message1',
        key2: 'message2'
      }
    });

    var sender = new GCM.Sender(project.config.get("GCMApiKey").key);

    if (err) {
      res.unknown();
    }


    users.forEach(function (user) {
      userRegistrationIds.push(user.registrationId);
    });

    sender.send(message, {registrationTokens: userRegistrationIds}, function (err, response) {
      if (err)
        console.error(err);
      else
        res.json({"success": true});
    });
  });
});

project.app.get("/notification/get/:notificationId", function (req, res) {
  var notificationId = req.params.notificationId;
  Notification.one({id: notificationId}, function (err, notification) {

    notification.productList = [];
    notification.branchList = [];

    if (err) {
      return res.unknown();
    }

    NotificationProduct.find({notificationId: notificationId}, function (err, products) {
      if (err) {
        return res.unknown();
      }
      products.forEach(function (product) {
        product.Product.categoryId = null;
        product.Product.companyId = null;
        notification.productList.push(product.Product);
      });

      NotificationBranch.find({notificationId: notificationId}, function (err, branchs) {
        if (err) {
          return res.unknown();
        }

        branchs.forEach(function (branch) {
          branch.Branch.companyId = null;
          branch.Branch.cityId = null;
          notification.branchList.push(branch.Branch);
        });

        res.json(notification);
      });
    });
  });
});

project.app.get("/notification/getall", function (req, res) {

  project.util.AuthorizedRouteForUser(req, res, function (userId) {

    UserNotification.find({userId: userId}, function (err, notifications) {
      if (err) {
        return res.unknown();
      }

      notifications.forEach(function (notification) {
        notification.userId = null;
        notification.Notification.branchList = null;
        notification.Notification.productList = null;
        notification.notificationId = notification.Notification;
        delete notification['Notification'];
      });

      res.json(notifications);
    });
  });
});