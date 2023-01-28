import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { CountdownComponent, CountdownEvent } from 'ngx-countdown';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { NgForm } from '@angular/forms';

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
  public error: string;

  constructor(
    private loginService: LoginService,
    private router: Router

  ) {
    this.pageTitle = 'Iniciar sesión';
    this.error='';
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
    }
  }

  ngOnDestroy(){
    if(this.status == "sentCode"){
      this.subscription.unsubscribe();
    }
  }

  onSubmit(form:NgForm) {
    this.error = '';
    this.loginService.access(form.value).subscribe({
      next: (data) => {
        form.reset();
        this.status = "sentCode";
        this.checking = true;
      },
      error: (error) => {
        let splitted = error.split("\\"); 
        this.error = splitted[1];
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
