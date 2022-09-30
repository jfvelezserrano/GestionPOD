import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';
import { Subject } from 'src/app/models/subject.model';
import { NgForm } from '@angular/forms';
import { Schedule } from 'src/app/models/schedule.model';
import { saveAs } from 'file-saver';
import { TeacherService } from 'src/app/services/teacher.service';
import { SubjectTeacherStatus } from 'src/app/models/subject-teacher-status.model';
import { TeacherRoles } from 'src/app/models/teacher-roles.model';

@Component({
  selector: 'app-subjects',
  templateUrl: './subjects.component.html',
  styleUrls: ['./subjects.component.css']
})
export class SubjectsComponent implements OnInit {

  public pageTitle: string;
  public occupation: string;
  public quarter: string;
  public titleChosen: string;
  public subjectsTeachersStatus: SubjectTeacherStatus[];
  public subjectToShow: SubjectTeacherStatus;
  public titles: string[];
  public idTeacherChosen: number;
  public teachers: TeacherRoles[];
  public turn: string;
  public records: Map<String, String[]>;
  public showLoader: boolean;
  public showLoaderCourse: boolean;
  public isCourse: boolean;
  public typeSort: string;
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
    private courseService: CourseService,
    private offcanvasService: NgbOffcanvas
  ) {
    this.showLoader = false;
    this.showLoaderCourse = true;
    this.isCourse = true;
    this.pageTitle = 'Asignaturas';
    this.typeSort = 'name';
    this.records = new Map<String, String[]>(null);
  }

  ngOnInit(): void {
    this.getAllTitles();
    this.getAllTeachers();
    this.searchSubjects();
  }

  searchSubjects(){
    this.showLoader = true;
    this.subjectService.search(this.typeSort, this.occupation, this.quarter, this.turn, this.titleChosen, this.idTeacherChosen)
    .subscribe({
      next: (data) => {
        this.showLoader = false;
        this.subjectsTeachersStatus = data;
        this.showLoaderCourse = false;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.showLoader = false;
          this.isCourse = false;
        }
      }
    });
  }

  getAllTitles(){
    this.subjectService.getTitlesCurrentCourse().subscribe({
      next: (data) => {
        this.titles = data;
        this.showLoaderCourse = false;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        this.isCourse = false;
      }
    });
  }

  getAllTeachers(){
    this.teacherService.getAllTeachersCurrentCourse().subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.teachers = data;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        this.isCourse = false;
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
    this.subjectService.getRecordSubject(this.subjectToShow.subject.id).subscribe({
      next: (data) => {
        this.records = data;
      }
    });
  }

  cleanFilter(form:NgForm){
    form.reset();
  }
  
  exportCSV() {
    this.courseService.exportCSV()
      .subscribe(response => { 
        this.saveCSV(response.body, response.headers.get('content-disposition'));
      });
  }
 
  saveCSV(body: string, fileName:string) {
    var blob = new Blob([body], {type: 'text/csv; charset=utf-8'});
    saveAs(blob, fileName + '.csv');
  }
}
