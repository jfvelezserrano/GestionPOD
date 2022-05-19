import { Injectable } from '@angular/core';
import { CanActivate, Router} from '@angular/router';
import { LoginService } from '../services/login.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(
    private router: Router,
    private loginService: LoginService
) {

}

canActivate(){
  let teacher:any = this.loginService.getTeacherLogged();

  if (teacher && teacher.name && teacher.roles.includes("ADMIN")) {
      return true;
  } else {
      this.router.navigate(['/subjects']);
      return false;
  }
}
}
