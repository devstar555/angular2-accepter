import { Component } from '@angular/core';
import { RemoteDataService } from '../../shared/remote-data.service';
import { RemoverService } from '../../shared/remover.service';
import { RemoverStatistic } from '../../shared/remover-statistic.model';
import { RemoverReason } from '../../shared/remover-reason.model';
import { RemoverOrder } from '../../shared/remover-order.model';

@Component({
  selector: 'reason-component',
  templateUrl: './reason.component.html',
  styleUrls: ['../app.component.css']
})
export class ReasonComponent {

  private statistic: RemoverStatistic;
  private reasons: RemoverReason[];
  private orders: RemoverOrder[];
  private hours = 168;

  constructor(
    private remoteDataService: RemoteDataService,
    private removerService: RemoverService
  ) {
    this.removerService.reasons$.subscribe(reasons => { this.reasons = reasons; this.sortReasons(); });
    this.removerService.statistic$.subscribe(statistic => this.statistic = statistic);
    this.remoteDataService.data$.subscribe(data => { this.removerService.setRemoteData(data); });
    this.remoteDataService.loadData(this.hours);
  }

  public vonHideOrders(): void {
    this.orders = null;
  }

  public onViewOrders(orders: RemoverOrder[]): void {
    this.orders = orders;
  }

  public onReasonChecked(reason: RemoverReason, enabled: boolean): void {
    this.removerService.setReasonEnabled(reason, enabled);
    this.sortReasons();
  }

  private sortReasons(): void {
    this.reasons = this.reasons.sort((r1, r2) => { return r2.directCount.total - r1.directCount.total; });
  }

  public buttonClicked() {
    this.statistic = null;
    this.reasons = null;
    this.remoteDataService.loadData( this.hours );
  }

  private reasonFieldWrapper(str:string):string {
    if (!str) {
      return '';
    }
    if (str.length > 30)  {
      return str.substring(0,30) + '...';
    } else {
      return str;
    }
  }

}
