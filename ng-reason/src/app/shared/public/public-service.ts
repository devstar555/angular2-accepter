import { Injectable, Input, Output, EventEmitter } from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {FilterModel} from "../../models/filter/allModel";
import {PersonModel} from "../../models/filter/personModel";
import {PlatformService} from "../platform/platform-service";
import {FilterService} from "../filter/filter-service";
import {CountryModel} from "../../models/filter/countryModel";
import {PropertyModel} from "../../models/property/propertyModel";

@Injectable()
export class PublicService {

  static id = "publicService";

  /************************************************
   * Event Emiiters
   * @type {boolean}
     */

  @Input() is_finished_fetching:boolean = false;
  @Output() _FetchingFinished$:EventEmitter<any> = new EventEmitter();

  setDataFetchingFlag(flag:boolean):void {
    this.is_finished_fetching = flag;
    this._FetchingFinished$.emit(this.is_finished_fetching);
  }

  getDataFetchingFlag$():boolean {
    return this.is_finished_fetching;
  }

  /************************************************/

  private data_changed: Subject<boolean> = new Subject<boolean>();

  _set_DataChangedEvent(): void {
    this.data_changed.next(true);
  }

  _get_DataChangedEvent(): Observable<any> {
    return this.data_changed.asObservable();
  }

  personData:any = [];
  streetData:any = [];
  companyData:any = [];
  phoneData:any = [];
  emailData:any = [];
  filterData:any = [];

  platformData:any = [];
  group_list:any = [];
  group_ids_list:any = [];

  country_list:Array<CountryModel> = new Array<CountryModel>();
  property_list:Array<PropertyModel> = new Array<PropertyModel>();

  constructor(
    private filterService: FilterService,
    private platformService: PlatformService
  ) {
    this.loadCountryData();
    this.loadPropertyData();
  }

  protected loadCountryData():void {
    this.filterService.dataCountry$.subscribe(data => {
      this.country_list = [];
      for (let i = 0; i < data.countries.length; i++) {
        let country:CountryModel = new CountryModel();
        country = data.countries[i];
        if (country) {
          this.country_list.push(data.countries[i]);
        }
      }
    });
    this.filterService.countryLoadData();
  }

  protected loadPropertyData():void {
    this.filterService.dataProperty$.subscribe(data => {
      this.property_list = [];
      for (let i = 0; i < data.properties.length; i++) {
        let property:PropertyModel = new PropertyModel();
        property = data.properties[i];
        if (property) {
          this.property_list.push(data.properties[i]);
        }
      }
    });
    this.filterService.propertyLoadData();
  }

  loadPlatformData():void {
    this.platformService.resetPlatformData();
    this.platformService.dataPlatform$.subscribe(data => {
      this.platformData = data.response;
      this.group_list = [];
      this.group_ids_list = [];
      for (var i = 0; i < this.platformData.length; i++) {
        this.group_list.push(this.platformData[i].name);
        this.group_ids_list.push(this.platformData[i].id);
      }
    });
    this.platformService.platformLoadData();
  }

  convertSpecialCharacter(str:string):string {
    const regex = /\/|\\|\(|\)|\[|\]|\|/g;
    let m;
    while ((m = regex.exec(str)) !== null) {
      if (m.index === regex.lastIndex) {
        regex.lastIndex++;
      }
      str = str.substr(0, m.index) + '\\' + str.substr(m.index);
      regex.lastIndex++;
    }
    return str;
  }

  convertToTableData(obj:any):any {
    if (obj.platformAccountGroupIds) {
      obj.platformAccountGroupIds = obj.platformAccountGroupIds.join(', ');
      obj.groupNames = this.setPlatformGroupNames(obj.platformAccountGroupIds);

      obj.country = this.convertNull(obj.country);
      obj.country_flag = this.getFlagString(obj.country);

    }

    if (obj.accounts) {
      obj.accounts = obj.accounts.join(', ');
    }
    obj.zip = this.convertNull(obj.zip);
    obj.description = this.convertNull(obj.description);
    obj.actionEdit = '<a class="action-btn edit"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>';
    obj.actionDelete = '<a class="action-btn remove"><i class="fa fa-trash-o" aria-hidden="true"></i></a>';
    obj.actionHistory = '<a class="action-btn history"><i class="fa fa-history" aria-hidden="true"></i></a>';
    return obj;
  }

  protected convertNull(description:string):string {
    if (description == null) return '';
    return description;
  }

  protected getFlagString(countrycode:string):string {
    if (countrycode == null)  return '';
    let temp_string = '<div class="country-item"><div class="img-thumbnail flag flag-icon-background flag-icon-';
    temp_string += countrycode.toLowerCase() + '" title="';
    temp_string += countrycode + '"></div><span>' + countrycode + '</span></div>';
    return temp_string;
  }

    public setPlatformGroupNames(platformAccountGroupIds:any):string {
    if (platformAccountGroupIds == "") {
      return '';
    }
    var temp_array = [];
    var temp_ids = platformAccountGroupIds.split(', ');
    for (var i = 0; i < temp_ids.length; i++) {
      temp_array.push(this.platformData[this.group_ids_list.indexOf(parseInt(temp_ids[i]))].name);
    }
    return temp_array.join(', ');
  }

