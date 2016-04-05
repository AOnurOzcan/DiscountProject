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
          }
        }
      });
    },
    initialize: function () {
      this.cityCollection = new CityCollection();
      this.mapInitialized = false;
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
    selectLocation: function (e) {
      e.preventDefault();
      var that = this;
      $("#locationModal").modal({
        autofocus: false,
        detachable: false,
        onVisible: function () {
          var coordinatesInput = $("input[name=coordinates]");
          var latLon = coordinatesInput.val().split(',');
          var lat = latLon[0];
          var lon = latLon[1];
          if (that.mapInitialized == false) {
            that.googleMapsInitialize(lat, lon);
            if (coordinatesInput.val().trim() != "") {
              that.map.setZoom(15);
              that.map.setCenter(lat, lon);
            }
          } else {
            that.map.setCenter(lat, lon);
            that.map.setZoom(14);
          }
        }
      }).modal('show');
    },
    searchAddress: function () {
      var that = this;
      GMaps.geocode({
        address: $('#searchAddressInput').val(),
        callback: function (results, status) {
          if (status == 'OK') {
            var latlng = results[0].geometry.location;
            that.map.setCenter(latlng.lat(), latlng.lng());
            that.map.setZoom(13);
          }
        }
      });
    },
    googleMapsInitialize: function (lat, lon) {
      var that = this;
      //Google maps initializing
      var lat = lat || 39.1667;
      var lng = lon || 35.6667;
      this.map = new GMaps({
        el: '#googleMap',
        zoom: 6,
        lat: lat,
        lng: lng,
        click: function (e) {
          that.map.removeMarkers();
          that.map.addMarker({
            lat: e.latLng.lat(),
            lng: e.latLng.lng(),
            title: 'Şube'
          });
          $("input[name=coordinates]").val(e.latLng.lat() + "," + e.latLng.lng());
        }
      });
      this.mapInitialized = true;
      this.map.addMarker({
        lat: lat,
        lng: lon,
        title: 'Şube'
      });
      this.map.addControl({
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
                that.map.setCenter(position.coords.latitude, position.coords.longitude);
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
      this.mapInitialized = false;
      if (this.params == undefined) {
        this.$el.html(addBranchTemplate({cities: this.cityCollection.toJSON()}));
        $("#searchAddressButton").on('click', function () {
          that.searchAddress();
        });
        this.validation();
        $('#cityDropdown').dropdown();

      } else {
        var branchModel = new BranchModel({id: this.params.branchId});
        branchModel.fetch({
          success: function (branch) {
            that.$el.html(addBranchTemplate({branch: branch.toJSON(), cities: that.cityCollection.toJSON()}));
            $("#searchAddressButton").on('click', function () {
              that.searchAddress();
            });
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