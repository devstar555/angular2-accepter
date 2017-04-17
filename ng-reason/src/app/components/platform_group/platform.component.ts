import {Component, OnInit} from '@angular/core';
import {PlatformService} from '../../shared/platform/platform-service';
import {PlatformModel} from '../../models/platform/index';
import {PublicService} from "../../shared/public/public-service";
import {Router} from '@angular/router';

@Component({
  selector: 'platform-component',
  templateUrl: './platform.component.html',
  styleUrls: ['../app.component.css']
})

export class PlatformComponent implements OnInit {

  platformData:any = [];
  error:any;

  public columns:Array<any> = [
    {title: 'Name', name: 'name', className: 'text-left col-md-2', sort: 'asc', filtering: {filterString: '', placeholder: ''}},
    {title: 'Description', name: 'description', className: 'col-md-2', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: 'Accounts', name: 'accounts', className: '', sort: true, filtering: {filterString: '', placeholder: ''}},
    {title: '', name: 'actionEdit', sort:false, className: 'accepter-col-action'},
    {title: '', name: 'actionDelete', sort:false, className: 'accepter-col-action'},
  ];

  constructor(
    private platformService: PlatformService,
    private publicService: PublicService,
    private router: Router
  ) {
    this.platformService.resetPlatformData();
    this.loadDataToModel();
  }

  public ngOnInit():void {
    this.publicService._get_DataChangedEvent().subscribe(_is_changed => {
      this.platformService.resetPlatformData();
      this.loadDataToModel();
    });
  }

  public loadDataToModel():void {
    this.platformService.dataPlatform$.subscribe(data => {
      this.platformData = [];
      for (var i = 0; i < data.response.length; i ++) {
        let platform:PlatformModel = new PlatformModel();
        platform = this.publicService.convertToTableData(data.response[i]);
        if (platform){
          this.platformData.push(platform);
        }
      }
      this.publicService.setDataFetchingFlag(true);
    });
    this.platformService.platformLoadData();
  }
}

