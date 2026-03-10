import { TestBed } from '@angular/core/testing';
import { AuthKeyInterceptor } from './auth-key.interceptor';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('AuthKeyInterceptor', () => {
  let interceptor: AuthKeyInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthKeyInterceptor,
        {
          provide: HTTP_INTERCEPTORS,
          useClass: AuthKeyInterceptor,
          multi: true,
        },
      ]
    });
    interceptor = TestBed.inject(AuthKeyInterceptor);
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });
});
