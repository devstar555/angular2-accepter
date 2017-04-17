import { Component, OnInit } from '@angular/core';
import {TestDataSetService} from "../../shared/test-data-set/test-data-set.service";

@Component({
  selector: 'app-test-data-set',
  templateUrl: './test-data-set.component.html',
  styleUrls: ['../app.component.css']
})
export class TestDataSetComponent implements OnInit {

  constructor(
    private testDataSetService:TestDataSetService
  ) {
    this.loadDataToModel();
    this.testDataSetService.testLoadData();
  }

  ngOnInit() {
  }

  testDataSet:any = {
    'timeoutMilliseconds' : 0,
    'expectAccept' : [],
    'expectReview' : [],
    'performance' : []
  };

  confirm_description:string = '';

  private loadDataToModel():void {
    this.testDataSetService.dataTest$.subscribe(data => {
      this.testDataSet = data;
    });
  }

  private onSubmitTestDataSet(submit_result:any):void {
    this.testDataSetService.testEditData(this.testDataSet).subscribe( data => {
      this.confirm_description = 'Tests saved successfully.';
      submit_result.open();
    },
    error => {
      this.confirm_description = 'There was an ERROR saving the tests.';
      submit_result.open();
    });
  }
}
