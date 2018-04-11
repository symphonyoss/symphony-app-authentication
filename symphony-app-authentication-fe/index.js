import { initApp as initAppService } from './services/bootstrapService';
import { getUserJWT as getUserJWTService } from './services/userService';

export const initApp = initAppService;
export const getUserJWT = getUserJWTService;
