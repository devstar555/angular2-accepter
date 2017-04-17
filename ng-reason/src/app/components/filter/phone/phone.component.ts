import {Component, OnInit} from '@angular/core';
import {FilterService} from '../../../shared/filter/filter-service';
import {PhoneModel} from '../../../models/filter/index';
import {PublicService} from "../../../shared/public/public-service";
import {Router} from '@angular/router';

@Component({
  selector: 'phone-component',
  templateUrl: './phone.component.html',
  styleUrls: ['../../app.component.css']
})

export class PhoneComponent implements OnInit {

  error:any;

  public columns:Array<any> = [
    {title: 'Phone Number', name: 'name', className: 'text-left col-md-2', sort:'asc', filtering: {filterString: '', placeholder: ''}},
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
    if (this.publicService.phoneData.length <= 1) {
      this.filterService.resetFilterData();
      this.loadDataToModel();
    }
  }

  public ngOnInit():void {
  }

  public loadDataToModel():void {
    this.filterService.dataPhone$.subscribe(data => {
      this.publicService.phoneData = [];
      for (var i = 0; i < data.filters.length; i++) {
        let phone:PhoneModel = new PhoneModel();
        phone = this.publicService.convertToTableData(data.filters[i]);
        if (phone){
          this.publicService.phoneData.push(phone);
        }
      }
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.phoneLoadData();
  }
}
