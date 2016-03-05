var Account = require("../model/Account");
var Company = require("../model/Company");

var Auth = project.db.define('Auth', {
  id: {type: 'serial'}
});

Auth.hasOne('Account', Account, {field: 'accountId'});
Auth.hasOne('Company', Company, {field: 'companyId'});

module.exports = Auth;