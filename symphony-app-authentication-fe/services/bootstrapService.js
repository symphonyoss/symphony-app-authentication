import { registerApplication } from './registerApplication';
import { cacheUserInfo } from './userService'

/*
* initApp                                   initializes the communication with the Symphony Client
* @params       config                      app settings
* @return       SYMPHONY.remote.hello       returns a SYMPHONY remote hello service.
*/
export const initApp = (config) => {
  let userInfo = {};
  let tokenA = '';

  const authenticationApiCalls = new AuthenticationApiCalls(config.baseAuthenticationUrl);

  SYMPHONY.services.register(`${config.appId}:controller`);
  
  const authenticateApplication = () => {
    return authenticationApiCalls.authenticateApp(config.appId);
  }

  const registerAuthenticatedApp = (appTokens) => {
    tokenA = appTokens.data.appToken;

    const appId = config.appId;
    const appData = { appId, tokenA };

    return registerApplication(config, appData);
  }

  const validateAppTokens = (symphonyToken) => {
    return authenticationApiCalls.validateTokens(tokenA, symphonyToken.tokenS, config.appId);
  }

  const getJwt = () => {
    const userInfoService = SYMPHONY.services.subscribe('extended-user-info');
    return userInfoService.getJwt();
  }  

  const validateJwtToken = (jwt) => {
    userInfo.jwt = jwt;
    
    return authenticationApiCalls.validateJwt(jwt);
  }

  const cacheJwt = (response) => {
    userInfo.userId = response.data;

    cacheUserInfo(userInfo);
  }  

  SYMPHONY.remote.hello()
  .then(authenticateApplication)
  .then(registerAuthenticatedApp)
  .then(validateAppTokens)
  .then(getJwt)
  .then(validateJwtToken)
  .then(cacheJwt)
  .fail(() => {
    console.error(`Fail to register application ${config.appId}`);
  });
};
