import http from 'k6/http';
import { randomString, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { describe, expect } from 'https://jslib.k6.io/k6chaijs/4.3.4.1/index.js';
import { check, group, sleep } from 'k6';

export let options = {
  scenarios: {
    contacts: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '5s', target: 10 },
        { duration: '30s', target: 0 },
      ],
      gracefulRampDown: '0s',
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
    }

    describe('order execute',()=> {
      const res1 = http.post(http.url`${URL}/product`, JSON.stringify({
        "sku":randomsku,"name":randomname,"quantity":num}),authHeaders);
      check(res1, {'status was 200': (r) => r.status == 200});
      sleep(3);

      const res2 = http.post(http.url`${URL}/order`, JSON.stringify({
        "email":"abc@xyz.cm"}),authHeaders);
      check(res2, {'status was 200': (r) => r.status == 200});
      expect(res2).to.have.validJsonBody();
      const order = res2.json();
     
      const res3 = http.post(http.url`${URL}/order/${order.iid}`, JSON.stringify({
        "sku":randomsku,"quantity":num}),authHeaders);
      check(res3, {'status was 200': (r) => r.status == 200});

      const res4 = http.post(http.url`${URL}/order/${order.iid}/checkout`, null, null);
      check(res4, {'status was 200': (r) => r.status == 200});
      //console.log(res4);

      const res5 = http.get(http.url`${URL}/order/${order.iid}`, authHeaders);
      check(res5, {'status was 200': (r) => r.status == 200});
      check(res5.json(), {'purchased': (r)=>r.state == "purchased"})
      //console.log(res5);
  
      sleep(3);
  
      //const res6 = http.del(http.url`${URL}/order/${order.iid}`, null , authHeaders);
      //check(res6, {'order delete success': (r) => r.status == 200});
  
      const res7 = http.del(http.url`${URL}/product/${randomsku}`, null , authHeaders);
      check(res7, {'product delete success': (r) => r.status == 200});

      sleep(0.5);
      
    });

  
  
  }
  