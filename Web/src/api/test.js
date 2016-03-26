project.app.get("/accountTest", function (req, res) {
  Account.find(function (err, account) {
    console.log(account);
  });
});

project.app.get("/menuTest", function (req, res) {
  //var authArray = req.session.admin.accountAuth.split(',');
  //console.log(admin);
  //
  //var menu2 = [{header: "Ürünler", links: []}, {header: "Bildirimler", links: []}];
  //
  //
  //for (var i = 0; i < authArray.length; i++) {
  //  switch (authArray[i]) {
  //    case "CREATE_PRODUCT":
  //      menu2.forEach(function (menuItem) {
  //        if (menuItem.header == "Ürünler") {
  //          menuItem.links.push({name: "Ürün Ekle", href: "#product/add"});
  //          return;
  //        }
  //      });
  //  }
  //}


  var menu = {
    companyName: req.session.admin.companyName == undefined ? "Yönetici Paneli" : req.session.admin.companyName,
    headers: [
      {
        header: "Ürünler",
        links: [{name: "Ürün Ekle", href: "#product/add"}, {name: "Ürün Listele", href: "#product/list"}]
      },
      {
        header: "Bildirimler",
        links: [{name: "Bildirim Ekle", href: "#notification/add"}, {
          name: "Bildirim Listele",
          href: "#notification/list"
        }]
      },
      {
        header: "Şubeler",
        links: [{name: "Şube Ekle", href: "#branch/add"}, {name: "Şube Listele", href: "#branch/list"}]
      },
      {
        header: "Kullanıcılar", links: [
        {name: "Kullanıcı Ekle", href: "#account/add"},
        {name: "Kullanıcıları Listele", href: "#account/list"}]
      },
      {
        header: "Kategoriler", links: [
        {name: "Kategori Seç", href: "#category/choose"},
        {name: "Ana Kategori Ekle", href: "#category/main/add"},
        {name: "Alt Kategori Ekle", href: "#category/sub/add"},
        {name: "Kategorileri Listele", href: "#category/list"}
      ]
      },
      {
        header: "Yüklemeler", links: [
        {name: "Resim Yükle", href: "#file/upload"}]
      }
    ]
  };

  req.session.admin.accountAuth.split(',').forEach(function (auth) {

  });

  res.json(menu);
});
