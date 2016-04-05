define([], function () {
  Number.prototype.addZero = function () {
    return this < 10 ? "0" + this : this;
  };

  String.prototype.dateFmt = function () {
    var date = new Date(this);
    var day = date.getDate();
    var monthIndex = date.getMonth();
    var year = date.getFullYear();
    //return day + "/" + months[monthIndex] + "/" + year;
    return day.addZero() + "/" + (monthIndex + 1).addZero() + "/" + year;
  };

  String.prototype.convertSqlDate = function () {
    var dateArray = this.split('/');
    var day = dateArray[0];
    var month = dateArray[1];
    var year = dateArray[2];
    return year + "-" + month + "-" + day;
  };
});