import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface S124Dataset {
  id: number;
  mrn: string;
  uuid: string;
  createdAt: string;
  validFrom: string;
  validTo: string;
  dataProductVersion: string;
  geometryWkt: string;
  referencedDatasetIds: number[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class S124DatasetService {
  private apiUrl = '/api/s124-datasets';

  constructor(private http: HttpClient) { }

  getDatasets(page: number = 0, size: number = 20, sortBy: string = 'createdAt', sortDirection: string = 'DESC'): Observable<Page<S124Dataset>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.get<Page<S124Dataset>>(this.apiUrl, { params });
  }

  getDataset(id: number): Observable<S124Dataset> {
    return this.http.get<S124Dataset>(`${this.apiUrl}/${id}`);
  }

  getDatasetCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`);
  }
}