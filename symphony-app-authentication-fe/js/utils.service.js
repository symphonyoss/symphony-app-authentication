/* eslint-disable no-useless-escape */
/* eslint-disable no-debugger */
export const Utils = (function utils() {
  const pub = {};

  pub.getParameterByName = (_name, _url) => {
    let [name, url] = [_name, _url];
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    const regex = new RegExp(`[?&]${name}(=([^&#]*)|&|#|$)`);
    const results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
  };

  return pub;
}())

Object.freeze(Utils);
