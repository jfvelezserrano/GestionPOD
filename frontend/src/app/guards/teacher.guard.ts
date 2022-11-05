import { Injectable } from '@angular/core';
import { CanActivate, Router} from '@angular/router';
import { TeacherRoles } from '../models/teacher-roles.model';
import { LoginService } from '../services/login.service';
import { SubjectService } from '../services/subject.service';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TeacherGuard implements CanActivate {
  constructor(
    private router: Router,
    private loginService: LoginService
) {

}

canActivate(){
  let teacher:any = this.loginService.getTeacherLogged();

  if (teacher && teacher.name && teacher.roles.includes("TEACHER")) {
      return true;
  } else {
      this.router.navigate(['/']);
      return false;
  }
}
}