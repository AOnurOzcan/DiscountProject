var Account = require("../model/Account");
var AccountCompany = require("../model/AccountCompany");
var BodyControl = require("../util/BodyControl");
var AuthorizedRoute = require("../util/AuthorizedRoute");
var md5 = require("js-md5");

/**
 * Kullanıcı isim ve şifresini yazıp login dediğinde buraya düşer.
 * BodyControl gelen isim ve şifrenin boş olup olmadığını kontrol eder.
 */
project.app.post("/login", BodyControl("username", "password"), function (req, res) {

  Account.one({username: req.body.username, password: req.body.password}, function (err, admin) {
    if (admin == null) {
      return res.unauthorized();
    }

    AccountCompany.one({accountId: admin.id}, function (err, accountCompany) {
      if (err) {
        return res.unknown();
      }
      if (accountCompany != null) {
        admin.companyId = accountCompany.companyId;
        admin.companyName = accountCompany.Company.companyName;
      }
      req.session.admin = admin;
      res.json(req.session.admin);
    });
  });
});

/**
 * Bu fonksiyon bir oturum olup olmadığını kontrol eder.
 */
project.app.get("/check", function (req, res) {

  if (req.session.admin == undefined) {
    res.unauthorized();
  } else {
    res.json({companyName: req.session.admin.companyName});
  }

});

/**
 * Bu fonksiyon oturumu sonlandırır.
 */
project.app.get("/logout", AuthorizedRoute(""), function (req, res) {

  req.session.destroy();
  res.unauthorized();
});
