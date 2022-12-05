import { Component, OnInit, Input } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject } from 'rxjs';
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
  public codeChosenSubject: string;
  public chosenHours: number;
  public currentSection: string = "Elección";
  public currentSubsection: string = "Elección > Mis Asignaturas";
  public subjectTeacher: SubjectTeacherBase | any;
  public subjectToShowAlert: SubjectTeacherConflicts;
  public subjectToDelete: SubjectTeacherConflicts;
  public subjectToEdit: SubjectTeacherConflicts;
  public typeSort: string;
  public records: Map<String, String[]>;
  public showLoader: boolean;
  public isCourse: boolean;
  public error: string;
  public testEmitter: BehaviorSubject<boolean>;
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
      this.error = "";
      this.showLoader = true;
      this.records = new Map<String, String[]>(null);
      this.testEmitter = new BehaviorSubject<boolean>(this.isCourse);
  }

  ngOnInit(): void {
    this.getMySubjects();
    this.testEmitter.subscribe(data => {
      if(data != undefined && data){
        this.getSubjectsInCurrentCourse();
      };
    })
  }

  getSubjectsInCurrentCourse(){
    this.subjectService.getAllInCurrentCourse().subscribe({
      next: (data) => {
        this.subjects = data;
      }
    });
  }

  onSubmit(form:NgForm){
    this.joinSubject(form);
  }

  onSubmitEdit(form:NgForm){
    this.idChosenSubject = this.subjectToEdit.subject.id;
    this.joinSubject(form);
  }

  joinSubject(form:NgForm){
     this.teacherService.joinSubject(this.idChosenSubject, form.value).subscribe({
      next: (data) => {
        this.getMySubjects();
        this.subjectTeacher = undefined;
        this.error = '';
      },
      error: (error) => {
        var splitted = error.split(";"); 
        if(splitted[0] == '404'){
          this.error = splitted[1];
        }
      }
    });
    form.resetForm();
  }

  unjoinToSubject(id:number){
    this.findIdInSubjectList();
    this.teacherService.unjoinSubject(id).subscribe({
      next: (data) => {
        this.getMySubjects();
      },
      error: (error) => {
        var splitted = error.split(";"); 
        if(splitted[0] == '404'){
          this.error = splitted[1];
        }
      }
    });
  }

  setUndefinedValue(){
    this.subjectTeacher = undefined;
  }

  findIdInSubjectList(){
    let array = this.subjects.filter((x: { code: any; }) => x.code == this.codeChosenSubject);
    if(array.length > 0){
      this.idChosenSubject = array[0].id;
    } else{
      this.idChosenSubject = -1;
    }
  }

  getSubjectById(){
    this.findIdInSubjectList();
    this.subjectService.getById(this.idChosenSubject).subscribe({
      next: (data) => {
        this.subjectTeacher = data;
        this.error = '';
      },
      error: (error) => {
        var splitted = error.split(";"); 
        if(splitted[0] == '404'){
          this.error = splitted[1];
        }
      }
    });
  }

  getMySubjects(){
    this.showLoader = true;
    this.teacherService.getSubjects(this.typeSort).subscribe({
      next: (data) => {
        this.showLoader = false;
        this.isCourse = true;
        this.testEmitter.next(this.isCourse);
        this.mySubjects = data;
      },
      error: (error) => {
        this.showLoader = false;
        var splitted = error.split(";"); 
        if(splitted[0] == '404'){
          this.isCourse = false;
          this.testEmitter.next(this.isCourse);
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
