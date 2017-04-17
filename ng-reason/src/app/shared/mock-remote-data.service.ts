import { RemoteData } from './remote-data.model';
import { Injectable } from '@angular/core';
import { Observable, Subject, ReplaySubject } from 'rxjs';

@Injectable()
export class MockRemoteDataService {

    private dataSubject: Subject<RemoteData> = new ReplaySubject<RemoteData>(1);

    constructor() {
    }

    get data$(): Observable<RemoteData> {
        return this.dataSubject.asObservable();
    }

    loadData(): void {
        this.dataSubject.next({
            orders: [],
            reasons: []
        });
    }

}
