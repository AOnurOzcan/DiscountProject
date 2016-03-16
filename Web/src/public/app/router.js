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
      outer.showView(this.adminView);
    },
    AddProduct: function () {
      outer.showView(this.addProductView);
    },
    ListProduct: function () {
      outer.showView(this.listProductView);
    },
    //EditProduct: function (productId) {
    //  outer.showView(this.editProductView, {productId: productId});
    //},
    AddBranch: function () {
      outer.showView(this.addBranchView, {});
    },
    ListBranch: function () {
      outer.showView(this.listBranchView);
    },
    EditBranch: function (branchId) {
      outer.showView(this.addBranchView, {branchId: branchId});
    }
  });

  /**
   * Groups all views together
   * @param {Array|Object} args
   * @author ME99987
   * @version 1.2
   *
   * @example 1.1 still works
   * var group = viewGroup([myAddView, myListView]);
   * group[0].group[1] // access myListView from myAddView
   *
   * @example 1.2
   * var group = viewGroup({
     *   addView: myAddView,
     *   listView: myListView
     * });
   * group[0].group.listView; // main group object is array, subgroup object is both array and object
   * group[0].group[1]; // supporting the previous one (1.1)
   */
  function viewGroup(args) {
    if (args == null) {
      throw Error("args cannot be null");
    }

    if (args.constructor == Array) {
      args.forEach(function (arg) {
        arg.group = args;
      });

      return args;
    }

    if (args.constructor == Object) {
      var properties = Object.getOwnPropertyNames(args);
      var temp = [];

      properties.forEach(function (property) {
        temp.push(args[property]);
        temp[property] = args[property];
        args[temp.length] = args[property];
        args[property].group = args;
      });

      return temp;
    }

    throw Error("unsupported type of args");
  }

  return Router;
});