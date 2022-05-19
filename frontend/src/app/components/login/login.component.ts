import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [LoginService]
})

export class LoginComponent implements OnInit {

  public pageTitle: any;
  public teacher: any;
  public status: any;
  public emailTeacher: any;
  public code: any;

  constructor(
    private loginService: LoginService,
  ) {
    this.pageTitle = 'Iniciar sesión';
  }

  ngOnInit(): void {}

  onSubmit(form:any) {
    this.loginService.access(form.value).subscribe(
      response => {
        if (response) {
          console.log(response);
          this.status = "success";
          form.reset();
        } else {
          this.status = "error";
        }
      });
  }
}
