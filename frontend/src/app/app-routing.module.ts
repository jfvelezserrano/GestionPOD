import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SubjectsComponent } from './components/subjects/subjects.component';
import { VerifyComponent } from './components/verify/verify.component';
import { AdminGuard } from './guards/admin.guard';
import { TeacherGuard } from './guards/teacher.guard';

const routes: Routes = [
  { path: "", component: LoginComponent },
  { path: "verify/:code", component: VerifyComponent},
  { path: "subjects", canActivate: [TeacherGuard], component: SubjectsComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
export const AppRoutingProviders: any[] = [];
