/**
 * Created by Onur Kuru on 5.3.2016.
 */

project.app.get("/sendconfirmationcode/:phoneNumber", function (req, res) {
  var phoneNumber = req.params.phoneNumber;
  res.send('true');
});