define(['backbone',
  'handlebars',
  'jquery',
  'text!CompanyHomepage/adminTemplate.html'], function (Backbone, Handlebars, $, CompanyTemplate) {

  var StatisticsCollection = Backbone.Collection.extend({
    url: "/companyStatistics"
  });
  var companyTemplate = Handlebars.compile(CompanyTemplate);

  var AdminView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {},
    initialize: function () {
      this.statistics = new StatisticsCollection();
    },
    render: function () {
      this.$el.html(companyTemplate({statistics: this.statistics.toJSON()}));
    }
  });

  return AdminView;

});