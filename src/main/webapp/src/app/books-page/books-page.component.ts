import {Component, OnInit} from '@angular/core';
import {LibraryService} from "../library.service";
import {navigator} from "angular-bootstrap-md/lib/free/utils/facade/browser";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-books-page',
  templateUrl: './books-page.component.html',
  styleUrls: ['./books-page.component.scss']
})
export class BooksPageComponent implements OnInit {
  private elements: any[];
  loading: boolean;
  headElements: ["ID"];

  constructor(private libraryService: LibraryService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      let value = params['value'];
      this.loading = true;
      this.libraryService.getBooks(value).subscribe(next => {
        this.elements = next.map(e => this.mapToBook(e))
        ;this.loading = false;
      });
    })

  }

  mapToBook(e) {
    e.bookId = e.bookId.substring(0, e.bookId.indexOf('.'));
    if (e.bookId.indexOf("-0") != -1 )
    e.bookId = e.bookId.substring(0, e.bookId.indexOf('-0'));
    return e;
  }
}
