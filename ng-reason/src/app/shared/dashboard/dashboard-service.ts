import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import { Observable, Subject, ReplaySubject } from 'rxjs';
import { Config } from '../../config/config';
import { ILog, ILoggerFactory } from '../logger.service';
import 'rxjs/add/operator/map';

@Injectable()
/**
 * Dashboard Service
 */
export class DashboardService {
  static id = "dashboardService";
  private _logger: ILog;

  protected basePath:string;

  /**
   * Constructor
   * @param http
   * @param config
     */
  constructor(private http: Http,
              private config: Config) {
    this.basePath = this.config.api;
    this.reqOptions = new RequestOptions({ headers: new Headers({"Content-Type": "application/json; charset=UTF-8"}) });
  }

  private reqOptions:any;
  private dashboardData: Subject<any> = new ReplaySubject<any>(1);

  private testRuns: Subject<any> = new ReplaySubject<any>(1);


  data: any;

  /**
   * Get Dashboard Data
   * @returns {Observable<T>}
     */
  get dataDashboard$(): Observable<any> {
    return this.dashboardData.asObservable();
  }

  /**
   * Get Test Run
   * @returns {Observable<any>}
     */
  get testRuns$(): Observable<any> {
    return this.testRuns.asObservable();
  }

  /**
   * Load dashboard data
   */
  dashboardLoadData() {
    this.http.get(this.basePath + 'dashboard', this.reqOptions)
      .map(x => x.json())
      .subscribe(data => {
        this.dashboardData.next(data);
      });
  }

  /**
   * Load testRuns data
   */
  testRunsLoadData() {
    this.http.get(this.basePath + 'testRuns', this.reqOptions)
      .map(x => x.json())
      .subscribe(data => {
        this.testRuns.next(data);
      });
  }
}
