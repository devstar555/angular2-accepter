export class ZipCodeModel {

  public zip:string;
  public countryCode:string;
  public placeName:string;
  public latitude: number;
  public longitude: number;

  constructor(
    zip?: string,
    countryCode?: string,
    placeName?: string,
    latitude?: number,
    longitude?: number
  ) {}
}
