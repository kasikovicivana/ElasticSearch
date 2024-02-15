import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddCandidateComponent } from './add-candidate/add-candidate.component';
import { DownloadFileComponent } from './download-file/download-file.component';
import { GeolocationSearchComponent } from './geolocation-search/geolocation-search.component';
import { SearchComponentComponent } from './search-component/search-component.component';

const routes: Routes = [


  {
    path: 'download/:filename',
    component: DownloadFileComponent,
  },
  {
    path: 'search',
    component: SearchComponentComponent,
  },
  {
    path: 'upload',
    component: AddCandidateComponent
  },
  {
    path: 'geolocation',
    component: GeolocationSearchComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]

})


export class AppRoutingModule { }
