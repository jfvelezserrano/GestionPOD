import { Component, OnInit } from '@angular/core';
import { LoginRequest } from 'src/app/models/login-request';
import { Teacher } from 'src/app/models/teacher';
import { LoginService } from '../../services/login.service';
import { Router, ActivatedRoute, Params } from '@angular/router'
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [LoginService]
})

export class LoginComponent implements OnInit {

  public page_title: any;
  public teacher: any;
  public status: any;
  public emailTeacher: any;
  public code: any;

  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _loginService: LoginService,
    private _titleService: Title
  ) {
    this._titleService.setTitle("Iniciar sesión - VoluntaWeb");
    this.page_title = 'Iniciar sesión';
  }

  ngOnInit(): void {}

  onSubmit(form:any) {
    this._loginService.access(form.value).subscribe(
      response => {
        if (response) {
          console.log(response);
          this.status = "success";
          form.reset();
        } else {
          this.status = "error";
        }
      },
      error => {
        this.status = "error";
      }
    );
  }
}
