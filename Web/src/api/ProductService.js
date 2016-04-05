var Product = require("../model/Product");
var Category = require("../model/Category");

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

      products.asyncForEach(function (product, done) {
        product.getCategory(function (err, category) {
          if (err) return res.unknown();
          product.categoryId = category;
          done();
        });
      }, function () {
        res.json(products);
      });
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
    Category.get(product.categoryId, function (err, category) {
      if (err) {
        return res.unknown();
      }
      product.parentCategory = category.parentCategory;
      res.json(product);
    });
  });
});
