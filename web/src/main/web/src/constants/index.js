export const API_BASE_URL = 'http://localhost:8080';
export const ACCESS_TOKEN = 'accessToken';

export const OAUTH2_DEFAULT_REDIRECT_URI = window.location.origin+'/ui/oauth2/redirect'

export const GOOGLE_AUTH_URL = API_BASE_URL + '/api/oauth2/authorize/google?redirect_uri=';
export const FACEBOOK_AUTH_URL = API_BASE_URL + '/api/oauth2/authorize/facebook?redirect_uri=';
export const GITHUB_AUTH_URL = API_BASE_URL + '/api/oauth2/authorize/github?redirect_uri=';
