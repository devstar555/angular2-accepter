import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HomeComponent }  from './index';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { DashboardService } from '../../shared/dashboard/dashboard-service';

@NgModule({
  declarations: [
    HomeComponent
  ],
  providers: [
  	DashboardService
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  bootstrap: [HomeComponent]
})
export class HomeModule { }
