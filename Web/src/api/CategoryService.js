var Category = require("../model/Category");

/**
 * Bu fonksiyon ana kategori ekler.
 */
project.app.post("/addMainCategory", function (req, res) {

  var mainCategory = {};
  mainCategory.categoryName = req.body.categoryName;

  Category.create(mainCategory, function (err, mainCategory) {
    if (err) {
      return res.unknown();
    }

    res.json(mainCategory);
  });
});

/**
 * Bu fonksiyon alt kategori ekler.
 */
project.app.post("/addSubCategory", function (req, res) {

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

/**
 * Bu fonksiyon verilen üst kategori Id'sine göre tüm alt kategorileri getirir.
 */
project.app.get("/getSubCategories/:parentCategoryId", function (req, res) {

  var parentCategoryId = req.params.parentCategoryId;

  Category.find({parentCategory: parentCategoryId}, function (err, subCategories) {
    if (err) {
      return res.unknown();
    }

    res.json(subCategories);
  });
});

/**
 * Bu fonksiyon verilen üst kategori Id'sine göre tüm alt kategorileri getirir.
 */
project.app.get("/getMainCategories", function (req, res) {

  Category.find({parentCategory: null}, function (err, mainCategories) {
    if (err) {
      return res.unknown();
    }

    res.json(mainCategories);
  });
});

/**
 * Bu fonksiyon tüm kategorileri getirir.
 */
project.app.get("/getAllCategories", function (req, res) {

  Category.find(function (err, categories) {

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