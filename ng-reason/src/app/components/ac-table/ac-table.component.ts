import {Component, OnInit, ChangeDetectorRef, Input, EventEmitter} from '@angular/core';
import {Router} from '@angular/router';
import {FilterService} from "../../shared/filter/filter-service";
import {PlatformService} from "../../shared/platform/platform-service";
import {PublicService} from "../../shared/public/public-service";
import {CountryModel} from "../../models/filter/countryModel";
import {PersonModel} from "../../models/filter/personModel";

import { Ng2MapComponent } from 'ng2-map';
import {PropertyModel} from "../../models/property/propertyModel";
import {ZipCodeModel} from "../../models/zipCode/zipcodeModel";

Ng2MapComponent['apiUrl'] =
  'https://maps.google.com/maps/api/js?key=AIzaSyAzi7u7bXMcd24fDD1xYgPTdI1k0QAhhoQ';

export class ColumnsModel {
  public name:string;
  public sort:string;
  public title:string;
  public className:string;
}

@Component({
  selector: 'app-ac-table',
  templateUrl: './ac-table.component.html',
  styleUrls: ['../app.component.css'],
  inputs: ['columns', 'data', 'tableType']
})

export class AcTableComponent implements OnInit {

  columns:Array<any> = [];
  data:Array<any> = [];
  tableType = 'entry';

  error:any;
  rows:Array<any> = [];

  constructor(
    private filterService: FilterService,
    private platformService: PlatformService,
    private ref:ChangeDetectorRef,
    private publicService: PublicService,
    private _router: Router
  ) {
  }

  public page:number = 1;
  public itemsPerPage:number = 15;
  public maxSize:number = 5;
  public numPages:number = 1;
  public length:number = 0;

  public config:any = {};

  protected temp_filterPattern:string = '';
  protected temp_config:any = {
    temp_filterPattern:'',
    config:{
      paging: true,
      sorting: {columns: []},
      filtering: {filterString: ''},
      className: ['table-filter']
    }
  };

  private currentItem:Array<any> = [];
  private historyData:any = [];
  private historyMode:boolean = false;

  public ngOnInit():void {
    this.config = {
      paging: true,
      sorting: {columns: this.columns},
      filtering: {filterString: ''},
      className: ['table-filter']
    };

    for (let i = 0; i < this.config.sorting.columns.length; i++) {
      let columns:ColumnsModel = new ColumnsModel();
      columns.name = this.config.sorting.columns[i].name;
      columns.className = this.config.sorting.columns[i].className;
      columns.sort = '';
      this.config.sorting.columns[i].sort = '';
      columns.title = this.config.sorting.columns[i].title;
      this.temp_config.config.sorting.columns.push(columns);
    }

    this.temp_config.config.sorting.columns[0].sort = 'asc';
    this.config.sorting.columns[0].sort = 'asc';

    this.publicService._FetchingFinished$.subscribe(
      (is_finished) => {
        setTimeout(() => {
          this.length = this.data.length;
          this.onChangeTable(this.config, true);
          this.onBackBtn();
        }, 1);
      }
    );
    this.onChangeTable(this.config, true);
    this.publicService._get_DataChangedEvent().subscribe(_is_changed => {
      this.onChangeTable(this.config, true);
    });
  }

  protected changePage(page:any, data:Array<any> = this.data):Array<any> {
    let start = (page.page - 1) * page.itemsPerPage;
    let end = page.itemsPerPage > -1 ? (start + page.itemsPerPage) : data.length;
    return data.slice(start, end);
  }

  protected changeSort(data:any, config:any, initial_flag:any):any {
    if (!config.sorting) {
      return data;
    }

    let columns = this.config.sorting.columns || [];
    let columnName:string = void 0;
    let sort:string = void 0;

    if (initial_flag == true) {
      columnName = 'name';
      sort = 'asc';
    } else {
      for (let i = 0; i < columns.length; i++) {
        if (columns[i].sort !== '' && columns[i].sort !== false) {
          columnName = columns[i].name;
          sort = columns[i].sort;
        }
      }
    }

    if (!columnName) {
      return data;
    }

    // simple sorting
    return data.sort((previous:any, current:any) => {
      if (previous[columnName] > current[columnName]) {
        return sort === 'desc' ? -1 : 1;
      } else if (previous[columnName] < current[columnName]) {
        return sort === 'asc' ? -1 : 1;
      }
      return 0;
    });
  }

