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
    el: "#page",
    validation: function () {
      var that = this;
      $('#loginForm').form({
        onSuccess: function (e) {
          that.login(e);
        },

        fields: {
          username: {
            identifier: 'username',
            rules: [
              {
                type: 'empty',
                prompt: 'Kullanıcı adı alanı boş geçilemez.'
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
          }
        }
      });
    },
    login: function (e) {
      e.preventDefault();
      var loginForm = $("#loginForm");
      loginForm.addClass("loading");
      var formValues = this.form().getValues; // Login Form'a girilen kullanıcı adı ve şifreyi al
      new LoginModel().save(formValues, { // Böyle bir kullanıcı olup olmadığını kontrol et
        success: function () { // Eğer var ise
          $("#page").html("");
          window.location.hash = 'statistics'; // Yönetim paneline yönlendir.
        },
        error: function () {
          loginForm.form('add errors', ['Kullanıcı Adınız ya da Şifreniz Yanlış! Lütfen Tekrar Deneyiniz!']);
          loginForm.removeClass("loading");
        }
      });
    },
    render: function () {
      this.$el.html(loginTemplate);
      this.validation();
    }
  });

  return {
    LoginView: LoginView,
    LoginModel: LoginModel,
    CheckSession: CheckSession
  }

});