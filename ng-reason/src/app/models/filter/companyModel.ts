export class CompanyModel {
	public id:number;
	public name:string;
  public platformAccountGroupIds:string;
  public groupNames:string;
	public country:string;
  public zip:string;
  public country_flag:string;
	public description:string;
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
