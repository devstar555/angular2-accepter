export class PhoneModel {

	public id:number;
	public name:string;
	public description:string;
  public platformAccountGroupIds:string;
  public groupNames:string;
	constructor(
		id?: number,
		name?: string,
    platformAccountGroupIds?: string,
    groupNames?: string,
    description?: string
		) {}
}
