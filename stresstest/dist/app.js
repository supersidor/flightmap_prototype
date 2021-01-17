"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const geolib_1 = require("geolib");
const serverUrl = 'http://localhost:8080/';
const axios = require('axios').default;
//let TestUser = require("./test-user");
var W3CWebSocket = require('websocket').w3cwebsocket;
function getUser(email) {
    return __awaiter(this, void 0, void 0, function* () {
        return axios.get(serverUrl + 'api/test/user', {
            params: {
                userName: email
            }
        }).then((resp) => Promise.resolve(resp.data));
    });
}
function getAircraft(testData, airCraftName) {
    return axios.get(serverUrl + 'api/aircraft/register', {
        params: {
            name: airCraftName
        },
        headers: {
            Authorization: 'Bearer '.concat(testData.token)
        }
    }).then((resp) => Promise.resolve(resp.data));
}
// axios.get('http://localhost:8080/api/test/user', {
//     params: {
//         userName: 'test1@flight.test'
//     }
// }).then(function (response) {
//     console.log("userresponse.data);
// }).catch(function (error) {
//     console.log(error);
function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
function produceData(testData, airCraftId) {
    return __awaiter(this, void 0, void 0, function* () {
        var client = new W3CWebSocket('ws://localhost:8080/console?token=' + testData.token);
        client.onmessage = function (e) {
            console.log("Received: '", e.data);
        };
        client.onopen = function () {
            console.log('WebSocket Client Connected');
            // function sendNumber() {
            //     if (client.readyState === client.OPEN) {
            //         var number = Math.round(Math.random() * 0xFFFFFF);
            //         client.send(number.toString());
            //         setTimeout(sendNumber, 1000);
            //     }
            // }
            // sendNumber();
        };
        const snooze = ms => new Promise(resolve => setTimeout(resolve, ms));
        while (client.readyState == 0) {
            yield snooze(500);
        }
        console.log();
        const speedMetersPerSec = 300 * 1000 / (60 * 60);
        if (client.readyState == 1) {
            let pos = {
                aircraftId: airCraftId,
                longitude: getRandomInt(-180, 180),
                latitude: 0,
                altitude: 1000,
                heading: getRandomInt(0, 360),
                timestamp: 0
            };
            for (var i = 0; i < 1000; i++) {
                pos.timestamp = new Date().getTime();
                let nextPos = geolib_1.computeDestinationPoint(pos, speedMetersPerSec, pos.heading);
                pos.latitude = nextPos.latitude;
                pos.longitude = nextPos.longitude;
                let posJson = JSON.stringify(pos);
                console.log("Sending", testData.user.email, i);
                client.send(posJson);
                yield snooze(1000);
            }
            client.close();
        }
        else {
            console.log("Failed to open socket");
        }
        //
        // subject.subscribe(
        //     msg => console.log('message received: ',msg), // Called whenever there is a message from the server.
        //     err => console.log(err), // Called if at any point WebSocket API signals some kind of error.
        //     () => console.log('complete') // Called when connection is closed (for whatever reason).
        // );
        return Promise.resolve("ok");
    });
}
// })
function main() {
    return __awaiter(this, void 0, void 0, function* () {
        //let users = new Array<User>();
        for (var i = 0; i < 1; i++) {
            const email = `test${i}@flight.test1`;
            getUser(email).then(testData => {
                console.log(`email:${testData.user.email} id:${testData.user.id}`);
                return getAircraft(testData, "test aircraft " + testData.user.email).then((airCraftId) => {
                    console.log(`email:${testData.user.email} airCraftId:${testData.user.id}`);
                    return produceData(testData, airCraftId);
                });
            }).then((status) => {
                console.log("status:", status);
            });
        }
    });
}
const callMain = () => __awaiter(void 0, void 0, void 0, function* () {
    yield main();
});
callMain();
//# sourceMappingURL=app.js.map