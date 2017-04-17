import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { TooltipModule } from 'ng2-bootstrap/tooltip';

import { RemoteDataService } from '../../shared/remote-data.service';
import { RemoverService } from '../../shared/remover.service';
import { ReasonComponent } from './index'

@NgModule({
  declarations: [
    ReasonComponent
  ],
  providers: [
    RemoteDataService,
    RemoverService
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    TooltipModule.forRoot()
  ],
  bootstrap: [ReasonComponent]
})
export class ReasonModule { }
