import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { LocalStorageService } from 'src/app/services/localstorage.service';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css'],
  providers: [LoginService, LocalStorageService]
})
export class VerifyComponent implements OnInit {

  public status: any;
  public code:any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService,
    private localStorageService: LocalStorageService,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe((params:Params) => {
      this.code = params['code'];
    });
    this.loginService.verify(this.code).subscribe(
      (response) => {
        this.localStorageService.setInLocalStorage("teacher", response);
        this.status = "success";
        this.router.navigate(['/subjects']);
      }
    );
  }

}
