define(['backbone', 'handlebars', 'text!ChooseCompany/chooseCompanyTemplate.html'], function (Backbone, Handlebars, ChooseCompanyTemplate) {

  var chooseCompanyTemplate = Handlebars.compile(ChooseCompanyTemplate);

  var CompanyCollection = Backbone.Collection.extend({
    url: "/company"
  });

  var Session = Backbone.Model.extend({
    url: "/accessCompanySession",
    initialize: function (properties) {
      if (properties != undefined) {
        this.url = properties.url;
      }
    }
  });

  var ChooseCompanyView = core.CommonView.extend({
    autoLoad: true,
    el: "#modal",
    initialize: function () {
      this.companyCollection = new CompanyCollection();
      this.isRendered = false;
    },
    render: function () {
      var that = this;
      if (this.isRendered == false) {
        this.$el.html(chooseCompanyTemplate({companies: this.companyCollection.toJSON()}));
        this.isRendered = true;
        var searchSelect = $('#search-select');
        var chooseCompanyButton = $('#chooseCompanyButton');
        var chooseCompanyModal = $("#chooseCompanyModal");
        var menuItems = $(".menuItem");

        $("#returnAdminView").on('click', function () {
          var session = new Session({url: "/endCompanySession"});
          session.fetch({
            success: function () {
              window.location.reload(false);
            }
          });
        });

        searchSelect.on('change', function (e) {
          that.companyId = searchSelect.val();
          that.companyName = searchSelect.find("option:selected").text();
        });

        searchSelect.dropdown({
          message: {
            noResults: 'Hiç sonuç bulunamadı.'
          }
        });

        searchSelect.dropdown('set text', this.session.companyName);

        chooseCompanyModal.modal({
          autofocus: false,
          onApprove: function () {
            var session = new Session({url: "/accessCompanySession/" + that.companyId});
            session.fetch({
              success: function () {
                chooseCompanyButton.text("Seçili Firma : " + that.companyName);
                menuItems.removeClass("disabled");
                window.location.hash = "cHomepage";
              }
            });
          },
          onHidden: function () {
            //window.location.hash = "cHomepage";
          }
        });
        chooseCompanyButton.on('click', function () {
          chooseCompanyModal.modal('show');
        });
      }
    }
  });

  return ChooseCompanyView;

});