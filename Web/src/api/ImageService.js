var Image = require("../model/Image");
var config = require('config');
var S3FS = require('s3fs');
var fs = require('fs');
var multiparty = require('connect-multiparty'), multipartyMiddleware = multiparty();
var s3Credentials = config.get('S3Credentials');
var s3fsImpl = new S3FS(s3Credentials['bucketName'], {
  accessKeyId: s3Credentials['accessKeyId'],
  secretAccessKey: s3Credentials['secretAccessKey']
});
s3fsImpl.create();

/**
 * IMAGE UPLOAD SERVICE TEST
 */
project.app.post("/uploadImage", multipartyMiddleware, function (req, res) {
  var files = [];
  if (req.files.imageURL.constructor !== Array) { //Bir resim seçildiyse
    files.push(req.files.imageURL);
  } else { //Birden çok resim seçildiyse
    files = req.files.imageURL;
  }
  files.asyncForEach(function (file, done) {
    var stream = fs.createReadStream(file.path);
    var extension = file.originalFilename.split(".").pop();
    var fileName = new Date().getTime() + "." + extension;
    return s3fsImpl.writeFile(fileName, stream, {"ContentType": "image/jpg"}).then(function () {
      fs.unlink(file.path, function (err) {
        if (err) {
          console.error(err);
          return res.unknown();
        }
        var image = {};
        image.imageName = req.body.imageName;
        image.imageURL = "https://s3.amazonaws.com/ooar1/" + fileName;
        image.companyId = req.session.admin.companyId;
        Image.create(image, function (err, image) {
          if (err) {
            console.error(err);
            return res.unknown();
          }
          done();
        });
      });
    });
  }, function () {
    res.json({status: "success"});
  });
});

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