import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {PublicService} from "../../shared/public/public-service";
import {ZipCodeModel} from "../../models/zipCode/zipcodeModel";
import {FilterService} from "../../shared/filter/filter-service";
import {PlatformService} from "../../shared/platform/platform-service";

@Component({
  selector: 'app-editform',
  templateUrl: './editform.component.html',
  styleUrls: ['../app.component.css']
})
export class EditformComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private publicService: PublicService,
    private filterService: FilterService,
    private platformService: PlatformService
  ) {
    this.publicService.loadPlatformData();
    this.tableType = route['data']['value']['type'];
    if (this.tableType == 'edit') this.tableType = 'platform';
    this.route.queryParams.subscribe(
      params => {
        this.id = params['id'];
        this.getEditData();
      }
    );
  }
  error:string = '';
  tableType:string = '';
  id:string = '';
  private zipCode = new ZipCodeModel();
  private mapData:any = {
    position: [this.zipCode.latitude, this.zipCode.longitude],
    center: {
      lat: this.zipCode.latitude,
      lng: this.zipCode.longitude
    }
  };
  private editData:any = {
    'name':'',
    'country': '',
    'group_names': [],
    'platformAccountGroupIds': [],
    'description': '',
    'accounts': [],
    'zip': ''
  };

  ngOnInit() {
  }

  protected getEditData():void {
    this.filterService.dataFilterById$.subscribe(data => {
      this.editData = data;
      this.editData.group_names = [];
      if (this.tableType != 'platform') {
        for (let i = 0; i < this.editData.platformAccountGroupIds.length; i++) {
          this.editData.group_names.push(this.publicService.platformData[this.publicService.group_ids_list.indexOf(parseInt(this.editData.platformAccountGroupIds[i]))].name);
        }
        this.getMapData(this.editData);
      }
      this.filterService.resetFilterDataFromId();
    });
    this.filterService.filterGetDataById(this.id, this.getFilterTypeFlag());
  }

  protected getFilterTypeFlag():number {
    let flag_number = 0;
    if (this.tableType == 'person') flag_number = 0;
    else if (this.tableType == 'company') flag_number = 1;
    else if (this.tableType == 'street') flag_number = 2;
    else if (this.tableType == 'phone') flag_number = 3;
    else if (this.tableType == 'email') flag_number = 4;
    else if (this.tableType == 'platform') flag_number = 5;
    return flag_number;
  }

  protected formFieldNameLabel():string {
    return this.publicService.getFormLabelString(this.tableType, this.editData.filterType);
  }
  protected showCountryField():boolean {
    if (this.tableType === 'person' || this.tableType === 'street' || this.tableType === 'company' ) {
      return true;
    } else {
      return false;
    }
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

  timer:any = [];
  protected getMapData(info:any):void {
    this.filterService.resetZipCodeData();
    this.zipCode = new ZipCodeModel();
    if (!info.zip || !info.country) return;
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
        instance.getMapData(instance.editData);
        clearInterval(instance.timer);
      }, 500);
  }
  protected getGroupIds():void {
    if (this.tableType === 'platform')  return;
    this.editData.platformAccountGroupIds = [];
    for (var i = 0; i < this.editData.group_names.length; i++){
      this.editData.platformAccountGroupIds.push(this.publicService.platformData[this.publicService.group_list.indexOf(this.editData.group_names[i])].id);
    }
  }

  protected onEditSubmit():void {
    this.getGroupIds();
    if (this.tableType == 'person') this.onEditFilterData(0);
    else if (this.tableType == 'company') this.onEditFilterData(1);
    else if (this.tableType == 'street') this.onEditFilterData(2);
    else if (this.tableType == 'phone') this.onEditFilterData(3);
    else if (this.tableType == 'email') this.onEditFilterData(4);
    else if (this.tableType == 'platform') this.onEditPlatformData();
  }

  private onEditFilterData(flag:number):void {
    this.filterService.filterEditData(flag, this.editData).subscribe(
      response => {
        this.publicService.editFilterData(flag, JSON.parse(response._body));
        this.filterService.resetFilterData();
        this.onBackBtn();
      }, error => {
        this.error = error;
      }
    );
  }

  private onEditPlatformData(): void {
    this.platformService.platformEditData(this.editData).subscribe(
      platformResponse => {
        this.platformService.resetPlatformData();
        this.onBackBtn();
        this.publicService._set_DataChangedEvent();
      }, error => {
        this.error = error;
      }
    );

  }
}
