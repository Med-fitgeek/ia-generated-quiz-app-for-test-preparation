import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KnowledgeUploadComponent } from './knowledge-upload.component';

describe('KnowledgeUploadComponent', () => {
  let component: KnowledgeUploadComponent;
  let fixture: ComponentFixture<KnowledgeUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KnowledgeUploadComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(KnowledgeUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
