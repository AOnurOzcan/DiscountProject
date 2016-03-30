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
    validation: function () {
      var that = this;
      $('#editProfileForm').form({
        onSuccess: function (e) {
          that.saveProfile(e);
        },
        fields: {
          username: {
            identifier: 'username',
            rules: [
              {
                type: 'empty',
                prompt: 'Kullanıcı adı alanı boş geçilemez!'
              }
            ]
          },
          email: {
            identifier: 'email',
            rules: [
              {
                type: 'empty',
                prompt: 'Email alanı boş geçilemez!'
              },
              {
                type: 'email',
                prompt: 'Geçersiz Email!'
              }
            ]
          },
          oldPassword: {
            identifier: 'oldPassword',
            rules: [
              {
                type: 'empty',
                prompt: 'Eski şifre alanı boş geçilemez!'
              }
            ]
          },
          password: {
            identifier: 'password',
            rules: [
              {
                type: 'empty',
                prompt: 'Yeni şifre alanı boş geçilemez!'
              }
            ]
          },
          password2: {
            identifier: 'password2',
            rules: [
              {
                type: 'empty',
                prompt: 'Yeni şifre tekrar alanı boş geçilemez!'
              },
              {
                type: 'match[password]',
                prompt: 'Şifre eşleşmedi!'
              }
            ]
          }
        }
      });
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
      this.validation();
    }
  });

  return EditProfileView;

});