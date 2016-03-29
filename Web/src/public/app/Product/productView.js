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
    url: "/mainCategory"
  });
  var SubCategoryCollection = Backbone.Collection.extend({
    initialize: function (models, options) {
      this.parentCategory = options.parentCategoryId;
    },
    url: function () {
      return "/subCategories/" + this.parentCategory;
    }
  });
  var ProductsCollection = Backbone.Collection.extend({
    url: "/product",
    model: Product
  });
  var ImageCollection = Backbone.Collection.extend({
    url: "/files"
  });

  // Ürün ekleme ve düzenleme
  var AddProductView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #addProductButton': 'addOrUpdateProduct',
      'click #updateProductButton': 'addOrUpdateProduct',
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
      $('#choosePictureModal').modal('show');
      $(".chooseImage").on('click', function (e) {
        that.copyURL(e);
      });
    },
    copyURL: function (e) {
      var imageURL = $(e.currentTarget).find("img").attr('src');
      $("#imageURL").val(imageURL);
      $('#choosePictureModal').modal('hide');
    },
    renderSubCategories: function () {
      var that = this;
      var subCategorySelectBox = this.$("#productSubCategorySelect");
      var mainCategorySelectBox = this.$("#productMainCategorySelect");
      subCategorySelectBox.parent().addClass("loading");

      var subCategoryCollectin = new SubCategoryCollection([], {parentCategoryId: mainCategorySelectBox.val()});
      subCategoryCollectin.fetch({
        success: function (subCategories) {
          subCategorySelectBox.html(subCategorySelectTemplate({subCategories: subCategories.toJSON()}));
          if (that.params == undefined) {
            subCategorySelectBox.dropdown('set value', "").dropdown('set text', "Seçiniz");
          } else {
            if ($(".deneme").is('select')) {
              that.form().setValues(that.product.toJSON());
              subCategorySelectBox.dropdown();
            } else {
              subCategorySelectBox.dropdown('set value', "").dropdown('set text', "Seçiniz");
            }
          }
          subCategorySelectBox.parent().removeClass("loading");
        }
      });
    },
    addOrUpdateProduct: function (e) {
      e.preventDefault();
      var that = this;
      var form = this.form();
      var formValues = form.getValues;
      var product = new Product();
      product.save(formValues, {
        success: function () {
          if (that.params == undefined) {
            alertify.success("Ürün başarıyla eklendi");
          } else {
            alertify.success("Ürün başarıyla güncellendi");
          }
          window.location.hash = "product/list";
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
            that.product = product;
            that.$el.html(addProductTemplate({
              product: product.toJSON(),
              mainCategories: that.mainCategoryCollection.toJSON(),
              images: that.imageCollection.toJSON()
            }));
            $("#productMainCategorySelect").dropdown("set selected", product.toJSON().categoryId.parentCategory);
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
    editProduct: function (e) {
      $(e.currentTarget).addClass("loading");
    },
    deleteProduct: function (e) {
      $(e.currentTarget).addClass("loading");
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