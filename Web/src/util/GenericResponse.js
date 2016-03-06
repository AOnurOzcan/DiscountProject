var stringify = require('json-stringify-safe');

project.app.use(function (req, res, next) {
  res.unauthorized = function () {
    res.type("application/json");
    res.status(401);
    res.json({error: true});
  };

  res.unknown = function () {
    res.type("application/json");
    res.status(500);
    res.json({error: true});
  };

  res.badRequest = function (need) {
    res.type("application/json");
    res.status(400);
    res.json({error: true, need: need});
  };

  res.ok = function (body) {
    res.type("application/json");
    res.send(stringify(body));
  };

  res.error = function (body) {
    res.type("application/json");
    res.status(417);
    res.send(stringify(body));
  };

  next();
});