import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {

  public pageTitle: string;
  public status: number;
  public message: string;

  constructor(
    private router: Router
  ) {
    this.pageTitle = 'Error';
    const navigation = this.router.getCurrentNavigation();
    if(navigation && navigation.extras.state){
      const state = navigation.extras.state as {
        status: number,
        message: string
      }

      this.status = state.status;
      this.message = state.message;
    }
  }


  ngOnInit(): void {
    if(!this.status){
      this.router.navigate(['subjects']);
    }
  }
}
