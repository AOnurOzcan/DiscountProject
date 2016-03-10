var CompanyCategory = require("../model/CompanyCategory");
var Category = require('../model/Category');
project.app.get("/company/withcategory", function (req, res) {

  CompanyCategory.find(function (err, result) {
    if (err) {
      res.send().unknown();
    }

    result.forEach(function (item) {
      item.category.parentCategory = null;
      item.categoryId = item.category;
      item.companyId = item.company;

      delete item['category'];
      delete item['company'];
    });

    res.json(result);
  });
});