export class StreetModel {

	public id:number;
	public name:string;
  public platformAccountGroupIds:string;
  public groupNames:string;
	public country:string;
  public country_flag:string;
	public description:string;
  public zip:string;

  constructor(
		id?: number,
		name?: string,
    platformAccountGroupIds?: string,
    groupNames?: string,
		country?: string,
    country_flag?: string,
		description?: string,
    zip?: string
		) {}
}
