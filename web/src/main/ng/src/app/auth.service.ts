import { Injectable } from '@angular/core';
import {ApiConst} from "./api-const";

@Injectable()
export class AuthService {
  private token = null

  setToken(token: string) {
    this.token = token
    localStorage.setItem(ApiConst.LOCAL_STORAGE_JWT_TOKEN, token);
  }

  getToken(): string {
    return this.token
  }

  isAuthenticated(): boolean {
    return !!this.token
  }

  initialize() {
    this.token = localStorage.getItem(ApiConst.LOCAL_STORAGE_JWT_TOKEN)
  }
}
