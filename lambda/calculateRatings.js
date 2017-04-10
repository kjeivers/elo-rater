'use strict';
const AWS = require('aws-sdk');

module.exports = {
    handler: lambdaHandler,
    writeRatingsPromise: writeRatingsPromise,
    writeRatingsPromises: writeRatingsPromises,
    recalculateAllRatingsPromise: recalculateAllRatingsPromise
};

function lambdaHandler(event, context, callback) {
    // recalculate all ratings
    console.log('Event: ' + JSON.stringify(event));
    const documentClient = new AWS.DynamoDB.DocumentClient({
        apiVersion: '2012-08-10',
        region: 'eu-central-1'
    });
    recalculateAllRatingsPromise(documentClient)
    .then(ratings => {
            const putItemParams = Object.values(ratings).map(rating => {
                return {
                    'TableName': 'ratings',
                    'Item': ratings[rating.playerId]
                };
            });
            const putPromises = putItemParams.map(param => new Promise((resolve, reject) => {
                documentClient.putItem(param).promise();
            }));
            return Promise.all(putPromises);
        }
    )
    .then(result => callback('done with '+JSON.stringify(result)));
}

function writeRatingsPromises(dynamodb, ratings) {
    const putPromises = Object.values(ratings)
        .map(rating => writeRatingsPromise(dynamodb, rating));
    return Promise.all(putPromises);
}

function writeRatingsPromise(dynamodb, rating) {
    const params = {
        'TableName': 'ratings',
        'Item': rating
    };
    return dynamodb.put(params).promise();
}

/* Go through all games, recalculate ratings and store the historical 
 * and current ratings for all players 
 */
function recalculateAllRatingsPromise(dynamodb) {

    const gamesScan = {
        TableName: 'games',
        Select: 'ALL_ATTRIBUTES'
    };
    const playersScan = {
        TableName:       'players',
        Select:          'SPECIFIC_ATTRIBUTES',
        AttributesToGet: ['id', 'initialRating', 'initialRatingTs']
    };
    return Promise.all([
        dynamodb.scan(gamesScan).promise(),
        dynamodb.scan(playersScan).promise()
    ]).then(array => {
        const games = array[0].Items;
        const players = array[1].Items;
        return calculateRatings(games, players);
    })
}

const k_factor = 15;

function calculateRatings(games, players) {
    const ratings = {};
    players.forEach(p =>
        ratings[p.id] = {
            playerId: p.id,
            rating: p.initialRating,
            ts: p.initialRatingTs,
            history: [
                {
                    rating: p.initialRating,
                    ts: p.initialRatingTs
                }
            ]
        });
    games.forEach(game => {
        const white = ratings[game.whiteId];
        const black = ratings[game.blackId];
        const whiteHistory = white.history;
        const blackHistory = black.history;
        const whiteRating = white.rating;
        const blackRating = black.rating;
        let resultForWhite, resultForBlack;
        switch(game.result) {
            case 'w':
                resultForWhite = 1;
                resultForBlack = 0;
                break;
            case 'b':
                resultForWhite = 0;
                resultForBlack = 1;
                break;
            case 'd':
                resultForWhite = 0.5;
                resultForBlack = 0.5;
                break;
        }
        white.rating = calculateNewRating(whiteRating, blackRating, resultForWhite);
        white.ts = game.ts;
        whiteHistory.push({
            rating: white.rating,
            ts: game.ts,
            opponent: game.blackId});
        black.rating = calculateNewRating(blackRating, whiteRating, resultForBlack);
        blackHistory.push({
            rating: black.rating,
            ts: game.ts,
            opponent: game.whiteId});
    });
    return ratings;
}

/**
 *
 * @param currentRating Current rating of the person to update rating for
 * @param ratingOpponent Current rating of the opponent
 * @param gameResult 1=won, 0.5= draw, 0=lost
 * @returns {number} The new rating of the person
 */
function calculateNewRating(currentRating, ratingOpponent, gameResult) {
    const diff = ratingOpponent - currentRating;
    const expectedResult = 1 / (1 + Math.pow(10, (diff / 400))); // percent chance of win
    const roundedExpectedResult = expectedResult.toFixed(2);
    const change = k_factor * (gameResult - roundedExpectedResult);
    const newRating = currentRating + change;
    return Math.round(newRating);
}

/* Test-method for running against DynamoDB from local dev env */
const documentClient = new AWS.DynamoDB.DocumentClient({
    apiVersion: '2012-08-10',
    region: 'eu-central-1'
});
recalculateAllRatingsPromise(documentClient)
    .then(ratings => writeRatingsPromises(documentClient, ratings) );