  protected changeFilter(data:any, config:any):any {
    let filteredData:Array<any> = data;
    this.columns.forEach((column:any) => {
      if (column.filtering) {
        if (column.filtering.filterString) {
          if (column.name == 'country_flag') {
            filteredData = filteredData.filter((item:any) => {
              return item['country'].toLowerCase().match(this.publicService.convertSpecialCharacter(column.filtering.filterString).toLowerCase());
            });
          }

          filteredData = filteredData.filter((item:any) => {
            return item[column.name].toLowerCase().match(this.publicService.convertSpecialCharacter(column.filtering.filterString).toLowerCase());
          });
        }
      }
    });

    if (!config.filtering) {
      return filteredData;
    }

    if (config.filtering.columnName) {
      return filteredData.filter((item:any) =>
        item[config.filtering.columnName].match(this.config.filtering.filterString));
    }

    let tempArray:Array<any> = [];
    filteredData.forEach((item:any) => {
      let flag = false;
      this.columns.forEach((column:any) => {
        if (column.name == 'country_flag') {
          if (item['country'].toString().match(this.config.filtering.filterString)) {
            flag = true;
          }
        } else {
          if (item[column.name]) {
            if (item[column.name].toString().match(this.config.filtering.filterString)) {
              flag = true;
            }
          }
        }
      });
      if (flag) {
        tempArray.push(item);
      }
    });
    filteredData = tempArray;

    return filteredData;
  }

  protected onChangeTable(config:any, initial_flag:any, page:any = {page: this.page, itemsPerPage: this.itemsPerPage}):any {
    var new_filterPattern = '';
    for (let i = 0; i < config.sorting.columns.length; i++) {
      if (config.sorting.columns[i].filtering) {
        new_filterPattern += config.sorting.columns[i].filtering.filterString;
      }
    }

    if (new_filterPattern == this.temp_filterPattern) {
      if (config.sorting) {
        Object.assign(this.config.sorting, config.sorting);
      }
    } else  {
      Object.assign(this.config.sorting, config.sorting);
      for (let i = 0; i < config.sorting.columns.length; i++) {
        this.config.sorting.columns[i].sort = this.temp_config.config.sorting.columns[i].sort;
      }
    }
    let filteredData = this.changeFilter(this.data, this.config);
    let sortedData = this.changeSort(filteredData, this.config, initial_flag);
    this.rows = page && config.paging ? this.changePage(page, sortedData) : sortedData;
    this.length = sortedData.length;


    if (config.sorting) {
      new_filterPattern = '';
      this.temp_config.config.sorting.columns = [];
      for (let i = 0; i < this.config.sorting.columns.length; i++) {

        if (this.config.sorting.columns[i].filtering) {
          new_filterPattern += this.config.sorting.columns[i].filtering.filterString;
        }

        let columns:ColumnsModel = new ColumnsModel();
        columns.name = this.config.sorting.columns[i].name;
        columns.className = this.config.sorting.columns[i].className;
        columns.sort = this.config.sorting.columns[i].sort;
        columns.title = this.config.sorting.columns[i].title;
        this.temp_config.config.sorting.columns.push(columns);
      }
    }
    this.temp_filterPattern = new_filterPattern;
    if (!initial_flag)
      this.ref.detectChanges();
  }

  protected onCellClick(data: any, delete_confirm: any): any {
    if (data.column === 'actionEdit'){
      this.onEditEntry(data.row.id);
    }
    if (data.column === 'actionDelete') {
      delete_confirm.open();
      this.currentItem = data.row;
    }
    if (data.column === 'actionHistory') {
      this.getFiltersHistory(data.row.id);
      this.historyMode = true;
    }
  }

  protected onAddNew ():void {
    let router_string  = '';
    if (this.tableType == 'platform') router_string = '/platform/add';
    else if (this.tableType == 'filter') router_string = '/filters/all/add';
    else  router_string = '/filters/'+this.tableType+'/add';
    this._router.navigate([router_string]);
  }

