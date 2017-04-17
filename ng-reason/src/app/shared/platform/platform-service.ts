import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import { Observable, Subject, ReplaySubject } from 'rxjs';
import { Config } from '../../config/config';
import { ILog, ILoggerFactory } from '../logger.service';
import 'rxjs/add/operator/map';

@Injectable()
export class PlatformService {
    static id = "platformService";
    private _logger: ILog;

    protected basePath:string;

    constructor(private http: Http,
                private config: Config) {
        this.basePath = this.config.api;
        this.reqOptions = new RequestOptions({ headers: new Headers({"Content-Type": "application/json; charset=UTF-8"}) });
    }

    private reqOptions:any;
    private platformData: Subject<any> = new ReplaySubject<any>(1);

    data: any;

    /**
     * [dataPlatform Account Group$ description]
     * @return {Observable<any>} [description]
     */
    get dataPlatform$(): Observable<any> {
        return this.platformData.asObservable();
    }

    /**
     * Reset data
     */
    public resetPlatformData(): void {
      delete this.platformData;
      this.platformData = new ReplaySubject<any>(1);
    }

    /**
     * Load platform data
     */
    platformLoadData() {
        this.http.get(this.basePath + 'platformAccountGroups', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
            this.platformData.next(data);
        });
    }

    /**
     * [addPlatformData description]
     * @param  {[type]}          addPlatformData [description]
     * @return {Observable<any>}                     [description]
     */
    platformAddData(addPlatformData): Observable<any> {
        let data = JSON.stringify(addPlatformData);
        return this.http.post(this.basePath + 'platformAccountGroups', data, this.reqOptions)
            .map(x => x.json())
    }

    /**
     * [platformEditData description]
     * @param  {[type]}          editPlatformData [description]
     * @return {Observable<any>}                      [description]
     */
    platformEditData(editPlatformData): Observable<any> {
        let id = JSON.stringify(editPlatformData.id);
        let data = JSON.stringify(editPlatformData);
        return this.http.put(this.basePath + 'platformAccountGroups/'+id, data, this.reqOptions)
            .map(x => x);
    }

    /**
     * [platformDeleteData description]
     * @param  {[type]}          id [description]
     * @return {Observable<any>}    [description]
     */
    platformDeleteData(id): Observable<any> {
        return this.http.delete(this.basePath + "platformAccountGroups/"+id, this.reqOptions)
            .map(x => x);
    }
}
