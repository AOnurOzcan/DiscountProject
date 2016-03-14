var ConfirmationCode = project.db.define('ConfirmationCode', {
  id: {type: 'serial'},
  phoneNumber: String,
  confirmationCode: Number
});

module.exports = ConfirmationCode;