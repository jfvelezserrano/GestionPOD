import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { CountdownComponent, CountdownEvent } from 'ngx-countdown';
import { Router } from '@angular/router';
import { LocalStorageService } from '../../services/localstorage.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [LoginService]
})

export class LoginComponent implements OnInit {

  @ViewChild('countdown') counter!: CountdownComponent;
  
  public pageTitle: any;
  public status: any;
  public teacher: any;
  public emailTeacher: any;
  public code: any;
  public checking:any;
  public subscription: Subscription | any;

  constructor(
    private loginService: LoginService,
    private localStorageService: LocalStorageService,
    private router: Router

  ) {
    this.pageTitle = 'Iniciar sesión';
  }

  ngOnInit(): void {
    if(this.localStorageService.getInLocalStorage("teacher") != null){
      this.router.navigate(['subjects']);
    }

    this.checking = false;
  }

  ngDoCheck() {
    if(this.loginService.getTeacherLogged() != null){
      this.router.navigate(['subjects']);
    }

    if(this.status == "sentCode" && this.checking){
      console.log("iniciado");
      const intervalTime = interval(10000);
      this.subscription = intervalTime.subscribe(val => this.checkLocalStorage());
      this.checking = false;
    }
  }

  ngOnDestroy(){
    if(this.status == "sentCode"){
      this.subscription && this.subscription.unsubscribe();
    }
  }

  onSubmit(form:any) {
    this.loginService.access(form.value).subscribe({
      next: (data) => {
        form.reset();
        this.status = "sentCode";
        this.checking = true;
      }/*,
      error: (error) => {
        console.error(error);
      }*/
    });
  }

  finish(e: CountdownEvent) {
    if (e.action == 'done') {
      this.status = "noCode";
      this.subscription && this.subscription.unsubscribe();
    }
  }

  checkLocalStorage(){
    console.log("check");
    if(this.localStorageService.getInLocalStorage("teacher") != null){
      this.subscription && this.subscription.unsubscribe();
      window.location.reload();
    }
  }
}
