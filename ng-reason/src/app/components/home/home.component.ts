import { Component, OnInit } from '@angular/core';
import { Http } from '@angular/http';
import { ILog, ILoggerFactory } from '../../shared/logger.service';
import { DashboardService } from '../../shared/dashboard/dashboard-service';
import { TestModel } from '../../models/dashboard/index';

@Component({
  selector: 'home-component',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent implements OnInit {

  static id = "homeComponent";
  private _logger: ILog;
  private test_detail_mode = false;

  public dashboardData:any = {
    testRun: {
      id: 0,
      result: ''
    },
    streetFilterCount: 0,
    companyFilterCount: 0,
    personFilterCount: 0,
    emailFilterCount: 0,
    phoneFilterCount: 0
  };

  public testRunsData:any = [];

  /**
     * Constructor
     * @param dashboardService
     * @param http
     */
  constructor(
    private dashboardService: DashboardService,
    private http: Http
  ) {
    this.loadDataToModel();
  }

  /**
   * Load initial data
   */
  public loadDataToModel():void {
    this.loadDashboardData();
  }

  /**
   * On init function
   */
  public ngOnInit():void {
  }

  /**
   * Load Dashboard data
   */
  private loadDashboardData():void {
    this.dashboardService.dataDashboard$.subscribe(data => {
      this.dashboardData = data;
    });
    this.dashboardService.dashboardLoadData();
  }

  /**
   * Load TestRuns Data
   */
  private loadTestRunsData():void {
    this.dashboardService.testRuns$.subscribe(data => {
      this.testRunsData = [];
      let testRuns = new TestModel();
      for (let i = 0; i < data.tests.length; i++) {
        let testRun:TestModel = new TestModel();
        testRun = data.tests[i];
        if (data.tests[i].id == this.dashboardData.testRun.id) {
          for (let j = 0; j < testRun.testCases.length; j++ ) {
            this.testRunsData.push(testRun.testCases[j]);
          }
          break;
        }
      }
    });
    this.dashboardService.testRunsLoadData();
  }

  /**
   * Checking test result
   * @param resultString
   * @returns {number}
     */
  private checkTestResult(testRun:any):any {
    if (testRun) {
      let resultString = testRun.result;
      if (resultString == 'PASSED') return {'val':1, 'state':'Pass'};
      if (resultString == 'FAILED') return {'val':2, 'state':'Failed'};
      return {'val':0, 'state':'Pending'};
    } else {
      return {'val':0, 'state':'Pending'};
    }
  }

  private bindTestRunResult(testRun:any):any {
    if (testRun) {
      let resultString = testRun.result;
      if (resultString == 'PASSED') return {'val':1, 'state':'Pass'};
      if (resultString == 'FAILED') return {'val':2, 'state':'Fail'};
      return {'val':0, 'state':'Pending'};
    } else {
      return {'val':0, 'state':'Pending'};
    }
  }
  /**
   * On back button click event
   */
  private onBackBtn():any {
    this.test_detail_mode = false;
  }

  /**
   * Show test details
   */
  private onShowTestDetail():any {
    this.test_detail_mode = true;
    this.loadTestRunsData();
  }
}

