var Image = require("../model/Image");

project.app.get('/files/all', function (req, res) {
  Image.find(function (err, images) {
    if (err) {
      return res.unknown();
    }

    res.json(images);
  });
});