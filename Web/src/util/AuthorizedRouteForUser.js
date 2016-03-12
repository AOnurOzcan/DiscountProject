/**
 * Created by Onur Kuru on 12.3.2016.
 */

require("./GenericResponse");
var Token = require("../model/Token");
module.exports = function (req, res, callback) {
  var tokenKey = req.query.tokenKey;

  if (tokenKey == undefined) {
    res.unauthorized();
  } else {
    Token.find({tokenKey: tokenKey}, function (err, result) {
      if (err) {
        res.unauthorized();
      } else {
        callback(result[0].userId);
      }
    });
  }
};
