/**
 * Created by Onur Kuru on 21.3.2016.
 */

var request = require('request');
var User = require('../model/User');
var Notification = require('../model/Notification');
var NotificationProduct = require('../model/NotificationProduct');
var NotificationBranch = require('../model/NotificationBranch');
var UserNotification = require('../model/UserNotification');
var Product = require('../model/Product');
var Preference = require('../model/Preference');
var UserProduct = require('../model/UserProduct');

////////////////////////////////// WEB /////////////////////////////////////

project.app.get("/notification/send/:id", function (req, res) {
  var companyId = req.session.admin.companyId;
  Notification.get(req.params.id, function (err, notification) {
    if (err) {
      return res.unknown();
    }
    /**
     * temel olarak bildirim içindeki ürünlerin kategorisine göre preferences tablosundan kullanıcıların takip ettiği kategori ve firma bilgilerine göre ilgili kullanıcılar getirilir.
     * kullanıcı getirirken kullanılan diğer parametreler bildirimdeki şubelerın bulunduğu şehir ile kullanıcıların şehirlerinin aynı olması
     * kullanıcıların tokenkey alanının boş olmaması(oturum açık) ve firma bildirimlerini açık olması
     */
    project.db.driver.execQuery(
      "SELECT * FROM User WHERE id IN(SELECT userId FROM Preference WHERE categoryId " +
      "IN(SELECT categoryId FROM Product INNER JOIN NotificationProduct ON Product.id = NotificationProduct.productId " +
      "INNER JOIN Notification ON NotificationProduct.notificationId = Notification.id WHERE Notification.id = ?) AND companyId = ?) " +
      "AND cityId IN(SELECT cityId FROM Branch INNER JOIN NotificationBranch ON Branch.id = NotificationBranch.branchId WHERE NotificationBranch.notificationId = ?) " +
      "AND tokenKey != '' AND notificationOpen = 1",
      [notification.id, companyId, notification.id],
      function (err, users) {

        if (err) {
          return res.unknown();
        }
        if (users.length == 0) {
          return res.error({error: "Gönderilecek Kullanıcı Bulunamadı"})
        }

        var userRegistrationIds = [];
        var userIds = [];
        var serverKey = "AAAAQQoOat4:APA91bFYhQXju60GjZCy8QN8PC0Nomuk4ppqUbH1SwPfcbMspEuc3rm1YyknWgYTkS5ELcLhBua-2Ns357LOk2AxRQQ4aa2Aa_w9XQ4_IUvq8NgmZWLdlWhoKfu6-9XbgPJ8WXmNzQVJ";

        if (err) {
          res.unknown();
        }
        users.forEach(function (user) {
          userRegistrationIds.push(user.registrationId);
          userIds.push(user.id);
        });

        var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera)
          registration_ids: userRegistrationIds,
          data: {
            id: notification.id,
            title: notification.name,
            content: notification.description
          }
        };

        request.post({
          headers: {
            'Authorization': 'key=' + serverKey,
            'Content-Type': 'application/json'
          },
          url: "https://fcm.googleapis.com/fcm/send",
          method: 'POST',
          body: JSON.stringify(message)
        }, function (err, response, body) {
          if (err) {
            console.log(err);
            return res.unknown();
          }

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
      if (err) return res.unknown();

      addProductArray.asyncForEach(function (productId, done) {
        NotificationProduct.create({notificationId: notification.id, productId: productId}, function (err) {
          if (err) return res.unknown();

          done();
        });
      }, function () {
        removeProductArray.asyncForEach(function (productId, done) {
          NotificationProduct.find({notificationId: notification.id, productId: productId}).remove(function (err) {
            if (err) return res.unknown();

            done();
          });
        }, function () {
          addBranchArray.asyncForEach(function (branchId, done) {
            NotificationBranch.create({notificationId: notification.id, branchId: branchId}, function (err) {
              if (err) return res.unknown();

              done();
            });
          }, function () {
            removeBranchArray.asyncForEach(function (branchId, done) {
              NotificationBranch.find({notificationId: notification.id, branchId: branchId}).remove(function (err) {
                if (err) return res.unknown();

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

//Verilen parametreye göre gönderilmemiş ya da gönderilmemiş olan tüm bildirimleri getir.
project.app.get("/getNotifications/:isSent", function (req, res) {
  var isSent = req.params.isSent;
  Notification.find({isSent: isSent, companyId: req.session.admin.companyId}, function (err, notifications) {
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

project.app.get("/user/notification", function (req, res) {

  project.util.AuthorizedRouteForUser(req, res, function (userId) {

    var start = req.query.startIndex;
    var length = req.query.notificationLength;

    /**
     * kullanıcı bildirimleri ara tablosu ile bildirimler tablosu birleştirilir ve sendDate alanına göre DESC olarak sıralanarak verilen limite göre ara tablo alanlarını getirir
     */
    project.db.driver.execQuery(
      "SELECT UserNotification.id, UserNotification.isRead, UserNotification.userId, UserNotification.notificationId FROM " +
      "UserNotification INNER JOIN Notification ON UserNotification.notificationId = Notification.id" +
      " WHERE userId =? ORDER BY Notification.sendDate DESC LIMIT ?,?",
      [userId, parseInt(start), parseInt(length)],
      function (err, userNotifications) {
        if (err) {
          return res.unknown();
        }

        userNotifications.asyncForEach(function (userNotification, done) {
          Notification.get(userNotification.notificationId, function (err, notification) {
            if (err) {
              return res.unknown();
            }
            notification.branchList = null;
            notification.productList = null;
            userNotification.notificationId = notification;
            userNotification.userId = null;

            if (userNotification.isRead == 0) {
              userNotification.isRead = false;
            } else {
              userNotification.isRead = true;
            }
            done();
          });
        }, function () {
          return res.json(userNotifications);
        });
      });
  });
});

project.app.get("/user/notification/:id", function (req, res) {
  var notificationId = req.params.id;
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
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


        products.asyncForEach(function (product, done) {
          product.Product.categoryId = null;
          product.Product.companyId = null;
          UserProduct.one({userId: userId, productId: product.Product.id}, function (err, result) {
            if (err) {
              return res.unknown();
            }

            if (result != null) {
              result.userId = null;
              result.productId = null;
            }

            product.Product.follower = result;
            notification.productList.push(product.Product);

            done();
          });
        }, function () {
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
  });
});

project.app.get("/notification/updatestatus/:id", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var notificationId = req.params.id;
    UserNotification.one({userId: userId, notificationId: notificationId}, function (err, userNotification) {
      if (err) {
        return res.unknown();
      }
      if (userNotification != null) {
        userNotification.isRead = true;
        userNotification.save(function (err, result) {
          if (err) {
            return res.unknown();
          }
          return res.json({result: 'success'});
        });
      }
    });
  });
});