  getFormLabelString(tableType:string, filterType:number):string {
    let fieldName:string = '';
    switch (tableType){
      case 'person':
        fieldName = 'Person';
        break;
      case 'company':
        fieldName = 'Company';
        break;
      case 'street':
        fieldName = 'Street';
        break;
      case 'phone':
        fieldName = 'Phone Number';
        break;
      case 'email':
        fieldName = 'E-Mail Address';
        break;
      case 'filter':
        if (filterType == 0) fieldName = 'Person';
        else if (filterType == 1) fieldName = 'Company';
        else if (filterType == 2) fieldName = 'Street';
        else if (filterType == 3) fieldName = 'Phone Number';
        else if (filterType == 4) fieldName = 'E-Mail Address';
        break;
      case 'platform':
        fieldName = 'PlatformGroupName';
        break;
      default:
        break;
    }
    return fieldName;
  }

  deleteFilterData(entry:any):void {
    this.filterData.splice(this.filterData.map(function(x) {return x.id; }).indexOf(entry.id), 1);
  }

  editFilterData(flag:number, entry:any):void {
    if (flag == 0) {
      this.personData[this.personData.map(function(x) {return x.id;}).indexOf(entry.id)] = this.convertToTableData(entry);
      let data:FilterModel = new FilterModel();
      data = entry;
      data.filter = 'Person';
      this.filterData[this.filterData.map(function(x) {return x.id;}).indexOf(entry.id)] = data;
    } else if (flag == 1) {
      this.companyData[this.companyData.map(function(x) {return x.id;}).indexOf(entry.id)] = this.convertToTableData(entry);
      let data:FilterModel = new FilterModel();
      data = entry;
      data.filter = 'Company';
      this.filterData[this.filterData.map(function(x) {return x.id;}).indexOf(entry.id)] = data;
    } else if (flag == 2) {
      this.streetData[this.streetData.map(function(x) {return x.id;}).indexOf(entry.id)] = this.convertToTableData(entry);
      let data:FilterModel = new FilterModel();
      data = entry;
      data.filter = 'Street';
      this.filterData[this.filterData.map(function(x) {return x.id;}).indexOf(entry.id)] = data;
    } else if (flag == 3) {
      this.phoneData[this.phoneData.map(function(x) {return x.id;}).indexOf(entry.id)] = this.convertToTableData(entry);
      let data:FilterModel = new FilterModel();
      data = entry;
      data.filter = 'Phone';
      this.filterData[this.filterData.map(function(x) {return x.id;}).indexOf(entry.id)] = data;
    } else if (flag == 4) {
      this.emailData[this.emailData.map(function(x) {return x.id;}).indexOf(entry.id)] = this.convertToTableData(entry);
      let data:FilterModel = new FilterModel();
      data = entry;
      data.filter = 'Email';
      this.filterData[this.filterData.map(function(x) {return x.id;}).indexOf(entry.id)] = data;
    }
    this._set_DataChangedEvent();
  }

  appendFilterData(flag:number, entry:any):void {
    if (flag == 0)  this.personData.push(this.convertToTableData(entry));
    else if (flag == 1) this.companyData.push(this.convertToTableData(entry));
    else if (flag == 2) this.streetData.push(this.convertToTableData(entry));
    else if (flag == 3) this.phoneData.push(this.convertToTableData(entry));
    else if (flag == 4) this.emailData.push(this.convertToTableData(entry));

    let data:FilterModel = new FilterModel();
    data = entry;
    if (flag == 0)  data.filter = 'Person';
    else if (flag == 1) data.filter = 'Company';
    else if (flag == 2) data.filter = 'Street';
    else if (flag == 3) data.filter = 'Phone';
    else if (flag == 4) data.filter = 'Email';
    this.filterData.push(data);
    this._set_DataChangedEvent();
  }

  appendMultipleData(entry:any):void {
    if (entry.personFilterResponse){
      this.personData.push(this.convertToTableData(entry.personFilterResponse));
      let data:FilterModel = new FilterModel();
      data = entry.personFilterResponse;
      data.filter = 'Person';
      this.filterData.push(data);
    }
    if (entry.companyFilterResponse) {
      this.companyData.push(this.convertToTableData(entry.companyFilterResponse));
      let data:FilterModel = new FilterModel();
      data = entry.companyFilterResponse;
      data.filter = 'Company';
      this.filterData.push(data);
    }
    if (entry.streetFilterResponse) {
      this.streetData.push(this.convertToTableData(entry.streetFilterResponse));
      let data:FilterModel = new FilterModel();
      data = entry.streetFilterResponse;
      data.filter = 'Street';
      this.filterData.push(data);
    }
    if (entry.phoneFilterResponse) {
      this.phoneData.push(this.convertToTableData(entry.phoneFilterResponse));
      let data:FilterModel = new FilterModel();
      data = entry.phoneFilterResponse;
      data.filter = 'Phone';
      this.filterData.push(data);
    }
    if (entry.emailFilterResponse) {
      this.emailData.push(this.convertToTableData(entry.emailFilterResponse));
      let data:FilterModel = new FilterModel();
      data = entry.emailFilterResponse;
      data.filter = 'Email';
      this.filterData.push(data);
    }
    this._set_DataChangedEvent();
  }
  hideTable(url:string):boolean {
    if(url.length > 0) {
      if (url.includes('add') || url.includes('edit'))  return true;
    }
    return false;
  }
}
