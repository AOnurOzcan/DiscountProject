define(['backbone',
  'handlebars',
  'jquery',
  'text!Statistic/statisticTemplate.html'], function (Backbone, Handlebars, $, StatisticTemplate) {

  var StatisticsCollection = Backbone.Collection.extend({
    url: "/statistics"
  });
  var statisticTemplate = Handlebars.compile(StatisticTemplate);

  var StatisticView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {},
    initialize: function () {
      this.statistics = new StatisticsCollection();
    },
    render: function () {
      this.$el.html(statisticTemplate({statistics: this.statistics.toJSON()}));
    }
  });

  return StatisticView;

});