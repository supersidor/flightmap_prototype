"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const geolib_1 = require("geolib");
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const serverUrl = 'http://localhost:8080/';
const axios = require('axios').default;
//let TestUser = require("./test-user");
var W3CWebSocket = require('websocket').w3cwebsocket;
function getUser(email) {
    let promise = axios.get(serverUrl + 'api/test/user', {
        params: {
            userName: email
        }
    });
    return rxjs_1.from(promise).pipe(operators_1.map(resp => resp.data));
}
function getAircraft(testData, airCraftName) {
    let promise = axios.get(serverUrl + 'api/aircraft/register', {
        params: {
            name: airCraftName
        },
        headers: {
            Authorization: 'Bearer '.concat(testData.token)
        }
    });
    return rxjs_1.from(promise).pipe(operators_1.map(resp => resp.data));
}
function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
function produceDataToSocket(socket, testData, airCraftId) {
    let pos = {
        aircraftId: airCraftId,
        longitude: getRandomInt(-180, 180),
        latitude: 0,
        altitude: 1000,
        heading: getRandomInt(0, 360),
        timestamp: 0
    };
    const speedMetersPerSec = 300 * 1000 / (60 * 60);
    return rxjs_1.range(1, 1000).pipe(operators_1.concatMap(idx => rxjs_1.of(idx).pipe(operators_1.delay(1000))), operators_1.mergeMap((idx) => {
        pos.timestamp = new Date().getTime();
        let nextPos = geolib_1.computeDestinationPoint(pos, speedMetersPerSec, pos.heading);
        pos.latitude = nextPos.latitude;
        pos.longitude = nextPos.longitude;
        let posJson = JSON.stringify(pos);
        //console.log("Sending", testData.user.email, idx)
        socket.send(posJson);
        return rxjs_1.of("send " + idx + " to " + testData.user.email);
    }));
}
function produceData(testData, airCraftId) {
    var client = new W3CWebSocket('ws://localhost:8080/console?token=' + testData.token);
    client.onmessage = function (e) {
        console.log("Received: '", e.data);
    };
    client.onopen = function () {
        console.log('WebSocket Client Connected');
    };
    function waitForSocketToOpen(socket) {
        return new Promise(function (resolve, reject) {
            (function waitForSocket() {
                if (client.readyState != 0) {
                    return resolve();
                }
                setTimeout(waitForSocket, 500);
            })();
        });
    }
    return rxjs_1.from(waitForSocketToOpen(client)).pipe(operators_1.mergeMap(() => produceDataToSocket(client, testData, airCraftId)), operators_1.finalize(() => client.close()));
    // const snooze = ms => new Promise(resolve => setTimeout(resolve, ms));
    //
    // while (client.readyState == 0) {
    //     await snooze(500);
    // }
    // console.log()
    // const speedMetersPerSec = 300 * 1000 / (60 * 60);
    // if (client.readyState == 1) {
    //     for (var i = 0; i < 1000; i++) {
    //         pos.timestamp = new Date().getTime();
    //         let nextPos = computeDestinationPoint(pos, speedMetersPerSec, pos.heading);
    //         pos.latitude = nextPos.latitude;
    //         pos.longitude = nextPos.longitude;
    //
    //         let posJson = JSON.stringify(pos);
    //         console.log("Sending", testData.user.email, i)
    //         client.send(posJson);
    //         await snooze(1000)
    //     }
    //     client.close()
    // } else {
    //     console.log("Failed to open socket");
    // }
    //
    // subject.subscribe(
    //     msg => console.log('message received: ',msg), // Called whenever there is a message from the server.
    //     err => console.log(err), // Called if at any point WebSocket API signals some kind of error.
    //     () => console.log('complete') // Called when connection is closed (for whatever reason).
    // );
    //return Promise.resolve("ok");
}
// })
function main() {
    // //let users = new Array<User>();
    // for (var i=0;i<1;i++){
    //     const email = `test${i}@flight.test1`;
    //     getUser(email).then( testData => {
    //         console.log(`email:${testData.user.email} id:${testData.user.id}`);
    //         return getAircraft(testData,"test aircraft "+testData.user.email).then(
    //             (airCraftId) => {
    //                 console.log(`email:${testData.user.email} airCraftId:${testData.user.id}`);
    //                 return produceData(testData,airCraftId)
    //             }
    //         )
    //     }).then( (status) => {
    //         console.log("status:",status);
    //     })
    // }
    // range(1, 10).pipe(
    //     delay(10000)
    // ).subscribe(
    //     x => console.log(x)
    // )
    // return;
    rxjs_1.range(1, 10).pipe(operators_1.mergeMap(idx => getUser(`test${idx}@flight.test2`)), operators_1.mergeMap(testData => {
        return rxjs_1.combineLatest([
            rxjs_1.of(testData), getAircraft(testData, "test aircraft " + testData.user.email)
        ]);
    }), operators_1.mergeMap(tuple => produceData(tuple[0], tuple[1]))).subscribe(x => console.log(x));
}
main();
// const callMain = async () => {
//     await main()
// };
// callMain()
//# sourceMappingURL=app.js.map