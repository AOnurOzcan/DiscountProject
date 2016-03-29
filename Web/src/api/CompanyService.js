var CompanyCategory = require("../model/CompanyCategory");
var Category = require('../model/Category');
var Company = require('../model/Company');

project.app.get('/company', function (req, res) {
  Company.find({}, function (err, companies) {
    if (err) {
      return res.unknown();
    }
    res.json(companies);
  });
});

//------------------------------ MOBILE --------------------------//

project.app.get("/company/withcategory", function (req, res) {

  CompanyCategory.find(function (err, result) {
    if (err) {
      res.send().unknown();
    }

    result.asyncForEach(function (item, done) {
      item.getCompany(function (err, Company) {
        if (err) {
          return res.unknown();
        }
        item.companyId = Company;
        delete item['company'];
        done();
      });
    }, function () {
      result.asyncForEach(function (item2, done) {
        item2.getCategory(function (err, Category) {
          if (err) {
            return res.unknown();
          }
          item2.categoryId = Category;
          delete item2['category'];
          done();
        });
      }, function () {
        res.json(result);
      });
    });
  });
});