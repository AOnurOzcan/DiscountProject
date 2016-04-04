var City = require("../model/City");

var User = project.db.define('User', {
  id: {type: 'serial'},
  firstName: String,
  lastName: String,
  phone: String,
  gender: Boolean,
  birthday: Date,
  notificationOpen: Boolean,
  tokenKey: String,
  registrationId: String
});

User.hasOne('city', City, {field: 'cityId'});

module.exports = User;