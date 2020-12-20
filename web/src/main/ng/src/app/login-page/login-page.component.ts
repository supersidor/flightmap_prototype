import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";


@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit {

  constructor(private route:ActivatedRoute) { }
  private static readonly OAUTH2_DEFAULT_REDIRECT_URI = window.location.origin+'/oauth2/redirect'

  redirectUrl = ''
  ngOnInit(): void {
    this.redirectUrl = LoginPageComponent.OAUTH2_DEFAULT_REDIRECT_URI;
    this.route.queryParams.subscribe( params => {
      console.log("params",params);
      if (params.redirect_uri){
        this.redirectUrl = params.redirect_uri;
        console.log("nw redirect Url",this.redirectUrl);
      }
    })

  }

}
