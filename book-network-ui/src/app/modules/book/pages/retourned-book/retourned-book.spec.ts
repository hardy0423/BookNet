import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RetournedBook } from './retourned-book';

describe('RetournedBook', () => {
  let component: RetournedBook;
  let fixture: ComponentFixture<RetournedBook>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RetournedBook]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RetournedBook);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
