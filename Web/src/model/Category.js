var Category = project.db.define('Category', {
  id: {type: 'serial'},
  categoryName: String
});

Category.hasOne('parent', Category, {field: 'parentCategory', reverse: 'subCategories'});

module.exports = Category;