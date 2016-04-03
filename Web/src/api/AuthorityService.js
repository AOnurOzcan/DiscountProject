/**
 * Created by Onur on 3.4.2016.
 */
var Authority = require('../model/Authority');

project.app.get('/authority', function (req, res) {

  Authority.find({}, function (err, authories) {
    if (err) return res.unknown();
    res.json(authories);
  });
});