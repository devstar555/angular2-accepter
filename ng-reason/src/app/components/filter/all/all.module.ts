import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {AllRoutingModule} from './all.routing';
import {SharedModule} from '../../../shared/shared.module';
import {Router} from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    HttpModule,
    AllRoutingModule,
    SharedModule
  ]
})

export class AllModule { }
