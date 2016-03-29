var Image = require("../model/Image");

project.app.get('/files', function (req, res) {
  Image.find({companyId: req.session.admin.companyId}, function (err, images) {
    if (err) {
      return res.unknown();
    }

    res.json(images);
  });
});

project.app.delete('/files/:id', function (req, res) {
  Image.find({id: req.params.id}).remove(function (err) {
    if (err) {
      return res.unknown();
    }
    res.json({status: "success"});
  });
});