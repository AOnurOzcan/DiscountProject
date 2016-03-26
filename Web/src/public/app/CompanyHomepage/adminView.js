define(['backbone',
  'handlebars',
  'jquery',
  'text!CompanyHomepage/adminTemplate.html'], function (Backbone, Handlebars, $, CompanyTemplate) {

  var companyTemplate = Handlebars.compile(CompanyTemplate);

  var AdminView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
    },
    render: function () {
      this.$el.html(companyTemplate);
    }
  });

  return AdminView;

});