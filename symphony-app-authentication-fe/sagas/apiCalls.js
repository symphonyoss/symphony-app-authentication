import axios from 'axios';

const baseAuthenticationUrl = "";

export default class ApiCalls {
  constructor(baseAuthenticationUrl) {
    this.baseAuthenticationUrl = `${baseAuthenticationUrl}/v1/application/jwt`
  }

  authenticateApp(podId, appId) {
    const url = `${baseAuthenticationUrl}/authenticate`;
    const payload = {
        podId,
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
    const url = `${baseAuthenticationUrl}/validate`;
    const payload = {
        jwt
    };

    return axios.post(url, payload);
  }
}