  protected onEditEntry(entry_id:string):void {
    let router_string = '';
    if (this.tableType == 'platform') router_string = '/platform/edit';
    else  router_string = '/filters/' + this.tableType + '/edit';
    this._router.navigate([router_string], {queryParams : {
      id:entry_id
    }});
  }

  protected onAddMultiple():void {
    this._router.navigate(['/filters/all/add'], {queryParams : {
      multi:true
    }});
  }

  protected onDeleteClick(delete_confirm:any, item:any):boolean {
    if (this.tableType === 'person')
      this.personDeleteFunction(delete_confirm, item);
    else if (this.tableType === 'street')
      this.streetDeleteFunction(delete_confirm, item);
    else if (this.tableType === 'email')
      this.emailDeleteFunction(delete_confirm, item);
    else if (this.tableType === 'phone')
      this.phoneDeleteFunction(delete_confirm, item);
    else if (this.tableType === 'company')
      this.companyDeleteFunction(delete_confirm, item);
    else if (this.tableType === 'platform')
      this.platformDeleteFunction(delete_confirm, item);

    this.publicService.deleteFilterData(item);
    return true;
  }

  protected onBackBtn(): void {
    this.historyMode = false;
  }

  protected getFiltersHistory(filterId:string) {
    this.filterService.resetHistoryData();
    this.filterService.dataHistory$.subscribe(data => {
      this.historyData = data.filterHistoryResponses;
    });
    this.filterService.historyLoadData(filterId);
  }

  protected personDeleteFunction(delete_confirm:any, item:any):void {
    this.filterService.personDeleteData(item.id).subscribe(
      personResponse => {
        delete_confirm.close();
        this.rows.splice(this.rows.map(function(x) {return x.id; }).indexOf(item.id), 1);
        this.data.splice(this.data.map(function(x) {return x.id; }).indexOf(item.id), 1);
      }, error => {
        this.error = error;
      });
  }

  protected streetDeleteFunction(delete_confirm:any, item:any):void {
    this.filterService.streetDeleteData(item.id).subscribe(
      streetResponse => {
        delete_confirm.close();
        this.rows.splice(this.rows.map(function(x) {return x.id; }).indexOf(item.id), 1);
        this.data.splice(this.data.map(function(x) {return x.id; }).indexOf(item.id), 1);
      }, error => {
        this.error = error;
      });
  }

  protected companyDeleteFunction(delete_confirm:any, item:any):void {
    this.filterService.companyDeleteData(item.id).subscribe(
      companyResponse => {
        delete_confirm.close();
        this.rows.splice(this.rows.map(function(x) {return x.id; }).indexOf(item.id), 1);
        this.data.splice(this.data.map(function(x) {return x.id; }).indexOf(item.id), 1);
      }, error => {
        this.error = error;
      });
  }

  protected emailDeleteFunction(delete_confirm:any, item:any):void {
    this.filterService.emailDeleteData(item.id).subscribe(
      emailResponse => {
        delete_confirm.close();
        this.rows.splice(this.rows.map(function(x) {return x.id; }).indexOf(item.id), 1);
        this.data.splice(this.data.map(function(x) {return x.id; }).indexOf(item.id), 1);
      }, error => {
        this.error = error;
      });
  }

  protected phoneDeleteFunction(delete_confirm:any, item:any):void {
    this.filterService.phoneDeleteData(item.id).subscribe(
      phoneResponse => {
        delete_confirm.close();
        this.rows.splice(this.rows.map(function(x) {return x.id; }).indexOf(item.id), 1);
        this.data.splice(this.data.map(function(x) {return x.id; }).indexOf(item.id), 1);
      }, error => {
        this.error = error;
      });
  }

  protected platformDeleteFunction(delete_confirm:any, item:any):boolean {
    this.platformService.platformDeleteData(item.id).subscribe(
      platformResponse => {
        delete_confirm.close();
        this.rows.splice(this.rows.map(function(x) {return x.id; }).indexOf(item.id), 1);
        this.data.splice(this.data.map(function(x) {return x.id; }).indexOf(item.id), 1);
      }, error => {
        this.error = error;
      });
    return true;
  }

}
