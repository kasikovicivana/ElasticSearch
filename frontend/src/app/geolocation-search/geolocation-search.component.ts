import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { map } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-geolocation-search',
  templateUrl: './geolocation-search.component.html',
  styleUrls: ['./geolocation-search.component.css']
})
export class GeolocationSearchComponent {

  city: string = '';
  radius?: number = undefined;
  geoResults: any[] = [];
  noResponseGeoSearch: boolean = false;

  constructor(private router: Router,
    private http: HttpClient,
    private sanitizer: DomSanitizer) { }

  findByGeolocation() {
    console.log(this.city + " " + this.radius)
    if (!this.city || !this.radius) {
      alert('Fields are required!')
      return;
    }

    this.getCoordinates(this.city).subscribe((coordinates) => {
      console.log(coordinates);
      if (!coordinates) {
        alert('Error while getting coordinates for city: ' + this.city )
        return;
      }
      let body = {
        "latitude": coordinates['latitude'],
        "longitude": coordinates['longitude'],
        "radius": this.radius
      }
      const httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
      }

      console.log(body)
      this.http.post<any[]>(environment.backendUrl + 'api/search/geolocation', JSON.stringify(body), httpOptions)
      .subscribe(data => {
        console.log(data)
        this.geoResults = data;
        if (this.geoResults.length == 0) {
          this.noResponseGeoSearch = true
        }
      })
    });
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
