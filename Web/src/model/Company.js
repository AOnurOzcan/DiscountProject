var Company = project.db.define('Company', {
  id: {type: 'serial'},
  companyName: String
});

module.exports = Company;