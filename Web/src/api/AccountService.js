var Account = require("../model/Account");
var Company = require("../model/Company");
var AccountAuthority = require("../model/AccountAuthority");
var Authority = require("../model/Authority");

//Hesap oluşturma
project.app.post('/account', function (req, res) {
  var account = {};
  var authList = req.body.accountAuth;
  var session = req.session.admin;
  account.username = req.body.username;
  account.email = req.body.email;

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

  Account.create(account, function (err, account) {
    if (err) return res.unknown();
    authList.asyncForEach(function (auth, done) {
      AccountAuthority.create({accountId: account.id, authorityId: auth}, function (err) {
        if (err) return res.unknown();
        done();
      });
    }, function () {
      res.json({status: true});
    });
  });
});

//Hesap silme
project.app.delete('/account/:id', function (req, res) {
  AccountAuthority.find({accountId: req.params.id}).remove(function (err) {
    if (err) return res.unknown();

    Account.find({id: req.params.id}).remove(function (err) {
      if (err) return res.unknown();

      res.json({status: "success"});
    });
  });
});

//Id'ye göre hesap getirme
project.app.get('/account/:id', function (req, res) {
  var account = {};
  Account.one({id: req.params.id}, function (err, acc) {
    if (err) {
      return res.unknown();
    }
    acc.accountAuth = [];
    AccountAuthority.find({accountId: acc.id}, function (err, accountAuthories) {
      if (err) return res.unknown();
      accountAuthories.asyncForEach(function (accountAuthority, done) {
        acc.accountAuth.push(accountAuthority.authority.id);
        done();
      }, function () {
        account.username = acc.username;
        account.email = acc.email;
        account.accountAuth = acc.accountAuth;
        res.json(account);
      });
    });

  });
});

//Profil düzenleme için oturum açan kişinin hesabını getirme /
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
  var addAuthArray = req.body.addAuthArray;
  var removeAuthArray = req.body.removeAuthArray;

  Account.get(req.params.id, function (err, account) {
    account.email = req.body.email;
    account.save(function (err) {
      if (err) return res.unknown();

      addAuthArray.asyncForEach(function (authId, done) {
        AccountAuthority.create({accountId: account.id, authorityId: authId}, function (err) {
          if (err) return res.unknown();
          done();
        });
      }, function () {
        removeAuthArray.asyncForEach(function (authId, done) {
          AccountAuthority.find({accountId: account.id, authorityId: authId}).remove(function (err) {
            if (err) return res.unknown();
            done();
          });
        }, function () {
          res.json({status: "success"});
        });
      });
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

  Account.find(params).only("id", "username", "email").each().filter(function (account) {
    return account.id != req.session.admin.id;
  }).get(function (accounts) {
    accounts.asyncForEach(function (account, done) {
      AccountAuthority.find({accountId: account.id}, function (err, accountAuths) {
        account.accountAuth = [];
        accountAuths.forEach(function (auth) {
          account.accountAuth.push(auth.authority.authorityValue);
        });
        done();
      });
    }, function () {
      res.json(accounts);
    });
  });
});
