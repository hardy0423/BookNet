import { TestBed } from '@angular/core/testing';

import { HttpTokenInterceptor } from './http-token-interceptor';

describe('HttpTokenInterceptor', () => {
  let service: HttpTokenInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HttpTokenInterceptor);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
