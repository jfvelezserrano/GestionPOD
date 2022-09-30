import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppRoutingProviders } from './app-routing.module';
import { HttpClientModule, HttpClientXsrfModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { LoginComponent } from './components/login/login.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SubjectsComponent } from './components/subjects/subjects.component';
import { FooterComponent } from './components/footer/footer.component';
import { RouterModule } from '@angular/router';
import { ScrollUpComponent } from './components/scroll-up/scroll-up.component';
import { VerifyComponent } from './components/verify/verify.component';
import { CountdownModule } from 'ngx-countdown';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { ErrorComponent } from './components/error/error.component';
import { PodsAdminComponent } from './components/pods-admin/pods-admin.component';
import { AdminSubjectsComponent } from './components/admin-subjects/admin-subjects.component';
import { AdminTeachersComponent } from './components/admin-teachers/admin-teachers.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ManagementAdminComponent } from './components/management-admin/management-admin.component';
import { LoaderComponent } from './components/loader/loader.component';
import { StatisticsComponent } from './components/statistics/statistics.component';
import { MyStatisticsComponent } from './components/my-statistics/my-statistics.component';
import { MySubjectsComponent } from './components/my-subjects/my-subjects.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NavbarComponent,
    SubjectsComponent,
    FooterComponent,
    ScrollUpComponent,
    VerifyComponent,
    ErrorComponent,
    PodsAdminComponent,
    AdminSubjectsComponent,
    AdminTeachersComponent,
    ManagementAdminComponent,
    LoaderComponent,
    StatisticsComponent,
    MyStatisticsComponent,
    MySubjectsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule,
    HttpClientModule,
    HttpClientXsrfModule,
    CountdownModule,
    NgbModule
  ],
  providers: [AppRoutingProviders, 
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
    
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
