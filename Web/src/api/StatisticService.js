var Account = require("../model/Account");
var Company = require("../model/Company");
var Branch = require("../model/Branch");
var Image = require("../model/Image");
var Notification = require("../model/Notification");
var Product = require("../model/Product");
var User = require("../model/User");

project.app.get('/companyStatistics', function (req, res) {
  var session = req.session.admin;
  var statistics = [];

  Account.count({companyId: session.companyId}, function (err, count) {
    if (err) return res.unknown();
    statistics.push({count: count, statisticName: "Kullanıcı"});
    Branch.count({companyId: session.companyId}, function (err, count) {
      if (err) return res.unknown();
      statistics.push({count: count, statisticName: "Şube"});
      Image.count({companyId: session.companyId}, function (err, count) {
        if (err) return res.unknown();
        statistics.push({count: count, statisticName: "Resim"});
        Notification.count({companyId: session.companyId, isSent: true}, function (err, count) {
          if (err) return res.unknown();
          statistics.push({count: count, statisticName: "Gönderilmiş Bildirim"});
          Product.count({companyId: session.companyId}, function (err, count) {
            if (err) return res.unknown();
            statistics.push({count: count, statisticName: "Ürün"});
            res.json(statistics);
          });
        });
      });
    });
  });
});

project.app.get('/adminStatistics', function (req, res) {
  var accountCount, companyCount, branchCount, imageCount, notificationCount, productCount, userCount;
});