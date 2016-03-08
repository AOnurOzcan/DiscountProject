require.config({

  waitSeconds: 150,
  paths: {
    'jquery': '../lib/jquery-1.12.1.min',
    'backbone': '../lib/backbone-min',
    'underscore': '../lib/underscore-min',
    'handlebars': '../lib/handlebars-v4.0.5',
    'bootstrapJs': '../lib/bootstrap.min',
    'jquerySerialize': '../lib/jquery-serialize-object',
    'core': 'Util/core',
    'outer': 'Util/outer'
  },
  shim: {
    bootstrapJs: {
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
  'handlebars',
  'bootstrapJs',
  'jquerySerialize'
], function ($, _, Backbone, Core, Outer) {
  window.outer = new Outer();
  window.core = Core;
  require(['router'], function (Router) {
    window.$router = new Router();
    Backbone.history.start();
  });

  $(document).ajaxError(function (event, xhr, settings, object) {
    if (xhr.status == 401) {
      window.location.hash = "login";
    }
  });
});