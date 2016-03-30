var Account = require("../model/Account");
var Category = require("../model/Category");
var Preference = require("../model/Preference");
var Company = require("../model/Company");
var Branch = require("../model/Branch");
var Image = require("../model/Image");
var Notification = require("../model/Notification");
var Product = require("../model/Product");
var User = require("../model/User");

project.app.get('/statistics', function (req, res) {
  var session = req.session.admin;
  var statistics = [];

  if (session.accountType == "ADMIN" && session.companyAccess == false) {
    Account.count({}, function (err, count) {
      if (err) return res.unknown();
      statistics.push({count: count, statisticName: "Hesap", icon: "user icon"});
      Branch.count({}, function (err, count) {
        if (err) return res.unknown();
        statistics.push({count: count, statisticName: "Şube", icon: "building icon"});
        Image.count({}, function (err, count) {
          if (err) return res.unknown();
          statistics.push({count: count, statisticName: "Resim", icon: "photo icon"});
          Notification.count({isSent: true}, function (err, count) {
            if (err) return res.unknown();
            statistics.push({count: count, statisticName: "Gönderilmiş Bildirim", icon: "announcement icon"});
            Product.count({}, function (err, count) {
              if (err) return res.unknown();
              statistics.push({count: count, statisticName: "Ürün", icon: "gift icon"});
              User.count({}, function (err, count) {
                if (err) return res.unknown();
                statistics.push({count: count, statisticName: "Kayıtlı Kullanıcı", icon: "users icon"});
                Category.count({parentCategory: null}, function (err, mainCategoryCount) {
                  if (err) return res.unknown();
                  statistics.push({count: mainCategoryCount, statisticName: "Ana Kategori", icon: "sidebar icon"});
                  Category.count({}, function (err, count) {
                    if (err) return res.unknown();
                    statistics.push({
                      count: count - mainCategoryCount,
                      statisticName: "Alt Kategori",
                      icon: "sitemap icon"
                    });
                    Company.count({}, function (err, count) {
                      if (err) return res.unknown();
                      statistics.push({count: count, statisticName: "Firma", icon: "food icon"});
                      res.json(statistics);
                    });
                  });
                });
              });
            });
          });
        });
      });
    });
  } else {
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
              Preference.count({companyId: session.companyId}, function (err, count) {
                if (err) return res.unknown();
                statistics.push({count: count, statisticName: "Sizden Bildirim Alacak Kullanıcı"});
                res.json(statistics);
              });
            });
          });
        });
      });
    });
  }
});