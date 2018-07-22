/*---
method: 'get'
summary: 'Dont be shy, say hello!'
parameters:
  - name: limit
    in: query
    desc: How many items to return at one time (max 100)
    required: false
  - name: limit
    in: query
    desc: How many items to return at one time (max 100)
    required: false
responses:
  - status: '200'
    desc: A paged array of pets
  - status: 'default'
    desc: unexpected error
---*/

function Database() {

}

Database.prototype.getAllUsers = function() {
    return [
        { username: 'one', score: 10 },
        { username: 'two', score: 12 },
        { username: 'three', score: 15 }
    ];
};

Database.prototype.topUsers = function(limit) {
    var users = this.getAllUsers();

    users.sort(function(record1, record2) {
        return record1.score - record2.score;
    });

    return users.slice(0, limit);
};

Database.prototype.bottomUsers = function(limit) {
    var users = this.getAllUsers();

    users.sort(function(record1, record2) {
        return record2.score - record1.score;
    });

    return users.slice(0, limit);
};

// Function handler
//----------------------------------------------------------------------------------------------------------------------

exports.handler = function(event, context, callback) {
    var db = connectToDatabase();

    var limit = (event.queryStringParameters.limit) ? event.queryStringParameters.limit : 10;
    var isBottom = (event.queryStringParameters.isTop === 'false');

    var scores = queryScores(db, isBottom, limit);
    var response = postResult(event, scores);

    callback(null, {
        statusCode: 200,
        body: JSON.stringify(response)
    });
};

function connectToDatabase() {
    return new Database()
}

function queryScores(db, isBottom, limit) {
    if(isBottom) {
        return db.topUsers(limit);
    }
    else {
        return db.bottomUsers(limit);
    }
}

function postResult(event, scores) {
    return {
        event: event,
        scores: scores
    };
}
