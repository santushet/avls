var r = require('rethinkdb'); // Initialise rethinkdb instance

var pubnub = require("pubnub")({ // Initialise pubnub instance
   subscribe_key:'sub-c-be626cbc-18f1-11e6-8b91-02ee2ddab7fe',
         publish_key:'pub-c-a64317a5-a592-4a93-86a5-470684e385f0'
});

// Establish a connection
var connection = null;
r.connect( {host: 'ec2-54-173-220-171.compute-1.amazonaws.com', port: 28015}, function(err, conn) {
    if (err) throw err;
    connection = conn;

      r.table('packet').run(connection,function(err,cursor)){
      cursor.each(function(err,result){
        if(err) throw err;
            pubnub.publish({
              channel:"updates",
              message:result,
              callback: function(m){console.log(m)}
            });
      });
    };
    r.table('packet').run(connection,function(err,cursor)){
      cursor.each(function(err,result){
        if(err) throw err;
            pubnub.publish({
              channel:"updates1",
              message:result,
              callback: function(m){console.log(m)}
            });
      });
    };

    r.table('packet').filter(r.row('unitNo').eq("355217044865989")) 
      .changes() // Look for any changes happening to this record and keeps returning it
      .run(connection, function(err, cursor) { // run the above query on this connection
        if (err) throw err;
        cursor.each(function(err, result) { // If there is no error, it returns a cursor, which is collection of JSON objects
            if (err) throw err;

            pubnub.publish({  //publishing the updated data through PubNub, in 'updates' channel
              channel: "updates",
              message: result, //this is the message payload we are sending
              // result contains a new value and the old value of this record. This is due to .changes() method.
              callback: function(m){console.log(m)}
            });
        });
    });
    
    
    r.table('packet').filter(r.row('unitNo').eq("353218071614755")) 
      .changes() // Look for any changes happening to this record and keeps returning it
      .run(connection, function(err, cursor) { // run the above query on this connection
        if (err) throw err;
        cursor.each(function(err, result) { // If there is no error, it returns a cursor, which is collection of JSON objects
            if (err) throw err;

            pubnub.publish({  //publishing the updated data through PubNub, in 'updates' channel
              channel: "updates1",
              message: result, //this is the message payload we are sending
              // result contains a new value and the old value of this record. This is due to .changes() method.
              callback: function(m){console.log(m)}
            });
        });
    });
})


