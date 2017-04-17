import { ModuleWithProviders }  from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';

import { AllComponent } from './components/filter/all/all.component';
import { PersonComponent } from './components/filter/person/person.component';
import { CompanyComponent } from './components/filter/company/company.component';
import { StreetComponent } from './components/filter/street/street.component';
import { EmailComponent } from './components/filter/email/email.component';
import { PhoneComponent } from './components/filter/phone/phone.component';

import { PlatformComponent } from './components/platform_group/platform.component';
import { ReasonComponent } from './components/reason/reason.component'
import {TestDataSetComponent} from "./components/test-data-set/test-data-set.component";

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'filters/person', component: PersonComponent,
    children: [{path: '', loadChildren: './components/filter/person/person.module#PersonModule'}]
  },
  { path: 'filters/company', component: CompanyComponent,
    children: [{path: '', loadChildren: './components/filter/company/company.module#CompanyModule'}]
  },
  { path: 'filters/street', component: StreetComponent,
    children: [{path: '', loadChildren: './components/filter/street/street.module#StreetModule'}]
  },
  { path: 'filters/phone', component: PhoneComponent,
    children: [{path: '', loadChildren: './components/filter/phone/phone.module#PhoneModule'}]
  },
  { path: 'filters/email', component: EmailComponent,
    children: [{path: '', loadChildren: './components/filter/email/email.module#EmailModule'}]
  },
  { path: 'filters/all', component: AllComponent,
    children: [{path: '', loadChildren: './components/filter/all/all.module#AllModule'}]
  },
  { path: 'platform', component: PlatformComponent,
    children: [{path: '', loadChildren: './components/platform_group/platform.module#PlatformModule'}]
  },
  {path: 'testdataset', component: TestDataSetComponent},
  { path: 'reason', component: ReasonComponent}
];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes);
