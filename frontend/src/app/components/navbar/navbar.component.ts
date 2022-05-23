import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { LoginService } from 'src/app/services/login.service';


@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  public teacher:any;
  public email:any;

  constructor(
    private loginService: LoginService,
    private router: Router) { }

  ngOnInit(): void {
  }

  ngDoCheck() {
    this.teacher = this.loginService.getTeacherLogged();
    if(this.teacher){
      this.email = this.teacher.email.substr(0, this.teacher.email.indexOf('@')); 
    }
  }

  logout() {
    this.loginService.logout().subscribe(
      response => {
        this.router.navigate(['']);
      }
    );
  }
}
