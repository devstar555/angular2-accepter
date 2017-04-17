import {Component, OnInit} from '@angular/core';
import {FilterService} from '../../../shared/filter/filter-service';
import {PersonModel} from '../../../models/filter/index';
import {PublicService} from "../../../shared/public/public-service";
import {Router} from '@angular/router';

@Component({
  selector: 'person-component',
  templateUrl: './person.component.html',
  styleUrls: ['../../app.component.css']
})

export class PersonComponent implements OnInit {

  error:any;

  columns:Array<any> = [
    {title: 'Person', name: 'name', className: 'text-left col-md-2', sort: 'asc', filtering: {filterString: '', placeholder: ''}},
    {title: 'Country', name: 'country_flag', className: 'text-center col-md-1', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Zip', name: 'zip', className: 'text-center col-md-1', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Group', name: 'groupNames', className: 'text-center col-md-1', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Description', name: 'description', className: '', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: '', name: 'actionEdit', sort:false, className: 'accepter-col-action'},
    {title: '', name: 'actionDelete', sort:false, className: 'accepter-col-action'},
    {title: '', name: 'actionHistory', sort:false, className: 'accepter-col-action'}
  ];

  constructor(
    private filterService: FilterService,
    private publicService: PublicService,
    private router: Router
  ) {
    this.publicService.loadPlatformData();
    if (this.publicService.personData.length <= 1) {
      this.filterService.resetFilterData();
      this.loadDataToModel();
    }
  }

  public ngOnInit():void {
  }

  protected loadDataToModel():void {
    this.filterService.dataPerson$.subscribe(data => {
      this.publicService.personData = [];
      for (var i = 0; i < data.filters.length; i ++) {
        let person:PersonModel = new PersonModel();
        person = this.publicService.convertToTableData(data.filters[i]);
        if (person){
          this.publicService.personData.push(person);
        }
      }
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.personLoadData();
  }
}

