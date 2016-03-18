var Branch = require("../model/Branch");
var AuthorizedRoute = require("../util/AuthorizedRoute");

project.app.post("/branch", AuthorizedRoute(""), function (req, res) {
  var branch = req.body;
  branch.companyId = req.session.admin.companyId;
  Branch.create(branch, function (err, savedBranch) {
    if (err) {
      res.unknown();
    }
    res.json(savedBranch);
  });
});

project.app.put("/branch/:id", AuthorizedRoute(""), function (req, res) {
  Branch.get(req.params.id, function (err, savedBranch) {
    delete savedBranch.City;
    savedBranch.save(req.body, function (err) {
      if (err) {
        res.unknown();
        return;
      }
      res.json({status: "success"});
    });
  });
});

project.app.get("/branch/:id", AuthorizedRoute(""), function (req, res) {

  Branch.one({id: req.params.id}, function (err, branch) {
    if (err) {
      res.unknown();
    }
    branch.companyId = branch.Company;
    branch.cityId = branch.City;
    delete branch.Company;
    delete branch.City;
    res.json(branch);
  });

});

project.app.delete("/branch/:id", AuthorizedRoute(""), function (req, res) {
  Branch.find({id: req.params.id}).remove(function (err) {
    if (err) {
      res.unknown();
    }
    res.json({status: "success"});
  })
});

project.app.get("/getBranches", AuthorizedRoute(""), function (req, res) {

  Branch.find({companyId: req.session.admin.companyId}, function (err, branches) {
    if (err) {
      console.log(err);
      res.unknown();
    }
    res.json(branches);
  });
});