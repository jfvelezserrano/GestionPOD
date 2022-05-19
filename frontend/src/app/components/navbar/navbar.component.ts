import { Component, OnInit } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { LocalStorageService } from 'src/app/services/localstorage.service';
import { LoginService } from 'src/app/services/login.service';


@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  constructor(
    private loginService: LoginService,
    private localStorageService: LocalStorageService,
    private router: Router) { }

  ngOnInit(): void {
  }

  logout() {
    this.loginService.logout().subscribe(
      response => {
        this.localStorageService.removeLocalStorage("teacher");
        this.router.navigate(['/']);
      }
    );
  }

}
