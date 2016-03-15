/**
 * Created by Onur Kuru on 6.3.2016.
 */
var City = require('../model/City');
var User = require('../model/User');
var Preference = require('../model/Preference');
var randtoken = require('rand-token');

project.app.post("/user/profile/create", function (req, res) {
  var user = req.body;

  City.find({cityName: user.cityId.cityName}, 1, function (err, result) {
    if (err) {
      res.send('error');
    }
    user.cityId = result[0].id;
    user.tokenKey = randtoken.generate(20);
    //var date = new Date();
    //user.birthday = date.getFullYear() + "-" + date.getMonth() + "-" + date.getDay();
    User.create(user, function (err, userResult) {

      if (err) {
        res.send(err);
      }
      res.json(userResult.tokenKey);
    });
  });
});

project.app.get("/user/profile/get", function (req, res) {

  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    User.find({id: userId}, function (err, user) {
      if (err) {
        res.unknown();
      }

      user.id = null;
      user.phone = null;
      user.tokenKey = null;
      res.json(user);
    });
  });
});

project.app.post("/user/preference/create", function (req, res) {
//TODO kullanıcının takip ettiği kategori firma ikilisini yeniden kayıt edilmeye çalışırken hata oluyo bunu gider.
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

project.app.get("/user/preference/all", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    Preference.find({userId: userId}, function (err, preferences) {
      if (err) {
        res.unknown();
      }
      if (preferences.length == 0) {
        res.json({});
      }
      preferences.forEach(function (preference) {
        preference.userId = null;
        preference.categoryId = preference.Category;
        preference.companyId = preference.Company;

        delete preference['Category'];
        delete preference['Company'];
      });
      res.json(preferences);
    });
  });
});


