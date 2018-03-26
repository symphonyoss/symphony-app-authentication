import { Utils } from '../js/utils.service';

const dependencies = [
  'ui',
  'extended-user-info',
  'modules',
  'entity',
  'dialogs',
];

/*
* registerApplication                       register application on symphony client
* @params       config                      app settings
* @return       SYMPHONY.remote.hello       returns a SYMPHONY remote hello service.
*/
export const registerApplication = (config, appData) => {
  const controllerName = `${config.appId}:controller`;

  return SYMPHONY.application.register(
          appData, 
          dependencies, 
        ).then(register);
};