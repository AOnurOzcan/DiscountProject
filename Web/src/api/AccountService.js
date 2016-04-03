var Account = require("../model/Account");
var Company = require("../model/Company");
var AccountAuthority = require("../model/AccountAuthority");

//Hesap oluşturma
project.app.post('/account', function (req, res) {
  var account = req.body;
  var session = req.session.admin;
  //Standart Admin Girişi
  if (session.accountType == "ADMIN" && session.companyAccess == false) {
    account.accountType = "ADMIN";
    account.createdBy = session.id;
  }
  //Firma Girişi
  else if (session.accountType == "COMPANY") {
    account.accountType = "COMPANY";
    account.companyId = session.companyId;
    account.createdBy = session.id;
  }
  //Admin üzerinden firma girişi
  else if (session.accountType == "ADMIN" && session.companyAccess == true) {
    account.accountType = "COMPANY";
    account.createdBy = session.id;
    account.companyId = session.companyId;
  }

  Account.create(account, function (err) {
    if (err) {
      return res.unknown();
    }
    res.json({status: true});
  });
});

//Hesap silme
project.app.delete('/account/:id', function (req, res) {
  Account.find({id: req.params.id}).remove(function (err) {
    if (err) {
      return res.unknown();
    }
    res.json({status: "success"});
  });
});

//Id'ye göre hesap getirme
project.app.get('/account/:id', function (req, res) {
  var account = {};
  Account.one({id: req.params.id}, function (err, acc) {
    if (err) {
      res.unknown();
    }
    acc.accountAuth = [];
    AccountAuthority.find({accountId: acc.id}, function (err, accountAuthories) {
      if (err) return res.unknown();
      accountAuthories.asyncForEach(function (accountAuthority, done) {
        accountAuthority.getAuthority(function (err, authority) {
          acc.accountAuth.push(authority.authorityCode);
          done();
        });
      }, function () {

        account.username = acc.username;
        account.password = acc.password;
        account.email = acc.email;
        account.accountAuth = acc.accountAuth;
        res.json(account);
      });
    });

  });
});

//Oturum açan kişinin hesabını getirme
project.app.get('/account', function (req, res) {
  var account = {};
  var session = req.session.admin;

  Account.one({id: session.id}, function (err, acc) {
    if (err) {
      res.unknown();
    }
    account.id = acc.id;
    account.username = acc.username;
    account.email = acc.email;
    account.accountAuth = acc.accountAuth;
    res.json(account);
  });
});

//Hesap düzenleme
project.app.put('/account/:id', function (req, res) {
  Account.get(req.params.id, function (err, account) {
    account.save(req.body, function (err) {
      if (err) {
        return res.unknown();
      }
      res.json({status: "success"});
    })
  });
});

//Oturum açmış olan kullanıcıya ait tüm hesapları getirir.
project.app.get('/accounts', function (req, res) {
  var session = req.session.admin;
  var params = {};

  if (session.accountType == "COMPANY" || (session.accountType == "ADMIN" && session.companyAccess == true)) {
    params.companyId = session.companyId;
  }

  Account.find(params).each().filter(function (account) {
    return account.id != req.session.admin.id;
  }).get(function (accounts) {
    res.json(accounts);
  });

});
