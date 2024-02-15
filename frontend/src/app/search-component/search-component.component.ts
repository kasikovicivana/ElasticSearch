import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

export interface QueryUnit {
  position: number;
  field: string;
  value: string;
  searchType: string;
}





@Component({
  selector: 'app-search-component',
  templateUrl: './search-component.component.html',
  styleUrls: ['./search-component.component.css']
})
export class SearchComponentComponent implements OnInit{


  ELEMENT_DATA: QueryUnit[] = [
    {position: 1, field: '', value: "", searchType: 'and'},
  ];
  results: any[] = [];
  noResponseAdvSearch: boolean = false;

  educationSelected: boolean=false;

  city: string = '';


  geoResults: any[] = [];
  thefile: any;
  noResponseGeoSearch: boolean = false;
  srcIFrame1: string = ''
  constructor(private router: Router,
              private http: HttpClient,
              private sanitizer: DomSanitizer) { }


  ngOnInit(): void {
    this.results = [];
    this.geoResults = [];
    this.noResponseAdvSearch=false;


  }

  selected(){
    this.educationSelected=true;
  }


searching() {



  let queryData = []
  for (let data of this.ELEMENT_DATA) {
    if (this.ELEMENT_DATA.length == 1) {
      data.searchType = "AND"
    }

    if (data.value == '' || data.searchType == '' || data.field=='') {
      alert('Greska')
      return;
    }
    let body = {
        "field": this.getField(data.field),
        "value": data.value,
        "operator": data.searchType,
      }
      queryData.push((body))
  }

  console.log(queryData)
  const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  }

  this.http.post<any[]>(environment.backendUrl + 'api/search', queryData, httpOptions).subscribe(data => {
    this.results = data
    console.log(data)
    if (this.results.length == 0) {
      this.noResponseAdvSearch = true;
    } else {
      this.noResponseAdvSearch = false;
    }
  })
}

getField(fieldData: string) {
  if (fieldData == 'signatoryName') {
    return "signatory_name";
  }
  if (fieldData == 'signatoryLastname') {
    return "signatory_lastname";
  }
  if (fieldData == 'governmentName') {
    return "government_name";
  }
  if (fieldData == 'content') {
    return "content_sr";
  }
  if (fieldData == 'governmentLevel') {
    return "government_level";
  }
  return '';
}

addRow(){
  console.log("Add elemnt");
  if (this.ELEMENT_DATA.length==0){
    this.ELEMENT_DATA.push({position: this.ELEMENT_DATA.length+1, field: '', value: "", searchType: 'and'})
  }else{
    this.ELEMENT_DATA.push({position: this.ELEMENT_DATA.length+1, field: '', value: "", searchType: ''})
  }

  console.log(this.ELEMENT_DATA);
}

deleteRow(position:number){
  this.ELEMENT_DATA=this.ELEMENT_DATA.filter(element=>element.position!=position)
  console.log(this.ELEMENT_DATA);
}

sortBy() {
  return this.ELEMENT_DATA.sort((a, b) => a.position > b.position ? 1 : a.position === b.position ? 0 : -1);
}

download(filename: string) {
  console.log(filename)

  this.http.get(environment.backendUrl + `api/file/${filename}`,{
    headers: new HttpHeaders({'Content-Type': 'application/json' }),
    responseType: 'blob' as 'json'
  })
    .subscribe(data => {
      console.log(data)
      const url = window.URL.createObjectURL(data as Blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
    });
}
}



