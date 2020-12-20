import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {User} from "../user";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit {

  constructor(private userService:UserService) { }

  user:User;

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe( user => {
      console.log(user)
      this.user = user;
    });
  }

}
