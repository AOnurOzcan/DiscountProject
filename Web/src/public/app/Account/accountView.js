define([
  'backbone',
  'handlebars',
  'text!Account/addAccountTemplate.html',
  'text!Account/listAccountTemplate.html'], function (Backbone, Handlebars, AddAccountTemplate, ListAccountTemplate) {

  //----------------- Templateler --------------------//
  var addAccountTemplate = Handlebars.compile(AddAccountTemplate);
  var listAccountTemplate = Handlebars.compile(ListAccountTemplate);

  // ---------------- Models & Collections ----------------- //
  var Account = Backbone.Model.extend({
    urlRoot: "/account"
  });
  var AccountCollection = Backbone.Collection.extend({
    url: "/account",
    model: Account
  });

  //------------------- Views ------------------------//

  var AddAccountView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #addAccountButton': 'AddOrSaveAccount',
      'click #saveAccountButton': 'AddOrSaveAccount'
    },
    initialize: function () {

    },
    AddOrSaveAccount: function (e) {
      e.preventDefault();
      var values = this.form().getValues; //Formdan verileri alıyoruz
      var account = new Account();
      account.save(values, {
        success: function () {
          if (values.id == undefined) {
            alertify.success("Hesap başarıyla oluşturuldu.");
          } else {
            alertify.success("Hesap bilgileri kaydedildi.");
          }
          window.location.hash = "/account/list";
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addAccountTemplate);
        $('.ui.dropdown').dropdown();
      } else {
        var account = new Account({id: this.params.accountId});
        account.fetch({
          success: function (savedAccount) {
            that.$el.html(addAccountTemplate({account: savedAccount.toJSON()}));
            that.form().setValues(account.toJSON());
            $('.ui.dropdown').dropdown("set selected", savedAccount.toJSON().accountAuth.split(','));
          }
        });
      }
    }
  });

  var ListAccountView = core.CommonView.extend({
    autoLoad: true,
    events: {
      'click #deleteAccountButton': 'DeleteAccount'
    },
    el: "#page",
    initialize: function () {
      this.accountCollection = new AccountCollection();
    },
    DeleteAccount: function (e) {
      var accountId = $(e.currentTarget).attr('data-id');
      this.deleteItem(this.accountCollection, accountId);
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