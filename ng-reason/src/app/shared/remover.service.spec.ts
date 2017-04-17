/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { RemoverService } from './remover.service';
import { RemoverReason } from './remover-reason.model';
import { RemoverStatistic } from './remover-statistic.model';

describe('Service: Remover', () => {
    let reasons: RemoverReason[];
    let statistic: RemoverStatistic;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers : [
                RemoverService
            ],
        });
        reasons = null;
        statistic = null;
    });

    it('should exist', inject([RemoverService], (service: RemoverService) => {
        expect(service).toBeTruthy();
    }));

    it('should work with empty data', inject([RemoverService], (service: RemoverService) => {
        service.reasons$.subscribe(x => reasons = x);
        service.statistic$.subscribe(x => statistic = x);
        service.setRemoteData({reasons: [], orders: []});

        expect(statistic.orderCount).toEqual(0);
        expect(statistic.matchedOrderCount).toEqual(0);
        expect(reasons.length).toEqual(0);
    }));

    it('should work with small data', inject([RemoverService], (service: RemoverService) => {
        service.reasons$.subscribe(x => reasons = x);
        service.statistic$.subscribe(x => statistic = x);
        service.setRemoteData({
            reasons: [
                {id: 1, type: 'susp', value: 'fischer'},
                {id: 2, type: 'susp', value: 'mueller'}
            ],
            orders: [
                {id: 1, action: 'accept', reasons: [1, 2]},
                {id: 2, action: 'reject', reasons: [1]},
                {id: 3, action: null, reasons: [2]}
            ]
        });

        expect(statistic.orderCount).toEqual(3);
        expect(statistic.matchedOrderCount).toEqual(3);
        expect(reasons.length).toEqual(2);

        expect(reasons[0].id).toEqual(1);
        expect(reasons[0].type).toEqual('susp');
        expect(reasons[0].value).toEqual('fischer');
        expect(reasons[0].enabled).toEqual(true);
        expect(reasons[0].orderCount).toEqual({total: 2, accept: 1, delegate: 0, reject: 1, pending: 0});
        expect(reasons[0].directCount).toEqual({total: 1, accept: 0, delegate: 0, reject: 1, pending: 0});
        expect(reasons[0].orders.length).toEqual(2);
        expect(reasons[0].orders[0].id).toEqual(1);
        expect(reasons[0].orders[0].action).toEqual('accept');
        expect(reasons[0].orders[0].matched).toEqual(true);
        expect(reasons[0].orders[0].reasons.length).toEqual(2);
        expect(reasons[0].orders[0].reasons[0].id).toEqual(1);
        expect(reasons[0].orders[0].reasons[1].id).toEqual(2);
        expect(reasons[0].orders[1].id).toEqual(2);
        expect(reasons[0].orders[1].action).toEqual('reject');
        expect(reasons[0].orders[1].matched).toEqual(true);
        expect(reasons[0].orders[1].reasons.length).toEqual(1);
        expect(reasons[0].orders[1].reasons[0].id).toEqual(1);

        expect(reasons[1].id).toEqual(2);
        expect(reasons[1].type).toEqual('susp');
        expect(reasons[1].value).toEqual('mueller');
        expect(reasons[1].enabled).toEqual(true);
        expect(reasons[1].orderCount).toEqual({total: 2, accept: 1, delegate: 0, reject: 0, pending: 1});
        expect(reasons[1].directCount).toEqual({total: 1, accept: 0, delegate: 0, reject: 0, pending: 1});
        expect(reasons[1].orders.length).toEqual(2);
        expect(reasons[1].orders[0].id).toEqual(1);
        expect(reasons[1].orders[0].action).toEqual('accept');
        expect(reasons[1].orders[0].matched).toEqual(true);
        expect(reasons[1].orders[0].reasons.length).toEqual(2);
        expect(reasons[1].orders[0].reasons[0].id).toEqual(1);
        expect(reasons[1].orders[0].reasons[1].id).toEqual(2);
        expect(reasons[1].orders[1].id).toEqual(3);
        expect(reasons[1].orders[1].action).toEqual(null);
        expect(reasons[1].orders[1].matched).toEqual(true);
        expect(reasons[1].orders[1].reasons.length).toEqual(1);
        expect(reasons[1].orders[1].reasons[0].id).toEqual(2);

        service.setReasonEnabled(reasons[0], false);

        expect(statistic.matchedOrderCount).toEqual(2);
        expect(reasons[0].enabled).toEqual(false);
        expect(reasons[0].orders[1].matched).toEqual(false);
        expect(reasons[1].directCount).toEqual({total: 2, accept: 1, delegate: 0, reject: 0, pending: 1});

        service.setReasonEnabled(reasons[1], false);

        expect(statistic.matchedOrderCount).toEqual(0);
        expect(reasons[0].orders[0].matched).toEqual(false);
        expect(reasons[0].directCount).toEqual({total: 2, accept: 1, delegate: 0, reject: 1, pending: 0});
        expect(reasons[1].orders[1].matched).toEqual(false);

        service.setReasonEnabled(reasons[0], true);

        expect(statistic.matchedOrderCount).toEqual(2);
        expect(reasons[0].enabled).toEqual(true);
        expect(reasons[0].orders[0].matched).toEqual(true);
        expect(reasons[0].orders[1].matched).toEqual(true);
        expect(reasons[1].directCount).toEqual({total: 1, accept: 0, delegate: 0, reject: 0, pending: 1});
        expect(reasons[1].orders[0].matched).toEqual(true);

        service.setReasonEnabled(reasons[1], true);

        expect(statistic.matchedOrderCount).toEqual(3);
        expect(reasons[0].directCount).toEqual({total: 1, accept: 0, delegate: 0, reject: 1, pending: 0});
        expect(reasons[1].orders[1].matched).toEqual(true);

    }));

    it('should filter unused orders and reasons', inject([RemoverService], (service: RemoverService) => {
        service.reasons$.subscribe(x => reasons = x);
        service.statistic$.subscribe(x => statistic = x);
        service.setRemoteData({
            reasons: [
                {id: 1, type: 'susp', value: 'fischer'},
                {id: 2, type: 'susp', value: 'mueller'}
            ],
            orders: [
                {id: 1, action: 'accept', reasons: [2]},
                {id: 2, action: 'reject', reasons: []}
            ]
        });

        expect(statistic.orderCount).toEqual(1);
        expect(statistic.matchedOrderCount).toEqual(1);
        expect(reasons.length).toEqual(1);
        expect(reasons[0].value).toEqual('mueller');
        expect(reasons[0].orders.length).toEqual(1);
        expect(reasons[0].orders[0].id).toEqual(1);
    }));

    it('should work with unknown reasons', inject([RemoverService], (service: RemoverService) => {
        service.reasons$.subscribe(x => reasons = x);
        service.statistic$.subscribe(x => statistic = x);
        service.setRemoteData({
            reasons: [
                {id: 1, type: 'susp', value: 'fischer'}
            ],
            orders: [
                {id: 1, action: 'accept', reasons: [1, 2]},
                {id: 2, action: 'reject', reasons: [2]}
            ]
        });

        expect(statistic.orderCount).toEqual(1);
        expect(statistic.matchedOrderCount).toEqual(1);
        expect(reasons.length).toEqual(1);
        expect(reasons[0].value).toEqual('fischer');
        expect(reasons[0].orders.length).toEqual(1);
        expect(reasons[0].orders[0].id).toEqual(1);
    }));

});
