var Category = require("../model/Category");
var Company = require("../model/Company");

var Product = project.db.define('Product', {
  id: {type: 'serial'},
  productName: String,
  previousPrice: Number,
  price: Number,
  stock: Number,
  productDescription: String,
  imageURL: String
});

Product.hasOne('category', Category, {field: 'categoryId'});
Product.hasOne('Company', Company, {field: 'companyId'});

module.exports = Product;