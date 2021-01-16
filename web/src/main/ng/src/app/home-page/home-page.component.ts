import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {User} from "../user";
import {AuthService} from "../auth.service";

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
    let socket = new WebSocket('ws://localhost:8080/ws?token='+this.auth.getToken());
// Connection opened
    socket.addEventListener('open', function (event) {
       console.log("send ")
       socket.send('Hello Server!');
    });

// Listen for messages
    socket.addEventListener('message', function (event) {
         console.log('Message from server ', event);
    });

  }

}
