define([
  'backbone',
  'handlebars',
  'text!Product/addProductTemplate.html',
  'text!Product/listProductTemplate.html',
  'text!Product/subCategorySelectTemplate.html'], function (Backbone, Handlebars, AddProductTemplate, ListProductTemplate, SubCategorySelectTemplate) {

  //--------------- Templates --------------//
  var addProductTemplate = Handlebars.compile(AddProductTemplate);
  var listProductTemplate = Handlebars.compile(ListProductTemplate);
  var subCategorySelectTemplate = Handlebars.compile(SubCategorySelectTemplate);

  //--------------- Models & Collections --------------//
  var Product = Backbone.Model.extend({
    urlRoot: "/product/"
  });
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
    url: "/product",
    model: Product
  });
  var ImageCollection = Backbone.Collection.extend({
    url: "/files/all"
  });

  // Ürün ekleme ve düzenleme
  var AddProductView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #addProductButton': 'addProduct',
      'change #productMainCategorySelect': 'renderSubCategories',
      'click #selectImage': 'openModal'
    },
    initialize: function () {
      this.mainCategoryCollection = new MainCategoryCollection();
      this.imageCollection = new ImageCollection();
    },
    openModal: function (e) {
      e.preventDefault();
      var that = this;
      $('.ui.modal').modal('show');
      $(".chooseImage").on('click', function (e) {
        that.copyURL(e);
      });
    },
    copyURL: function (e) {
      var imageURL = $(e.currentTarget).find("img").attr('src');
      $("#imageURL").val(imageURL);
      $('.ui.modal').modal('hide');
    },
    renderSubCategories: function () {
      var subCategorySelectBox = $("#productSubCategorySelect");
      subCategorySelectBox.parent().addClass("loading");
      new SubCategoryCollection([], {parentCategoryId: $("#productMainCategorySelect").val()}).fetch({
        success: function (subCategories) {
          subCategorySelectBox.html(subCategorySelectTemplate({subCategories: subCategories.toJSON()}));
          $("#productSubCategorySelect").dropdown('set value', "").dropdown('set text', "Seçiniz");
          subCategorySelectBox.parent().removeClass("loading");
        }
      });
    },
    addProduct: function (e) {
      e.preventDefault();
      var that = this;
      var form = this.form();
      var formValues = form.getValues;
      var product = new Product();
      product.save(formValues, {
        success: function () {
          that.render;
          alert("ürün başarıyla eklendi");
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addProductTemplate({
          mainCategories: this.mainCategoryCollection.toJSON(),
          images: this.imageCollection.toJSON()
        }));
        $('.ui.dropdown').dropdown();
      } else {
        var product = new Product({id: this.params.productId});
        product.fetch({
          success: function (product) {
            that.$el.html(addProductTemplate({
              product: product.toJSON(),
              mainCategories: that.mainCategoryCollection.toJSON()
            }));
            $('.ui.dropdown').dropdown();
            that.form().setValues(product.toJSON());
          }
        });
      }
    }
  });

  // Ürün listeleme
  var ListProductView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #deleteProductButton': 'deleteProduct',
      'click #editProductButton': 'editProduct'
    },
    initialize: function () {
      this.productCollection = new ProductsCollection();
    },

    deleteProduct: function (e) {
      var productId = $(e.currentTarget).attr("data-id");
      this.deleteItem(this.productCollection, productId, $("#listProductForm"));
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