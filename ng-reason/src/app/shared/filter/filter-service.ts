import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import { Observable, Subject, ReplaySubject } from 'rxjs';
import { Config } from '../../config/config';
import { ILog, ILoggerFactory } from '../logger.service';
import 'rxjs/add/operator/map';

@Injectable()
export class FilterService {
    static id = "filterService";
    private _logger: ILog;

    protected basePath:string;

    constructor(private http: Http,
                private config: Config) {
        this.basePath = this.config.api;
        this.reqOptions = new RequestOptions({ headers: new Headers({"Content-Type": "application/json; charset=UTF-8"}) });
    }

    private reqOptions:any;
    private personData: Subject<any> = new ReplaySubject<any>(1);
    private companyData: Subject<any> = new ReplaySubject<any>(1);
    private streetData: Subject<any> = new ReplaySubject<any>(1);
    private phoneData: Subject<any> = new ReplaySubject<any>(1);
    private emailData: Subject<any> = new ReplaySubject<any>(1);
    private countryData: Subject<any> = new ReplaySubject<any>(1);
    private historyData: Subject<any> = new ReplaySubject<any>(1);
    private propertyData: Subject<any> = new ReplaySubject<any>(1);
    private zipcodeData: Subject<any> = new ReplaySubject<any>(1);
    private filterDataFromId: Subject<any> = new ReplaySubject<any>(1);

    data: any;

    /**
    * [dataZipCode Description]
    * @returns {Observable<any>}
    */
    get dataZipCode$(): Observable<any> {
        return this.zipcodeData.asObservable();
    }

    /**
    * [dataProperty$ description]
    * @returns {Observable<any>}
    */
    get dataProperty$(): Observable<any> {
        return this.propertyData.asObservable();
    }

    /**
     * [dataPerson$ description]
     * @return {Observable<any>} [description]
     */
    get dataPerson$(): Observable<any> {
        return this.personData.asObservable();
    }

    /**
     * [dataCompany$ description]
     * @return {Observable<any>} [description]
     */
    get dataCompany$(): Observable<any> {
        return this.companyData.asObservable();
    }

    /**
     * [dataStreet$ description]
     * @return {Observable<any>} [description]
     */
    get dataStreet$(): Observable<any> {
        return this.streetData.asObservable();
    }

    /**
     * [dataPhone$ description]
     * @return {Observable<any>} [description]
     */
    get dataPhone$(): Observable<any> {
      return this.phoneData.asObservable();
    }

    /**
     * [dataEmail$ description]
     * @return {Observable<any>} [description]
     */
    get dataEmail$(): Observable<any> {
      return this.emailData.asObservable();
    }

    /**
     * [dataCountry$ description]
     * @returns {Observable<any>}
     */
    get dataCountry$(): Observable<any> {
      return this.countryData.asObservable();
    }

    /**
     * [dataFilterHistory$ description]
     * @returns {Observable<any>}
     */
    get dataHistory$(): Observable<any> {
      return this.historyData.asObservable();
    }

    get dataFilterById$(): Observable<any> {
      return this.filterDataFromId.asObservable();
    }

    public resetFilterData(): void {
      this.personData = new ReplaySubject<any>(1);
      this.companyData = new ReplaySubject<any>(1);
      this.streetData = new ReplaySubject<any>(1);
      this.phoneData = new ReplaySubject<any>(1);
      this.emailData = new ReplaySubject<any>(1);
    }

    public resetFilterDataFromId(): void {
      this.filterDataFromId = new ReplaySubject<any>(1);
    }

    /**
    * Reset Property Data
    */
    public resetPropertyData(): void {
      this.propertyData = new ReplaySubject<any>(1);
    }

    /**
    * Reset ZipCode Data
    */
    public resetZipCodeData(): void {
      this.zipcodeData = new ReplaySubject<any>(1);
    }

     /**
     * Reset History Data
     */
    public resetHistoryData(): void {
      this.historyData = new ReplaySubject<any>(1);
    }

    /**
     * Reset person data
     */
    public resetPersonData(): void {
      this.personData = new ReplaySubject<any>(1);
    }

    /**
    * Reset Company data
    */
    public resetCompanyData(): void {
      this.companyData = new ReplaySubject<any>(1);
    }

    /**
     * Reset Street data
     */
    public resetStreetData(): void {
      this.streetData = new ReplaySubject<any>(1);
    }

    /**
     * Reset Phone data
     */
    public resetPhoneData(): void {
      this.phoneData = new ReplaySubject<any>(1);
    }

    /**
     * Reset Email data
     */
    public resetEmailData(): void {
      this.emailData = new ReplaySubject<any>(1);
    }

