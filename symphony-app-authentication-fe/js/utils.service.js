/* eslint-disable no-useless-escape */
/* eslint-disable no-debugger */
export const Utils = (function utils() {
  const pub = {};
  let userRooms = [];

  const timestampToDate = (_ts) => {
    const date = new Date(Number(_ts));
    const monthNames = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ];
    const month = monthNames[date.getMonth()];
    return `${month} ${date.getDate()}, ${date.getFullYear()}`;
  };

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

  pub.normalizeInstanceList = (rawInstanceList) => {
    const instances = [];
    let op; // parsed optional properties
    for (const obj in rawInstanceList) {
      if (obj) {
        op = JSON.parse(rawInstanceList[obj].optionalProperties);
        instances.push({
          instanceId: rawInstanceList[obj].instanceId,
          name: rawInstanceList[obj].name,
          lastPosted: op.lastPostedDate ? timestampToDate(op.lastPostedDate) : 'not available',
          created: rawInstanceList[obj].createdDate ?
            timestampToDate(rawInstanceList[obj].createdDate) : 'not available',
          streamType: op.streamType,
          streams: op.streams || [],
          postingLocationRooms: [],
          notPostingLocationRooms: [],
        });
      }
    }

    // store all posting location rooms into instances...
    instances.map((instance) => {
      if (instance.streamType === 'CHATROOM') {
        instance.streams.map(stream => {
            // remove deactivated rooms from user rooms list
            if (userRooms.filter(userRoom => stream === userRoom.threadId).length > 0) {
              instance.postingLocationRooms.push(
                userRooms.filter(userRoom => stream === userRoom.threadId)[0]
              )
            }
          }
        );
      }
      return instance;
    });

    // stores all indexes of the rooms (object) that are not posting locations into an array
    let idx,
      aux;

    instances.map((instance) => {
      idx = [];
      aux = userRooms.slice();
      instance.streams.map((stream) => {
        for (let k = 0, n = aux.length; k < n; k += 1) {
          if (aux[k].threadId === stream) {
            idx.push(k);
          }
        }
        return idx;
      });
      // remove from the user rooms array all those are posting locations rooms
      for (let i = 0, n = aux.length; i < n; i += 1) {
        for (let j = 0, l = idx.length; j < l; j += 1) {
          if (i === idx[j]) {
            aux.splice(i, 1);
            idx.splice(j, 1);
            for (let k = 0, s = idx.length; k < s; k += 1) idx[k] -= 1;
            i -= 1;
            break;
          }
        }
      }
      instance.notPostingLocationRooms = aux.slice();
      return instance;
    });
    return instances;
  };

  return pub;
}())

Object.freeze(Utils);
