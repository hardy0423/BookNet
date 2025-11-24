import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './menu.html',
  styleUrl: './menu.scss',
})
export class Menu {
  isMobileMenuOpen = false;


  async logout() {
  }
}
