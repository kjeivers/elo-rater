'use strict';

const expect = require('chai').expect;
const assert = require('chai').assert;

const calculateRatings = require('../lambda/calculateRatings');

describe("Rating calculation (Lambda)", function(done) {


    it("Test rating - one game", function () {

        const games = {
            "Items": [
                {
                    "ts": "2017-01-04",
                    "result": "w",
                    "id": "8e1f045c-ffa7-4194-b97c-26e62555c9f5",
                    "blackId": "p2",
                    "whiteId": "p1"
                }
            ]
        };
        const players = {
            Items: [
                {
                    "initialRating": 1583,
                    "id": "p1",
                    "initialRatingTs": "2017-01-01"
                },
                {
                    "initialRating": 1572,
                    "id": "p2",
                    "initialRatingTs": "2017-01-01"
                }
            ]
        };

        const dynamodb = {
            scan: function (params) {
                return {
                    promise: function () {
                        switch (params.TableName) {
                            case 'games':
                                return Promise.resolve(games);
                            case 'players':
                                return Promise.resolve(players);
                            default:
                                return Promise.reject('unknown table ' + params.TableName);
                        }
                    }
                }

            }
        };
        return calculateRatings.recalculateAllRatingsPromise(dynamodb)
            .then(ratings => {
                expect(ratings['p1'].rating).to.be.equal(1590);
                expect(ratings['p2'].rating).to.be.equal(1565);
            });
    });


    it("Test writeRatingPromise", function () {
        const dynamodb = {
            put: function (params) {
                return {
                    promise: function() {
                        switch (params.TableName) {
                            case 'ratings':
                                return Promise.resolve(params.Item); //Fake response, for test assertion
                            default:
                                return Promise.reject('unknown table ' + params.TableName);
                        }
                    }
                }
            }
        };
        const rating = {
            playerId: 'a',
            rating: 1000,
            "ts": "2017-01-01",
            history: [
                {
                    rating: 1000,
                    ts: "2017-01-01"
                }
            ]
        };
        return calculateRatings.writeRatingsPromise(dynamodb, rating)
        .then(result => {
            expect(result).to.be.equal(rating);
        })
    });

});