import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {ZipCodeModel} from "../../models/zipCode/zipcodeModel";
import {CountryModel} from "../../models/filter/countryModel";
import {PropertyModel} from "../../models/property/propertyModel";
import {PublicService} from "../../shared/public/public-service";
import {FilterService} from "../../shared/filter/filter-service";
import {PlatformService} from "../../shared/platform/platform-service";

@Component({
  selector: 'app-addform',
  templateUrl: './addform.component.html',
  styleUrls: ['../app.component.css']
})
export class AddformComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private publicService: PublicService,
    private filterService: FilterService,
    private platformService: PlatformService
  ) {
    this.publicService.loadPlatformData();
    this.tableType = route['data']['value']['type'];
    if (this.tableType == 'filter')  {
      this.route.queryParams.subscribe(
          params => {
            if (params['multi'] == 'true')  this.tableType = 'multiple';
            else  this.tableType = 'filter';
        }
      )
    } else if (this.tableType == 'add') {
      this.tableType = 'platform';
    }
  }
  error:string = '';
  tableType:string = '';
  private newData:any = {
    'filterType': 0,
    'name':'',
    'country': '',
    'zip': '',
    'group_names': [],
    'platformAccountGroupIds': [],
    'description': '',
    'accounts': []
  };

  private filterData:any = {
    'companyName': '',
    'streetName': '',
    'phoneName': '',
    'emailName': '',
    'personName': '',
    'country': '',
    'zip': '',
    'description': '',
    'platformAccountGroupIds': [],
    'group_names': []
  };

  private zipCode = new ZipCodeModel();
  private mapData:any = {
    position: [this.zipCode.latitude, this.zipCode.longitude],
    center: {
      lat: this.zipCode.latitude,
      lng: this.zipCode.longitude
    }
  };

  ngOnInit() {
  }

  protected onBackBtn(): void {
    let url_string = this.tableType;
    if (this.tableType == 'filter' || this.tableType == 'multiple') {
      url_string = 'filters/all';
    } else if (this.tableType == 'platform') {
      url_string = 'platform';
      this.publicService._set_DataChangedEvent();
    } else {
      url_string = 'filters/' + this.tableType;
    }
    this.router.navigate([url_string]);
  }

  protected formFieldNameLabel():string {
    return this.publicService.getFormLabelString(this.tableType, this.newData.filterType);
  }
  protected showCountryField():boolean {
    if (this.tableType === 'person' || this.tableType === 'street' || this.tableType === 'company' ) {
      return true;
    } else if (this.tableType === 'filter' && (this.newData.filterType == 0 || this.newData.filterType == 1 || this.newData.filterType == 2)) {
      return true;
    } else {
      return false;
    }
  }

  timer:any = [];
  protected getMapData(info:any):void {
    this.filterService.resetZipCodeData();
    this.zipCode = new ZipCodeModel();
    if (info.zip.length > 0 && info.country.length > 0) {
      this.filterService.dataZipCode$.subscribe(data => {
        if (data.response) {
          this.zipCode = data.response;
        }
        this.mapData.center.lat = +this.zipCode.latitude;
        this.mapData.center.lng = +this.zipCode.longitude;
        this.mapData.position = [+this.zipCode.latitude, +this.zipCode.longitude];
      });
      this.filterService.zipcodeLoadData(info.country, info.zip);
    }
  }
  protected zipcodeKeydown(flag:number):void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    var instance = this;
    this.timer = setInterval(
      function(){
        if (flag == 1) {
        instance.getMapData(instance.newData);
        } else if (flag == 2) {
          instance.getMapData(instance.filterData);
        }
        clearInterval(instance.timer);
      }, 500);
  }

  protected onAddSubmit():void {
    this.getGroupIds();
    if (this.tableType == 'person') this.AddFilterData(0);
    else if (this.tableType == 'company') this.AddFilterData(1);
    else if (this.tableType == 'street') this.AddFilterData(2);
    else if (this.tableType == 'phone') this.AddFilterData(3);
    else if (this.tableType == 'email') this.AddFilterData(4);
    else if (this.tableType == 'filter') this.AddFilterData(this.newData.filterType);
    else if (this.tableType == 'platform') this.AddPlatformData();
  }

  protected onMultipleFilterSubmit():void {
    this.filterData.platformAccountGroupIds = [];
    for (var i = 0; i < this.filterData.group_names.length; i++) {
      this.filterData.platformAccountGroupIds.push(this.publicService.platformData[this.publicService.group_list.indexOf(this.filterData.group_names[i])].id);
    }
    this.AddFiltersData();
  }
  protected AddFilterData(filterType:number):void {
    this.filterService.filterAddData(filterType, this.newData).subscribe(
      filterResponse => {
        this.filterService.resetFilterData();
        this.publicService.appendFilterData(filterType, filterResponse);
        this.onBackBtn();
      }, error => {
        this.error = error;
      });
  }
  protected AddFiltersData():void {
    this.filterService.filtersAddData(this.filterData).subscribe(
      filterResponse => {
        this.publicService.appendMultipleData(filterResponse);
        this.filterService.resetFilterData();
        this.onBackBtn();
      }, error => {
        this.error = error;
      }
    )
  }
  protected AddPlatformData():void {
    this.platformService.platformAddData(this.newData).subscribe(
      platformResponse => {
        this.platformService.resetPlatformData();
        this.onBackBtn();
        this.publicService._set_DataChangedEvent();
      }, error => {
        this.error = error;
      });
  }

  protected getGroupIds():void {
    if (this.tableType === 'platform')  return;
    this.newData.platformAccountGroupIds = [];
    for (var i = 0; i < this.newData.group_names.length; i++){
      this.newData.platformAccountGroupIds.push(this.publicService.platformData[this.publicService.group_list.indexOf(this.newData.group_names[i])].id);
    }
  }
  protected onSelectFilterType($event):void {
    this.newData.filterType = $event;
  }
}
