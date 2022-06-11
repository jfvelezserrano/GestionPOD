import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { SubjectService } from 'src/app/services/subject.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-my-subjects',
  templateUrl: './my-subjects.component.html',
  styleUrls: ['./my-subjects.component.css']
})
export class MySubjectsComponent implements OnInit {

  public subjects:any|undefined;
  public mySubjects:any|undefined;
  public idChosenSubject:number|any;
  public subject:any;
  public subjectToDelete:any;
  public subjectToEdit:any;
  public typeSort:any;
  public records:any;
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
      this.records = new Map<String, String[]>(null);
  }

  ngOnInit(): void {
    this.getMySubjects();
    this.getSubjectsInCurrentCourse();
  }

  getSubjectsInCurrentCourse(){
    this.subjectService.getAllCurrentCourse().subscribe({
      next: (data) => {
        this.subjects = data;
      }
    });
  }

  onSubmit(form:NgForm){
    this.joinSubject(form);
    form.reset();
  }

  onSubmitEdit(form:NgForm){
    this.idChosenSubject = this.subjectToEdit[0].id;
    this.joinSubject(form);
    form.reset();
  }

  joinSubject(form:NgForm){
    this.teacherService.joinSubject(this.idChosenSubject, form.value).subscribe({
      next: (data) => {
        this.getMySubjects();
        this.subject=null;
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
        this.subject = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getMySubjects(){
    this.teacherService.getMySubjects(this.typeSort).subscribe({
      next: (data) => {
        this.mySubjects = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getRecordSubject(){
    this.subjectService.getRecordSubject(this.subject[0].id).subscribe({
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

  openDelete(model:any, subjectToDelete:any) {
    this.modalService.open(model, {});
    this.subjectToDelete = subjectToDelete;
  }

  openEdit(model:any, subject:any) {
    this.modalService.open(model, {});
    this.subjectToEdit = subject;
  }

  openRecord(model:any, subject:any) {
    this.offcanvasService.open(model, { position: 'end' });
    this.subject = subject;
    this.getRecordSubject();
  }

  openAlert(model:any, subject:any) {
    this.modalService.open(model, {});
    this.subject = subject;
  }

  openJoinSubject(model:any) {
    this.modalService.open(model, {});
  }

}
