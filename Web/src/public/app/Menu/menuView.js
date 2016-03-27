define(['backbone',
  'handlebars',
  'jquery',
  'text!Menu/menuTemplate.html'], function (Backbone, Handlebars, $, MenuTemplate) {

  var menuTemplate = Handlebars.compile(MenuTemplate);

  var LogoutModel = Backbone.Model.extend({
    urlRoot: '/logout'
  });

  var MenuView = core.CommonView.extend({
    el: $(".menu"),
    autoLoad: true,
    events: {
      'click #logoutButton': 'logout'
    },
    initialize: function () {
      this.isRendered = false;
    },
    logout: function () {
      var logoutModel = new LogoutModel();
      logoutModel.fetch({
        success: function () {
          window.location.reload(false);
        }
      });
    },
    render: function () {
      if (this.isRendered == false) { // Eğer menü daha önce render edilmediyse
        this.$el.html(menuTemplate({session: this.session})); // Ekrana bas
        this.isRendered = true; // Render edildi olarak işaretle
      }
    }
  });

  Handlebars.registerHelper('checkAccount', function (companyName, accountType) {
    if (accountType == "COMPANY") {
      return companyName;
    } else {
      return "Yönetim Paneli";
    }
  });

  return MenuView;

});