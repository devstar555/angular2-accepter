export class EmailModel {
	public id:number;
	public name:string;
  public platformAccountGroupIds:string;
  public groupNames:string;
	public description:string;
	constructor(
			id?: number,
			name?: string,
      platformAccountGroupIds?: string,
      groupNames?: string,
			description?: string
	) { }
}
