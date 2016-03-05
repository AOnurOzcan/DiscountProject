var City = require('../model/City');
var Company = require('../model/Company');

var Branch = project.db.define('Branch', {
  id: {type: 'serial'},
  name: String,
  address: String,
  phone: String,
  locationURL: String,
  workingHours: String
});

Branch.hasOne('Company', Company, {field: 'companyId', autoFetch: true});
Branch.hasOne('City', City, {field: 'cityId', autoFetch: true});

module.exports = Branch;