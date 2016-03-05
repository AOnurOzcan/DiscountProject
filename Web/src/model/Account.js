var Account = project.db.define('Account', {
  id: {type: 'serial'},
  username: String,
  password: String,
  accountType: ["ADMIN", "COMPANY"],
  accountAuth: ["CREEATE_ACCOUNT", "REMOVE_ACCOUNT"]
});

module.exports = Account;