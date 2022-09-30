import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { CountdownComponent, CountdownEvent } from 'ngx-countdown';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { Teacher } from 'src/app/models/teacher.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [LoginService]
})

export class LoginComponent implements OnInit {

  @ViewChild('countdown') counter!: CountdownComponent;
  
  public pageTitle: string;
  public status: string;
  public emailTeacher: string;
  public checking: boolean;
  public subscription: Subscription;
  public existsTeacher: boolean;
  public isMailSent: boolean;

  constructor(
    private loginService: LoginService,
    private router: Router

  ) {
    this.pageTitle = 'Iniciar sesión';
    this.existsTeacher = true;
    this.isMailSent = true;
  }

  ngOnInit(): void {
    if(this.loginService.getTeacherLogged() != null){
      this.router.navigate(['subjects']);
    }
    this.checking = false;
  }

  ngDoCheck() {
    if(this.loginService.getTeacherLogged() != null){
      this.router.navigate(['subjects']);
    }

    if(this.status == "sentCode" && this.checking){
      const intervalTime = interval(10000);
      this.subscription = intervalTime.subscribe(val => this.checkLocalStorage());
      this.checking = false;
      this.existsTeacher = true;
    }
  }

  ngOnDestroy(){
    if(this.status == "sentCode"){
      this.subscription.unsubscribe();
    }
  }

  onSubmit(form:any) {
    this.existsTeacher = true;
    this.isMailSent = true;
    this.loginService.access(form.value).subscribe({
      next: (data) => {
        form.reset();
        this.status = "sentCode";
        this.checking = true;
      },
      error: (error) => {
        if(error === '404'){
          this.existsTeacher = false;
        }
        if(error === '400'){
          this.isMailSent = false;
        }
      }
    });
  }

  finish(e: CountdownEvent) {
    if (e.action == 'done') {
      this.status = "noCode";
      this.subscription.unsubscribe();
    }
  }

  checkLocalStorage(){
    if(this.loginService.getTeacherLogged() != null){
      this.subscription.unsubscribe();
      window.location.reload();
    }
  }
}
