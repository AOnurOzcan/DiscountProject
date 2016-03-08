/**
 * Created by Onur Kuru on 6.3.2016.
 */
var City = require("../model/City");
project.app.get("/city/all", function (req, res) {
  City.find(function (err, result) {

    if (err) {
      res.send('false');
    }

    res.json(result);
  });
});