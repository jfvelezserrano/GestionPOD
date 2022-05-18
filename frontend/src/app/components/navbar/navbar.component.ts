import { Component, OnInit } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { LoginService } from 'src/app/services/login.service';


@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  constructor(
    private _loginService: LoginService,
    private _router: Router,
    private _route: ActivatedRoute) { }

  ngOnInit(): void {
  }

  logout() {
    this._loginService.logout().subscribe(
      response => {
        localStorage.removeItem("teacher");
        this._router.navigate(['/']);
      },
      error => {
        console.log(<any>error);
      }
    );
  }

}
