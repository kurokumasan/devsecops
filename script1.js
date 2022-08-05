import http from 'k6/http';
import { randomString, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { check, group, sleep } from 'k6';

export let options = {
  scenarios: {
    contacts: {
      executor: 'shared-iterations',
      vus: 100,
      iterations: 1000,
      maxDuration: '30s',
    }
  }  
}

const URL = 'http://192.168.146.128:8080/api/v1';

export default function () {
  const randomsku = randomString(8);
  const randomname = randomString(8);
  const num = randomIntBetween(5, 10);
  const authHeaders = {
    headers: {
        'Content-Type': 'application/json'
    }
  };
  const res1 = http.post(http.url`${URL}/product`, JSON.stringify({
    "sku":randomsku,"name":randomname,"quantity":num}),authHeaders);
  check(res1, {'status was 200': (r) => r.status == 200});
  //console.log(res1)

  const res2 = http.get(http.url`${URL}/product/${randomsku}`, authHeaders);
  check(res2, {'query was 200': (r) => r.status == 200});
  //console.log(res2)

  const res3 = http.del(http.url`${URL}/product/${randomsku}`, null , authHeaders);
  check(res3, {'query was 200': (r) => r.status == 200});
  sleep(0.5);

}