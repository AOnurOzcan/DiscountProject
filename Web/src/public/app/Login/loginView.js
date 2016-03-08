define(['backbone', 'handlebars', 'jquery', 'text!Login/loginTemplate.html'], function (Backbone, Handlebars, $, LoginTemplate) {

  var LoginModel = Backbone.Model.extend({
    urlRoot: "/login"
  });

  var CheckSession = Backbone.Model.extend({
    urlRoot: "/check"
  });

  var loginTemplate = Handlebars.compile(LoginTemplate);

  var LoginView = core.CommonView.extend({
    autoLoad: true,
    events: {
      'click #loginButton': 'login'
    },
    el: $(".page"),
    login: function (e) {
      e.preventDefault();
      var formValues = this.form("#loginForm").getValues; // Login Form'a girilen kullanıcı adı ve şifreyi al
      new LoginModel().save(formValues, { // Böyle bir kullanıcı olup olmadığını kontrol et
        success: function () { // Eğer var ise
          window.location.hash = 'cHomepage'; // Yönetim paneline yönlendir.
        },
        error: function (error) {
          $("span[name=errorMessage]").show();
        }
      });
    },
    render: function () {
      this.$el.html(loginTemplate);
    }
  });

  return {
    LoginView: LoginView,
    LoginModel: LoginModel,
    CheckSession: CheckSession
  }

});