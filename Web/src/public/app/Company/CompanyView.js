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
    url: '/company'
  });

  var AddCompanyView = core.CommonView.extend({
    autoLoad: true,
    el: '#page',
    events: {
      'click #addCompanyyButton': 'saveCompany',
      'click #updateCompanyButton': 'saveCompany'
    },
    saveCompany: function (e) {
      e.preventDefault();
      var that = this;
      var form=this.form();
      var values= form.getValues;
      var companyModel= new CompanyModel();
      companyModel.save(values, {
        success: function () {
          that.render();
          if (that.params == undefined) {
            alertify.success("Firma başarıyla eklendi");
          } else {
            alertify.success("Firma başarıyla güncellendi");
          }
          window.location.hash = "company/list";
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
      'click #editCompanyButton': 'editCompany'
    },
    initialize: function () {
      this.companyCollection = new CompanyCollection();
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