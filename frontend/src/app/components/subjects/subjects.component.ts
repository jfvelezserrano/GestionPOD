import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-subjects',
  templateUrl: './subjects.component.html',
  styleUrls: ['./subjects.component.css']
})
export class SubjectsComponent implements OnInit {

  public pageTitle:any;

  constructor(
  ) {
    this.pageTitle = 'Asignaturas';
  }

  ngOnInit(): void {
  }

}
