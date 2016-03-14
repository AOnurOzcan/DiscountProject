/**
 * Created by Onur Kuru on 12.3.2016.
 */

require("./GenericResponse");
var User = require("../model/User");
module.exports = function (req, res, callback) {
  var tokenKey = req.query.tokenKey;

  if (tokenKey == undefined) {
    res.unauthorized();
  } else {
    User.find({tokenKey: tokenKey}, function (err, result) {
      if (err) {
        res.unauthorized();
      } else {
        if (result.length > 1) {
          //TODO aynı tokena sahip 1 den fazla kullanıcı varsa haber ver
        } else {
          callback(result[0].id);
        }
      }
    });
  }
};
