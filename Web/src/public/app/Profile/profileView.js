define(['backbone', 'handlebars', 'text!Profile/profileTemplate.html'], function (Backbone, Handlebars, ProfileTemplate) {

  //-------------------------------- Templates --------------------------------- //
  var profileTemplate = Handlebars.compile(ProfileTemplate);

  //------------------------------ Models & Collections ----------------------------//
  var Profile = Backbone.Model.extend({
    urlRoot: "/account"
  });

  var EditProfileView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    events: {
      'click #saveProfileButton': 'saveProfile'
    },
    initialize: function () {
      this.profile = new Profile();
    },
    saveProfile: function (e) {
      e.preventDefault();
      var values = this.form().getValues;
      this.profile.save(values, {
        success: function () {
          alertify.success("Profil kaydetme başarılı.");
        }
      });
    },
    render: function () {
      this.$el.html(profileTemplate({account: this.profile.toJSON()}));
    }
  });

  return EditProfileView;

});