define([
  'backbone',
  'handlebars',
  'text!User/addUserTemplate.html',
  'text!User/listUserTemplate.html'], function (Backbone,
                                                Handlebars,
                                                AddUserTemplate,
                                                ListUserTemplate) {

  //----------------- Templateler --------------------//
  var addUserTemplate = Handlebars.compile(AddUserTemplate);
  var listUserTemplate = Handlebars.compile(ListUserTemplate);

  // ---------------- Models & Collections ----------------- //
  var User = Backbone.Model.extend({
    urlRoot: "/account"
  });
  var UserCollection = Backbone.Collection.extend({
    url: "/accounts",
    model: User
  });
  var Companyollection = Backbone.Collection.extend({
    url: "/company"
  });

  //------------------- Views ------------------------//

  var AddUserView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #addUserButton': 'saveUser',
      'click #saveUserButton': 'saveUser'
    },
    initialize: function () {
      this.companyCollection = new Companyollection();
    },
    saveUser: function (e) {
      e.preventDefault();
      var values = this.form().getValues; //Formdan verileri alıyoruz
      var user = new User();
      if (values.accountAuth != undefined) {
        values.accountAuth = values.accountAuth.toString();
      }
      user.save(values, {
        success: function () {
          window.location.hash = "/user/list";
          if (values.id == undefined) {
            alertify.success("Hesap başarıyla oluşturuldu.");
          } else {
            alertify.success("Hesap bilgileri kaydedildi.");
          }
        }
      });
    },

    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addUserTemplate({companies: this.companyCollection.toJSON()}));
        $('.accountAuthDropdown').dropdown();
        $('.accountTypeDropdown').dropdown();
        $(".companyDropdown").dropdown().addClass('disabled');
      } else {
        var user = new User({id: this.params.accountId});
        user.fetch({
          success: function (savedUser) {
            that.$el.html(addUserTemplate({
              user: savedUser.toJSON(),
              companies: that.companyCollection.toJSON()
            }));
            that.form().setValues(user.toJSON());
            $('#accountAuthDropdown').dropdown("set selected", savedUser.toJSON().accountAuth.split(','));
          }
        });
      }
    }
  });

  var ListUserView = core.CommonView.extend({
    autoLoad: true,
    events: {
      'click deleteUserButton': 'deleteUser'
    },
    el: "#page",
    initialize: function () {
      this.accountCollection = new UserCollection();
    },
    deleteUser: function (e) {
      var userId = $(e.currentTarget).attr('data-id');
      this.deleteItem(this.accountCollection, userId);
    },
    render: function () {
      this.$el.html(listUserTemplate({
        accounts: this.accountCollection.toJSON()
      }));
    }
  });

  return {
    AddUserView: AddUserView,
    ListUserView: ListUserView
  }

});