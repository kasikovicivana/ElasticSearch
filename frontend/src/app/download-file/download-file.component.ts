import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-download-file',
  templateUrl: './download-file.component.html',
  styleUrls: ['./download-file.component.css']
})
export class DownloadFileComponent implements OnInit {
  filename: string = '';

  constructor(private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      let date = params['filename'];
      this.filename = date
      console.log(date); 
  });
  }

  ngOnInit(): void { 
  }

}
