define([
  'backbone',
  'Login/loginView',
  'CompanyHomepage/adminView',
  'Menu/menuView',
  'Product/productView',
  'Branch/branchView',
  'Upload/uploadView',
  'Account/accountView',
  'Category/CategoryView',
  'Notification/NotificationView'
], function (Backbone,
             LoginView,
             AdminView,
             MenuView,
             ProductView,
             BranchView,
             UploadView,
             Account,
             CategoryView,
             NotificationView) {

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
      'category/choose': 'ChooseCategory',
      'category/main/add': 'AddMainCategory',
      'category/main/edit/:id': 'EditMainCategory',
      'category/sub/add': 'AddSubCategory',
      'category/sub/edit/:id': 'EditSubCategory',
      'category/list': 'ListCategory',
      'notification/add': 'AddNotification',
      'notification/edit/:id': 'EditNotification',
      'notification/list': 'ListNotification',
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
      this.chooseCategoryView = [this.menuView, new CategoryView.ChooseCategoryView()];
      this.addMainCategoryView = [this.menuView, new CategoryView.AddMainCategoryView()];
      this.addSubCategoryView = [this.menuView, new CategoryView.AddSubCategoryView()];
      this.listCategoryView = [this.menuView, new CategoryView.ListCategoryView()];
      this.addNotificationView = [this.menuView, new NotificationView.AddNotificationView()];
      this.listNotificationView = [this.menuView, new NotificationView.ListNotificationView()];
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
          outer.showView(that.loginView); // Login formunu ekrana bas
        }
      });
    },

    CompanyHomepage: function () {
      this.CheckSessionShowView(this.adminView);
    },

    AddProduct: function () {
      this.CheckSessionShowView(this.addProductView);
    },

    ListProduct: function () {
      this.CheckSessionShowView(this.listProductView);
    },

    EditProduct: function (productId) {
      this.CheckSessionShowView(this.addProductView, {productId: productId});
    },

    AddBranch: function () {
      this.CheckSessionShowView(this.addBranchView);
    },

    ListBranch: function () {
      this.CheckSessionShowView(this.listBranchView);
    },

    EditBranch: function (branchId) {
      this.CheckSessionShowView(this.addBranchView, {branchId: branchId});
    },

    FileUpload: function () {
      this.CheckSessionShowView(this.fileUploadView);
    },

    AddAccount: function () {
      this.CheckSessionShowView(this.addAccountView);
    },

    EditAccount: function (accountId) {
      this.CheckSessionShowView(this.addAccountView, {accountId: accountId});
    },

    ListAccount: function () {
      this.CheckSessionShowView(this.listAccountView);
    },

    ChooseCategory: function () {
      this.CheckSessionShowView(this.chooseCategoryView);
    },

    AddMainCategory: function () {
      this.CheckSessionShowView(this.addMainCategoryView);
    },

    EditMainCategory: function (id) {
      this.CheckSessionShowView(this.addMainCategoryView, {mainCategoryId: id});
    },

    AddSubCategory: function () {
      this.CheckSessionShowView(this.addSubCategoryView);
    },

    EditSubCategory: function (id) {
      this.CheckSessionShowView(this.addSubCategoryView, {subCategoryId: id});
    },

    ListCategory: function () {
      this.CheckSessionShowView(this.listCategoryView);
    },

    AddNotification: function () {
      this.CheckSessionShowView(this.addNotificationView);
    },

    EditNotification: function (id) {
      this.CheckSessionShowView(this.addNotificationView, {notificationId: id});
    },

    ListNotification: function () {
      this.CheckSessionShowView(this.listNotificationView);
    },

    /* Bu fonksiyon önce oturumu kontrol eder, oturum varsa sayfayı ekrana basar.*/
    CheckSessionShowView: function () {
      var args = arguments;
      this.checkSession.fetch({ // Oturum açık mı kontrol et
        success: function () {
          outer.showView.apply(outer, args);
        }
      });
    }
  });

  return Router;
});