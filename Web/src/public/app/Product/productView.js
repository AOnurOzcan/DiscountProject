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
    el: ".page",
    events: {
      'click #addProductButton': 'addProduct',
      'change #productMainCategorySelect': 'getSubCategories'
    },
    initialize: function () {
      this.mainCategoryCollection = new MainCategoryCollection();
    },
    getSubCategories: function () {
      var subCategoryCollection = new SubCategoryCollection([], {parentCategoryId: $("#productMainCategorySelect").val()});
      subCategoryCollection.fetch({
        success: function (subCategories) {
          $("#productSubCategorySelect").html(subCategorySelectTemplate({subCategories: subCategories.toJSON()}));
        }
      });

    },
    addProduct: function (e) {
      e.preventDefault();

      var form = $("#addProductForm")[0];
      var oData = new FormData(form);
      var oReq = new XMLHttpRequest();
      oReq.open("POST", "/testUpload", true);
      $("#addProductButton").attr("disabled", true);
      oReq.send(oData);

      oReq.onload = function (oEvent) {
        if (oReq.status == 200) {
          alert("Dosya yükleme başarılır");
          $("#addProductButton").attr("disabled", false);
        } else {
          alert("Dosya yükleme başarısız!!!");
          $("#addProductButton").attr("disabled", false);
        }
      };


    },
    render: function () {
      this.$el.html(addProductTemplate({
        mainCategories: this.mainCategoryCollection.toJSON()
      }));
      this.$el.on('change.bs.fileinput', function (abc) {
        var fileName = $("#imageURL").val().replace(/^.*\./, '');
        if (!(fileName == "png" || fileName == "jpg")) {
          $(".fileinput").fileinput('clear');
          alert("Yanlış Dosya Uzantısı");
        }
      });
    }
  });

  var ListProductView = core.CommonView.extend({
    autoLoad: true,
    el: ".page",
    initialize: function () {
      this.productCollection = new ProductsCollection();
    },
    render: function () {
      this.$el.html(listProductTemplate({products: this.productCollection.toJSON()}));
    }
  });

  return {
    AddProductView: AddProductView,
    ListProductView: ListProductView
  }

});