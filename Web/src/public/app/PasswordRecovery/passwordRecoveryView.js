define([
  'backbone',
  'handlebars',
  'text!PasswordRecovery/passwordRecoveryTemplate.html'], function (Backbone,
                                                                    Handlebars,
                                                                    PasswordRecoveryTemplate) {

  var passwordRecoveryTemplate = Handlebars.compile(PasswordRecoveryTemplate);

  var CheckTokenModel = Backbone.Model.extend({
    url: "/passwordRecoveryCheck",
    initialize: function (properties) {
      if (properties != undefined) {
        this.url = this.url + "/" + properties.token;
      }
    }
  });

  var ResetPasswordModel = Backbone.Model.extend({
    url: "/resetPassword"
  });

  var PasswordRecoveryView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {
      'click #passwordRecoveryButton': 'resetPassword'
    },
    resetPassword: function (e) {
      e.preventDefault();
      var accountId = $("#passwordRecoveryButton").attr("data-id");
      var values = this.form().getValues;
      values.accountId = accountId;
      var resetPassword = new ResetPasswordModel();
      resetPassword.save(values, {
        success: function () {
          alertify.success("Şifre değiştirme işlemi başarılı");
          window.location.hash = "";
        }
      });

    },
    render: function () {
      var that = this;
      var token = this.params.token;
      var checkToken = new CheckTokenModel({token: token});
      checkToken.fetch({
        success: function (account) {
          debugger
          that.$el.html(passwordRecoveryTemplate({account: account.toJSON()}));
        }
      });
    }
  });

  return PasswordRecoveryView;
});