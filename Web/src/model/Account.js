var Company = require("../model/Company");

var Account = project.db.define('Account', {
  id: {type: 'serial'},
  username: String,
  password: String,
  accountType: ["ADMIN", "COMPANY"],
  accountAuth: ["CREATE_ACCOUNT", "REMOVE_ACCOUNT"]
});

Account.hasOne('Company', Company, {field: 'companyId'});

module.exports = Account;