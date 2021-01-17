import {TestUser} from "./test-user";
import {Position} from "./position";
import {computeDestinationPoint} from "geolib";

const serverUrl = 'http://localhost:8080/'
const axios = require('axios').default
//let TestUser = require("./test-user");
var W3CWebSocket = require('websocket').w3cwebsocket;

async function getUser(email:string) : Promise<TestUser>{
    return axios.get(serverUrl+'api/test/user', {
        params: {
            userName: email
        }
    }).then ( (resp) => Promise.resolve(resp.data))
}
function getAircraft(testData: TestUser, airCraftName: string): Promise<number> {
    return axios.get(serverUrl+'api/aircraft/register', {
        params: {
            name: airCraftName
        },
        headers: {
            Authorization: 'Bearer '.concat(testData.token)
        }
    }).then ( (resp) => Promise.resolve(resp.data))
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
async function produceData(testData: TestUser, airCraftId: number): Promise<String>{
    var client = new W3CWebSocket('ws://localhost:8080/console?token='+testData.token);
    client.onmessage = function(e) {
        console.log("Received: '",e.data);
    };
    client.onopen = function() {
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

    while (client.readyState==0){
        await snooze(500);
    }
    console.log()
    const speedMetersPerSec =  300*1000/ (60*60);
    if (client.readyState==1){
        let pos:Position = {
            aircraftId:airCraftId,
            longitude: getRandomInt(-180,180),
            latitude: 0,
            altitude: 1000,
            heading:getRandomInt(0,360),
            timestamp: 0
        }
        for (var i=0;i<1000;i++){
            pos.timestamp = new Date().getTime();
            let nextPos = computeDestinationPoint(pos,speedMetersPerSec,pos.heading);
            pos.latitude = nextPos.latitude;
            pos.longitude = nextPos.longitude;

            let posJson = JSON.stringify(pos);
            console.log("Sending",testData.user.email,i)
            client.send(posJson);
            await snooze(1000)
        }
        client.close()
    }else{
        console.log("Failed to open socket");
    }


    //
    // subject.subscribe(
    //     msg => console.log('message received: ',msg), // Called whenever there is a message from the server.
    //     err => console.log(err), // Called if at any point WebSocket API signals some kind of error.
    //     () => console.log('complete') // Called when connection is closed (for whatever reason).
    // );
    return Promise.resolve("ok");
}

// })
async function  main(){
    //let users = new Array<User>();
    for (var i=0;i<1;i++){
        const email = `test${i}@flight.test1`;
        getUser(email).then( testData => {
            console.log(`email:${testData.user.email} id:${testData.user.id}`);
            return getAircraft(testData,"test aircraft "+testData.user.email).then(
                (airCraftId) => {
                    console.log(`email:${testData.user.email} airCraftId:${testData.user.id}`);
                    return produceData(testData,airCraftId)
                }
            )
        }).then( (status) => {
            console.log("status:",status);
        })
    }
}


const callMain = async () => {
    await main()
};
callMain()
