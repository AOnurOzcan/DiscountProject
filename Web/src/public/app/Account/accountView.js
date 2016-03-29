define([
  'backbone',
  'handlebars',
  'text!Account/addAccountTemplate.html',
  'text!Account/listAccountTemplate.html'], function (Backbone,
                                                      Handlebars,
                                                      AddAccountTemplate,
                                                      ListAccountTemplate) {

  //----------------- Templateler --------------------//
  var listAccountTemplate = Handlebars.compile(ListAccountTemplate);
  var addAccountTemplate = Handlebars.compile(AddAccountTemplate);

  // ---------------- Models & Collections ----------------- //
  var Account = Backbone.Model.extend({
    urlRoot: "/account"
  });
  var AccountCollection = Backbone.Collection.extend({
    url: "/accounts",
    model: Account
  });
  var Companyollection = Backbone.Collection.extend({
    url: "/company"
  });

  //------------------- Views ------------------------//

  var AddAccountView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {
      'change .accountTypeDropdown': 'ChangeAccountType',
      'click #addAccountButton': 'saveAccount',
      'click #saveAccountButton': 'saveAccount'
    },
    initialize: function () {
      this.companyCollection = new Companyollection();
    },
    ChangeAccountType: function (e) {
      var accountType = $(".accountTypeDropdown").dropdown('get value');
      if (accountType == "COMPANY") {
        $(".companyDropdown").removeClass('disabled');
      } else {
        $(".companyDropdown").addClass('disabled');
        $(".companyDropdown").dropdown('restore defaults');
      }
    },
    saveAccount: function (e) {
      e.preventDefault();
      var values = this.form().getValues;
      if (values.accountAuth != undefined) {
        values.accountAuth = values.accountAuth.toString();
      }
      var account = new Account();
      account.save(values, {
        success: function () {
          alertify.success("Hesap ekleme başarılı");
          window.location.hash = "account/list";
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addAccountTemplate({companies: this.companyCollection.toJSON()}));
        $('#accountAuthDropdown').dropdown();
        $('.accountTypeDropdown').dropdown();
        $(".companyDropdown").dropdown().addClass('disabled');
      } else {
        var user = new Account({id: this.params.accountId});
        user.fetch({
          success: function (savedUser) {
            that.$el.html(addAccountTemplate({
              account: savedUser.toJSON(),
              companies: that.companyCollection.toJSON()
            }));
            that.form().setValues(user.toJSON());
            $('.accountAuthDropdown').dropdown();
            $('.accountTypeDropdown').dropdown();
            $('#accountAuthDropdown').dropdown("set selected", savedUser.toJSON().accountAuth.split(','));
          }
        });
      }
    }
  });

  var ListAccountView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {
      'click .deleteAccountButton': 'deleteAccount'
    },
    initialize: function () {
      this.accountCollection = new AccountCollection();
    },
    deleteAccount: function (e) {
      var userId = $(e.currentTarget).attr('data-id');
      this.deleteItem(this.accountCollection, userId);
    },
    render: function () {
      this.$el.html(listAccountTemplate({accounts: this.accountCollection.toJSON()}));
    }
  });

  return {
    AddAccountView: AddAccountView,
    ListAccountView: ListAccountView
  }

});