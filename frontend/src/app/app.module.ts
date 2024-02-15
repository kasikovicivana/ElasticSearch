import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field'; 
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { FileUploadModule } from 'ng2-file-upload';
import { MatTableModule } from '@angular/material/table';
import { DownloadFileComponent } from './download-file/download-file.component';
import { AppNavbarComponent } from './app-navbar/app-navbar.component';
import { SearchComponentComponent } from './search-component/search-component.component';
import { AddCandidateComponent } from './add-candidate/add-candidate.component';
import { GeolocationSearchComponent } from './geolocation-search/geolocation-search.component';

@NgModule({
    declarations: [
        AppComponent,
        DownloadFileComponent,
        AppNavbarComponent,
        SearchComponentComponent,
        AddCandidateComponent,
        GeolocationSearchComponent,
    ],
    providers: [],
    bootstrap: [AppComponent],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatRadioModule,
        MatSelectModule,
        MatIconModule,
        MatInputModule,
        BrowserAnimationsModule,
        HttpClientModule,
        FileUploadModule,
        MatTableModule,
        
    ]
})
export class AppModule { }
