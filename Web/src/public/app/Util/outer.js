window.define(['backbone'], function (Backbone) {

  return Backbone.View.extend({

    showView: function (view, params) {

      // Bu fonksiyona parametre oliarak verilen view'ler birden fazla ise
      var $content = $(".page");
      $content.children().detach();

      view.forEach(function (view) {
        view.params = params;
        view.load.call(view, params);
      });

      $content.hide();
      $content.fadeIn(500);
    }
  });
});



