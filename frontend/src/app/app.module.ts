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
import { ErrorIntercept } from './interceptors/error.interceptor';
import { ErrorComponent } from './components/error/error.component';
import { PodsAdminComponent } from './components/pods-admin/pods-admin.component';
import { AdminSubjectsComponent } from './components/admin-subjects/admin-subjects.component';
import { AdminTeachersComponent } from './components/admin-teachers/admin-teachers.component';

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
    AdminTeachersComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule,
    HttpClientModule,
    HttpClientXsrfModule,
    CountdownModule
  ],
  providers: [AppRoutingProviders, 
    {provide: HTTP_INTERCEPTORS, useClass: ErrorIntercept, multi: true}
    
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
