window.define(['backbone'], function (Backbone) {

  return Backbone.View.extend({

    showView: function (view, params) {

      view.forEach(function (view) {
        view.params = params;
        view.load.call(view, params);
      });

    }
  });
});



