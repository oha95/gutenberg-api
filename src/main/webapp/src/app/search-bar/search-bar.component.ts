import { Component, OnInit } from '@angular/core';
import {LibraryService} from "../library.service";
import {Validators} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss']
})
export class SearchBarComponent implements OnInit {
  searchWord : string ;


  constructor(private libraryService: LibraryService,private router:Router) {
    console.log(this.searchWord);
  }

  ngOnInit() {
  }

  onSubmit() {
    this.router.navigate(["result"],{queryParams:{value : this.searchWord}});
  }

}
