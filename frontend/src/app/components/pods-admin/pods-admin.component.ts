import { Component, OnInit, ViewChild } from '@angular/core';
import { CourseService } from 'src/app/services/course.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Params } from '@angular/router';

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
  public pod: any;
  public showLoader:boolean|any = false;
  public showLoaderCreate:boolean|any = false;
  
  constructor(
    private courseService: CourseService,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.getPods();
  }

  getPods(){
    this.showLoader = true;
    this.courseService.getPODs().subscribe({
      next: (data) => { 
        this.showLoader = false;
        this.pods = data;
      }
    });
  }

  onSubmit(form:any) {
    this.showLoaderCreate = true;

    var formData = new FormData();

    let fileUploadSubjects = this.fileSubjects.nativeElement;
    formData.append("fileSubjects", fileUploadSubjects.files[0]);

    let fileUploadTeachers = this.fileTeachers.nativeElement;
    formData.append("fileTeachers", fileUploadTeachers.files[0]);

    formData.append("course", this.course.value);

    this.courseService.createPOD(formData).subscribe({
      next: (data) => {
        this.showLoaderCreate = false;
        this.getPods();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  open(model:any, podToDelete:any) {
    this.modalService.open(model, {});
    this.pod = podToDelete;
  }

  deletePodById(id:any){
    this.courseService.deletePod(id).subscribe({
      next: (_) => {
        this.getPods();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
