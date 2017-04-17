/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { AddformComponent } from './addform.component';

describe('AddformComponent', () => {
  let component: AddformComponent;
  let fixture: ComponentFixture<AddformComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddformComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
