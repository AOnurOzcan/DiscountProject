define([
  'backbone',
  'handlebars',
  'text!Branch/addBranchTemplate.html',
  'text!Branch/listBranchTemplate.html', 'gmaps'], function (Backbone, Handlebars, AddBranchTemplate, ListBranchTemplate, GMaps) {

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
      'click #selectLocation': 'selectLocation'
    },
    validation: function () {
      var that = this;
      $('#addBranchForm').form({
        onSuccess: function (e) {
          that.saveBranch(e);
        },
        fields: {
          name: {
            identifier: 'name',
            rules: [
              {
                type: 'empty',
                prompt: 'Şube adı alanı boş geçilemez!'
              }
            ]
          },
          address: {
            identifier: 'address',
            rules: [
              {
                type: 'empty',
                prompt: 'Adres alanı boş geçilemez!'
              }
            ]
          },
          cityId: {
            identifier: 'cityId',
            rules: [
              {
                type: 'empty',
                prompt: 'Şehir alanı boş geçilemez!'
              }
            ]
          },
          phone: {
            identifier: 'phone',
            rules: [
              {
                type: 'empty',
                prompt: 'Telefon alanı boş geçilemez!'
              }
            ]
          },
          workingHours: {
            identifier: 'workingHours',
            rules: [
              {
                type: 'empty',
                prompt: 'Çalışma Saatleri alanı boş geçilemez!'
              }
            ]
          },
          locationURL: {
            identifier: 'locationURL',
            rules: [
              {
                type: 'empty',
                prompt: 'Konum alanı boş geçilemez!'
              },
              {
                type: 'url',
                prompt: 'Geçersiz URL!'
              }
            ]
          }
        }
      });
    },
    initialize: function () {
      this.cityCollection = new CityCollection();
    },
    saveBranch: function (e) {
      e.preventDefault();
      var form = this.form();
      var values = form.getValues;
      var model = new BranchModel();
      model.save(values, {
        success: function () {
          window.location.hash = "branch/list";
          if (this.params == undefined) {
            alertify.success("Şube ekleme başarılı");
          } else {
            alertify.success("Şube güncelleme başarılı");
          }
        }
      });
    },
    selectLocation: function () {
      var that = this;
      $("#locationModal").modal({
        observeChanges: true,
        onVisible: function () {
          var latLon = $("input[name=locationURL]").val().split(',');
          var lat = latLon[0];
          var lon = latLon[1];
          that.googleMapsInitialize(lat, lon);
        }
      }).modal('show');
    },
    googleMapsInitialize: function (lat, lon) {
      //Google maps initializing
      var lat = lat || 43.765346;
      var lng = lon || 30.360312;
      var map = new GMaps({
        el: '#googleMap',
        lat: lat,
        lng: lng,
        click: function (e) {
          map.addMarker({
            lat: e.latLng.lat(),
            lng: e.latLng.lng(),
            title: 'Şube'
          });
          $("input[name=locationURL]").val(e.latLng.lat() + "," + e.latLng.lng());
        },
        open: function () {
          alert("aasd");
        }
      });
      map.addMarker({
        lat: lat,
        lng: lon,
        title: 'Şube'
      });
      map.addControl({
        position: 'top_right',
        content: 'Konumuma git',
        style: {
          margin: '5px',
          padding: '2px 7px',
          border: 'solid 1px #717B87',
          background: '#fff'
        },
        events: {
          click: function () {
            GMaps.geolocate({
              success: function (position) {
                map.setCenter(position.coords.latitude, position.coords.longitude);
              },
              error: function (error) {
                alert('Konum bulma başarısız. Sebep : ' + error.message);
              },
              not_supported: function () {
                alert("Tarayıcınız konum bulmayı desteklemiyor. Lütfen modern bir tarayıcı kullanınız.");
              }
            });
          }
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addBranchTemplate({cities: this.cityCollection.toJSON()}));
        this.validation();
        $('#cityDropdown').dropdown();

      } else {
        var branchModel = new BranchModel({id: this.params.branchId});
        branchModel.fetch({
          success: function (branch) {
            that.$el.html(addBranchTemplate({branch: branch.toJSON(), cities: that.cityCollection.toJSON()}));
            that.validation();
            that.form().setValues(branch.toJSON());
            $('#cityDropdown').dropdown();
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