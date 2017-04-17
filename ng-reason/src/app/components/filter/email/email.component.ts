import { Component, OnInit } from '@angular/core';
import { FilterService } from '../../../shared/filter/filter-service';
import { EmailModel } from '../../../models/filter/index';
import {PublicService} from "../../../shared/public/public-service";
import {Router} from '@angular/router';

@Component({
  selector: 'email-component',
  templateUrl: './email.component.html',
  styleUrls: ['../../app.component.css']
})

export class EmailComponent implements OnInit {

  error:any;

  public columns:Array<any> = [
    {title: 'E-Mail Address', name: 'name', className: 'text-left col-md-2', sort:'asc',filtering: {filterString: '', placeholder: ''}},
    {title: 'PlatformGroups', name: 'groupNames', className: 'text-center col-md-2', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Description', name: 'description', className: '', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: '', name: 'actionEdit', sort:false, className: 'accepter-col-action'},
    {title: '', name: 'actionDelete', sort:false, className: 'accepter-col-action'},
    {title: '', name: 'actionHistory', sort:false, className: 'accepter-col-action'}
  ];


  constructor (
    private filterService: FilterService,
    private publicService: PublicService,
    private router: Router
  ) {
    this.publicService.loadPlatformData();
    if (this.publicService.emailData.length <= 1) {
      this.filterService.resetFilterData();
      this.loadDataToModel();
    }
  }

  public ngOnInit():void {
  }

  public loadDataToModel():void {
    this.filterService.dataEmail$.subscribe(data => {
      this.publicService.emailData = [];
      for (var i = 0; i < data.filters.length; i++) {
        let email:EmailModel = new EmailModel();
        email = this.publicService.convertToTableData(data.filters[i]);
        if (email){
          this.publicService.emailData.push(email);
        }
      }
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.emailLoadData();
  }
}
