import { Component } from '@angular/core';
import {skipUntil} from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiResponse } from '../../services/models';
import { AuthenticationControllerService } from '../../services/services';
import { CodeInputModule } from 'angular-code-input';

@Component({
  selector: 'app-activate-account',
  imports: [CommonModule, FormsModule, RouterModule, CodeInputModule],
  templateUrl: './activate-account.html',
  styleUrls: ['./activate-account.scss'],
})
export class ActivateAccount {

  message = '';
  isOkay = true;
  submitted = false;

  constructor(
    private router: Router,
    private authService: AuthenticationControllerService
  ) {}

  private async confirmAccount(token: string) {
    try {
      const response: ApiResponse = await this.authService.activateAccount({ token });

      console.log("RESPONSE ", response)

      if (response.StatusCode === 200 || response.Message?.toLowerCase().includes('activated')) {
        this.message = 'Your account has been successfully activated. Now you can proceed to login.';
        this.isOkay = true;
      } else {
        this.message = response.Message || 'Token has expired or is invalid.';
        this.isOkay = false;
      }
    } catch (err: any) {
      console.error(err);
      this.message = err?.error?.message || 'An error occurred while activating your account.';
      this.isOkay = false;
    } finally {
      this.submitted = true;
    }
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  onCodeCompleted(token: string) {
    console.log("Code Completed")
    this.confirmAccount(token);
  }

  protected readonly skipUntil = skipUntil;
}
