define(['backbone', 'handlebars', 'text!Branch/addBranchTemplate.html', 'text!Branch/listBranchTemplate.html'], function (Backbone, Handlebars, AddBranchTemplate, ListBranchTemplate) {

  var addBranchTemplate = Handlebars.compile(AddBranchTemplate);
  var listBranchTemplate = Handlebars.compile(ListBranchTemplate);

  var BranchModel = Backbone.Model.extend({
    urlRoot: "/branch"
  });

  var BranchCollection = Backbone.Collection.extend({
    url: "/getBranches",
    model: BranchModel
  });

  var CityCollection = Backbone.Collection.extend({
    url: "/city/all"
  });

  var AddBranchView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #addBranchButton': 'addBranch',
      'click #saveBranchButton': 'saveBranch'
    },
    validation: function (property, value, object) {
      switch (property) {
        case "name":
          if (value == "") {
            return "Bu alan boş geçilemez!";
          }
          return true;
        case "address":
          if (value == "") {
            return "Bu alan boş geçilemez!";
          }
          return true;
        case "cityId":
          if (value == "") {
            return "Bu alan boş geçilemez!";
          }
          return true;
        case "phone":
          if (value == "") {
            return "Bu alan boş geçilemez!";
          }
          return true;
        case "workingHours":
          if (value == "") {
            return "Bu alan boş geçilemez!";
          }
          return true;
        case "locationURL":
          if (value == "") {
            return "Bu alan boş geçilemez!";
          }
          return true;
        default:
          return true;
      }
    },
    initialize: function () {
      this.cityCollection = new CityCollection();
    },
    addBranch: function (e) {
      e.preventDefault();
      var that = this;
      var form = this.form();
      var values = form.executeValidation(this.validation);
      if (values == null) return;
      var branchModel = new BranchModel();
      branchModel.save(values, {
        success: function () {
          that.render();
        },
        error: function () {
          alert("HATAAAA!");
        }
      });
    },
    saveBranch: function (e) {
      e.preventDefault();
      var form = this.form();
      var values = form.executeValidation(this.validation);
      if (values == null) return;
      var model = new BranchModel();
      model.save(values, {
        success: function () {
          window.location.hash = "branch/list";
        },
        error: function () {
          alert("hataa");
        }
      });
    },
    render: function () {
      var that = this;

      if (this.params.branchId == undefined) {
        this.$el.html(addBranchTemplate({cities: this.cityCollection.toJSON()}));
      } else {
        var branchModel = new BranchModel({id: this.params.branchId});
        branchModel.fetch({
          success: function (branch) {
            that.$el.html(addBranchTemplate({branch: branch.toJSON(), cities: that.cityCollection.toJSON()}));
            that.form().setValues(branch.toJSON());
          },
          error: function () {
            alert("hataaa branch");
          }
        });
      }
      $('.ui.dropdown').dropdown();
    }
  });

  var ListBranchView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #deleteBranchButton': 'deleteBranch'
    },
    initialize: function () {
      this.branchCollection = new BranchCollection();
    },
    deleteBranch: function (e) {
      var branchId = $(e.currentTarget).attr("data-id");
      this.deleteItem(this.branchCollection, branchId);
    },
    render: function () {
      this.$el.html(listBranchTemplate({branches: this.branchCollection.toJSON()}));
    }
  });

  return {
    AddBranchView: AddBranchView,
    ListBranchView: ListBranchView
  }

});