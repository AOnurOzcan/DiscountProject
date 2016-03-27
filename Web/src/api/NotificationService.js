/**
 * Created by Onur Kuru on 21.3.2016.
 */

var GCM = require('node-gcm');
var User = require('../model/User');
var Notification = require('../model/Notification');
var NotificationProduct = require('../model/NotificationProduct');
var NotificationBranch = require('../model/NotificationBranch');
var UserNotification = require('../model/UserNotification');

////////////////////////////////// WEB /////////////////////////////////////

project.app.get("/notification/send/:id", function (req, res) {
  User.find({}, function (err, users) {
    var userRegistrationIds = [];
    var userIds = [];
    var message = new GCM.Message({
      data: {
        key1: req.params.id
      }
    });

    var sender = new GCM.Sender(project.config.get("GCMApiKey").key);

    if (err) {
      res.unknown();
    }


    users.forEach(function (user) {
      userRegistrationIds.push(user.registrationId);
      userIds.push(user.id);
    });

    sender.send(message, {registrationTokens: userRegistrationIds}, function (err, response) {
      if (err) {
        console.error(err);
      }
      else {
        Notification.get(req.params.id, function (err, notification) {
          notification.isSent = true;
          notification.sendDate = new Date();
          notification.peopleCount = userRegistrationIds.length;
          notification.save(function (err) {
            if (err) {
              return res.unknown();
            }
            userIds.asyncForEach(function (userId, done) {
              UserNotification.create({
                notificationId: notification.id,
                userId: userId
              }, function (err) {
                if (err) {
                  return res.unknown();
                }
                done();
              });
            }, function () {
              res.json({"success": true});
            });
          });
        });
      }
    });
  });
});

project.app.get("/notification/:id", function (req, res) {
  var notificationId = req.params.id;
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

// Bildirim ekleme servisi
project.app.post("/notification", function (req, res) {
  var products = req.body.products;
  var branches = req.body.branches;
  var notification = {};
  notification.name = req.body.name;
  notification.startDate = req.body.startDate;
  notification.endDate = req.body.endDate;
  notification.description = req.body.description;
  notification.companyId = req.session.admin.companyId;

  Notification.create(notification, function (err, savedNotification) {
    if (err) {
      return res.unknown();
    }
    products.asyncForEach(function (product, done) {
      NotificationProduct.create({
        notificationId: savedNotification.id,
        productId: product
      }, function (err) {
        if (err) {
          return res.unknown();
        }
        done();
      });
    }, function () {
      branches.asyncForEach(function (branch, done) {
        NotificationBranch.create({notificationId: savedNotification.id, branchId: branch}, function (err) {
          if (err) {
            return res.unknown();
          }
          done();
        });
      }, function () {
        res.json({status: "success"});
      });
    });
  });
});

//Bildirim düzenleme servisi
project.app.put("/notification/:id", function (req, res) {
  var addProductArray = req.body.addProductArray;
  var removeProductArray = req.body.removeProductArray;
  var addBranchArray = req.body.addBranchArray;
  var removeBranchArray = req.body.removeBranchArray;
  Notification.get(req.params.id, function (err, notification) {
    notification.name = req.body.name;
    notification.startDate = req.body.startDate;
    notification.endDate = req.body.endDate;
    notification.description = req.body.description;
    notification.save(function (err) {
      if (err) {
        return res.unknown();
      }
      addProductArray.asyncForEach(function (productId, done) {
        NotificationProduct.create({notificationId: notification.id, productId: productId}, function (err) {
          if (err) {
            return res.unknown();
          }
          done();
        });
      }, function () {
        removeProductArray.asyncForEach(function (productId, done) {
          NotificationProduct.find({notificationId: notification.id, productId: productId}).remove(function (err) {
            if (err) {
              return res.unknown();
            }
            done();
          });
        }, function () {
          addBranchArray.asyncForEach(function (branchId, done) {
            NotificationBranch.create({notificationId: notification.id, branchId: branchId}, function (err) {
              if (err) {
                return res.unknown();
              }
              done();
            });
          }, function () {
            removeBranchArray.asyncForEach(function (branchId, done) {
              NotificationBranch.find({notificationId: notification.id, branchId: branchId}).remove(function (err) {
                if (err) {
                  return res.unknown();
                }
                done();
              });
            }, function () {
              res.json({status: "success"});
            });
          });
        });
      });
    });

  });
});

//Bildirim silme servisi
project.app.delete("/notification/:id", function (req, res) {
  var notificationId = req.params.id;
  NotificationProduct.find({notificationId: notificationId}).remove(function (err) {
    if (err) {
      return res.unknown();
    }
    NotificationBranch.find({notificationId: notificationId}).remove(function (err) {
      if (err) {
        return res.unknown();
      }
      Notification.find({id: notificationId}).remove(function (err) {
        if (err) {
          return res.unknown();
        }
        res.json({status: "success"});
      });
    });
  });
});

//Tüm gönderilmemiş olan bildirimleri getir.
project.app.get("/getNotifications/:isSent", function (req, res) {
  var isSent = req.params.isSent;
  Notification.find({isSent: isSent}, function (err, notifications) {
    notifications.asyncForEach(function (notification, done) {
      NotificationProduct.find({notificationId: notification.id}, function (err, notificationProduct) {
        if (err) {
          return res.unknown();
        }
        notification.products = [];
        notificationProduct.forEach(function (notProduct) {
          notification.products.push(notProduct.Product);
        });
        done();
      });
    }, function () {
      notifications.asyncForEach(function (notification, done) {
        NotificationBranch.find({notificationId: notification.id}, function (err, notificationBranch) {
          if (err) {
            return res.unknown();
          }
          notification.branches = [];
          notificationBranch.forEach(function (notBranch) {
            notification.branches.push(notBranch.Branch);
          });
          done();
        });
      }, function () {
        res.json(notifications);
      });
    });
  });
});

/////////////////////////////////// MOBILE /////////////////////////////////////

project.app.get("/notification", function (req, res) {

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