import { registerApplication } from './registerApplication';
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
  let userInfo = {};
  let tokenA = '';

  // create our own service
  SYMPHONY.services.register(`${config.appId}:controller`);

  
  const authenticateApplication = (podInfo, appId) => {
    return authenticateApp(podInfo.pod, appId);
  }

  const registerAuthenticatedApp = (appTokens) => {
    tokenA = appTokens.data.appToken;

    const appId = config.appId;
    const appData = { appId, tokenA };

    return registerApplication(config, appData, enrichers);
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
