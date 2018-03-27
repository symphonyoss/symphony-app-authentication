/*
* registerApplication                       register application on symphony client
* @params       config                      app settings
* @return       SYMPHONY.remote.hello       returns a SYMPHONY remote hello service.
*/
export const registerApplication = (config, appData) => {
  const controllerName = `${config.appId}:controller`;

  if (config.dependencies.indexOf('extended-user-info') == -1) {
    config.dependencies.push('extended-user-info');
  }

  return SYMPHONY.application.register(appData, config.dependencies, config.exportedDependencies);
};