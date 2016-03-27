define([
  'backbone',
  'handlebars',
  'text!Notification/AddNotificationTemplate.html',
  'text!Notification/ListNotificationTemplate.html',
  'text!Notification/SendedNotificationTemplate.html'], function (Backbone,
                                                                  Handlebars,
                                                                  AddNotificationTemplate,
                                                                  ListNotificationTemplate,
                                                                  SendedNotificationTemplate) {

  var addNotificationTemplate = Handlebars.compile(AddNotificationTemplate);
  var listNotificationTemplate = Handlebars.compile(ListNotificationTemplate);
  var sendedNotificationTemplate = Handlebars.compile(SendedNotificationTemplate);

  //------------------------ Models & Collections --------------------------//
  var NotificationCollection = Backbone.Collection.extend({
    url: '/notification',
    initialize: function (properties) {
      if (properties != undefined && properties.url != "") {
        this.url = properties.url;
      }
    }
  });
  var NotificationModel = Backbone.Model.extend({
    urlRoot: '/notification',
    initialize: function (properties) {
      if (properties != undefined && properties.url != undefined) {
        this.urlRoot = properties.url;
      }
    }
  });
  var ProductCollection = Backbone.Collection.extend({
    url: '/product'
  });
  var BranchCollection = Backbone.Collection.extend({
    url: '/branch'
  });

  var AddNotificationView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #addNotificationButton': 'addOrEditNotification',
      'click #updateNotificationButton': 'addOrEditNotification'
    },
    initialize: function () {
      this.productCollection = new ProductCollection();
      this.branchCollection = new BranchCollection();
      this.addProductArray = [];
      this.removeProductArray = [];
      this.addBranchArray = [];
      this.removeBranchArray = [];
    },
    addOrEditNotification: function (e) {
      e.preventDefault();
      var that = this;
      var values = this.form().getValues;
      if (this.params == undefined) {
        values.products = values.products.split(',');
        values.branches = values.branches.split(',');
      } else {
        values.addProductArray = this.addProductArray;
        values.removeProductArray = this.removeProductArray;
        values.addBranchArray = this.addBranchArray;
        values.removeBranchArray = this.removeBranchArray;
      }
      var notification = new NotificationModel();
      notification.save(values, {
        success: function () {
          that.addProductArray.length = 0;
          that.removeProductArray.length = 0;
          that.removeBranchArray.length = 0;
          that.addBranchArray.length = 0;
          if (that.params == undefined) {
            alertify.success("Bildirim Ekleme Başarılı");
          } else {
            alertify.success("Bildirim Güncelleme Başarılı");
          }
          window.location.hash = "notification/list";
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addNotificationTemplate({
          products: this.productCollection.toJSON(),
          branches: this.branchCollection.toJSON()
        }));
        $(".ui.dropdown").dropdown({
          message: {
            noResults: 'Hiç sonuç bulunamadı.'
          }
        });
      } else {
        var notification = new NotificationModel({id: this.params.notificationId});
        notification.fetch({
          success: function (notification) {
            that.$el.html(addNotificationTemplate({
              notification: notification.toJSON(),
              products: that.productCollection.toJSON(),
              branches: that.branchCollection.toJSON()
            }));
            that.form().setValues(notification.toJSON());
            $(".ui.dropdown").dropdown({
              message: {
                noResults: 'Hiç sonuç bulunamadı.'
              }
            });
            notification.toJSON().productList.forEach(function (product) {
              $('#notificationProductDropdown').dropdown("set selected", product.id);
            });
            notification.toJSON().branchList.forEach(function (branch) {
              $('#notificationBranchDropdown').dropdown("set selected", branch.id);
            });

            //Ürün ekleme çıkarma -> başlangıç
            $("#notificationProductDropdown").dropdown({
              onAdd: function (addedValue) { //Bir ürün seçildiğinde
                var product = notification.toJSON().productList.find(function (product) {
                  return product.id == addedValue;
                });
                if (product == undefined) { //Seçilen ürün zaten daha önceden eklenmemişse
                  that.addProductArray.push(addedValue); //Eklenecekler listesine ekle
                } else { // Seçilen ürün daha önceden eklenmişse, silinecekler listesinden sil
                  that.removeProductArray.splice(that.removeProductArray.indexOf(addedValue), 1);
                }
              },
              onRemove: function (removedValue) { //Bir ürün silindiğinde
                var product = notification.toJSON().productList.find(function (product) {
                  return product.id == removedValue;
                });

                if (product == undefined) {
                  that.addProductArray.splice(that.addProductArray.indexOf(removedValue), 1);
                } else {
                  that.removeProductArray.push(removedValue); //Silinecekler listesine ata
                }
              }
            });//Ürün ekleme çıkarma -> son

            //Şube ekleme çıkarma -> başlangıç
            $("#notificationBranchDropdown").dropdown({
              onAdd: function (addedValue) { //Bir ürün seçildiğinde
                var branch = notification.toJSON().branchList.find(function (branch) {
                  return branch.id == addedValue;
                });

                if (branch == undefined) { //Seçilen ürün zaten daha önceden eklenmemişse
                  that.addBranchArray.push(addedValue); //Eklenecekler listesine ekle
                } else { // Seçilen ürün daha önceden eklenmişse, silinecekler listesinden sil
                  that.removeBranchArray.splice(that.removeBranchArray.indexOf(addedValue), 1);
                }
                console.log("AddBranchArray");
                console.log(that.addBranchArray);
                console.log("RemoveBranchArray");
                console.log(that.removeBranchArray);
                console.log("---------------------------");
              },
              onRemove: function (removedValue) { //Bir ürün silindiğinde
                var branch = notification.toJSON().branchList.find(function (branch) {
                  return branch.id == removedValue;
                });

                if (branch == undefined) {
                  that.addBranchArray.splice(that.addBranchArray.indexOf(removedValue), 1);
                } else {
                  that.removeBranchArray.push(removedValue); //Silinecekler listesine ata
                }
                console.log("AddBranchArray");
                console.log(that.addBranchArray);
                console.log("RemoveBranchArray");
                console.log(that.removeBranchArray);
                console.log("---------------------------");
              }
            });//Şube ekleme çıkarma -> son
          }
        });
      }
    }
  });

  var ListNotificationView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #deleteNotificationButton': 'deleteNotification',
      'click #sendNotificationButton': 'openModal'
    },
    initialize: function () {
      this.notificationCollection = new NotificationCollection({url: "/getNotifications/0"});
    },
    openModal: function (e) {
      var that = this;
      $(".ui.modal").modal({
        onApprove: function () {
          $(".approveSending").addClass('loading');
          that.sendNotification(e);
          return false;
        },
        onHidden: function () {
          that.render();
        }
      }).modal('show');
    },
    deleteNotification: function (e) {
      var notificationId = $(e.currentTarget).attr("data-id");
      this.deleteItem(this.notificationCollection, notificationId);
    },
    sendNotification: function (e) {
      var that = this;
      var notificationId = $(e.currentTarget).attr("data-id");
      var sendNotification = new NotificationModel({url: "/notification/send/" + notificationId});
      sendNotification.fetch({
        success: function () {
          that.notificationCollection.remove(that.notificationCollection.get(notificationId));
          alertify.success("Bildirim Başarıyla Gönderildi.");
          $(".ui.modal").modal('hide');
        }
      });
    },
    render: function () {
      this.$el.html(listNotificationTemplate({notifications: this.notificationCollection.toJSON()}));

    }
  });

  var SendedNotificationView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {},
    initialize: function () {
      this.notificationCollection = new NotificationCollection({url: "/getNotifications/1"});
    },
    render: function () {
      this.$el.html(sendedNotificationTemplate({notifications: this.notificationCollection.toJSON()}));
    }
  });

  Handlebars.registerHelper('formatDate', function (date) {
    var months = ["Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"];
    var date = new Date(date);
    var day = date.getDate();
    var monthIndex = date.getMonth();
    var year = date.getFullYear();
    return day + "/" + months[monthIndex] + "/" + year;
  });

  return {
    AddNotificationView: AddNotificationView,
    ListNotificationView: ListNotificationView,
    SendedNotificationView: SendedNotificationView
  }

});