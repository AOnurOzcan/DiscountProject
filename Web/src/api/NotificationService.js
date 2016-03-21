/**
 * Created by Onur Kuru on 21.3.2016.
 */

var GCM = require('node-gcm');
var User = require('../model/User');

project.app.get("/notification/send", function (req, res) {

  User.find({}, function (err, users) {
    var userRegistrationIds = [];
    var message = new GCM.Message({
      data: {
        key1: 'message1',
        key2: 'message2'
      }
    });

    var sender = new GCM.Sender(project.config.get("GCMApiKey").key);

    if (err) {
      res.unknown();
    }


    users.forEach(function (user) {
      userRegistrationIds.push(user.registrationId);
    });

    sender.send(message, {registrationTokens: userRegistrationIds}, function (err, response) {
      if (err)
        console.error(err);
      else
        res.json({"success": true});
    });
  });
});