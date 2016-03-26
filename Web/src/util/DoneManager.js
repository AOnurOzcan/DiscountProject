module.exports = function (finished) {
  var x = 0;

  var done = function () {
    if (--x > 0) return;
    finished();
  };

  done.iterate = function () {
    x++;
  };

  return done;
};