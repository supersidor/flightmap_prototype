import {TestUser} from "./test-user";
import {Position} from "./position";
import {computeDestinationPoint} from "geolib";
import {combineLatest, from, Observable, of, range} from "rxjs";
import {concatMap, delay, finalize, flatMap, map, mergeMap, tap, withLatestFrom} from "rxjs/operators";
import {AxiosResponse} from "axios";

const serverUrl = 'http://localhost:8080/'
const axios = require('axios').default
//let TestUser = require("./test-user");
var W3CWebSocket = require('websocket').w3cwebsocket;

function getUser(email: string): Observable<TestUser> {
    let promise: Promise<AxiosResponse<TestUser>> = axios.get(serverUrl + 'api/test/user', {
        params: {
            userName: email
        }
    });
    return from(promise).pipe(
        map(resp => resp.data)
    )
}

function getAircraft(testData: TestUser, airCraftName: string): Observable<number> {
    let promise: Promise<AxiosResponse<number>> = axios.get(serverUrl + 'api/aircraft/register', {
        params: {
            name: airCraftName
        },
        headers: {
            Authorization: 'Bearer '.concat(testData.token)
        }
    })
    return from(promise).pipe(
        map(resp => resp.data)
    )
}

function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function produceDataToSocket(socket, testData: TestUser, airCraftId: number): Observable<String> {
    let pos: Position = {
        aircraftId: airCraftId,
        longitude: getRandomInt(-180, 180),
        latitude: 0,
        altitude: 1000,
        heading: getRandomInt(0, 360),
        timestamp: 0
    }
    const speedMetersPerSec = 300 * 1000 / (60 * 60);

    return range(1, 1000).pipe(
        concatMap(idx => of(idx).pipe(delay(1000))),
        mergeMap((idx) => {
                    pos.timestamp = new Date().getTime();
                    let nextPos = computeDestinationPoint(pos, speedMetersPerSec, pos.heading);
                    pos.latitude = nextPos.latitude;
                    pos.longitude = nextPos.longitude;

                    let posJson = JSON.stringify(pos);
                    //console.log("Sending", testData.user.email, idx)
                    socket.send(posJson);
                    return of("send "+idx +" to "+testData.user.email);
        })
    )
}

function produceData(testData: TestUser, airCraftId: number): Observable<String> {

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

    return from(waitForSocketToOpen(client)).pipe(
        mergeMap(
            () => produceDataToSocket(client, testData, airCraftId)
        ),
        finalize(() => client.close())
    )


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
    range(1, 10).pipe(
        mergeMap(idx => getUser(`test${idx}@flight.test2`)),
        mergeMap(testData => {
            return combineLatest([
                of(testData), getAircraft(testData, "test aircraft " + testData.user.email)
            ])
        }),
        mergeMap(tuple => produceData(tuple[0], tuple[1]))
    ).subscribe(
        x => console.log(x)
    );
}

main()

// const callMain = async () => {
//     await main()
// };
// callMain()
