import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PodsAdminComponent } from './pods-admin.component';

describe('PodsAdminComponent', () => {
  let component: PodsAdminComponent;
  let fixture: ComponentFixture<PodsAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PodsAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PodsAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
