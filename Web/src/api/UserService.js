/**
 * Created by Onur Kuru on 6.3.2016.
 */
var City = require('../model/City');
var User = require('../model/User');
project.app.post("/user/createprofil", function (req, res) {
  var user = req.body;

  City.find({cityName: user.cityId.cityName},1, function (err, result) {
    if (err) {
      res.send('error');
    }
    user.cityId = result[0].id;
    //var date = new Date();
    //user.birthday = date.getFullYear() + "-" + date.getMonth() + "-" + date.getDay();
    User.create(user, function (err,result) {

      if (err) {
        res.send(err);
      }
      //Token oluşturulup ön tarafa gönderilecek
      res.json(result);
    });
  });
});

project.app.post("/user/createpreferences", function (req,res) {

});
