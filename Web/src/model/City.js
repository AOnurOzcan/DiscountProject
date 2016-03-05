var City = project.db.define('City', {
  id: {type: 'serial'},
  cityName: String
});

module.exports = City;