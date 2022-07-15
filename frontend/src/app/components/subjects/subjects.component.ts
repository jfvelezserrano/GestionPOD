import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';
import { SubjectModel } from 'src/app/models/subject';
import { NgForm } from '@angular/forms';
import { Schedule } from 'src/app/models/schedule';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-subjects',
  templateUrl: './subjects.component.html',
  styleUrls: ['./subjects.component.css']
})
export class SubjectsComponent implements OnInit {

  public pageTitle:string = "";
  public occupation:any;
  public quarter:any;
  public titleChosen:any;
  public subjects: any;
  public subjectToShow: any;
  public titles:any;
  public idTeacherChosen:any;
  public teachers:any;
  public turn:any;
  public records:any;
  public showLoader:boolean = false;
  public typeSort:any;
  public valuesSorting:any = [
    {value: 'name', name: "Nombre"},
    {value: 'code', name: "Código"},
    {value: 'title', name: "Titulación"},
    {value: 'totalHours', name: "Horas"},
    {value: 'campus', name: "Campus"}
  ];

  constructor(
    private subjectService: SubjectService,
    private teacherService: TeacherService,
    private offcanvasService: NgbOffcanvas
  ) {
    this.pageTitle = 'Asignaturas';
    this.typeSort = "name";
    this.records = new Map<String, String[]>(null);
  }

  ngOnInit(): void {
    this.getAllTeachers();
    this.getAllTitles();
    this.searchSubjects();
  }

  searchSubjects(){
    this.showLoader = true;
    this.subjectService.search(this.typeSort, this.occupation, this.quarter, this.turn, this.titleChosen, this.idTeacherChosen)
    .subscribe({
      next: (data) => {
        this.showLoader = false;
        this.subjects = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getAllTitles(){
    this.subjectService.getTitlesCurrentCourse().subscribe({
      next: (data) => {
        this.titles = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getAllTeachers(){
    this.teacherService.getAllTeachersCurrentCourse().subscribe({
      next: (data) => {
        this.teachers = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getValues(values:any){
    return values;
  }

  asIsOrder(a:any, b:any) {
    return 1;
  }

  openRecord(model:any, subject:any) {
    this.offcanvasService.open(model, { position: 'end' });
    this.subjectToShow = subject;
    this.getRecordSubject();
  }

  getRecordSubject(){
    this.subjectService.getRecordSubject(this.subjectToShow[0].id).subscribe({
      next: (data) => {
        this.records = data;
      }
    });
  }

  cleanFilter(form:NgForm){
    form.reset();
  }
}
