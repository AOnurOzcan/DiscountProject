define(['backbone',
  'handlebars',
  'jquery',
  'text!Menu/menuTemplate.html'], function (Backbone, Handlebars, $, MenuTemplate) {

  var menuTemplate = Handlebars.compile(MenuTemplate);

  var LogoutModel = Backbone.Model.extend({
    urlRoot: '/logout'
  });

  var CheckSession = Backbone.Model.extend({
    urlRoot: "/check"
  });

  var MenuView = core.CommonView.extend({
    el: $(".menu"),
    autoLoad: true,
    events: {
      'click #logoutButton': 'logout'
    },
    initialize: function () {
      this.isRendered = false;
      this.checkSession = new CheckSession();
    },
    logout: function () {
      var logoutModel = new LogoutModel();
      logoutModel.fetch({
        success: function () {
          window.location.hash = 'login';
        }
      });
    },
    render: function () {
      if (this.isRendered == false) { // Eğer menü daha önce render edilmediyse
        this.$el.html(menuTemplate({account: this.checkSession.toJSON()})); // Ekrana bas
        this.isRendered = true; // Render edildi olarak işaretle
      }
    }
  });

  Handlebars.registerHelper('checkAccount', function (account) {
    if (account.accountType == "ADMIN") {
      return '<li><a href="#">Ana Kategori Ekle</a></li>' +
        '<li><a href="#">Alt Kategori Ekle</a></li>'
    }
  });

  return MenuView;

});