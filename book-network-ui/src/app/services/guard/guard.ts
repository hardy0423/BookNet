import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Token } from '../token/token';
import { isPlatformBrowser } from '@angular/common';
import { isPlatformServer } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';

export const authGuard: CanActivateFn = () => {
  const tokenService = inject(Token);
  const router = inject(Router);

  if (isPlatformServer(inject(PLATFORM_ID))) {
    return true;
  }

  if (!tokenService.isTokenValid()) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
