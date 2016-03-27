var CompanyCategory = require("../model/CompanyCategory");
var Category = require('../model/Category');
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