import axios from 'axios';

const baseAuthenticationUrl = "";

export default class ApiCalls {

    constructor(baseAuthenticationUrl) {
        this.baseAuthenticationUrl = `${baseAuthenticationUrl}/v1/application/jwt`
    }

    authenticateApp(podId, appId) {
        // use appId 
        const url = `${baseAuthenticationUrl}/authenticate`;
        const payload = {
            podId
        }

        return axios.post(url, payload);
    }

    validateTokens(applicationToken, symphonyToken, appId) {
        // use appId 
        const url = `${baseAuthenticationUrl}/tokens/validate`;
        const payload = {
            applicationToken,
            symphonyToken
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