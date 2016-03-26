var Company = require('../model/Company');
var Category = require('../model/Category');

var CompanyCategory = project.db.define('CompanyCategory', {
  id: {type: 'serial'}
});

CompanyCategory.hasOne('category', Category, {field: 'categoryId'});
CompanyCategory.hasOne('company', Company, {field: 'companyId'});

module.exports = CompanyCategory;