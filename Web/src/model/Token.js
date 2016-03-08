/**
 * Created by Onur Kuru on 7.3.2016.
 */

var User = require("../model/User");

var Token = project.db.define('Token', {
  id: {type: 'serial'},
  tokenKey: String,
});

Token.hasOne('User', User, {field: 'userId', autoFetch: true});

module.exports = Token;