define([
  'backbone',
  'handlebars',
  'text!Product/addProductTemplate.html',
  'text!Product/listProductTemplate.html',
  'text!Product/subCategorySelectTemplate.html'], function (Backbone, Handlebars, AddProductTemplate, ListProductTemplate, SubCategorySelectTemplate) {

  var addProductTemplate = Handlebars.compile(AddProductTemplate);
  var listProductTemplate = Handlebars.compile(ListProductTemplate);
  var subCategorySelectTemplate = Handlebars.compile(SubCategorySelectTemplate);

  var MainCategoryCollection = Backbone.Collection.extend({
    url: "/getMainCategories"
  });

  var SubCategoryCollection = Backbone.Collection.extend({
    initialize: function (models, options) {
      this.parentCategory = options.parentCategoryId;
    },
    url: function () {
      return "/getSubCategories/" + this.parentCategory;
    }
  });

  var ProductsCollection = Backbone.Collection.extend({
    url: "/allProducts"
  });


  var AddProductView = core.CommonView.extend({
    autoLoad: true,
    el: $(".page"),
    events: {
      'click #addProductButton': 'addProduct',
      'change #productMainCategorySelect': 'getSubCategories'
    },
    initialize: function () {
      this.mainCategoryCollection = new MainCategoryCollection();
      this.subCategorySelectView = new SubCategorySelectView();
    },
    getSubCategories: function () {
      var that = this;
      var subCategoryCollection = new SubCategoryCollection([], {parentCategoryId: $("#productMainCategorySelect").val()});
      subCategoryCollection.fetch({
        success: function (subCategories) {
          that.subCategorySelectView.render(subCategories.toJSON());
        }
      });

    },
    addProduct: function (e) {
      e.preventDefault();
      var form = this.form();
      var values = form.getValues;
      console.log($("input:file").val());
    },
    render: function () {
      this.$el.html(addProductTemplate({
        mainCategories: this.mainCategoryCollection.toJSON()
      }));
    }
  });

  var ListProductView = core.CommonView.extend({
    autoLoad: true,
    el: $(".page"),
    initialize: function () {
      this.productCollection = new ProductsCollection();
    },
    render: function () {
      this.$el.html(listProductTemplate({products: this.productCollection.toJSON()}));
    }
  });

  var SubCategorySelectView = Backbone.View.extend({
    render: function (subCategories) {
      $("#productSubCategorySelect").html(subCategorySelectTemplate({subCategories: subCategories}));
    }
  });

  return {
    AddProductView: AddProductView,
    ListProductView: ListProductView
  }

});