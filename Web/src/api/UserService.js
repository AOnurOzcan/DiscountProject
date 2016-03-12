/**
 * Created by Onur Kuru on 6.3.2016.
 */
var City = require('../model/City');
var User = require('../model/User');
var Token = require('../model/Token');
var Preference = require('../model/Preference');
var randtoken = require('rand-token');

project.app.post("/user/createprofil", function (req, res) {
  var user = req.body;

  City.find({cityName: user.cityId.cityName}, 1, function (err, result) {
    if (err) {
      res.send('error');
    }
    user.cityId = result[0].id;
    //var date = new Date();
    //user.birthday = date.getFullYear() + "-" + date.getMonth() + "-" + date.getDay();
    User.create(user, function (err, userResult) {

      if (err) {
        res.send(err);
      }
      var tokenKey = randtoken.generate(20);
      Token.create({userId: userResult.id, tokenKey: tokenKey}, function (err, tokenResult) {
        if (err) {
          res.unknown();
        }
        res.json(tokenKey);
      });
    });
  });
});

project.app.post("/user/createpreferences", function (req, res) {
//TODO kullanıcının takip ettiği kategori firma ikilisini yeniden kayıt edilmeye çalışırken hata oluyo bunu gider.
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    var preferences = req.body;
    preferences.forEach(function (preference) {
      preference.categoryId = preference.categoryId.id;
      preference.companyId = preference.companyId.id;
      preference.userId = userId;
    });

    Preference.create(preferences, function (err, result) {
      if (err) {
        res.unknown();
      }

      res.send('true');
    });
  });

});
