var Account = require("../model/Account");
var Company = require("../model/Company");
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
    Company.one({id: admin.companyId}, function (err, company) {
      admin.companyName = company.companyName;
      req.session.admin = admin;
      res.json({status: true});
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
    res.json({companyName: req.session.admin.companyName, accountAuth: req.session.admin.accountAuth});
  }

});

/**
 * Bu fonksiyon oturumu sonlandırır.
 */
project.app.get("/logout", AuthorizedRoute(""), function (req, res) {

  req.session.destroy();
  res.json({status: true});
});
