var Category = project.db.define('Category', {
  id: {type: 'serial'},
  categoryName: String
});

Category.hasOne('parent', Category, {field: 'parentCategory'});

module.exports = Category;