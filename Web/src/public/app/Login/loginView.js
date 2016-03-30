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
    el: "#page",
    login: function (e) {
      e.preventDefault();
      var loginForm = $("#loginForm");
      loginForm.addClass("loading");
      loginForm.removeClass("error");
      var formValues = this.form("#loginForm").getValues; // Login Form'a girilen kullanıcı adı ve şifreyi al
      new LoginModel().save(formValues, { // Böyle bir kullanıcı olup olmadığını kontrol et
        success: function () { // Eğer var ise
          window.location.hash = 'statistics'; // Yönetim paneline yönlendir.
        },
        error: function (error) {
          loginForm.removeClass("loading");
          $(".error").html('<ul class="list"><li>Kullanıcı Adınız ya da Şifreniz Yanlış! Lütfen Tekrar Deneyiniz!</li></ul>');
          loginForm.addClass("error");
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