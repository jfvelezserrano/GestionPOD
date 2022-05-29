import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-admin-teachers',
  templateUrl: './admin-teachers.component.html',
  styleUrls: ['./admin-teachers.component.css']
})
export class AdminTeachersComponent implements OnInit {

  public teachers:any|undefined;
  public id:any;
  public idPod:any;
  public idTeacher:any;

  constructor(
    private teacherService: TeacherService,
    private courseService: CourseService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe((params:Params) => {
      this.id = params['id'];
    });

    this.teacherService.getTeachersByPOD(this.id).subscribe({
      next: (data) => {
        this.teachers = data;
      }
    });
  }

  deleteTeacherInPod(){
    this.route.params.subscribe((params:Params) => {
      this.idPod = params['idPod'];
      this.idTeacher = params['idTeacher'];
    });

    this.courseService.deleteTeacherInPod(this.idPod, this.idTeacher).subscribe({
      next: () => {
        window.location.reload;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
