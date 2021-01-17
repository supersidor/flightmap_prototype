import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {User} from "../user";
import {AuthService} from "../auth.service";
import {webSocket} from "rxjs/webSocket";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit {

  constructor(private userService:UserService,private auth: AuthService) { }

  user:User;

  ngOnInit(): void {
    // this.userService.getCurrentUser().subscribe( user => {
    //   console.log(user)
    //   this.user = user;
    // });
    let subject = webSocket('ws://localhost:8080/ws?token='+this.auth.getToken());

    subject.subscribe(
      msg => console.log('message received: ',msg), // Called whenever there is a message from the server.
      err => console.log(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );
    //let socket = new WebSocket('ws://localhost:8080/console?token='+this.auth.getToken());
// Connection opened
//     socket.addEventListener('open', function (event) {
//        console.log("Channel is opened ")
//        //socket.send('Hello Server!');
//     });
//
//     socket.addEventListener('close', function (event) {
//       console.log("Channel is closed ")
//       //socket.send('Hello Server!');
//     });
//
// // Listen for messages
//     socket.addEventListener('message', function (event) {
//          console.log('Message from server ', event);
//     });

  }

}
