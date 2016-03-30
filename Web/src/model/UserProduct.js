var User = require("../model/User");
var Product = require("../model/Product");

var UserProduct = project.db.define('UserProduct', {
  id: {type: 'serial'}
});

UserProduct.hasOne('user', User, {field: 'userId'});
UserProduct.hasOne('product', Product, {field: 'productId'});

module.exports = UserProduct;