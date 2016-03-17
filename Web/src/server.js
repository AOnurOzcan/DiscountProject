var express = require('express');
var config = require('config');
var requireDir = require('require-dir');
var bodyParser = require('body-parser');
var session = require('express-session');
var orm = require("orm");
var compress = require('compression');


var straightLine = new Array(50).join('-');
console.log(straightLine);

var app = express();
app.use(compress());
console.log('Express initialized');
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
console.log("Body Parser initialized");

app.use(express.static(__dirname + '/public'));
console.log("Static field forwarded");

app.use(session({
  secret: 'discount-project-web',
  resave: false,
  saveUninitialized: true
}));
console.log("Session initialized");

var project = {};
global.project = project;
global.p = project;
project.config = config;
project.express = express;
global.requireDir = requireDir;

console.log("Connecting to database...");
orm.connect(config.get("database"), function (err, db) { // create connection from pool
  if (err) { // if any error occurs.
    return console.log("Error occured in database connection! {message}", err);
  }
  db.settings.set('instance.cache', false);
  console.log("Connected to database");
  project.db = db;
  project.app = app;

  project.util = {
    BodyControl: require("./util/BodyControl"),
    //Get: require("./util/Get"),
    //RequestLogger: require("./util/RequestLogger"),
    AuthorizedRoute: require("./util/AuthorizedRoute"),
    AuthorizedRouteForUser: require("./util/AuthorizedRouteForUser")
  };
  project.router = requireDir('./api', {recurse: true});

  project.server = app.listen(config.get('port'));
  console.log('Application started to listen at {family} {address}:{port}', project.server.address());
});