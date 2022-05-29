import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';

@Component({
  selector: 'app-admin-subjects',
  templateUrl: './admin-subjects.component.html',
  styleUrls: ['./admin-subjects.component.css']
})
export class AdminSubjectsComponent implements OnInit {

  public subjects:any|undefined;
  public id:any;
  public idPod:any;
  public idSubject:any;

  constructor(
    private subjectService: SubjectService,
    private courseService: CourseService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe((params:Params) => {
      this.id = params['id'];
    });

    this.subjectService.getSubjectsByPOD(this.id).subscribe({
      next: (data) => {
        this.subjects = data;
      }
    });
  }

  deleteSubjectInPod(){
    this.route.params.subscribe((params:Params) => {
      this.idPod = params['idPod'];
      this.idSubject = params['idSubject'];
    });

    this.courseService.deleteSubjectInPod(this.idPod, this.idSubject).subscribe({
      next: () => {
        window.location.reload;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

}