    /**
    * Load Property data
    */
    propertyLoadData() {
      this.http.get(this.basePath + 'properties', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
          this.propertyData.next(data);
        });
    }

    /**
    * Load ZipCode Data
    * @param countryCode
    * @param zip
     */
    zipcodeLoadData(countryCode, zip) {
      this.http.get(this.basePath + 'zipCodes/'+countryCode+'/'+zip, this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
          this.zipcodeData.next(data);
        });
    }

    /**
     * Load history data
     */
    historyLoadData(filterId) {
      this.http.get(this.basePath + 'filtersHistory/'+filterId, this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
          this.historyData.next(data);
        });
    }

    /**
     * Load person data
     */
    personLoadData() {
        this.http.get(this.basePath + 'filters/person', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
            this.personData.next(data);
        });
    }

    /**
     * Load phone data
     */
    phoneLoadData() {
      this.http.get(this.basePath + 'filters/phone', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
          this.phoneData.next(data);
        });
    }

    /**
     * Load company data
     */
    companyLoadData() {
        this.http.get(this.basePath + 'filters/company', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
            this.companyData.next(data);
        });
    }

    /**
     * Load street data
     */
    streetLoadData() {
        this.http.get(this.basePath + 'filters/street',this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
            this.streetData.next(data);
        });
    }

    /**
     * Load email data
     */
    emailLoadData() {
      this.http.get(this.basePath + 'filters/email', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
          this.emailData.next(data);
        });
    }

    /**
    * Load Country data
    */
    countryLoadData() {
      this.http.get(this.basePath + 'countries', this.reqOptions)
        .map(x => x.json())
        .subscribe(data => {
          this.countryData.next(data);
        });
    }

    /**
     * [AddData description]
     * @param  {[type]}          addPersonFilterData [description]
     * @return {Observable<any>}                     [description]
     */
    filterAddData(flag:number, addFilterData):Observable<any> {
      let data = JSON.stringify(addFilterData);
      if (flag == 0) {
        return this.http.post(this.basePath + 'filters/person', data, this.reqOptions)
          .map(x => x.json());
      } else if (flag == 1) {
        return this.http.post(this.basePath + 'filters/company', data, this.reqOptions)
          .map(x => x.json());
      } else if (flag == 2) {
        return this.http.post(this.basePath + 'filters/street', data, this.reqOptions)
          .map(x => x.json());
      } else if (flag == 3) {
        return this.http.post(this.basePath + 'filters/phone', data, this.reqOptions)
          .map(x => x.json());
      } else {
        return this.http.post(this.basePath + 'filters/email', data, this.reqOptions)
          .map(x => x.json());
      }
    }

    filterEditData(flag:number, editFilterData):Observable<any> {
      let id = JSON.stringify(editFilterData.id);
      let data = JSON.stringify(editFilterData);
      if (flag == 0) {
        return this.http.put(this.basePath + 'filters/person/'+id, data, this.reqOptions)
          .map(x => x);
      } else if (flag == 1) {
        return this.http.put(this.basePath + 'filters/company/'+id, data, this.reqOptions)
          .map(x => x);
      } else if (flag == 2) {
        return this.http.put(this.basePath + 'filters/street/'+id, data, this.reqOptions)
          .map(x => x);
      } else if (flag == 3) {
        return this.http.put(this.basePath + 'filters/phone/'+id, data, this.reqOptions)
          .map(x => x);
      } else {
        return this.http.put(this.basePath + 'filters/email/'+id, data, this.reqOptions)
          .map(x => x);
      }
    }

    filterGetDataById(id:string, flag:number) {
      if (flag == 0) {
        this.http.get(this.basePath + 'filters/person/' + id , this.reqOptions)
          .map(x => x.json())
          .subscribe(data => {
            this.filterDataFromId.next(data);
          });
      } else if (flag == 1) {
        this.http.get(this.basePath + 'filters/company/' + id , this.reqOptions)
          .map(x => x.json())
          .subscribe(data => {
            this.filterDataFromId.next(data);
          });
      } else if (flag == 2) {
        this.http.get(this.basePath + 'filters/street/' + id , this.reqOptions)
          .map(x => x.json())
          .subscribe(data => {
            this.filterDataFromId.next(data);
          });
      } else if (flag == 3) {
        this.http.get(this.basePath + 'filters/phone/' + id , this.reqOptions)
          .map(x => x.json())
          .subscribe(data => {
            this.filterDataFromId.next(data);
          });
      } else if (flag == 5) {
        this.http.get(this.basePath + 'platformAccountGroups/' + id , this.reqOptions)
          .map(x => x.json())
          .subscribe(data => {
            this.filterDataFromId.next(data);
          });
      } else {
        this.http.get(this.basePath + 'filters/email/' + id , this.reqOptions)
          .map(x => x.json())
          .subscribe(data => {
            this.filterDataFromId.next(data);
          });
      }
    }

    /**
     * [personAddData description]
     * @param  {[type]}          addPersonFilterData [description]
     * @return {Observable<any>}                     [description]
     */
    personAddData(addPersonFilterData): Observable<any> {
        let data = JSON.stringify(addPersonFilterData);
        return this.http.post(this.basePath + 'filters/person', data, this.reqOptions)
            .map(x => x.json())
    }

    /**
     * [companyAddData description]
     * @param  {[type]}          addCompanyFilterData [description]
     * @return {Observable<any>}                      [description]
     */
    companyAddData(addCompanyFilterData): Observable<any> {
        let body = JSON.stringify(addCompanyFilterData);
        return this.http.post(this.basePath + 'filters/company', body, this.reqOptions)
            .map(x => x.json());
    }

    /**
     * [streetAddData description]
     * @param  {[type]}          addStreetFilterData [description]
     * @return {Observable<any>}                     [description]
     */
    streetAddData(addStreetFilterData): Observable<any> {
        let body = JSON.stringify(addStreetFilterData);
        return this.http.post(this.basePath + 'filters/street',body,this.reqOptions)
            .map(x => x.json());
    }

    /**
     * [phoneAddData]
     * @param addPhoneFilterData
     * @returns {any}
     */
    phoneAddData(addPhoneFilterData): Observable<any> {
      let body = JSON.stringify(addPhoneFilterData);
      return this.http.post(this.basePath + 'filters/phone',body,this.reqOptions)
        .map(x => x.json());
    }

    /**
     * [emailAddData]
     * @param addEmailFilterData
     * @returns {any}
     */
    emailAddData(addEmailFilterData): Observable<any> {
      let body = JSON.stringify(addEmailFilterData);
      return this.http.post(this.basePath + 'filters/email',body,this.reqOptions)
        .map(x => x.json());
    }

    filtersAddData(addFiltersData): Observable<any> {
      let body = JSON.stringify(addFiltersData);
      return this.http.post(this.basePath + 'filters',body,this.reqOptions)
        .map(x => x.json());
    }

    /**
     * [personEditData description]
     * @param  {[type]}          editPersonFilterData [description]
     * @return {Observable<any>}                      [description]
     */
    personEditData(editPersonFilterData): Observable<any> {
        let id = JSON.stringify(editPersonFilterData.id);
        let data = JSON.stringify(editPersonFilterData);
        return this.http.put(this.basePath + 'filters/person/'+id, data, this.reqOptions)
            .map(x => x);
    }

    /**
     * [companyEditData description]
     * @param  {[type]}          editCompanyFilterData [description]
     * @return {Observable<any>}                       [description]
     */
    companyEditData(editCompanyFilterData): Observable<any> {
        let id = JSON.stringify(editCompanyFilterData.id);
        let data = JSON.stringify(editCompanyFilterData);
        return this.http.put(this.basePath + 'filters/company/'+id, data, this.reqOptions)
            .map(x => x);
    }

    /**
     * [streetEditData description]
     * @param  {[type]}          editStreetFilterData [description]
     * @return {Observable<any>}                      [description]
     */
    streetEditData(editStreetFilterData): Observable<any> {
        let id = JSON.stringify(editStreetFilterData.id);
        let data = JSON.stringify(editStreetFilterData);
        return this.http.put(this.basePath + 'filters/street/'+id, data, this.reqOptions)
            .map(x => x);
    }

    /**
     * [phoneEditData]
     * @param editPhoneFilterData
     * @returns {any}
     */
    phoneEditData(editPhoneFilterData): Observable<any> {
      let id = JSON.stringify(editPhoneFilterData.id);
      let data = JSON.stringify(editPhoneFilterData);
      return this.http.put(this.basePath + 'filters/phone/'+id, data, this.reqOptions)
        .map(x => x);
    }

    /**
     * [emailEditData]
     * @param editEmailFilterData
     * @returns {any}
     */
    emailEditData(editEmailFilterData): Observable<any> {
      let id = JSON.stringify(editEmailFilterData.id);
      let data = JSON.stringify(editEmailFilterData);
      return this.http.put(this.basePath + 'filters/email/'+id, data, this.reqOptions)
        .map(x => x);
    }

    /**
     * [personDeleteData description]
     * @param  {[type]}          id [description]
     * @return {Observable<any>}    [description]
     */
    personDeleteData(id): Observable<any> {
        return this.http.delete(this.basePath + "filters/person/"+id,this.reqOptions)
            .map(x => x);
    }

    /**
     * [companyDeleteData description]
     * @param  {[type]}          id [description]
     * @return {Observable<any>}    [description]
     */
    companyDeleteData(id): Observable<any> {
        return this.http.delete(this.basePath + "filters/company/"+id,this.reqOptions)
            .map(x => x);
    }

    /**
     * [streetDeleteData description]
     * @param  {[type]}          id [description]
     * @return {Observable<any>}    [description]
     */
    streetDeleteData(id): Observable<any>{
        return this.http.delete(this.basePath + "filters/street/"+id,this.reqOptions)
            .map(x => x);
    }

    /**
     * [phoneDeleteData]
     * @param id
     * @returns {any}
     */
    phoneDeleteData(id): Observable<any>{
      return this.http.delete(this.basePath + "filters/phone/"+id,this.reqOptions)
        .map(x => x);
    }

    /**
     * [emailDeleteData]
     * @param id
     * @returns {any}
     */
    emailDeleteData(id): Observable<any>{
      return this.http.delete(this.basePath + "filters/email/"+id,this.reqOptions)
        .map(x => x);
    }
}
