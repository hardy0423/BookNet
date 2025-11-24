import { Router, RouterModule } from '@angular/router';
import { Component } from '@angular/core';
import { RegisterUserDto } from '../../services/models';
import { AuthenticationControllerService } from '../../services/services';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiResponse } from '../../services/models';


@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
  standalone: true
})
export class Register {
  registerRequest: RegisterUserDto = { email: "", password: "", firstName: "", lastName: "" };
  errorMessage: Array<string> = [];

  constructor(private router: Router, private authService: AuthenticationControllerService) {
  }

  async register() {
    this.errorMessage = [];
    try {
      const response: ApiResponse = await this.authService.register({ body: this.registerRequest });
      console.log("API RESPONSE ", response);
      if (response.StatusCode === 200) {
        this.router.navigate(['activate-account']);
      } else {
        this.errorMessage.push('An error occurred during registration.');
      }
    } catch (err: any) {
      if (err?.error?.message) {
        this.errorMessage.push(err.error.message);
      } else {
        this.errorMessage.push('Unable to contact the server.');
      }
      console.error(err);
    }
  }

  login() {
    this.router.navigate(['/login']);
  }

}
