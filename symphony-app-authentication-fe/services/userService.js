export default class UserService {
  userInfoCache = {};
  
  cacheUserInfo = (userInfo) => {
    userInfoCache = Object.assign({}, userInfo);
  }
  
  getUserJWT = () => {
    return userInfoCache.jwt;
  }
};