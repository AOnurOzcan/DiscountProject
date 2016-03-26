var Product = require("../model/Product");
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
 * Bu fonksiyon ürünleri kaydeder. Geriye kaydettiği ürünün id'li halini döndürür.
 * POST isteğiyle gönderilen veriler req parametresinin body objesine düşer.
 */
project.app.post("/product", function (req, res) {

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
project.app.get("/product", function (req, res) {

  if (req.session.admin != undefined) {
    Product.find({companyId: req.session.admin.companyId}, function (err, products) {
      if (err) {
        return res.unknown();
      }

      products.forEach(function (product) {
        product.categoryId = product.Category;
        delete product.Category;
      });

      res.json(products);
    });
  }
});

project.app.put("/product/:id", function (req, res) {
  Product.get(req.params.id, function (err, savedProduct) {
    delete savedProduct.Category;
    savedProduct.save(req.body, function (err) {
      if (err) {
        return res.unknown();
      }
      res.json({status: "success"});
    });
  });
});

/**
 * Bu fonksiyon id si parametre olarak gönderilen ürünü siler
 */
project.app.delete("/product/:id", function (req, res) {
  Product.find({id: req.params.id}).remove(function (err) {
    if (err) {
      return res.unknown();
    }
    res.json({status: "success"});
  })
});

/**
 * Verilen id'ye göre ürün getirir.
 */
project.app.get("/product/:id", function (req, res) {
  Product.one({id: req.params.id}, function (err, product) {
    if (err) {
      return res.unknown();
    }
    product.categoryId = product.Category;
    delete product.Category;
    res.json(product);
  });
});

/**
 * IMAGE UPLOAD SERVICE TEST
 */
project.app.post("/testUpload", multipartyMiddleware, function (req, res) {
  var file = req.files.imageURL;
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
        res.json(image);
      });
    });
  });
});