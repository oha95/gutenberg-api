import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {BooksPageComponent} from "./books-page/books-page.component";
import {SearchBarComponent} from "./search-bar/search-bar.component";

const routes: Routes = [
  {path: '', component: SearchBarComponent, pathMatch: 'full'},
  {path: 'result', component: BooksPageComponent}
];
@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
