const enum LogType {
	Debug,
	Info,
	Warning,
	Error,
	Success
}

export interface ILog {
	debug(source: string, message?: string, data?: any): void;
	info(source: string, message?: string, data?: any): void;
	error(source: string, message?: string, data?: any): void;
	warn(source: string, message?: string, data?: any): void;
}

export interface ILoggerService {

	log(logType: LogType, message: string, data?: any);

}

export class LoggerService implements ILoggerService {
	static id = "loggerService";
	
	/*@ngInject*/
	constructor(private $log) {

	}

	log(logType: LogType, message: string, data?: any) {
		switch (logType) {

			case LogType.Debug:					
				if(data){
					this.$log.debug(message, data);
				}
				else {
					this.$log.debug(message);
				}
				break;
			case LogType.Info:					
				if(data){
					this.$log.info(message, data);
				}
				else {
					this.$log.info(message);
				}
				break;
			case LogType.Error:					
				if(data){
					this.$log.error(message, data);
				}
				else {
					this.$log.error(message);
				}
				break;
			case LogType.Warning:
				if(data){
					this.$log.warn(message, data);
				}
				else {
					this.$log.warn(message);
				}
				break;
			default:					
				if(data){
					this.$log.log(message, data);
				}
				else {
					this.$log.log(message);
				}
				break;
		}
	}
}

export default class Logger implements ILog {

	constructor(
		private sourceId: string,
		private loggerService: ILoggerService
		) {

	}

	debug(source: string, message?: string, data?: any) {
		this._log(this.sourceId, source, LogType.Debug, message, data);
	}

	info(source: string, message?: string, data?: any) {
		this._log(this.sourceId, source, LogType.Info, message, data);
	}

	error(source: string, message?: string, data?: any) {
		this._log(this.sourceId, source, LogType.Error, message, data);
	}

	warn(source: string, message?: string, data?: any) {
		this._log(this.sourceId, source, LogType.Warning, message, data);
	}

	private _log(sourceId: string, source: string, logType: LogType, message?: string, data?: any) {
		var msg = `[${sourceId}::${source}] ${message}`;
		this.loggerService.log(logType, msg, data);
	}

}

export interface ILoggerFactory {
	(sourceId: string): ILog;
}
