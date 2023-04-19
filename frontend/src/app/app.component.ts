import { Component, OnInit } from '@angular/core';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'frontend';
  public subscription: Subscription;
  
  ngOnInit(): void {
    const intervalTime = interval(1200000);
    this.subscription = intervalTime.subscribe(val => this.reloadWindow());
  }

  ngOnDestroy(){
    this.subscription.unsubscribe();
  }

  reloadWindow(){
    window.location.reload();
  }
}
