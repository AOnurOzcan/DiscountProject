require("./GenericResponse");

module.exports = function () {
  var args = Array.prototype.slice.call(arguments);

  return function (req, res, next) {
    var check = args.every(function (property) {
      return req.body[property] != undefined;
    });

    if (check == false) {
      res.badRequest(args);
    } else {
      next();
    }
  };
};