var Company = require("../model/Company");

var Image = project.db.define('Image', {
  id: {type: 'serial'},
  imageName: String,
  imageURL: String
});

Image.hasOne('Company', Company, {field: 'companyId'});

module.exports = Image;