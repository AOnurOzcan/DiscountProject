var nodemailer = require('nodemailer');
var randtoken = require('rand-token');
var AuthorizedRoute = require("../util/AuthorizedRoute");
var Account = require("../model/Account");
var PasswordRecovery = require("../model/PasswordRecovery");

//Mail Gönderme
var sendMail = function (mailOptions) {

  var transporter = nodemailer.createTransport({
    service: 'Yandex',
    auth: {
      user: 'bilgi@osymsorucevap.com', // Your email id
      pass: 'kajmerap' // Your password
    }
  });

  transporter.sendMail(mailOptions, function (error, info) {
    if (error) {
      console.log(error);
      //res.json({yo: 'error'});
    } else {
      console.log('Message sent: ' + info.response);
      //res.json({yo: info.response});
    }
  });
};

//Şifre sıfırlama maili gönderme
project.app.get('/sendResetMail/:accountId', AuthorizedRoute("RECOVERY_PASSWORD"), function (req, res) {

  var accountId = req.params.accountId;
  var userMail;

  Account.get(accountId, function (err, account) {
    if (err) return res.unknown();

    userMail = account.email;
    var now = new Date();
    var recoveryObject = {};
    recoveryObject.accountId = accountId;
    recoveryObject.recoveryKey = randtoken.generate(40);
    recoveryObject.expiryDate = new Date(now.setDate(now.getDate() + 1));

    PasswordRecovery.create(recoveryObject, function (err) {
      if (err) return res.unknown();

      var text = "Şifrenizi sıfırlamak için www.osymsorucevap.com/#passwordRecovery/" + recoveryObject.recoveryKey +
        " adresine tıklayın. Bu link 24 saat sonra geçersiz olacaktır.";
      var mailOptions = {
        from: 'bilgi@osymsorucevap.com', // sender address
        to: userMail, // list of receivers
        subject: 'Şifre Sıfırlama', // Subject line
        text: text //, // plaintext body
      };

      var transporter = nodemailer.createTransport({
        service: 'Yandex',
        auth: {
          user: 'bilgi@osymsorucevap.com', // Your email id
          pass: 'kajmerap' // Your password
        }
      });

      transporter.sendMail(mailOptions, function (error, info) {
        if (error) {
          console.log(error);
          res.unknown();
        } else {
          console.log('Message sent: ' + info.response);
          res.json({status: "success"});
        }
      });
    });
  });

});

project.app.get('/passwordRecoveryCheck/:token', function (req, res) {
  var token = req.params.token;
  PasswordRecovery.one({recoveryKey: token}, function (err, user) {
    if (err) return res.unknown();
    if (user == null) {
      return res.unauthorized();
    } else {
      if (user.expiryDate.getTime() < new Date().getTime()) {
        return res.unauthorized();
      } else {
        res.json({accountId: user.accountId});
      }
    }
  });
});

project.app.post('/resetPassword', function (req, res) {

  var pass1 = req.body.password1;
  var pass2 = req.body.password2;
  var accountId = req.body.accountId;

  if (pass1 === pass2) {
    Account.get(accountId, function (err, account) {
      if (err) return res.unknown();

      account.password = pass1;
      account.save(function (err) {
        if (err) return res.unknown();

        PasswordRecovery.find({accountId: accountId}).remove(function (err) {
          if (err) return res.unknown();

          res.json({status: "success"});
        });
      });
    });
  }
});