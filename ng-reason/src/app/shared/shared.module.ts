import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import {RlTagInputModule} from 'angular2-tag-input';
import {Ng2MapModule} from 'ng2-map';
import { AddformComponent }  from '../components/addform/addform.component';
import { EditformComponent } from '../components/editform/editform.component';

@NgModule({
  declarations: [
    AddformComponent,
    EditformComponent,
  ],
  providers: [
  ],
  imports: [
    CommonModule,
    FormsModule,
    HttpModule,
    RlTagInputModule,
    Ng2MapModule,
  ],
  exports: [
    AddformComponent,
    EditformComponent
  ]
})

export class SharedModule { }
