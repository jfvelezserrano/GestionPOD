import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { SubjectTeacherBase } from 'src/app/models/subject-teacher-base.model';
import { NgForm } from '@angular/forms';
import { Schedule } from 'src/app/models/schedule.model';
import { Subject } from 'src/app/models/subject.model';



@Component({
  selector: 'app-admin-subjects',
  templateUrl: './admin-subjects.component.html',
  styleUrls: ['./admin-subjects.component.css']
})
export class AdminSubjectsComponent implements OnInit {

  public subjectsTeachersBase: Array<SubjectTeacherBase> = [];
  public idPod: number;
  public subject: Subject;
  public titles: string[];
  public allCampus: string[];
  public types: string[];
  public newSubject: Subject;
  public scheduleList: Array<Schedule> = [];
  public assistanceCareersList: Array<string> = [];
  public page: number;
  public typeSort:string;
  public isMore: boolean;
  public records: Map<String, String[]>;
  public showLoader: boolean = false;
  public valuesSorting: any = [
    {value: 'name', name: "Nombre"},
    {value: 'code', name: "Código"},
    {value: 'title', name: "Titulación"},
    {value: 'totalHours', name: "Horas"},
    {value: 'campus', name: "Campus"}
  ];

  constructor(
    private subjectService: SubjectService,
    private courseService: CourseService,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private offcanvasService: NgbOffcanvas) { 
      this.typeSort = "name";

      this.records = new Map<String, String[]>(null);
      this.subject = new Subject(null, '', '', '', null, '', null, '', '', '', '', [], []);
      this.newSubject = new Subject(null, '', '', '', null, '', null, '', '', '', '', [], []);
    }

  ngOnInit(): void {
    this.route.params.subscribe((params:Params) => {
      this.idPod = params['id'];
    });

    this.getSubjectsInPod();
  }

  getSubjectsInPod(){
    this.showLoader = true;
    this.page = 0;
    this.isMore = true;

    this.subjectService.getSubjectsByPOD(this.idPod, this.page,this.typeSort).subscribe({
      next: (data) => {
        this.showLoader = false;
        this.subjectsTeachersBase = data;
      }
    });
  }

  loadMoreSubjects() {
    this.page = this.page + 1;

    this.subjectService.getSubjectsByPOD(this.idPod, this.page,this.typeSort)
    .subscribe({
      next: (data) => {
        this.subjectsTeachersBase = this.subjectsTeachersBase.concat(data);
        this.isMore = Object.keys(data).length == 12;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  @HostListener("window:scroll", ["$event"])
  onWindowScroll() {
    let pos = (document.documentElement.scrollTop || document.body.scrollTop) + document.documentElement.offsetHeight;
    let max = document.documentElement.scrollHeight;

    if(pos == max && this.isMore){
      this.loadMoreSubjects();
    }
  }

  deleteSubjectInPod(idSubjectToDelete:any){

    this.courseService.deleteSubjectInPod(this.idPod, idSubjectToDelete).subscribe({
      next: (_) => {
        this.getSubjectsInPod();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  deleteModal(model:any, subject:any) {
    this.modalService.open(model, {});
    this.subject = subject;
  }

  createModal(model:any) {
    this.getAllTitles();
    this.getAllCampus();
    this.getAllTypes();
    this.modalService.open(model, { size: 'lg' });
  }

  recordModal(model:any, subject:any) {
    this.offcanvasService.open(model, { position: 'end' });
    this.subject = subject;
    this.getRecordSubject();
  }

  getRecordSubject(){
    this.subjectService.getRecordSubject(this.subject.id).subscribe({
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

  getAllTitles(){
    this.subjectService.getTitles().subscribe({
      next: (data) => {
        this.titles = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getAllCampus(){
    this.subjectService.getCampus().subscribe({
      next: (data) => {
        this.allCampus = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getAllTypes(){
    this.subjectService.getTypes().subscribe({
      next: (data) => {
        this.types = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  onSubmit(form:NgForm){
    this.newSubject.schedules = this.scheduleList;
    this.newSubject.assistanceCareers = this.assistanceCareersList;

    this.subjectService.createSubject(this.newSubject, this.idPod).subscribe({
      next: (data) => {
        this.getSubjectsInPod();
        //this.newSubject = {};
        form.reset;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  addSchedule(dayWeek:any, startTime:any, endTime:any){
    let schedule = new Schedule(null, dayWeek, startTime, endTime);
    this.scheduleList.push(schedule);
  }
}