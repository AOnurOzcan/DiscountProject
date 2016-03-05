require("./GenericResponse");

module.exports = function (req, res, next) {
  if (req.session.admin == undefined) {
    res.unauthorized();
  } else {
    next();
  }
};