var Account = require("../model/Account");
var Company = require("../model/Company");

var AccountCompany = project.db.define('AccountCompany', {
  id: {type: 'serial'}
});

AccountCompany.hasOne('Account', Account, {field: 'accountId'});
AccountCompany.hasOne('Company', Company, {field: 'companyId', autoFetch: true});

module.exports = AccountCompany;