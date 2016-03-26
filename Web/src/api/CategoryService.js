var Category = require("../model/Category");
var CompanyCategory = require("../model/CompanyCategory");

/**
 * Bu fonksiyon ana kategori ekler.
 */
project.app.post("/mainCategory", function (req, res) {

  var mainCategory = {};
  mainCategory.categoryName = req.body.categoryName;

  Category.create(mainCategory, function (err, mainCategory) {
    if (err) {
      return res.unknown();
    }

    res.json(mainCategory);
  });
});

project.app.put("/mainCategory/:id", function (req, res) {
  Category.get(req.params.id, function (err, category) {
    category.save(req.body, function (err, savedCategory) {
      if (err) {
        return res.unknown();
      }
      res.json({status: "success"});
    });
  });
});

/**
 * Bu fonksiyon tüm ana kategorileri getirir.
 */
project.app.get("/mainCategory", function (req, res) {

  Category.find({parentCategory: null}, function (err, mainCategories) {
    if (err) {
      return res.unknown();
    }

    res.json(mainCategories);
  });
});

project.app.get("/mainCategory/:id", function (req, res) {
  Category.one({parentCategory: null, id: req.params.id}, function (err, mainCategory) {
    if (err) {
      return res.unknown();
    }
    res.json(mainCategory);
  });
});

/**
 * Bu fonksiyon tüm kategorileri, alt kategorileriyle birlikte getirir.
 */
project.app.get("/mainCategoryWithSubs", function (req, res) {

  Category.find({parentCategory: null}, function (err, categories) {

    if (err) {
      return res.unknown();
    }
    categories.asyncForEach(function (category, done) {
      if (category.parentCategory == null) {
        category.getSubCategories(function (err, subCategories) {
          if (err) {
            return res.unknown();
          }
          category.subCategories = subCategories;
          done();
        });
      }
    }, function () {
      res.json(categories);
    });
  });
});

project.app.delete("/mainCategory/:id", function (req, res) {
  Category.find({id: req.params.id}).remove(function (err) {
    if (err) {
      return res.unknown();
    }
    res.json({status: "success"});
  });
});

// Sub Categories

/**
 * Bu fonksiyon alt kategori ekler.
 */
project.app.post("/subCategory", function (req, res) {

  var subCategory = {};
  subCategory.categoryName = req.body.categoryName;
  subCategory.parentCategory = req.body.parentCategory;

  Category.create(subCategory, function (err, subCategory) {
    if (err) {
      return res.unknown();
    }

    res.json(subCategory);
  });
});

project.app.put("/subCategory/:id", function (req, res) {
  Category.get(req.params.id, function (err, category) {
    category.save(req.body, function (err, savedCategory) {
      if (err) {
        return res.unknown();
      }
      res.json({status: "success"});
    });
  });
});

/**
 * Bu fonksiyon verilen üst kategori Id'sine göre tüm alt kategorileri getirir.
 */
project.app.get("/subCategories/:parentCategoryId", function (req, res) {

  var parentCategoryId = req.params.parentCategoryId;

  Category.find({parentCategory: parentCategoryId}, function (err, subCategories) {
    if (err) {
      return res.unknown();
    }

    res.json(subCategories);
  });
});

project.app.get("/subCategory/:id", function (req, res) {
  Category.get(req.params.id, function (err, subCategory) {
    if (err) {
      return res.unknown();
    }
    res.json(subCategory);
  });
});

project.app.delete("/subCategory/:id", function (req, res) {
  Category.find({id: req.params.id}).remove(function (err) {
    if (err) {
      return res.unknown();
    }
    res.send({status: "success"});
  });
});


/**
 * Firma faaliyet gösterdiği kategorileri seçip kaydettiğinde buraya düşer. Seçilen kategoriler kaydedilir.
 * İşareti kaldırılan kategoriler silinir.
 */
project.app.post("/preferences", function (req, res) {
  var addArray = req.body.addArray;
  var removeArray = req.body.removeArray;

  addArray.asyncForEach(function (preference, done) {
    CompanyCategory.create({companyId: req.session.admin.companyId, categoryId: preference}, function (err, result) {
      if (err) {
        return res.unknown();
      }

      done(result);
    });
  }, function (result) {
    removeArray.asyncForEach(function (preference, done) {
      CompanyCategory.find({categoryId: preference}).remove(function (err) {
        if (err) {
          return res.unknown();
        }
        done(result);
      });
    }, function () {
      res.json({status: true, result: result});
    });
  });
});

/**
 * Firmanın kaydettiği tercihleri döndürür.
 */
project.app.get("/preferences", function (req, res) {
  CompanyCategory.find({companyId: req.session.admin.companyId}, function (err, result) {
    if (err) {
      return res.unknown();
    }
    res.json(result);
  });
});

//------------------------------- MOBILE -------------------------------//

project.app.get("/getAllCategories", function (req, res) {

  Category.find({parentCategory: null}, function (err, categories) {

    if (err) {
      return res.unknown();
    }
    categories.forEach(function (category) {
      if (category.parentCategory != null) {
        category.parentCategory = category.parent;
        delete category['parent'];
      }
    });

    res.json(categories);
  });
});