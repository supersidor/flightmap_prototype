import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {LoginPageComponent} from './login-page/login-page.component';
import {AppRoutingModule} from "./app-routing.module";
import {OAuthRedirectPageComponent} from './oauth-redirect-page/oauth-redirect-page.component';
import {HomePageComponent} from './home-page/home-page.component';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {TokenInterceptor} from "./token.interceptor";
import {AuthService} from "./auth.service";
import { MapTestPageComponent } from './map-test-page/map-test-page.component';

export function initializeAuth(auth: AuthService) {
   return () => {
     auth.initialize();
  }
}

@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    OAuthRedirectPageComponent,
    HomePageComponent,
    PageNotFoundComponent,
    MapTestPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      multi: true,
      useClass: TokenInterceptor
    },
    AuthService,
    {provide: APP_INITIALIZER, useFactory: initializeAuth, deps: [AuthService], multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
