/*
* registerApplication                       register application on symphony client
* @params       config                      app settings
* @return       SYMPHONY.remote.hello       returns a SYMPHONY remote hello service.
*/
export const registerApplication = (config, appData) => {
  // The next 3 lines were added to insert extended-user-info as a dependency only if it don't alredy exists.
  let dependencies = new Set(config.dependencies);
  dependencies.add('extended-user-info');
  config.dependencies = [... dependencies];

  return SYMPHONY.application.register(appData, config.dependencies, config.exportedDependencies);
};