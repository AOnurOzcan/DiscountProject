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

  var PasswordRecoveryView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {},
    render: function () {
      var that = this;
      var token = this.params.token;
      var checkToken = new CheckTokenModel({token: token});
      checkToken.fetch({
        success: function () {
          that.$el.html(passwordRecoveryTemplate);
        }
      });
    }
  });

  return PasswordRecoveryView;
});