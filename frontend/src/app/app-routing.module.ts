import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SubjectsComponent } from './components/subjects/subjects.component';
import { VerifyComponent } from './components/verify/verify.component';
import { ErrorComponent } from './components/error/error.component';
import { AdminGuard } from './guards/admin.guard';
import { TeacherGuard } from './guards/teacher.guard';
import { PodsAdminComponent } from './components/pods-admin/pods-admin.component';
import { AdminSubjectsComponent } from './components/admin-subjects/admin-subjects.component';
import { AdminTeachersComponent } from './components/admin-teachers/admin-teachers.component';
import { ManagementAdminComponent } from './components/management-admin/management-admin.component';

const routes: Routes = [
  { path: "", component: LoginComponent },
  { path: "error", component: ErrorComponent },
  { path: "verify/:code", component: VerifyComponent},
  { path: "pods",  canActivate: [AdminGuard], component: PodsAdminComponent},
  { path: "pods/:id/subjects",  canActivate: [AdminGuard], component: AdminSubjectsComponent},
  { path: "pods/:id/teachers",  canActivate: [AdminGuard], component: AdminTeachersComponent},
  { path: "subjects", canActivate: [TeacherGuard], component: SubjectsComponent},
  { path: "admin", canActivate: [AdminGuard], component: ManagementAdminComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
export const AppRoutingProviders: any[] = [];
