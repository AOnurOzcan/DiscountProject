/**
 * Created by Onur Kuru on 6.3.2016.
 */
var City = require('../model/City');
var User = require('../model/User');
var Preference = require('../model/Preference');
var randtoken = require('rand-token');
var UserProduct = require('../model/UserProduct');
var NotificationProduct = require('../model/NotificationProduct');
var NotificationBranch = require('../model/NotificationBranch');
var Notification = require('../model/Notification');

//Kullanıcı profili oluşturmak kullanılıyor
project.app.post("/user/profile/create", function (req, res) {
  var user = req.body;

  City.find({cityName: user.cityId.cityName}, 1, function (err, result) {
    if (err) {
      res.send('error');
    } else {
      user.cityId = result[0].id;
      user.birthday = project.util.ParseDate(user.birthday);
      user.tokenKey = randtoken.generate(20);
      User.create(user, function (err, userResult) {

        if (err) {
          res.send(err);
        }
        res.json(userResult.tokenKey);
      });
    }
  });
});

//Kullanıcı profilini düzenlemede kullanılır.
project.app.put("/user/profile/edit", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    User.get(userId, function (err, user) {

      if (err) {
        return res.unknown();
      }

      user.firstName = req.body.firstName;
      user.lastName = req.body.lastName;
      user.gender = req.body.gender;
      user.birthday = project.util.ParseDate(req.body.birthday);
      user.cityId = req.body.cityId.id;
      user.notificationOpen = req.body.notificationOpen;

      delete user['City'];
      user.save(function (err, savedUser) {
        if (err) {
          return res.unknown();
        }
        res.json({success: true});
      });
    });
  });
});

//Kullanıcı profili getirmede kullanılıyor
project.app.get("/user/profile/get", function (req, res) {

  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    User.get(userId, function (err, user) {
      if (err) {
        res.unknown();
      }

      user.id = null;
      user.tokenKey = null;
      user.registrationId = null;
      user.cityId = user.City;
      res.json(user);
    });
  });
});

//Kullanıcı ya yeni tercih eklemede kullanılıyor
project.app.post("/user/preference/create", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var preferences = req.body;
    preferences.forEach(function (preference) {
      preference.categoryId = preference.categoryId.id;
      preference.companyId = preference.companyId.id;
      preference.userId = userId;
      if (preference.id != undefined) {
        delete preference['id'];
      }
    });

    Preference.create(preferences, function (err, result) {
      if (err) {
        res.unknown();
      }

      res.json('true');
    });
  });
});

//Kullanıcının bir tercihini silmede kullanılıyor
project.app.post("/user/preference/delete", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var preferences = req.body;
    var deleteIdList = [];
    preferences.forEach(function (preference) {
      deleteIdList.push(preference.id);
    });

    Preference.find({id: deleteIdList}, function (err, preferences) {
      if (err) {
        res.unknown();
      }

      preferences.forEach(function (preference) {
        preference.remove(function (err) {
          if (err) {
            res.unknown();
          }
        });
      });
      res.json('true');

    });
  });
});

//Kullanıcının tüm tercihlerini getirmede kullanılır
project.app.get("/user/preference/all", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    Preference.find({userId: userId}, function (err, preferences) {
      if (err) {
        res.unknown();
      }

      preferences.forEach(function (preference) {
        preference.userId = null;
        preference.Category.parentCategory = null;
        preference.categoryId = preference.Category;
        preference.companyId = preference.Company;

        delete preference['Category'];
        delete preference['Company'];
      });
      res.json(preferences);

    });
  });
});

//Kullanıcının listesine eklediği ürünleri getirir
project.app.get("/user/products/all", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    UserProduct.find({userId: userId}, function (err, userProducts) {
      if (err) {
        return res.unknown();
      }

      userProducts.asyncForEach(function (userProduct, done) {
        userProduct.getProduct(function (err, product) {
          if (err) {
            return res.unknown();
          }
          product.categoryId = null;
          product.companyId = null;
          userProduct.productId = product;
          userProduct.userId = null;
          delete userProduct['product'];
          done();
        });
      }, function () {
        res.json(userProducts);
      });
    });
  });
});

//Kullanıcının listesine yeni ürün ekler
project.app.post("/user/product/create", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var reqValues = req.body;
    var userProduct = {userId: userId, productId: reqValues.productId.id};
    UserProduct.create(userProduct, function (err, result) {
      if (err) {
        return res.unknown();
      }
      res.json(result.id);
    });
  });
});

//Kullanıcının listesinden yeni ürün siler
project.app.delete("/user/product/delete/:id", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var deleteId = req.params.id;
    UserProduct.get(deleteId, function (err, userProduct) {
      if (err) {
        return res.unknown();
      }
      userProduct.remove(function (err, result) {
        if (err) {
          return res.unknown();
        }
        res.json({result: 'success'});
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
          UserProduct.find({userId: userId, productId: product.Product.id}, function (err, results) {
            if (err) {
              return res.unknown();
            }

            results.forEach(function (result) {
              result.userId = null;
              result.productId = null;
            });

            product.Product.followList = results;
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