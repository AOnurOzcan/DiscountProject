/**
 * Created by Onur Kuru on 12.3.2016.
 */

require("./GenericResponse");
var User = require("../model/User");
module.exports = function (req, res, callback) {
  var tokenKey = req.query.tokenKey;

  if (tokenKey == undefined) {
    return res.unauthorized();
  } else {
    User.one({tokenKey: tokenKey}, function (err, result) {
      if (err) {
        return res.unknown();
      }

      if (result == null) {
        return res.unauthorized();
      }

      callback(result.id);
    });
  }
};
