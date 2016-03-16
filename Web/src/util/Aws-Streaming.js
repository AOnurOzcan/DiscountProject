// Initialize aws client
// =====================
var Knox = require('knox');
var moment = require('moment');
var crypto = require('crypto');

// Create the knox client with your aws settings
Knox.aws = Knox.createClient({
  key: 'AKIAI5MWYHAWGD4FLBZQ',
  secret: 'gICw5i4yOLJopBtKJhF5lIHmdAD+cogU7hJwhC6i',
  bucket: 'ooar1'
});

// S3 upload service - stream buffers to S3
// ========================================
var s3UploadService = function (req, next) {
  req.files = {};

  req.busboy.on('file', function (fieldname, file, filename, encoding, mimetype) {
    if (!filename) {
      // If filename is not truthy it means there's no file
      return;
    }
    // Create the initial array containing the stream's chunks
    file.fileRead = [];

    file.on('data', function (chunk) {
      // Push chunks into the fileRead array
      this.fileRead.push(chunk);
    });

    file.on('error', function (err) {
      console.log('Error while buffering the stream: ', err);
    });

    file.on('end', function () {
      // Concat the chunks into a Buffer
      var finalBuffer = Buffer.concat(this.fileRead);

      req.files[fieldname] = {
        buffer: finalBuffer,
        size: finalBuffer.length,
        filename: filename,
        mimetype: mimetype
      };

      // Generate date based folder prefix
      var datePrefix = moment().format('YYYY[/]MM');
      var key = crypto.randomBytes(10).toString('hex');
      var hashFilename = key + '-' + filename;

      var pathToArtwork = '/artworks/' + datePrefix + '/' + hashFilename;

      var headers = {
        'Content-Length': req.files[fieldname].size,
        'Content-Type': req.files[fieldname].mimetype,
        'x-amz-acl': 'public-read'
      };

      Knox.aws.putBuffer(req.files[fieldname].buffer, pathToArtwork, headers, function (err, response) {
        if (err) {
          console.error('error streaming image: ', new Date(), err);
          return next(err);
        }
        if (response.statusCode !== 200) {
          console.error('error streaming image: ', new Date(), err);
          return next(err);
        }
        console.log('Amazon response statusCode: ', response.statusCode);
        console.log('Your file was uploaded');
        next(null, response.url);
      });
    });
  });

  req.busboy.on('error', function (err) {
    console.error('Error while parsing the form: ', err);
    next(err);
  });

  req.busboy.on('finish', function (response) {
    console.log('Done parsing the form!');
  });

  // Start the parsing
  req.pipe(req.busboy);
};

module.exports = s3UploadService;