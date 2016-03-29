var CompanyCategory = require("../model/CompanyCategory");
var Category = require('../model/Category');
var Company = require('../model/Company');

//Firma ekler
project.app.post('/company', function (req, res) {
  var company = req.body;
  Company.create(company, function (err, company) {
    if (err) {
      return res.unknown();
    }
    res.json(company);
  });
});

//Firma siler
project.app.delete('/company/:id', function (req, res) {
  var companyId = req.params.id;
  Company.find({id: companyId}).remove(function (err) {
    if (err) return res.unknown();
    res.json({status: "success"});
  });
});

//Firma düzenle
project.app.put('/company/:id', function (req, res) {
  var companyId = req.params.id;
  Company.get(companyId, function (err, company) {
    if (err) return res.unknown();
    company.save(req.body, function (err) {
      if (err) return res.unknown();
      res.json({status: "success"});
    });
  });
});

//ID'ye göre firma getir
project.app.get('/company/:id', function (req, res) {
  Company.get(req.params.id, function (err, company) {
    if (err) return res.unknown();
    res.json(company);
  });
});

//Tüm firmaları getirir.
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

