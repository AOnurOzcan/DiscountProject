var Product = require("../model/Product");

/**
 * Bu fonksiyon ürünleri kaydeder. Geriye kaydettiği ürünün id'li halini döndürür.
 * POST isteğiyle gönderilen veriler req parametresinin body objesine düşer.
 */
project.app.post("/addProduct", function (req, res) {

  var product = {};
  product.productName = req.body.productName;
  product.categoryId = req.body.categoryId;
  product.previousPrice = req.body.previousPrice;
  product.price = req.body.price;
  product.stock = req.body.stock;
  product.productDescription = req.body.productDescription;
  product.imageURL = req.body.imageURL;
  product.companyId = req.session.admin.companyId;

  Product.create(product, function (err, product) {
    if (err) {
      return res.unknown();
    }

    res.json(product);
  });

});

/**
 * Bu fonksiyon oturum açmış olan firmanın tüm ürünleri getirir.
 */
project.app.get("/allProducts", function (req, res) {

  if (req.session.admin != undefined) {
    Product.find({companyId: req.session.admin.companyId}, function (err, products) {
      if (err) {
        return res.unknown();
      }

      res.json(products);
    });
  }
});


var S3FS = require('s3fs');
var fs = require('fs');


var multiparty = require('connect-multiparty'), multipartyMiddleware = multiparty();

var s3fsImpl = new S3FS('ooar1', {
  accessKeyId: 'AKIAI5MWYHAWGD4FLBZQ',
  secretAccessKey: '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'
});
s3fsImpl.create();


/**
 * IMAGE UPLOAD SERVICE TEST
 */
project.app.post("/testUpload", multipartyMiddleware, function (req, res) {
  var file = req.files.file;
  console.log(req.files);
  var stream = fs.createReadStream(file.path);
  return s3fsImpl.writeFile(file.originalFilename, stream).then(function () {
    fs.unlink(file.path, function (err) {
      if (err) {
        console.error(err);
      }
    });
    res.send("OK");
  });
});

