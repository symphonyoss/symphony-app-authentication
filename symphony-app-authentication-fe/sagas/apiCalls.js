import axios from 'axios';

export default class ApiCalls {
  constructor(baseAuthenticationUrl) {
    this.baseAuthenticationUrl = `${baseAuthenticationUrl}/v1/application`
  }

  authenticateApp(appId) {
    const url = `${baseAuthenticationUrl}/authenticate`;
    const payload = {
        appId
    }

    return axios.post(url, payload);
  }

  validateTokens(applicationToken, symphonyToken, appId) {
    const url = `${baseAuthenticationUrl}/tokens/validate`;
    const payload = {
        applicationToken,
        symphonyToken,
        appId
    };

    return axios.post(url, payload);
  }

  validateJwt(jwt) {
    const url = `${baseAuthenticationUrl}/jwt/validate`;
    const payload = {
        jwt
    };

    return axios.post(url, payload);
  }
}