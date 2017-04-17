import {Component, OnInit} from '@angular/core';
import {FilterService} from '../../../shared/filter/filter-service';
import {FilterModel} from '../../../models/filter/index';
import {PublicService} from "../../../shared/public/public-service";
import {Router} from '@angular/router';

@Component({
  selector: 'all-component',
  templateUrl: './all.component.html',
  styleUrls: ['../../app.component.css']
})

export class AllComponent implements OnInit {

  filterData:any = [];
  error:any;

  public columns:Array<any> = [
    {title: 'Value', name: 'name', className: ['text-left', 'col-md-3'], sort:'asc', filtering: {filterString: '', placeholder: ''}},
    {title: 'Description', name: 'description', className: '', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Filter', name: 'filter', className: 'text-center col-md-3', sort: true, filtering: {filterString: '', placeholder: ''}}
  ];

  constructor (
    private filterService: FilterService,
    private publicService: PublicService,
    private router: Router
  ) {
    this.publicService.loadPlatformData();
    if (this.publicService.filterData.length <= 0) {
      this.filterService.resetFilterData();
      this.loadDataToModel();
    }
  }

  public ngOnInit():void {
  }

  public loadDataToModel():void {
    this.publicService.filterData = [];
    this.loadCompanyData();
    this.loadPersonData();
    this.loadStreetData();
    this.loadPhoneData();
    this.loadEmailData();
  }

  public loadCompanyData():void {
    this.filterService.dataCompany$.subscribe(data => {
      for (var i = 0; i < data.filters.length; i++) {
        let company:FilterModel = new FilterModel();
        company = this.publicService.convertToTableData(data.filters[i]);
        if (company){
          company.filter = 'Company';
          this.publicService.filterData.push(company);
        }
      }
      this.filterService.resetCompanyData();
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.companyLoadData();
  }

  public loadPersonData():void {
    this.filterService.dataPerson$.subscribe(data => {
      for (var i = 0; i < data.filters.length; i++) {
        let person:FilterModel = new FilterModel();
        person = this.publicService.convertToTableData(data.filters[i]);
        if (person){
          person.filter = 'Person';
          this.publicService.filterData.push(person);
        }
      }
      this.filterService.resetPersonData();
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.personLoadData();
  }

  public loadStreetData():void {
    this.filterService.dataStreet$.subscribe(data => {
      for (var i = 0; i < data.filters.length; i++) {
        let street:FilterModel = new FilterModel();
        street = this.publicService.convertToTableData(data.filters[i]);
        if (street){
          street.filter = 'Street';
          this.publicService.filterData.push(street);
        }
      }
      this.filterService.resetStreetData();
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.streetLoadData();
  }

  public loadPhoneData():void {
    this.filterService.dataPhone$.subscribe(data => {
      for (var i = 0; i < data.filters.length; i++) {
        let phone:FilterModel = new FilterModel();
        phone = this.publicService.convertToTableData(data.filters[i]);
        if (phone){
          phone.filter = 'Phone';
          this.publicService.filterData.push(phone);
        }
      }
      this.filterService.resetPhoneData();
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.phoneLoadData();
  }

  public loadEmailData():void {
    this.filterService.dataEmail$.subscribe(data => {
      for (var i = 0; i < data.filters.length; i++) {
        let email:FilterModel = new FilterModel();
        email = this.publicService.convertToTableData(data.filters[i]);
        if (email){
          email.filter = 'Email';
          this.publicService.filterData.push(email);
        }
      }
      this.filterService.resetEmailData();
      this.publicService.setDataFetchingFlag(true);
    });
    this.filterService.emailLoadData();
  }
}
