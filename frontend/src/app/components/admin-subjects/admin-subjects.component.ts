import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { SubjectModel } from 'src/app/models/subject';
import { NgForm } from '@angular/forms';
import { Schedule } from 'src/app/models/schedule';



@Component({
  selector: 'app-admin-subjects',
  templateUrl: './admin-subjects.component.html',
  styleUrls: ['./admin-subjects.component.css']
})
export class AdminSubjectsComponent implements OnInit {

  public subjects:any = [];
  public idPod:any;
  public subject:SubjectModel|any;
  public titles:any;
  public allCampus:any;
  public types:any;
  public newSubject:SubjectModel;
  public scheduleList:Array<Schedule> = [];
  public assistanceCareersList:Array<string> = [];
  public page:any;
  public typeSort:any;
  public isMore:any;
  public valuesSorting:any = [
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
      this.newSubject = new SubjectModel(null, '', '', '', null, '', null, '', '', '', '', null, null, [], []);
    }

  ngOnInit(): void {
    this.route.params.subscribe((params:Params) => {
      this.idPod = params['id'];
    });

    this.getSubjectsInPod();
  }

  getSubjectsInPod(){
    this.page = 0;
    this.isMore = true;

    this.subjectService.getSubjectsByPOD(this.idPod, this.page,this.typeSort).subscribe({
      next: (data) => {
        this.subjects = data;
      }
    });
  }

  loadMoreSubjects() {
    this.page = this.page + 1;

    this.subjectService.getSubjectsByPOD(this.idPod, this.page,this.typeSort)
    .subscribe({
      next: (data) => {
        this.subjects = this.subjects.concat(data);
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

  historical(model:any, subject:any) {
    this.offcanvasService.open(model, { position: 'end' });
    this.subject = subject;
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
    this.newSubject.assitanceCareers = this.assistanceCareersList;

    this.subjectService.createSubject(this.newSubject, this.idPod).subscribe({
      next: (data) => {
        this.getSubjectsInPod();
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