import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { SubjectTeacherBase } from 'src/app/models/subject-teacher-base.model';
import { SubjectTeacherConflicts } from 'src/app/models/subject-teacher-conflicts.model';
import { Subject } from 'src/app/models/subject.model';
import { SubjectService } from 'src/app/services/subject.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-my-subjects',
  templateUrl: './my-subjects.component.html',
  styleUrls: ['./my-subjects.component.css']
})
export class MySubjectsComponent implements OnInit {

  public subjects: Subject[];
  public mySubjects: SubjectTeacherConflicts[];
  public idChosenSubject: number;
  public subjectTeacher: SubjectTeacherBase;
  public subjectToShowAlert: SubjectTeacherConflicts;
  public subjectToDelete: SubjectTeacherConflicts;
  public subjectToEdit: SubjectTeacherConflicts;
  public isCourse: boolean;
  public showLoaderCourse: boolean;
  public typeSort: string;
  public records: Map<String, String[]>;
  public showLoader: boolean;
  public valuesSorting:any = [
    {value: 'name', name: "Nombre"},
    {value: 'code', name: "Código"},
    {value: 'title', name: "Titulación"},
    {value: 'totalHours', name: "Horas"},
    {value: 'campus', name: "Campus"}
  ];

  constructor(private subjectService: SubjectService,
    private teacherService: TeacherService,
    private modalService: NgbModal,
    private offcanvasService: NgbOffcanvas) { 
      this.typeSort = "name";
      this.showLoader = false;
      this.records = new Map<String, String[]>(null);
      this.showLoaderCourse = true;
      this.isCourse = true;
  }

  ngOnInit(): void {
    this.getMySubjects();
    this.getSubjectsInCurrentCourse();
  }

  getSubjectsInCurrentCourse(){
    this.subjectService.getAllInCurrentCourse().subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.subjects = data;
      }
    });
  }

  onSubmit(form:NgForm){
    this.joinSubject(form);
    form.reset();
  }

  onSubmitEdit(form:NgForm){
    this.idChosenSubject = this.subjectToEdit.subject.id;
    this.joinSubject(form);
    form.reset();
  }

  joinSubject(form:NgForm){
    this.teacherService.joinSubject(this.idChosenSubject, form.value).subscribe({
      next: (data) => {
        this.getMySubjects();
        //this.subject=null;
        form.reset;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  unjoinToSubject(id:number){
    this.teacherService.unjoinSubject(id).subscribe({
      next: (data) => {
        this.getMySubjects();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getSubjectById(){
    this.subjectService.getById(this.idChosenSubject).subscribe({
      next: (data) => {
        this.subjectTeacher = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getMySubjects(){
    this.showLoader = true;
    this.teacherService.getSubjects(this.typeSort).subscribe({
      next: (data) => {
        this.showLoader = false;
        this.showLoaderCourse = false;
        this.mySubjects = data;
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

  getRecordSubject(){
    this.subjectService.getRecordSubject(this.subjectTeacher.subject.id).subscribe({
      next: (data) => {
        this.records = data;
      }
    });
  }

  getValues(values:any){
    return values;
  }

  asIsOrder(a:any, b:any) {
    return 1;
  }

  openDelete(model:any, subjectToDelete: SubjectTeacherConflicts) {
    this.modalService.open(model, {});
    this.subjectToDelete = subjectToDelete;
  }

  openEdit(model:any, subject: SubjectTeacherConflicts) {
    this.modalService.open(model, {});
    this.subjectToEdit = subject;
  }

  openRecord(model:any, subject: SubjectTeacherConflicts) {
    this.offcanvasService.open(model, { position: 'end' });
    this.subjectTeacher = subject;
    this.getRecordSubject();
  }

  openAlert(model:any, subject: SubjectTeacherConflicts) {
    this.modalService.open(model, {});
    this.subjectToShowAlert = subject;
  }

  openJoinSubject(model:any) {
    this.modalService.open(model, {});
  }

}
