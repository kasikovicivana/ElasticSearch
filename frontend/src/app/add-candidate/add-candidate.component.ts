import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { FileUploader } from 'ng2-file-upload';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs';
import { DocumentDto } from '../model/job-application.model';

@Component({
  selector: 'app-add-candidate',
  templateUrl: './add-candidate.component.html',
  styleUrls: ['./add-candidate.component.css']
})
export class AddCandidateComponent {
  constructor(private router: Router,
    private _formBuilder: FormBuilder,
    private http: HttpClient
    ) { }

    url =  environment.backendUrl + 'api/index';
    uploader: FileUploader = new FileUploader({url: this.url})
    selectedFile: File | null = null;

    lawChecked: boolean = true; // Set Law checkbox to true by default
    contractChecked: boolean = false;
    resultsFetched: boolean = false;
    res = {filename: "", name: "", surname: "", address: "", govName: "", govLevel: "", content: ""}

  onCheckboxChange(checkedBox: string): void {
    if (checkedBox === 'law') {
      this.contractChecked = !this.lawChecked; // Uncheck Contract if Law is checked
    } else if (checkedBox === 'contract') {
      this.lawChecked = !this.contractChecked; // Uncheck Law if Contract is checked
    }
  }
    onSubmit() {
      const dto = new DocumentDto(this.contractChecked, new FileUploader({url: this.url}));
      if (dto.document && this.selectedFile) {
        const formData: FormData = new FormData();
        formData.append('file', this.selectedFile, this.selectedFile.name);
        formData.append('isContract', String(this.contractChecked));

        this.http.post(this.url, formData).subscribe(d => {
          console.log(d)
          this.res.filename = (d as any)["serverFilename"];
          this.res.name = (d as any)["signatoryName"];
          this.res.surname = (d as any)["signatoryLastname"];
          this.res.address = (d as any)["address"];
          this.res.govName = (d as any)["governmentName"];
          this.res.govLevel = (d as any)["governmentLevel"];
          this.res.content = (d as any)["content"];
          this.resultsFetched = true;
          // this.router.navigate([''])
        },
          err => {console.log(err)});
      }else{
        alert('All field are required!')
        return;
      }
    }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }


  getCoordinates(cityName: string) {
      const url = `https://nominatim.openstreetmap.org/search?q=${cityName}&format=json&limit=1`;

      return this.http.get(url).pipe(
        map((response: any) => {
          if (response && response.length) {
            const result = response[0];
            return {
              latitude: result.lat,
              longitude: result.lon
            };
          } else {
            return null;
          }
        })
      );
    }
}
