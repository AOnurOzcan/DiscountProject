var Account = require('../model/Account');
var Authority = require('../model/Authority');

var AccountAuthority = project.db.define('AccountAuthority', {
  id: {type: 'serial'}
});

AccountAuthority.hasOne('account', Account, {field: 'accountId'});
AccountAuthority.hasOne('authority', Authority, {field: 'authorityId'});

module.exports = AccountAuthority;

