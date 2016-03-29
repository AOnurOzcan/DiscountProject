var Company = require("../model/Company");

var Account = project.db.define('Account', {
  id: {type: 'serial'},
  username: String,
  password: String,
  email: String,
  accountType: ["ADMIN", "COMPANY"],
  accountAuth: ["CREATE_ACCOUNT", "REMOVE_ACCOUNT"]
});

Account.hasOne('company', Company, {field: 'companyId'});
Account.hasOne('creator', Account, {field: 'createdBy'});

module.exports = Account;