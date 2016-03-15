var User = require("../model/User");
var Category = require("../model/Category");
var Company = require("../model/Company");

var Preference = project.db.define('Preference', {
  id: {type: 'serial'}
});

Preference.hasOne('User', User, {field: 'userId'});
Preference.hasOne('Category', Category, {field: 'categoryId', autoFetch: true});
Preference.hasOne('Company', Company, {field: 'companyId', autoFetch: true});

module.exports = Preference;


