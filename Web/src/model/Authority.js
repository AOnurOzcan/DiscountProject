var Authority = project.db.define('Authority', {
  id: {type: 'serial'},
  authorityCode: String,
  authorityValue: String,
  authorityType: ['ADMIN', 'COMMON']
});

module.exports = Authority;

