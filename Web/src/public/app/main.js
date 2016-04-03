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
    'googlemaps': '../lib/googlemaps',
    'async': '../lib/async',
    'gmaps': '../lib/gmaps.min',
    'nprogress': '../lib/nprogress'
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
    },
    gmaps: {
      deps: ["googlemaps"],
      exports: "gmaps"
    }
  },
  googlemaps: {
    params: {
      key: 'AIzaSyAP3ORAEACIf0t1euI3Nwd22uR-bFtr6no'
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
  'nprogress',
  'handlebars',
  'semanticJs',
  'jquerySerialize',
  'async',
  'googlemaps',
  'gmaps'
], function ($, _, Backbone, Core, Outer, Alertify, NProgress) {
  NProgress.configure({showSpinner: false});
  window.outer = new Outer();
  window.core = Core;
  window.alertify = Alertify;
  require(['router'], function (Router) {
    window.$router = new Router();
    Backbone.history.start();
  });

  var loading = $("#loading");

  $(document).on({
    ajaxStart: function () {
      //Login sayfasında değilse ve loading divinde active classı yoksa(sayfa refresh edilmediyse)
      if (window.location.hash != "" && !loading.hasClass("active")) {
        NProgress.start();
      }
    },
    ajaxStop: function () {
      //loading divinde active classı varsa sil
      if (loading.hasClass("active")) {
        loading.removeClass("active");
      } else { //Yoksa NProggressi durdur.
        NProgress.done();
      }
    }
  });

  $.ajaxSetup({cache: false});

  alertify.alert()
    .setting({
      'label': 'Tamam'
    });

  $(document).ajaxError(function (event, xhr, settings, object) {
    if (xhr.status == 401) {
      window.location.hash = ""; // Login sayfasına gönder
    } else if (xhr.status == 500) {
      alertify.alert("Hata!", "Bilinmeyen bir hata oluştu!");
    }
  });
});