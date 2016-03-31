var Account = require('../model/Account');

var PasswordRecovery = project.db.define('PasswordRecovery', {
  id: {type: 'serial'},
  recoveryKey: String,
  expiryDate: Date
});

PasswordRecovery.hasOne('account', Account, {field: "accountId"});

module.exports = PasswordRecovery;