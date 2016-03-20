define([
  'backbone',
  'Login/loginView',
  'CompanyHomepage/adminView',
  'Menu/menuView',
  'Product/productView',
  'Branch/branchView',
  'Upload/uploadView',
  'Account/accountView'
], function (Backbone,
             LoginView,
             AdminView,
             MenuView,
             ProductView,
             BranchView,
             UploadView,
             Account) {

  var Router = Backbone.Router.extend({
    routes: {
      '': 'Login',
      'cHomepage': 'CompanyHomepage',
      'product/add': 'AddProduct',
      'product/list': 'ListProduct',
      'product/edit/:productId': 'EditProduct',
      'branch/add': 'AddBranch',
      'branch/list': 'ListBranch',
      'branch/edit/:branchId': 'EditBranch',
      'account/add': 'AddAccount',
      'account/edit/:id': 'EditAccount',
      'account/list': 'ListAccount',
      'file/upload': 'FileUpload'
    },

    initialize: function () {
      this.menuView = new MenuView();
      this.checkSession = new LoginView.CheckSession();
      this.loginView = [new LoginView.LoginView()];
      this.adminView = [this.menuView, new AdminView()];
      this.addProductView = [this.menuView, new ProductView.AddProductView()];
      this.listProductView = [this.menuView, new ProductView.ListProductView()];
      this.addBranchView = [this.menuView, new BranchView.AddBranchView()];
      this.listBranchView = [this.menuView, new BranchView.ListBranchView()];
      this.fileUploadView = [this.menuView, new UploadView.UploadView()];
      this.addAccountView = [this.menuView, new Account.AddAccountView()];
      this.listAccountView = [this.menuView, new Account.ListAccountView()];
    },

    Login: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {  // Oturum açıksa
          window.location.hash = 'cHomepage'; // Firma anasayfasına yönlendir
        },
        error: function () { // Oturum yoksa
          outer.showView(that.loginView); // Login formunu ekrana bas
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

    EditProduct: function (productId) {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addProductView, {productId: productId});
        }
      });
    },

    AddBranch: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addBranchView);
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
    },

    FileUpload: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.fileUploadView);
        }
      });
    },

    AddAccount: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addAccountView);
        }
      });
    },

    EditAccount: function (accountId) {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.addAccountView, {accountId: accountId});
        }
      });
    },

    ListAccount: function () {
      var that = this;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView(that.listAccountView);
        }
      });
    }
  });

  return Router;
});