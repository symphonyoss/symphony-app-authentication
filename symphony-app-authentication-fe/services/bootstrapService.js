import {
    authenticateApp,
    validateTokens,
    validateJwt
  } from '../sagas/apiCalls';

/*
* initApp                                   initializes the communication with the Symphony Client
* @params       config                      app settings
* @return       SYMPHONY.remote.hello       returns a SYMPHONY remote hello service.
*/
export const initApp = (config) => {

  const authenticateApplication = (podInfo, appId) => {
    return authenticateApp(podInfo.pod, appId);
  }

  const validateAppTokens = (symphonyToken, appId) => {
    return validateTokens(tokenA, symphonyToken.tokenS, appId);
  }

  const validateJwtToken = (jwt) => {
    userInfo.jwt = jwt;
    
    return validateJwt(jwt);
  }

  SYMPHONY.remote.hello()
  .then(authenticateApplication)
  .then(validateAppTokens)
  .then(validateJwtToken)
  .fail(() => {
    console.error(`Fail to register application ${config.appId}`);
  });
};
