define([
  'backbone',
  'handlebars',
  'underscore',
  'text!Account/addAccountTemplate.html',
  'text!Account/listAccountTemplate.html'], function (Backbone,
                                                      Handlebars,
                                                      _,
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
      'change .accountTypeDropdown': 'ChangeAccountType'
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
      this.addAuthArray = [];
      this.removeAuthArray = [];
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
      if (this.params == undefined) {
        values.accountAuth = values.accountAuth.split(',');
      } else {
        values.addAuthArray = this.addAuthArray;
        values.removeAuthArray = this.removeAuthArray;
      }
      var account = new Account();
      account.save(values, {
        success: function () {
          that.addAuthArray.length = 0;
          that.removeAuthArray.length = 0;
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
        var accountAuthDropdown = $('#accountAuthDropdown');
        accountAuthDropdown.dropdown();
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
            var accountAuthDropdown = $('#accountAuthDropdown');
            that.validation();
            that.form().setValues(user.toJSON());
            accountAuthDropdown.dropdown("set selected", savedUser.toJSON().accountAuth.toString().split(','));
            $('#accountAuthDropdown').dropdown({
              onAdd: function (addedValue) { //Bir ürün seçildiğinde
                var auth = savedUser.toJSON().accountAuth.find(function (authId) {
                  return authId == addedValue;
                });
                if (auth == undefined) { //Seçilen ürün zaten daha önceden eklenmemişse
                  that.addAuthArray.push(addedValue); //Eklenecekler listesine ekle
                } else { // Seçilen ürün daha önceden eklenmişse, silinecekler listesinden sil
                  that.removeAuthArray.splice(that.removeAuthArray.indexOf(addedValue), 1);
                }
              },
              onRemove: function (removedValue) { //Bir ürün silindiğinde
                var auth = savedUser.toJSON().accountAuth.find(function (authId) {
                  return authId == removedValue;
                });

                if (auth == undefined) {
                  that.addAuthArray.splice(that.addAuthArray.indexOf(removedValue), 1);
                } else {
                  that.removeAuthArray.push(removedValue); //Silinecekler listesine ata
                }
              }
            });
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

  Handlebars.registerHelper("checkAuthType", function (authType, options) {
    var accountType = $router.checkSession.attributes.accountType;

    if (accountType == "ADMIN") {
      return options.fn(this);
    } else {
      if (authType == "COMMON") {
        return options.fn(this);
      }
    }
  });

  return {
    AddAccountView: AddAccountView,
    ListAccountView: ListAccountView
  }

});