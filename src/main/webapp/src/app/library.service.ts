import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LibraryService {

  constructor(private http: HttpClient) {
  }

  getBooks(word) :Observable<any>{
      return this.http.get("http://gutenberg.eu-west-3.elasticbeanstalk.com/api/search/"+word);
  }
}
