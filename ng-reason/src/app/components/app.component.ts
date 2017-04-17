import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RemoteDataService } from '../shared/remote-data.service';
import { RemoverService } from '../shared/remover.service';
import { RemoverStatistic } from '../shared/remover-statistic.model';
import { RemoverReason } from '../shared/remover-reason.model';
import { RemoverOrder } from '../shared/remover-order.model';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {

  showNavbar:boolean = false;

  constructor(
    private _router:Router
  ){
    this._router.events.subscribe((val) => {
      if (val.url.indexOf('filter') >= 0) {
        this.showNavbar = true;
      } else {
        this.showNavbar = false;
      }
    });
  }
  ngOnInit():void {
  }
}
