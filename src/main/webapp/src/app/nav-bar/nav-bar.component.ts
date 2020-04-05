import { Component, OnInit } from '@angular/core';
import {LibraryService} from "../library.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {
  search: string;


  constructor(private libraryService : LibraryService, private router:Router) { }

  ngOnInit(): void {
  }

  click() : void{
    console.log(this.search)
    this.router.navigate(["result"],{queryParams:{value : this.search}});
  }


}
