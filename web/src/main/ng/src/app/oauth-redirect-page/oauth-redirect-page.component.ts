import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ApiConst} from "../api-const";
import {AuthService} from "../auth.service";

@Component({
  template: ''
})
export class OAuthRedirectPageComponent implements OnInit {

  constructor(private route: ActivatedRoute,private router:Router,private auth:AuthService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe( params => {
      console.log("params",params);
      const token = params[ApiConst.OAUTH_TOKEN_PARAM];
      this.auth.setToken(token);
      this.router.navigate(['/'])
    })
  }

}
