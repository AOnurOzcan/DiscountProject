define([
  'backbone',
  'handlebars',
  'text!Upload/uploadTemplate.html'
], function (Backbone, Handlebars, UploadTemplate) {

  var uploadTemplate = Handlebars.compile(UploadTemplate);

  var ImageCollection = Backbone.Collection.extend({
    url: "/files/all"
  });
  var ImageModel = Backbone.Model.extend({});

  var UploadView = core.CommonView.extend({
    autoLoad: true,
    el: "#page",
    events: {
      'click #uploadButton': 'uploadFile'
    },
    initialize: function () {
      this.imageCollection = new ImageCollection();
    },
    uploadFile: function (e) {
      e.preventDefault();
      var that = this;
      var form = $("#uploadForm")[0];
      var oData = new FormData(form);
      var oReq = new XMLHttpRequest();
      oReq.open("POST", "/testUpload", true);
      $("#uploadForm").addClass("loading");
      oReq.send(oData);
      oReq.onload = function (oEvent) {
        if (oReq.status == 200) {
          that.imageCollection.fetch({
            success: function () {
              that.render();
            }
          });
        } else {
          alert("Dosya yükleme başarısız!!!");
        }
      };
    },
    render: function () {
      this.$el.html(uploadTemplate({images: this.imageCollection.toJSON()}));
      $("#fileUpload").on('change', function () {
        $("#uploadButton").show();

        //Get count of selected files
        var countFiles = $(this)[0].files.length;

        var imgPath = $(this)[0].value;
        var extn = imgPath.substring(imgPath.lastIndexOf('.') + 1).toLowerCase();
        var image_holder = $("#image-holder");
        image_holder.empty();

        if (extn == "png" || extn == "jpg" || extn == "jpeg") {
          if (typeof (FileReader) != "undefined") {

            //loop for each file selected for uploaded.
            for (var i = 0; i < countFiles; i++) {

              var reader = new FileReader();
              reader.onload = function (e) {
                $("<img />", {
                  "src": e.target.result,
                  "class": "ui small rounded image"
                }).appendTo(image_holder);
              };

              image_holder.show();
              reader.readAsDataURL($(this)[0].files[i]);
            }

          } else {
            alert("This browser does not support FileReader.");
          }
        } else {
          alert("Pls select only images");
        }
      });
    }
  });
  return {
    UploadView: UploadView
  }

});