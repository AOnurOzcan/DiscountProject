require("./GenericResponse");

module.exports = function () {
  var args = Array.prototype.slice.call(arguments);
  return function (req, res, next) {
    if (req.session.admin == undefined) {
      res.unauthorized();
    } else {
      if (args[0] != "") {
        var auths = req.session.admin.accountAuth.split(",");
        var check = auths.some(function (auth) {
          return auth == args[0];
        });
        if (check == true) {
          next();
        } else {
          res.unauthorized();
        }
      } else {
        next();
      }
    }
  };
};

//module.exports = function (req, res, next) {
//  if (req.session.admin == undefined) {
//    res.unauthorized();
//  } else {
//    next();
//  }
//};