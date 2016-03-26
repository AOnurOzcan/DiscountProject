require.config({

  waitSeconds: 150,
  paths: {
    'jquery': '../lib/jquery-1.12.1.min',
    'backbone': '../lib/backbone-min',
    'underscore': '../lib/underscore-min',
    'handlebars': '../lib/handlebars-v4.0.5',
    'semanticJs': '../lib/semantic.min',
    'alertify': '../lib/alertify.min',
    'jquerySerialize': '../lib/jquery-serialize-object',
    'core': 'Util/core',
    'outer': 'Util/outer',
    'googleMaps': 'http://maps.google.com/maps/api/js?key=AIzaSyAP3ORAEACIf0t1euI3Nwd22uR-bFtr6no'
  },
  shim: {
    semanticJs: {
      deps: ['jquery']
    },
    jquery: {
      exports: '$'
    },
    underscore: {
      exports: '_'
    },
    backbone: {
      deps: ['jquery', 'underscore'],
      exports: 'Backbone'
    },
    handlebars: {
      exports: 'Handlebars'
    }
  }
});

require([
  'jquery',
  'underscore',
  'backbone',
  'core',
  'outer',
  'alertify',
  'googleMaps',
  'handlebars',
  'semanticJs',
  'jquerySerialize'
], function ($, _, Backbone, Core, Outer, Alertify) {
  window.outer = new Outer();
  window.core = Core;
  window.alertify = Alertify;
  require(['router'], function (Router) {
    window.$router = new Router();
    Backbone.history.start();
  });

  //var $body = $("#ld");
  //$(document).on({
  //  ajaxStart: function () {
  //    $body.addClass("active");
  //  },
  //  ajaxStop: function () {
  //    $body.removeClass("loading");
  //  }
  //});
  $.ajaxSetup({cache: false});
  $(document).ajaxError(function (event, xhr, settings, object) {
    if (xhr.status == 401) {
      window.location.hash = ""; // Login sayfasına gönder
    } else if (xhr.status == 500) {
      alertify.alert("Bilinmeyen bir hata oluştu!");
    }
  });
});