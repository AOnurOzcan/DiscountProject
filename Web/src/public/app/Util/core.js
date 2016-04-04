window.define(['backbone', 'handlebars'], function (Backbone, Handlebars) {

  "use strict";

  var CommonView = Backbone.View.extend({
    events: {
      "click .back": "goBack",
      "click .language": "languageSelect",
      "click .languageTR": "languageTR",
      "click .languageEN": "languageEN"
    },

    /**
     * Required for multiple page support
     * @author ME99987
     */
    "load": function () {
      if (this["autoLoad"] == true) {
        var self = this;
        var collections = [];

        Object.getOwnPropertyNames(this).forEach(function (attr) {
          if (self[attr] instanceof Backbone.Collection || self[attr] instanceof Backbone.Model) {
            if (attr.charAt(0) != '_') {
              collections.push(self[attr]);
            }
          }
        });
        this.waitData(collections);
        return;
      }

      throw Error("Please Implement the load method");
    },

    /**
     * Goes back :)
     */
    "goBack": function () {
      window.history.back();
    },

    /**
     * Waits for model or collection, when it's fetch callback will be called
     * @param {*|Array} model Which data or data's are calling
     * @param {Function} [callback] When data arrived, which function will be called. If it's undefined then, automaticly it's calling this.render
     * @author ME99987
     * @example
     * load: function() {
     *  this.waitData(this.visibleFields);
     * },
     *
     * @example
     * load: function() {
     *  this.waitData([this.myModel, this.myCollection, new Collection()]);
     * },
     *
     * @example
     * load: function() {
     *  this.waitData(new Collection(), function() {
     *   this.render("how about this?");
     *  });
     * },
     *
     * @example 25.01.2016 update
     * load: function() {
     *  this.waitData([[new Collection(), {"a": 1}]], function() {
     *   this.render("how about this?");
     *  });
     * },
     */
    "waitData": function (model, callback) {
      var page = $("#page");
      page.hide();
      var self = this;
      if (callback == undefined) {
        callback = this.render;
      }

      if (model.constructor == Array) {
        var group = [];
        model.forEach(function (model) {
          if (model.constructor == Array) {
            var m = model.shift();
            group.push(m.fetch.apply(m, model));
          } else {
            group.push(model.fetch());
          }
        });

        $.when.apply($, group).then(function () {

          //TODO eğer view menü ise fade in efekti kaldırılacak
          callback.call(self);
          page.fadeIn(400);

          if (self.ready) {
            self.ready(); // Activate i18n
          }
        });
        return;
      }

      $.when(model["fetch"]()).then(function () {
        callback.call(self);
        page.fadeIn(400);

      });
    },

    "assign": function (view, field) {
      this[view].setElement(this.$(field));
    },

    /**
     * Controls form in view
     * @param {String} [select] Which form will be selected (default "form")
     * @returns {{getValues: Object, setError: Function, setClear: Function}}
     */
    "form": function (select) {
      (select || (select = "form"));

      var parent = this;
      var $element = this.$(select);
      return {
        /**
         * Gets input values from view.
         * @returns {Object} Collected data
         */
        get getValues() {
          return $element.serializeObject();
        },

        /**
         * Sets values to inputs by name.
         * @param values - an array which contains names and values.
         */
        "setValues": function (values) {
          for (var element in values) {
            if (values.hasOwnProperty(element)) {
              if (values[element] instanceof Object) { // Eğer property bir obje ise, objenin id alanını set et
                $element.find("[name='" + element + "']").val(values[element].id);
              } else { // değilse kendisini set et
                $element.find("[name='" + element + "']").val(values[element]);
              }
            }
          }
        },


        /**
         * Sets error to input
         * @param name Which input
         * @param info Error information
         */
        "setError": function (name, info) {
          var $children = $element.find("[name='" + name + "']");

          var parent = $children.parent();
          if (parent.hasClass("input-group")) {
            parent = parent.parent();
          }

          parent.addClass("has-error");

          var helpBlock = parent.find(".help-block");
          if (helpBlock.length != 0) {
            helpBlock.text(info);
            helpBlock.fadeIn(750);
          }
        },


        /**
         * Clears the input (sets error to 0)
         * @param name Which input
         */
        "setClear": function (name) {
          var $children = $element.find("[name='" + name + "']");
          var parent = $children.parent();

          if (parent.hasClass("input-group")) {
            parent = parent.parent();
          }

          var helpBlock = parent.find(".help-block");
          parent.removeClass("has-error");
          helpBlock.fadeOut(500);
        },


        /**
         * Clears all inputs
         */
        "clearAll": function () {
          var values = this.getValues;
          var keys = Object.getOwnPropertyNames(values);

          var self = this;
          keys.forEach(function (key) {
            $element.find('[name="' + key + '"]').val(""); // Clears all form elements
            $element.find('input[type=checkbox]').prop('checked', false);
            self.setClear(key); // Clears validation errors
          })
        },


        /**
         * Checks form's validation
         * @param validation
         * @param [scope]
         * @returns {boolean}
         */
        "executeValidation": function (validation, scope) {
          var self = this;
          var result = true;
          var values = this.getValues;
          if (scope == undefined) {
            scope = parent;
          }
          var temp = {}; // simple carry object

          Object.getOwnPropertyNames(values).forEach(function (propertyName) { // check all values is filled with acceptable values..
            var validationResult = validation.call(scope, propertyName, values[propertyName], values, temp);
            if (validationResult != true) { // check validation
              self.setError(propertyName, validationResult);
              result = false;
            } else {
              self.setClear(propertyName);
            }
          });
          return result == true ? values : null;
        }
      }
    },

    "deleteItem": function (collection, id) {
      var that = this;
      collection.get(id).destroy({
        wait: true, //wait until server response to remove model from collection
        success: function () { //If success
          alertify.success("Silme işlemi başarıyla gerçekleşti");
          that.render();
        }
      });
    }
  });

  Handlebars.registerHelper('can', function (permission, options) {
    var session = $router.checkSession.attributes;
    var auths = session.accountAuth;

    var check = auths.some(function (auth) {
      return auth == permission;
    });

    if (check == true) {
      return options.fn(this);
    } else {
      return options.inverse(this);
    }
  });

  Handlebars.registerHelper('isAdmin', function (options) {
    var accountType = $router.checkSession.attributes.accountType;
    if (accountType == "ADMIN") {
      return options.fn(this);
    } else {
      return options.inverse(this);
    }
  });

  return {
    CommonView: CommonView
  }

});

