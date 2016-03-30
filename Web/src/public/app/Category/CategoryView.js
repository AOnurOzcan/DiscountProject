define([
  'backbone',
  'handlebars',
  'text!Category/ChooseCategoryTemplate.html',
  'text!Category/AddMainCategoryTemplate.html',
  'text!Category/AddSubCategoryTemplate.html',
  'text!Category/ListCategoryTemplate.html',
  'text!Category/TableTemplate.html'], function (Backbone,
                                                 Handlebars,
                                                 ChooseCategoryTemplate,
                                                 AddMainCategoryTemplate,
                                                 AddSubCategoryTemplate,
                                                 ListCategoryTemplate,
                                                 TableTemplate) {

  //------------------------ Templates --------------------//
  var chooseCategoryTemplate = Handlebars.compile(ChooseCategoryTemplate);
  var addMainCategoryTemplate = Handlebars.compile(AddMainCategoryTemplate);
  var addSubCategoryTemplate = Handlebars.compile(AddSubCategoryTemplate);
  var listCategoryTemplate = Handlebars.compile(ListCategoryTemplate);
  var tableTemplate = Handlebars.compile(TableTemplate);

  //--------------------- Models & Collections --------------//
  var PreferenceModel = Backbone.Model.extend({
    urlRoot: "/preferences"
  });
  var MainCategoryModel = Backbone.Model.extend({
    urlRoot: "/mainCategory"
  });
  var SubCategoryModel = Backbone.Model.extend({
    urlRoot: "/subCategory"
  });

  var MainCategoryCollection = Backbone.Collection.extend({
    url: "/mainCategory",
    initialize: function (opts) {
      if (opts != undefined) {
        this.url = opts.url;
      }
    }
  });

  var SubCategoryCollection = Backbone.Collection.extend({
    url: "/subCategory",
    model: SubCategoryModel,
    initialize: function (properties) {
      if (properties != undefined) {
        this.url = properties.url
      }
    }
  });
  var PreferenceCollection = Backbone.Collection.extend({
    url: "/preferences"
  });

  //------------------------- Views -------------------------//
  var ChooseCategoryView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #savePreferences': 'savePreferences',
      'click .subCategoryCheckbox': 'checkboxChange'
    },
    initialize: function () {
      this.allCategories = new MainCategoryCollection({url: "/mainCategoryWithSubs"});
      this.preferencesCollection = new PreferenceCollection();
      this.addArray = [];
      this.removeArray = [];
    },
    checkboxChange: function (e) {
      var checkbox = e.currentTarget;

      var preference = this.preferencesCollection.find(function (preference) {
        return preference.get("categoryId") == checkbox.value;
      });

      if (preference == undefined) { //Değiştirilen checkbox listede yok ise
        if (checkbox.checked) { //checklendiyse
          this.addArray.push(checkbox.value);
        } else { //checki kaldırıldıysa
          this.addArray.splice(this.addArray.indexOf(checkbox.value), 1);
        }
      } else { //listede var ise
        if (!checkbox.checked) { // tiki kaldırıldıysa
          this.removeArray.push(checkbox.value);
        } else {
          this.removeArray.splice(this.removeArray.indexOf(checkbox.value), 1);
        }
      }
    },
    savePreferences: function (e) {
      $(e.currentTarget).attr("disabled", true).addClass("loading");
      var that = this;
      if (this.addArray.length == 0 && this.removeArray.length == 0) {
        $(e.currentTarget).attr("disabled", false).removeClass("loading");
        return alertify.error("Güncellenecek veri yok.");
      }
      var preferenceModel = new PreferenceModel();
      preferenceModel.save({addArray: this.addArray, removeArray: this.removeArray}, {
        success: function () {
          that.preferencesCollection.fetch({
            success: function () {
              that.addArray.length = 0;
              that.removeArray.length = 0;
              alertify.success("Tercihleriniz başarıyla kaydedildi!");
              $(e.currentTarget).attr("disabled", false).removeClass("loading");
            }
          });
        }
      });
    },
    render: function () {
      this.$el.html(chooseCategoryTemplate({categories: this.allCategories.toJSON()}));
      this.preferencesCollection.toJSON().forEach(function (preference) {
        $('input:checkbox[value="' + preference.categoryId + '"]').attr("checked", true);
      });
      $('.ui.accordion').accordion();
    }
  });

  var AddMainCategoryView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    validation: function () {
      var that = this;
      $('#addMainCategoryForm').form({
        onSuccess: function (e) {
          that.saveMainCategory(e);
        },
        fields: {
          categoryName: {
            identifier: 'categoryName',
            rules: [
              {
                type: 'empty',
                prompt: 'Ana kategori alanı boş geçilemez!'
              }
            ]
          }
        }
      });
    },
    saveMainCategory: function (e) {
      e.preventDefault();
      var that = this;
      var values = this.form().getValues;
      var mainCategoryModel = new MainCategoryModel();
      mainCategoryModel.save(values, {
        success: function () {
          if (that.params == undefined) {
            alertify.success("Ana kategori ekleme başarılı");
          } else {
            alertify.success("Ana kategori güncelleme başarılı");
          }
          window.location.hash = "category/list";
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addMainCategoryTemplate());
        this.validation();
      } else {
        var categoryModel = new MainCategoryModel({id: this.params.mainCategoryId});
        categoryModel.fetch({
          success: function (mainCategory) {
            that.$el.html(addMainCategoryTemplate({mainCategory: mainCategory.toJSON()}));
            that.form().setValues(mainCategory.toJSON());
            that.validation();
          }
        });
      }
    }
  });

  var AddSubCategoryView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    validation: function () {
      var that = this;
      $('#addSubCategoryForm').form({
        onSuccess: function (e) {
          that.saveSubCategory(e);
        },
        fields: {
          categoryName: {
            identifier: 'categoryName',
            rules: [
              {
                type: 'empty',
                prompt: 'Alt kategori alanı boş geçilemez!'
              }
            ]
          },
          parentCategory: {
            identifier: 'parentCategory',
            rules: [
              {
                type: 'empty',
                prompt: 'Ana kategori alanı boş geçilemez!'
              }
            ]
          }
        }
      });
    },
    initialize: function () {
      this.mainCategoryCollection = new MainCategoryCollection();
    },
    saveSubCategory: function (e) {
      e.preventDefault();
      var that = this;
      var values = this.form().getValues;
      var subCategoryModel = new SubCategoryModel();
      subCategoryModel.save(values, {
        success: function () {
          if (that.params == undefined) {
            alertify.success("Alt kategori ekleme başarılı");
          } else {
            alertify.success("Alt kategori güncelleme başarılı");
          }
          window.location.hash = "category/list";
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addSubCategoryTemplate({mainCategories: this.mainCategoryCollection.toJSON()}));
        $('.ui.dropdown').dropdown();
        this.validation();
      } else {
        var categoryModel = new SubCategoryModel({id: this.params.subCategoryId});
        categoryModel.fetch({
          success: function (subCategory) {
            that.$el.html(addSubCategoryTemplate({
              subCategory: subCategory.toJSON(),
              mainCategories: that.mainCategoryCollection.toJSON()
            }));
            that.form().setValues(subCategory.toJSON());
            $("#subCategorySelect").dropdown("set selected", subCategory.toJSON().parentCategory);
            $('.ui.dropdown').dropdown();
            that.validation();
          }
        });
      }
    }
  });

  var ListCategoryView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'change #mainCategorySelect': 'changeMainCategory',
      'click #editMainCategoryButton': 'editMainCategory',
      'click #deleteCategoryButton': 'deleteSubCategory',
      'click #deleteMainCategoryButton': 'deleteMainCategory'
    },
    initialize: function () {
      this.mainCategoryCollection = new MainCategoryCollection();
    },
    changeMainCategory: function () {
      var that = this;
      var mainCategoryId = $("#mainCategorySelect").val().trim();
      if (mainCategoryId != "") {
        $("#deleteMainCategoryButton").removeClass("disabled");
        $("#editMainCategoryButton").removeClass("disabled");
        this.subCategories = new SubCategoryCollection({url: "/subCategories/" + mainCategoryId});
        this.subCategories.fetch({
          success: function (subCategories) {
            that.$el.find("tbody").html(tableTemplate({subCategories: subCategories.toJSON()}));
          }
        });
      }

    },
    editMainCategory: function () {
      var mainCategoryId = $("#mainCategorySelect").val();
      if (mainCategoryId == "") {
        alertify.error("Düzenleyeceğiniz ana kategoriyi seçiniz")
      } else {
        window.location.hash = "category/main/edit/" + mainCategoryId;
      }
    },
    deleteSubCategory: function (e) {
      var subCategoryId = $(e.currentTarget).attr("data-id");
      this.deleteItem(this.subCategories, subCategoryId);
    },
    deleteMainCategory: function (e) {
      var mainCategoryId = $("#mainCategorySelect").val();
      this.deleteItem(this.mainCategoryCollection, mainCategoryId);
    },

    render: function () {
      this.$el.html(listCategoryTemplate({mainCategories: this.mainCategoryCollection.toJSON()}));
      $('.ui.dropdown').dropdown();
    }
  });

  return {
    ChooseCategoryView: ChooseCategoryView,
    AddMainCategoryView: AddMainCategoryView,
    AddSubCategoryView: AddSubCategoryView,
    ListCategoryView: ListCategoryView
  }
});