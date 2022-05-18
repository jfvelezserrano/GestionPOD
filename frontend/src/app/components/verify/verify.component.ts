import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css'],
  providers: [LoginService]
})
export class VerifyComponent implements OnInit {

  public status: any;
  public code:any;

  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private _loginService: LoginService,
  ) {
  }

  ngOnInit(): void {
    this._route.params.subscribe((params:Params) => {
      this.code = params['code'];
    });
    this._loginService.verify(this.code).subscribe(
      response => {
        localStorage.setItem('teacher', JSON.stringify(response));
        this.status = "success";
        this._router.navigate(['/subjects']);
      },
      error => {
        this.status = "error";
      }
    );
  }

}
