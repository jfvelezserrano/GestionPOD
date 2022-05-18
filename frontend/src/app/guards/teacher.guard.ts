import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { catchError, map, Observable, of} from 'rxjs';
import { LoginService } from '../services/login.service';

@Injectable({
  providedIn: 'root'
})
export class TeacherGuard implements CanActivate {
  constructor(
    private _router: Router,
    private _loginService: LoginService
) {

}

canActivate(){
  let teacher = this._loginService.getTeacherLogged();


  if (teacher && teacher.name && teacher.roles.includes("TEACHER")) {
      return true;
  } else {
      this._router.navigate(['/']);
      return false;
  }
}
}
