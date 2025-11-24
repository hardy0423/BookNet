import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyBook } from './my-book';

describe('MyBook', () => {
  let component: MyBook;
  let fixture: ComponentFixture<MyBook>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyBook]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyBook);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
