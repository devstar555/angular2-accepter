import { Component, OnInit } from '@angular/core';
import { FilterService } from '../../../shared/filter/filter-service';
import { StreetModel } from '../../../models/filter/index';
import {PublicService} from "../../../shared/public/public-service";
import {Router} from '@angular/router';

@Component({
  selector: 'street-component',
  templateUrl: './street.component.html',
  styleUrls: ['../../app.component.css']
})

export class StreetComponent implements OnInit {

  error:any;

  public columns:Array<any> = [
    {title: 'Street', name: 'name', className: 'text-left col-md-2', sort:'asc', filtering: {filterString: '', placeholder: ''}},
    {title: 'Country', name: 'country_flag', className: 'text-center col-md-1', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Zip', name: 'zip', className: 'text-center col-md-1', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Group', name: 'groupNames', className: 'text-center col-md-1', sort: true, filtering: {filterString: '', placeholder: ''}},
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
    if (this.publicService.streetData.length <= 1) {
      this.filterService.resetFilterData();
      this.loadDataToModel();
    }
  }

  public ngOnInit():void {
  }

  public loadDataToModel():void {
    this.filterService.dataStreet$.subscribe(data => {
      this.publicService.streetData = [];
      for (var i = 0; i < data.filters.length; i++) {
        let street:StreetModel = new StreetModel();
        street = this.publicService.convertToTableData(data.filters[i]);
        if (street){
          this.publicService.streetData.push(street);
        }
      }
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.streetLoadData();
  }
}
