define([
  'backbone',
  'Login/loginView',
  'CompanyHomepage/adminView',
  'Menu/menuView',
  'Product/productView'], function (Backbone, LoginView, AdminView, MenuView, ProductView) {

  var Router = Backbone.Router.extend({
    routes: {
      '': 'Login',
      'cHomepage': 'CompanyHomepage',
      'product/add': 'AddProduct',
      'product/list': 'ListProduct'
    },

    initialize: function () {
      this.menuView = new MenuView();
      this.loginView = [new LoginView.LoginView()];
      this.adminView = [this.menuView, new AdminView()];
      this.addProductView = [this.menuView, new ProductView.AddProductView()];
      this.listProductView = [this.menuView, new ProductView.ListProductView()];
      this.checkSession = new LoginView.CheckSession();
    },

    Login: function () {
      var that = this;
      $('.menu').empty(); // Login ekranında menü görünmemesi gerektiği için menüyü siliyoruz.
      this.menuView.isRendered = false; // Login'den farklı bir ekrana geçildiğinde menünün basılması için false yaptık.
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {  // Oturum açıksa
          window.location.hash = 'cHomepage'; // Firma anasayfasına yönlendir
        },
        error: function () { // Oturum yoksa
          outer.showView(that.loginView);
        }
      });
    },

    CompanyHomepage: function () {
      outer.showView(this.adminView);
    },
    AddProduct: function () {
      outer.showView(this.addProductView);
    },
    ListProduct: function () {
      outer.showView(this.listProductView);
    }
  });

  return Router;
});