var Company = require('../model/Company');
var Category = require('../model/Category');

var CompanyCategory = project.db.define('CompanyCategory', {
  id: {type: 'serial'}
});

CompanyCategory.hasOne('category', Category, {field: 'categoryId', autoFetch: true});
CompanyCategory.hasOne('company', Company, {field: 'companyId', autoFetch: true});

module.exports = CompanyCategory;