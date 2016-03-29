define([
  'backbone',
  'handlebars',
  'text!Company/AddCompanyTemplate.html',
  'text!Company/ListCompanyTemplate.html'], function (Backbone, Handlebars, AddCompanyTemplate, ListCompanyTemplate) {

  var addCompanyTemplate = Handlebars.compile(AddCompanyTemplate);
  var listCompanyTemplate = Handlebars.compile(ListCompanyTemplate);

  var CompanyModel = Backbone.Model.extend({
    urlRoot: '/company'
  });
  var CompanyCollection = Backbone.Collection.extend({
    url: '/company',
    model: CompanyModel
  });

  var AddCompanyView = core.CommonView.extend({
    autoLoad: true,
    el: '#page',
    events: {
      'click #addCompanyButton': 'saveCompany',
      'click #updateCompanyButton': 'saveCompany'
    },
    saveCompany: function (e) {
      e.preventDefault();
      var that = this;
      var form = this.form();
      var values = form.getValues;
      var companyModel = new CompanyModel();
      companyModel.save(values, {
        success: function () {
          window.location.hash = "company/list";
          if (that.params == undefined) {
            alertify.success("Firma başarıyla eklendi");
          } else {
            alertify.success("Firma başarıyla güncellendi");
          }
        }
      });
    },
    render: function () {
      var that = this;
      if (this.params == undefined) {
        this.$el.html(addCompanyTemplate());
      } else {
        var companyModel = new CompanyModel({id: this.params.companyId});
        companyModel.fetch({
          success: function (company) {
            that.$el.html(addCompanyTemplate({company: company.toJSON()}));
            that.form().setValues(company.toJSON());
          }
        });
      }
    }
  });

  var ListCompanyView = core.CommonView.extend({
    autoLoad: true,
    el: '#page',
    events: {
      'click #deleteCompanyButton': 'deleteCompany'
    },
    initialize: function () {
      this.companyCollection = new CompanyCollection();
    },
    deleteCompany: function (e) {
      var companyId = $(e.currentTarget).attr("data-id");
      this.deleteItem(this.companyCollection, companyId);
    },
    render: function () {
      this.$el.html(listCompanyTemplate({companies: this.companyCollection.toJSON()}));
    }
  });

  return {
    AddCompanyView: AddCompanyView,
    ListCompanyView: ListCompanyView
  }
});