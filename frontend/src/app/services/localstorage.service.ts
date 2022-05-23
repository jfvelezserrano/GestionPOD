import { Injectable } from '@angular/core';
import ls from 'localstorage-slim';
import encUTF8 from 'crypto-js/enc-utf8';
import AES from 'crypto-js/aes';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor() { 
    ls.config.encrypt = true;
    ls.config.ttl = 60*60;
    ls.config.secret = 'iuhdfHDFISWENoihpfe89423of98239hoi89UHsdfsdfJOAHE98fas2U30RPFW90dfsdf8203Iosujef0sdfsd4r2o3';
    ls.config.encrypter = (data:any, secret:any):string => AES.encrypt(JSON.stringify(data), secret).toString();
    ls.config.decrypter = (data:any, secret:any) => {
      try {
        return JSON.parse(AES.decrypt(data, secret).toString(encUTF8));
      } catch (e) {
        return null;
      }};
    ls.flush();
  }

  setInLocalStorage(key:any, value:any){
    ls.set(key, value);
  }

  getInLocalStorage(key:any){
    return ls.get(key);
  }

  removeLocalStorage(key:any){
    ls.remove(key);
  }

}
