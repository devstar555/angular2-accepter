export class TestModel {

  public id:number;
  public end:string;
  public start:string;
  public result:string;
  public testCases: {id:number, name:string, expected:string, actaul:string, result:string}[];

  constructor(
    id?: number,
    end?: string,
    start?: string,
    result?: string,
    testCases?: {id:number, name:string, expected:string, actaul:string, result:string}[]
  ) {}
}
