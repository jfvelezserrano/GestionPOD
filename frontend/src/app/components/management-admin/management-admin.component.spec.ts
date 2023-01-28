import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagementAdminComponent } from './management-admin.component';

describe('ManagementAdminComponent', () => {
  let component: ManagementAdminComponent;
  let fixture: ComponentFixture<ManagementAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManagementAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManagementAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
