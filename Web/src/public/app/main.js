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
    'nprogress': '../lib/nprogress',
    'jqueryUi': '../lib/jquery-ui.min',
    'util': 'Util/util'
  },
  shim: {
    semanticJs: {
      deps: ['jquery']
    },
    jqueryUi: {
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
  'gmaps',
  'jqueryUi',
  'util'
], function ($, _, Backbone, Core, Outer, Alertify, NProgress) {
  NProgress.configure({showSpinner: false});
  window.outer = new Outer();
  window.core = Core;
  window.alertify = Alertify;
  require(['router'], function (Router) {
    window.$router = new Router();
    Backbone.history.start();
  });

  var tr = {
    monthNames: ["Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"],
    monthNamesShort: ["Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"],
    dayNames: ["Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"],
    dayNamesShort: ["Pz", "Pt", "Sa", "Ça", "Pe", "Cu", "Ct"],
    dayNamesMin: ["Pz", "Pt", "Sa", "Ça", "Pe", "Cu", "Ct"],
    weekHeader: "Hf",
    dateFormat: "dd/mm/yy",
    firstDay: 1,
    changeMonth: true,
    changeYear: true
  };

  $.datepicker.setDefaults($.extend(tr));

  var loading = $("#loading");
  var page = $("#page");

  $(document).on({
    ajaxStart: function () {
      $("button").addClass("disabled");
      //Login sayfasında değilse ve loading divinde active classı yoksa(sayfa refresh edilmediyse)
      if (window.location.hash != "" && !loading.hasClass("active")) {
        NProgress.start();
      }
    },
    ajaxStop: function () {
      page.fadeIn(400);
      $("button").removeClass("disabled");

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