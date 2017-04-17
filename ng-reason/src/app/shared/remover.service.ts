import { RemoteData } from './remote-data.model';
import { RemoverReason } from './remover-reason.model';
import { RemoverOrder } from './remover-order.model';
import { RemoverCount } from './remover-count.model';
import { RemoverStatistic } from './remover-statistic.model';
import { Injectable } from '@angular/core';
import { Subject, ReplaySubject } from 'rxjs';
import 'rxjs/add/operator/map';

@Injectable()
export class RemoverService {

    private statisticSubject: Subject<RemoverStatistic> = new ReplaySubject<RemoverStatistic>(1);
    private reasonsSubject: Subject<RemoverReason[]> = new ReplaySubject<RemoverReason[]>(1);

    private statistic: RemoverStatistic;
    private reasons: RemoverReason[];
    private orders: RemoverOrder[];

    constructor() {

    }

    get statistic$() {
        return this.statisticSubject.asObservable();
    }

    get reasons$() {
        return this.reasonsSubject.asObservable();
    }

    setRemoteData(remoteData: RemoteData) {
        this.reasons = remoteData.reasons.map(x => { return {
            id: x.id, type: x.type, value: x.value, orders: [], enabled: true
        }; });

        let reasonsMap = new Map(<[number, RemoverReason][]>this.reasons.map(x => [x.id, x]));
        this.orders = remoteData.orders.map(x => { return {
            id: x.id,
            action: x.action,
            reasons: x.reasons.map(r => reasonsMap.get(r)).filter(r => r)
        }; }).filter(x => x.reasons.length > 0);

        let ordersMap = new Map(<[number, RemoverOrder][]>this.orders.map(c => [c.id, c]));
        remoteData.orders.forEach(order => {
            order.reasons.forEach(reasonId => {
                if (reasonsMap.get(reasonId)) {
                    reasonsMap.get(reasonId).orders.push(ordersMap.get(order.id));
                }
            });
        });

        this.reasons = this.reasons.filter(x => x.orders.length > 0);
        this.orders.forEach(o => this.updateOrder(o));
        this.reasons.forEach(r => this.updateReason(r));
        this.statistic = {
            orderCount: this.orders.length,
            matchedOrderCount: this.orders.filter(x => x.matched).length
        };

        this.reasonsSubject.next(this.reasons);
        this.statisticSubject.next(this.statistic);
    }

    setReasonEnabled(reason: RemoverReason, enabled: boolean) {
        reason.enabled = enabled;
        reason.orders.forEach(o => this.updateOrder(o));
        reason.orders.forEach(o => o.reasons.forEach(r => this.updateReason(r)));
    }

    private updateOrder(order: RemoverOrder) {
        let oldMatched = order.matched;
        order.matched = order.reasons.filter(r => r.enabled).length > 0;

        if (this.statistic && order.matched !== oldMatched) {
            if (oldMatched) {
                this.statistic.matchedOrderCount--;
            } else {
                this.statistic.matchedOrderCount++;
            }
        }
    }

    private updateReason(reason: RemoverReason) {
        let directlyAffectedLength: number = reason.enabled ? 1 : 0;
        let directlyAffectedOrders: RemoverOrder[] = reason.orders.filter(
            o => o.reasons.filter(r => r.enabled).length === directlyAffectedLength);

        reason.orderCount = this.count(reason.orders);
        reason.directCount = this.count(directlyAffectedOrders);
    }

    private count(orders: RemoverOrder[]): RemoverCount {
        return {
            total: orders.length,
            accept: orders.filter(r => r.action === 'accept').length,
            delegate: orders.filter(r => r.action === 'delegate').length,
            reject: orders.filter(r => r.action === 'reject').length,
            pending: orders.filter(r => !r.action).length
        };
    }

}
