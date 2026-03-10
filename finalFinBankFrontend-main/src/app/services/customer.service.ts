import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
   constructor(private http:HttpClient) {}

 register(data:any){

 return this.http.post(

 "http://localhost:8080/customer/register",

 data,
 { responseType: 'text' as 'json' }

 );
  }


 login(data:any){

return this.http.post(

"http://localhost:8080/customer/login",

data,

{ responseType:'text' }

);

}
getCustomer(){

return this.http.get(

"http://localhost:8080/customer/profile"

);

}

}
