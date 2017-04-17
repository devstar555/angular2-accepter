import { RemoteData } from './remote-data.model';
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Config } from '../config/config';
import { Observable, Subject, ReplaySubject } from 'rxjs';
import 'rxjs/add/operator/map';


@Injectable()
export class RemoteDataService {
    private dataSubject: Subject<RemoteData> = new ReplaySubject<RemoteData>( 1 );
    protected basePath:string;

    constructor(
      private http: Http,
      private config: Config
    ) {
      this.basePath = this.config.api;
    }

    get data$(): Observable<RemoteData> {
        return this.dataSubject.asObservable();
    }

    loadData( hours ): void {
        this.http.get(this.basePath + 'manualReviews/' + hours * 3600 ).map( x => x.json() ).subscribe( data => {
            this.dataSubject.next( data );
        });
    }

}
