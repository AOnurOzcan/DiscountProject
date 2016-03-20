var Account = require("../model/Account");

//Hesap oluşturma
project.app.post('/account', function (req, res) {
  var account = req.body;
  account.accountType = 'COMPANY';
  account.companyId = req.session.admin.companyId;
  Account.create(account, function (err, savedAccount) {
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
  Account.one({id: req.params.id}, function (err, account) {
    if (err) {
      res.unknown();
    }
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

//Tüm hesapları getir
project.app.get('/account', function (req, res) {
  Account.find({companyId: req.session.admin.companyId}).each().filter(function (account) {
    return account.username != req.session.admin.username;
  }).get(function (accounts) {
    res.json(accounts);
  });
});