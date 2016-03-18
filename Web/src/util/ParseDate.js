module.exports = function (date) {
  if (date == undefined) {
    return;
  }
  var year, month, day;
  var dateAttributes = date.split("/");
  day = dateAttributes[0];
  month = dateAttributes[1];
  year = dateAttributes[2];

  return year + "-" + month + "-" + day;
};
