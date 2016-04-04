/**
 * Created by Onur Kuru on 5.3.2016.
 */
var User = require('../model/User');
var randtoken = require('rand-token');

project.app.get("/confirmationcode/send/:phoneNumber", function (req, res) {

  var phoneNumber = req.params.phoneNumber;
  var confirmationCode = Math.floor(Math.random() * (9999 - 1000) + 1000);
  var information = {confirmationCode: confirmationCode, phoneNumber: phoneNumber, count: 0};

  if (req.session.userConfirmationCode == undefined) {
    req.session.userConfirmationCode = information;
  } else {
    if (req.session.userConfirmationCode.count == 5) {//onay kodu gonderme limiti yarım saat sonra tekrar

    } else {
      req.session.userConfirmationCode.count++;
    }
  }

  res.json({confirmationCode: confirmationCode});
});

project.app.post("/confirmationcode/check", function (req, res) {
  var confirmationCodeJSON = req.body;

  if (req.session.userConfirmationCode != undefined) {
    var information = req.session.userConfirmationCode;
    information.count++;

    if (confirmationCodeJSON.confirmationCode == information.confirmationCode) {//kodlar aynı hesap varmı ona bak

      User.find({phone: confirmationCodeJSON.phoneNumber}, function (err, userResult) {
        if (err) {
          res.unknown();
        }

        if (userResult.length == 0) {// kullanıcı ilk kez kayıt oluşturuyo
          res.json({confirmationCode: 'true', tokenKey: 'err'})
        } else {//kullanıcı var. oturum kapatmış. yeni token oluştur.
          userResult[0].tokenKey = randtoken.generate(20);
          userResult[0].save(function (err) {
            if (err) {
              res.unknown();
            }
            res.json({confirmationCode: 'true', tokenKey: userResult[0].tokenKey});
          });
        }
      });
    } else {//onay kodu yanlıs
      return res.json({confirmationCode: 'err', tokenKey: 'err'});
    }
  }
});

project.app.post("/session/create", function (req, res) {

  var phoneNumber = req.body.phoneNumber;
  User.find({phone: phoneNumber}, function (err, user) {
    if (err) {
      res.unknown();
    }

    if (user.length == 0) {
      res.json({tokenKey: 'err'});
    } else {
      var tokenKey = randtoken.generate(20);
      user[0].tokenKey = tokenKey;
      user[0].save(function (err) {
        if (err) {
          res.unknown();
        }
        res.json({tokenKey: tokenKey});
      });
    }
  });
});

project.app.get("/session/delete", function (req, res) {
  project.util.AuthorizedRouteForUser(req, res, function (userId) {
    User.get(userId, function (err, user) {
      user.tokenKey = null;
      user.save(function (err, result) {
        if (err) {
          return res.unknown();
        }
        res.json({result: "success"});
      });
    });
  });
});