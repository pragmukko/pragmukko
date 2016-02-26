'use strict';

var express = require('express')
    ,path = require('path')
    ,publicPath = path.join(__dirname, '/build')
    ,cors = require('cors')
    ,app = express();

app.use(cors());
app.use('/', express.static(publicPath));

var server = app.listen(3000, 'localhost', infoLog);

function infoLog() {
  var host = server.address().address,
      port = server.address().port;

  console.log('Example app listening at http://%s:%s', host, port);
}
