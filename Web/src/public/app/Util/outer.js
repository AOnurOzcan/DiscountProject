window.define(['backbone'], function (Backbone) {

  return Backbone.View.extend({

    showView: function (view, params) {
      debugger;
      var $content = this.$('.page');
      $content.children().detach();
      if (view.constructor === Array) {
        view.forEach(function (view) {
          view.params = params;
          view.load.call(view, params);
          var $el = $(view.el);

            $el.hide();
            $content.append($el);
            $el.fadeIn(500);
        });
      } else {

        view.load.call(view, params);
        view.params = params;
        var $el = $(view.el);
        $el.hide();
        $content.append($el);
        $el.fadeIn(500);
      }
    }
  });
});



