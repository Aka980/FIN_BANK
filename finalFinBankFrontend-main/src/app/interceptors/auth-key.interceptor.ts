import { Injectable } from '@angular/core';
import {
HttpInterceptor,
HttpRequest,
HttpHandler,
HttpEvent
} from '@angular/common/http';

import { Observable } from 'rxjs';

@Injectable()
export class AuthKeyInterceptor implements HttpInterceptor {

intercept(
req: HttpRequest<any>,
next: HttpHandler

): Observable<HttpEvent<any>> {

const token = localStorage.getItem("token");

if(token){

req = req.clone({

setHeaders:{
Authorization:"Bearer "+token
}

});

}

return next.handle(req);

}

}

// Why we do this (simple)

// This sends JWT token automatically to backend

// Backend /profile/my needs token → otherwise 403