export class FilterModel {
	public id:number;
	public name:string;
	public country:string;
    public platformAccountGroupIds:string;
    public groupNames:string;
	public description:string;
  public filter:string;
	constructor(
			id?: number,
			name?: string,
      platformAccountGroupIds?: string,
      groupNames?: string,
			country?: string,
			description?: string,
      filter?: string
	) { }
}
