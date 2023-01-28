import { Component, HostListener, OnInit } from '@angular/core';

@Component({
  selector: 'app-scroll-up',
  templateUrl: './scroll-up.component.html',
  styleUrls: ['./scroll-up.component.css']
})
export class ScrollUpComponent implements OnInit {  

  public showScrollUp: boolean;

  constructor() {
    this.showScrollUp = false;
  }

  ngOnInit(): void {}

  scrollUp() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }

  @HostListener("window:scroll", ["$event"])
  onWindowScroll() {
    let max = document.documentElement.scrollHeight;
    this.showScrollUp = window.scrollY >= max*0.1;
  }

}
