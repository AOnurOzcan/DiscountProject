define(['backbone', 'handlebars', 'text!Profile/profileTemplate.html'], function (Backbone, Handlebars, ProfileTemplate) {

  //-------------------------------- Templates --------------------------------- //
  var profileTemplate = Handlebars.compile(ProfileTemplate);

  //------------------------------ Models & Collections ----------------------------//
  var Profile = Backbone.Model.extend({
    url: "/account"
  });

  var EditProfileView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {},
    initialize: function () {
      this.profile = new Profile();
    },
    render: function () {
      this.$el.html(profileTemplate({account: this.profile.toJSON()}));
    }
  });

  return EditProfileView;

});