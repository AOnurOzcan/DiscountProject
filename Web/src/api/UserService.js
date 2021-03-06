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
var Product = require('../model/Product');

//Kullanıcı profili oluşturmak kullanılıyor
project.app.post("/user/profile/create", function (req, res) {
  var user = req.body;

  user.cityId = req.body.cityId.id;
  user.birthday = project.util.ParseDate(user.birthday);
  user.tokenKey = randtoken.generate(20);
  User.create(user, function (err, userResult) {
    if (err) {
      return res.unknown();
    }
    return res.json(userResult.tokenKey);
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
        return res.json({success: true});
      });
    });
  });
});

//Kullanıcı profili getirmede kullanılıyor
project.app.get("/user/profile/get", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    User.get(userId, function (err, user) {
      if (err) {
        return res.unknown();
      }

      user.id = null;
      user.tokenKey = null;
      user.registrationId = null;
      user.getCity(function (err, result) {
        if (err) {
          return res.unknown();
        }
        user.cityId = result;
        delete user['city'];
        return res.json(user);
      });
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
        return res.unknown();
      }

      return res.json('true');
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
        return res.unknown();
      }

      preferences.asyncForEach(function (preference, done) {
        preference.remove(function (err) {
          if (err) {
            return res.unknown();
          }
          done();
        });
      }, function () {
        return res.json('true');
      });
    });
  });
});

//Kullanıcının tüm tercihlerini getirmede kullanılır
project.app.get("/user/preference/all", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    Preference.find({userId: userId}, function (err, preferences) {
      if (err) {
        return res.unknown();
      }

      preferences.forEach(function (preference) {
        preference.userId = null;
        preference.Category.parentCategory = null;
        preference.categoryId = preference.Category;
        preference.companyId = preference.Company;

        delete preference['Category'];
        delete preference['Company'];
      });
      return res.json(preferences);
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
        return res.json(userProducts);
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
      return res.json(result.id);
    });
  });
});

//Kullanıcının listesinden yeni ürün siler
project.app.delete("/user/product/delete/:id", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var deleteId = req.params.id;
    if (deleteId == 'all') {//id kısmından gelen parametre all ise kullanıcının tüm urunlerı sılınır
      UserProduct.find({userId: userId}, function (err, userProducts) {
        if (err) {
          return res.unknown();
        }
        userProducts.asyncForEach(function (userProduct, done) {
          userProduct.remove(function (err, result) {
            if (err) {
              return res.unknown();
            }
            done();
          });
        }, function () {
          return res.json({result: 'success'});
        });
      });
    } else {
      UserProduct.get(deleteId, function (err, userProduct) {
        if (err) {
          return res.unknown();
        }
        userProduct.remove(function (err, result) {
          if (err) {
            return res.unknown();
          }
          return res.json({result: 'success'});
        });
      });
    }
  });
});

project.app.get("/user/product/search", function (req, res) {
  var searchParam = req.query.queryParam;
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    Product.find({productName: project.orm.like('%' + searchParam + '%')}, function (err, products) {
      if (err) {
        return res.unknown();
      }
      products.asyncForEach(function (product, done) {
        product.getCompany(function (err, company) {
          product.companyId = company;
          product.categoryId = null;
          product.follower = null;
          delete product['Company'];
          done();
        });
      }, function () {
        return res.json(products);
      });
    });
  });
});
