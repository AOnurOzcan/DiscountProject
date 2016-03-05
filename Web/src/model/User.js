var City = require("../model/City");

var User = project.db.define('', {
  id: {type: 'serial'},
  firstName: String,
  lastName: String,
  phone: String,
  gender: Boolean,
  birthday: Date,
  notificationOpen: Boolean
});

User.hasOne('City', City, {field: 'cityId'});

module.exports = User;