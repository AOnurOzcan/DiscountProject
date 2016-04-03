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
  var AuthorityCollection = Backbone.Collection.extend({
    url: '/authority'
  });

  var PasswordRecovery = Backbone.Model.extend({
    url: "/sendResetMail",
    initialize: function (properties) {
      if (properties != undefined) {
        this.url += "/" + properties.id;
      }
    }
  });

  //------------------- Views ------------------------//

  var AddAccountView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {
      'change .accountTypeDropdown': 'ChangeAccountType',
      //'click #addAccountButton': 'saveAccount',
      //'click #saveAccountButton': 'saveAccount'
    },
    validation: function () {
      var that = this;
      $('#addAccountForm').form({
        onSuccess: function (e) {
          that.saveAccount(e);
        },
        fields: {
          username: {
            identifier: 'username',
            rules: [
              {
                type: 'empty',
                prompt: 'Kullanıcı adı alanı boş geçilemez!'
              }
            ]
          },
          password: {
            identifier: 'password',
            rules: [
              {
                type: 'empty',
                prompt: 'Şifre alanı boş geçilemez!'
              }
            ]
          },
          email: {
            identifier: 'email',
            rules: [
              {
                type: 'empty',
                prompt: 'Email alanı boş geçilemez!'
              },
              {
                type: 'email',
                prompt: 'Geçersiz Email!'
              }
            ]
          },
          accountAuth: {
            identifier: 'accountAuthDropdown',
            rules: [
              {
                type: 'minCount[1]',
                prompt: 'En az bir yetki seçiniz!'
              }
            ]
          }
        }
      });
    },
    initialize: function () {
      this.companyCollection = new Companyollection();
      this.authorityCollection = new AuthorityCollection();
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
      var that = this;
      var values = this.form().getValues;
      if (values.accountAuth != undefined) {
        values.accountAuth = values.accountAuth.toString();
      }
      var account = new Account();
      account.save(values, {
        success: function () {
          if (that.params == undefined) {
            alertify.success("Hesap ekleme başarılı");
          } else {
            alertify.success("Hesap düzenleme başarılı");
          }
          window.location.hash = "account/list";
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addAccountTemplate({
          companies: this.companyCollection.toJSON(),
          authorities: this.authorityCollection.toJSON()
        }));
        $('#accountAuthDropdown').dropdown();
        $('.accountTypeDropdown').dropdown();
        $(".companyDropdown").dropdown().addClass('disabled');
        this.validation();
      } else {
        var user = new Account({id: this.params.accountId});
        user.fetch({
          success: function (savedUser) {
            that.$el.html(addAccountTemplate({
              account: savedUser.toJSON(),
              companies: that.companyCollection.toJSON(),
              authorities: that.authorityCollection.toJSON()
            }));
            that.validation();
            that.form().setValues(user.toJSON());
            $('.accountAuthDropdown').dropdown();
            $('.accountTypeDropdown').dropdown();
            $('#accountAuthDropdown').dropdown("set selected", savedUser.toJSON().accountAuth);
          }
        });
      }
    }
  });

  var ListAccountView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {
      'click .deleteAccountButton': 'deleteAccount',
      'click .recoverPassword': 'recoverPassword'
    },
    initialize: function () {
      this.accountCollection = new AccountCollection();
    },
    recoverPassword: function (e) {
      var button = $(e.currentTarget);
      var accountId = button.attr("data-id");
      var accountEmail = button.attr("data-mail");
      alertify.confirm("Onay", '<b>' + accountEmail + '</b>' + " adresine şifre sıfırlama isteği gönderilecek. Onaylıyor musunuz?",
        function () { //Tamam'a basarsa
          var passwordRecovery = new PasswordRecovery({id: accountId});
          passwordRecovery.fetch({
            success: function () {
              alertify.success("Şifre sıfırlama isteği gönderildi.");
            }
          });
        },
        function () { //Vazgeç'e basarsa.
          alertify.message("İsteğiniz iptal edildi.");
        }).set('labels', {ok: 'Tamam', cancel: 'Vazgeç'});
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