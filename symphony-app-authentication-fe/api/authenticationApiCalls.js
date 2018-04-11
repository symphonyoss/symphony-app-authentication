import axios from 'axios';

export default class AuthenticationApiCalls {
  constructor(baseUrl) {
    this.baseAuthenticationUrl = `${baseUrl}/v1/application`
  }

  authenticateApp(appId) {
    const url = `${this.baseAuthenticationUrl}/authenticate`;
    const payload = {
        appId
    }

    return axios.post(url, payload);
  }

  validateTokens(appToken, symphonyToken, appId) {
    const url = `${this.baseAuthenticationUrl}/tokens/validate`;
    const payload = {
        appToken,
        symphonyToken,
        appId
    };

    return axios.post(url, payload);
  }

  validateJwt(jwt) {
    const url = `${this.baseAuthenticationUrl}/jwt/validate`;
    const payload = {
        jwt
    };

    return axios.post(url, payload);
  }
}