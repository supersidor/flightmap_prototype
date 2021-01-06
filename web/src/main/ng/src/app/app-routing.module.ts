import {NgModule} from '@angular/core'
import {RouterModule, Routes} from '@angular/router'
import {LoginPageComponent} from './login-page/login-page.component'
import {OAuthRedirectPageComponent} from "./oauth-redirect-page/oauth-redirect-page.component";
import {HomePageComponent} from "./home-page/home-page.component";
import {PageNotFoundComponent} from "./page-not-found/page-not-found.component";
import {AuthGuard} from "./auth.guard";
import {MapTestPageComponent} from "./map-test-page/map-test-page.component";

const routes: Routes = [
  { path: '', component:HomePageComponent, canActivate: [AuthGuard], pathMatch: 'full'},
  { path: 'login',component: LoginPageComponent },
  { path: 'maptest',component: MapTestPageComponent },
  { path: 'oauth2_result/redirect',component: OAuthRedirectPageComponent },
  { path: '**',component: PageNotFoundComponent },



  /*
  { path: '/login',component: LoginPageComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full'}
   */
  /*{
    path: '', component: AuthLayoutComponent, children: [
      {path: '', redirectTo: '/login', pathMatch: 'full'},
      {path: 'login', component: LoginPageComponent},
      {path: 'register', component: RegisterPageComponent}
    ]
  },
  {
    path: '', component: SiteLayoutComponent, canActivate: [AuthGuard], children: [
      {path: 'overview', component: OverviewPageComponent},
      {path: 'analytics', component: AnalyticsPageComponent},
      {path: 'history', component: HistoryPageComponent},
      {path: 'order', component: OrderPageComponent},
      {path: 'categories', component: CategoriesPageComponent},
      {path: 'categories/new', component: CategoriesFormComponent},
      {path: 'categories/:id', component: CategoriesFormComponent}
    ]
  }*/
]

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {
}
