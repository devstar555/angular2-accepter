import {NgModule}  from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AddformComponent} from '../../addform/addform.component';
import {EditformComponent} from '../../editform/editform.component';

const routes: Routes = [
  { path: '',
    children: [
      { path: 'add', component: AddformComponent, data: {type: 'person'}},
      { path: 'edit', component: EditformComponent, data: {type: 'person'}}
    ]
  }
];

@NgModule ({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})

export class PersonRoutingModule {}
