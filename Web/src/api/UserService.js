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
