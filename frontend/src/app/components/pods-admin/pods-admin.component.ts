import { Component, OnInit, ViewChild } from '@angular/core';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-pods-admin',
  templateUrl: './pods-admin.component.html',
  styleUrls: ['./pods-admin.component.css']
})
export class PodsAdminComponent implements OnInit {

  @ViewChild('fileSubjects') fileSubjects!: any;
  @ViewChild('fileTeachers') fileTeachers!: any;
  @ViewChild('course') course!: any;

  public pods:any|undefined;
  
  constructor(
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.courseService.getPODs().subscribe({
      next: (data) => {
        this.pods = data;
      }
    });
  }

  onSubmit(form:any) {

    var formData = new FormData();

    let fileUploadSubjects = this.fileSubjects.nativeElement;
    formData.append("fileSubjects", fileUploadSubjects.files[0]);

    let fileUploadTeachers = this.fileTeachers.nativeElement;
    formData.append("fileTeachers", fileUploadTeachers.files[0]);

    formData.append("course", this.course.value);

    console.log(this.course.value);

    this.courseService.createPOD(formData).subscribe({
      next: (data) => {
        window.location.reload();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
