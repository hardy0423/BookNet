import { Component, ChangeDetectorRef } from '@angular/core';
import { AuthenticationRequest } from '../../services/models/authentication-request';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthenticationControllerService } from '../../services/services';
import { Router, RouterModule } from '@angular/router';
import { ApiResponse } from '../../services/models';
import { Token } from '../../services/token/token';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {
  authRequest: AuthenticationRequest = { email: "", password: "" };
  errorMessage: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationControllerService,
    private cdr: ChangeDetectorRef,
    private tokenService: Token
  ) {
  }

  ngOnInit() {

  }

  login() {
    this.errorMessage = [];
    this.authService.authenticate({ body: this.authRequest })
      .then((response: ApiResponse) => {
        this.tokenService.saveAuthResponse(response);
        this.router.navigate(['/books']);
      })
      .catch((err: any) => {
        if (err?.error?.businessErrorCode || err?.error) {
          const message = err.error.businessErrorDescription || err.error.error || 'Authentication failed';
          this.errorMessage.push(message);
        } else if (err?.error?.validationErrors) {
          this.errorMessage = err.error.validationErrors;
        } else {
          this.errorMessage.push('Authentication failed');
        }
        this.cdr.detectChanges();
      });
  }

  register() {
    this.router.navigate(['/register']);
  }
}
