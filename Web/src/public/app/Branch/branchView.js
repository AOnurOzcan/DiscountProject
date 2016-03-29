define([
  'backbone',
  'handlebars',
  'text!Branch/addBranchTemplate.html',
  'text!Branch/listBranchTemplate.html'], function (Backbone, Handlebars, AddBranchTemplate, ListBranchTemplate) {

  var addBranchTemplate = Handlebars.compile(AddBranchTemplate);
  var listBranchTemplate = Handlebars.compile(ListBranchTemplate);

  var BranchModel = Backbone.Model.extend({
    urlRoot: "/branch"
  });

  var BranchCollection = Backbone.Collection.extend({
    url: "/branch",
    model: BranchModel
  });

  var CityCollection = Backbone.Collection.extend({
    url: "/city/all"
  });

  var AddBranchView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #saveBranchButton': 'saveBranch'
    },
    validation: function () {
      var that = this;
      $('#addBranchForm').form({
        onSuccess: function (e) {
          that.addBranch(e);
        },
        fields: {
          name: {
            identifier: 'name',
            rules: [
              {
                type: 'empty',
                prompt: 'Şube adı alanı boş geçilemez!'
              },
              {
                type: 'minLength[6]',
                prompt: 'Şube adı alanı minimum 6 karakter olmalı!'
              }
            ]
          },
          address: {
            identifier: 'address',
            rules: [
              {
                type: 'empty',
                prompt: 'Bu alan boş geçilemez!'
              }
            ]
          },
          cityId: {
            identifier: 'cityId',
            rules: [
              {
                type: 'empty',
                prompt: 'Bu alan boş geçilemez!'
              }
            ]
          },
          phone: {
            identifier: 'phone',
            rules: [
              {
                type: 'empty',
                prompt: 'Bu alan boş geçilemez!'
              }
            ]
          },
          workingHours: {
            identifier: 'workingHours',
            rules: [
              {
                type: 'empty',
                prompt: 'Bu alan boş geçilemez!'
              }
            ]
          },
          locationURL: {
            identifier: 'locationURL',
            rules: [
              {
                type: 'empty',
                prompt: 'Bu alan boş geçilemez!'
              }
            ]
          }
        }
      });
    },
    initialize: function () {
      this.cityCollection = new CityCollection();
    },
    addBranch: function (e) {
      e.preventDefault();
      var that = this;
      var form = this.form();
      var values = form.getValues;
      var branchModel = new BranchModel();
      branchModel.save(values, {
        success: function () {
          that.render();
          alertify.success("Şube başarıyla eklendi.");
        }
      });
    },
    saveBranch: function (e) {
      e.preventDefault();
      var form = this.form();
      var values = form.getValues;
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
      if (this.params == undefined) {
        this.$el.html(addBranchTemplate({cities: this.cityCollection.toJSON()}));
        this.validation();
        $('.ui.dropdown').dropdown();
        function initialize() {
          var mapProp = {
            center: new google.maps.LatLng(51.508742, -0.120850),
            zoom: 5,
            mapTypeId: google.maps.MapTypeId.ROADMAP

          };
          var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);
        }

        google.maps.event.addDomListener(window, 'load', initialize);
      } else {
        var branchModel = new BranchModel({id: this.params.branchId});
        branchModel.fetch({
          success: function (branch) {
            that.$el.html(addBranchTemplate({branch: branch.toJSON(), cities: that.cityCollection.toJSON()}));
            that.validation();
            that.form().setValues(branch.toJSON());
            $('.ui.dropdown').dropdown();
          }
        });
      }
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