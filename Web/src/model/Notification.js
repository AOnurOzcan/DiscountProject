var Notification = project.db.define('Notification', {
  id: {type: 'serial'},
  name: String,
  startDate: Date,
  endDate: Date,
  sendDate: Date,
  description: String,
  isSent: Boolean,
  peopleCount: Number
});

module.exports = Notification;