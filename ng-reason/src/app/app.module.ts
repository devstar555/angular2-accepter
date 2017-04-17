import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {Ng2TableModule} from 'ng2-table/ng2-table';
import {Ng2Bs3ModalModule} from 'ng2-bs3-modal/ng2-bs3-modal';
import {PaginationModule} from 'ng2-bootstrap/pagination';
import {TabsModule} from 'ng2-bootstrap/tabs';

import {HomeModule} from './components/home/home.module';
import {ReasonModule} from './components/reason/reason.module';

import {AppComponent} from './components/app.component';
import {FilterService} from './shared/filter/filter-service';
import {PlatformService} from './shared/platform/platform-service';
import {PublicService} from "./shared/public/public-service";

import {StreetComponent, CompanyComponent, PersonComponent, PhoneComponent, EmailComponent, AllComponent}  from './components/filter/index';
import {PlatformComponent} from "./components/platform_group/platform.component";

import {AcTableComponent} from "./components/ac-table/ac-table.component";
import {Config}  from './config/config';
import {routing} from './app.routes';
import {TestDataSetComponent} from "./components/test-data-set/test-data-set.component";
import {SharedModule} from "./shared/shared.module";
import {RlTagInputModule} from 'angular2-tag-input';
import {TestDataSetService} from "./shared/test-data-set/test-data-set.service";

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    HomeModule,
    ReasonModule,
    Ng2TableModule,
    PaginationModule.forRoot(),
    Ng2Bs3ModalModule,
    RlTagInputModule,
    routing
  ],
  providers: [
    Config,
    FilterService,
    PlatformService,
    PublicService,
    TestDataSetService
  ],
  declarations: [
    AppComponent,
    StreetComponent,
    CompanyComponent,
    PersonComponent,
    PhoneComponent,
    EmailComponent,
    AllComponent,
    AcTableComponent,
    PlatformComponent,
    TestDataSetComponent
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
