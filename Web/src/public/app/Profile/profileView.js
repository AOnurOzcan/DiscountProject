define(['backbone', 'handlebars', 'text!Profile/profileTemplate.html'], function (Backbone, Handlebars, ProfileTemplate) {

  //-------------------------------- Templates --------------------------------- //
  var profileTemplate = Handlebars.compile(ProfileTemplate);

  //------------------------------ Models & Collections ----------------------------//
  var Profile = Backbone.Model.extend({
    urlRoot: "/profile"
  });

  var Account = Backbone.Model.extend({
    urlRoot: "/account"
  });


  var EditProfileView = core.CommonView.extend({
    el: "#page",
    autoLoad: true,
    validation: function () {
      var that = this;
      $('#editProfileForm').form({
        onSuccess: function (e) {
          that.saveProfile(e);
        },
        fields: {
          email: {
            identifier: 'email',
            rules: [
              {
                type: 'email',
                prompt: 'Geçerli bir e-posta adresi giriniz!'
              }
            ]
          }
        }
      });
    },
    initialize: function () {
      this.account = new Account();
    },
    saveProfile: function (e) {
      e.preventDefault();
      var values = this.form().getValues;
      new Profile().save(values, {
        success: function () {
          alertify.success("Profil kaydetme başarılı.");
        }
      });
    },
    render: function () {
      this.$el.html(profileTemplate({account: this.account.toJSON()}));
      this.validation();
    }
  });

  return EditProfileView;

});