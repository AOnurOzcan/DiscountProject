var Account = require("../model/Account");
var AccountCompany = require("../model/AccountCompany");

project.app.post("/login", function (req, res) {

  Account.one({username: req.body.username, password: req.body.password}, function (err, admin) {
    AccountCompany.one({accountId: admin.id}, function (err, companyId) {
      if (err) {
        return res.unknown();
      }

      if (admin == null) {
        return res.unauthorized();
      }

      req.session.admin = admin;
      req.session.admin.companyId = companyId.companyId;
      res.json(admin);
    });
  });
});

project.app.get("/check", function (req, res) {

  var session = {};

  if (req.session.admin == undefined) {
    res.unauthorized();
  } else {
    session.username = req.session.admin.username;
    session.accountType = req.session.admin.accountType;
    session.accountAuth = req.session.admin.accountAuth;
    res.json(session);
  }

});

project.app.get("/logout", function (req, res) {
  req.session.destroy();
  res.json(null);
});