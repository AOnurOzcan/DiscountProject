define([
  'backbone',
  'Login/loginView',
  'CompanyHomepage/adminView',
  'Menu/menuView',
  'Product/productView',
  'Branch/branchView'], function (Backbone, LoginView, AdminView, MenuView, ProductView, BranchView) {

  var Router = Backbone.Router.extend({
    routes: {
      '': 'Login',
      'cHomepage': 'CompanyHomepage',
      'product/add': 'AddProduct',
      'product/list': 'ListProduct',
      'product/edit/:productId': 'EditProduct',
      'branch/add': 'AddBranch',
      'branch/list': 'ListBranch',
      'branch/edit/:branchId': 'EditBranch'
    },

    initialize: function () {
      this.menuView = new MenuView();
      this.loginView = [new LoginView.LoginView()];
      this.adminView = [this.menuView, new AdminView()];
      this.addProductView = [this.menuView, new ProductView.AddProductView()];
      this.listProductView = [this.menuView, new ProductView.ListProductView()];
      //this.editProductView = [this.menuView, new ProductView.EditProductView()];
      this.addBranchView = [this.menuView, new BranchView.AddBranchView()];
      this.listBranchView = [this.menuView, new BranchView.ListBranchView()];
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
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {  // Oturum açıksa
          outer.showView(that.adminView);
        }
      });
    },
    AddProduct: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addProductView);
        }
      });
    },
    ListProduct: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.listProductView);
        }
      });
    },
    //EditProduct: function (productId) {
    //  outer.showView(this.editProductView, {productId: productId});
    //},
    AddBranch: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addBranchView, {});
        }
      });
    },
    ListBranch: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.listBranchView);
        }
      });
    },
    EditBranch: function (branchId) {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addBranchView, {branchId: branchId});
        }
      });
    }
  });

  return Router;
});