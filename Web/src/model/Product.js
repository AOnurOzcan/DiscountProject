var Category = require("../model/Category");

var Product = project.db.define('Product', {
  id: {type: 'serial'},
  productName: String,
  previousPrice: Number,
  price: Number,
  stock: Number,
  productDescription: String,
  imageURL: String
});

Product.hasOne('Category', Category, {field: 'categoryId'});

module.exports = Product;