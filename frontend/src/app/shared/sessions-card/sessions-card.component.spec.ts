import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionsCardComponent } from './sessions-card.component';

describe('SessionsCardComponent', () => {
  let component: SessionsCardComponent;
  let fixture: ComponentFixture<SessionsCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionsCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SessionsCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
