import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import { Observable, Subject, ReplaySubject } from 'rxjs';
import { Config } from '../../config/config';
import 'rxjs/add/operator/map';

@Injectable()
export class TestDataSetService {

  constructor(
    private http: Http,
    private config: Config
  ) {
    this.basePath = this.config.api;
    this.reqOptions = new RequestOptions({ headers: new Headers({"Content-Type": "application/json; charset=UTF-8"}) });
  }

  protected basePath:string;
  private reqOptions:any;
  private testData: Subject<any> = new ReplaySubject<any>(1);

  get dataTest$(): Observable<any> {
    return this.testData.asObservable();
  }

  public resetTestData(): void {
    delete this.testData;
    this.testData = new ReplaySubject<any>(1);
  }

  testLoadData() {
    this.http.get(this.basePath + 'testDataSet', this.reqOptions)
      .map(x => x.json())
      .subscribe(data => {
        this.testData.next(data);
      });
  }

  testEditData(testdata): Observable<any> {
    let data = JSON.stringify(testdata);
    return this.http.put(this.basePath + 'testDataSet', data, this.reqOptions)
      .map(x => x);
  }
}
