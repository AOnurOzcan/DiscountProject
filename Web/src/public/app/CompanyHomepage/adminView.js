define(['backbone',
  'handlebars',
  'jquery',
  'text!CompanyHomepage/adminTemplate.html'], function (Backbone, Handlebars, $, CompanyTemplate) {

  var companyTemplate = Handlebars.compile(CompanyTemplate);

  var LogoutModel = Backbone.Model.extend({
    urlRoot: '/logout'
  });

  var AdminView = core.CommonView.extend({
    autoLoad: true,
    el: $(".page"),
    events: {
      'click #logoutButton': 'logout'
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
      this.$el.html(companyTemplate);
    }
  });

  return AdminView;

});