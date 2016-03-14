/**
 * Created by Onur Kuru on 5.3.2016.
 */

var ConfirmationCode = require('../model/ConfirmationCode');
var User = require('../model/User');
var randtoken = require('rand-token');

project.app.get("/confirmationcode/send/:phoneNumber", function (req, res) {

  var phoneNumber = req.params.phoneNumber;
  var confirmationCode = Math.floor(Math.random() * (9999 - 1000) + 1000);
  var confirmationCodeJSON = {phoneNumber: phoneNumber, confirmationCode: confirmationCode};

  ConfirmationCode.find({phoneNumber: phoneNumber}, function (err, result) {

    if (err) {
      res.unknown();
    }

    if (result.length == 0) {//Eğer kayıt bulunamazsa yeni kayıt eklenir
      ConfirmationCode.create(confirmationCodeJSON, function (err, result) {
        if (err) {
          res.unknown();
        }
        //TODO oluşturulan onay kodu veritabanına kayıt edildikten sonra SMSapi ile kullanıcıya gönderilecek
        res.json({confirmationCode: confirmationCode});
      });
    } else {//kayıt varsa güncellenir

      result[0].confirmationCode = confirmationCodeJSON.confirmationCode;
      result[0].save(function (err, result) {
        if (err) {
          res.unknown();
        }
        //TODO oluşturulan onay kodu veritabanına kayıt edildikten sonra SMSapi ile kullanıcıya gönderilecek
        res.json({confirmationCode: confirmationCode});
      });
    }
  });
});

project.app.post("/confirmationcode/check", function (req, res) {
  var confirmationCodeJSON = req.body;

  ConfirmationCode.find(confirmationCodeJSON, function (err, confirmationCodeResult) {
    if (err) {
      res.unknown();
    }

    if (confirmationCodeResult.length == 0) {//geçersiz onay kodu
      res.json({confirmationCode: 'err', tokenKey: 'err'});
    } else {//eğer onay kodu varsa kullanıcının sonraki ekranını belirlemek için bu telefon numarası için bir profil oluşturulup oluşturulmadığına bakılır

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
            res.json({confirmationCode: 'true', tokenKey: tokenResult[0].tokenKey});
          });
        }
      });
    }
  });
});