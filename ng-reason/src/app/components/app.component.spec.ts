/* tslint:disable:no-unused-variable */

import { TestBed, async } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RemoteDataService } from '../shared/remote-data.service';
import { RemoverService } from '../shared/remover.service';
import { MockRemoteDataService } from '../shared/mock-remote-data.service';

describe('App: NgReason', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      providers: [
        RemoverService,
        {provide: RemoteDataService, useClass: MockRemoteDataService}
      ]
    });
  });

  it('should create the app', async(() => {
    let fixture = TestBed.createComponent(AppComponent);
    let app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

});
