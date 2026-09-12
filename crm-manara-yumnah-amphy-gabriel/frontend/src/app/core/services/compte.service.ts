// src/app/core/services/compte.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CompteDTO {
  prenom: string;
  nom: string;
  email: string;
  role: string;
  telephone?: string;
  adresse?: string;       // parent seulement
  diplome?: string;       // animateur seulement
  specialite?: string;    // animateur seulement
  dateEmbauche?: string;  // animateur seulement (lecture seule)
}

export interface CompteUpdateRequest {
  prenom: string;
  nom: string;
  email: string;
  telephone?: string;
  motDePasse?: string;
  motDePasseConf?: string;
  adresse?: string;
  diplome?: string;
  specialite?: string;
}

@Injectable({ providedIn: 'root' })
export class CompteService {

  private apiUrl = 'http://localhost:8080/api/compte';

  constructor(private http: HttpClient) {}

  getCompte(): Observable<CompteDTO> {
    return this.http.get<CompteDTO>(this.apiUrl);
  }

  updateCompte(data: CompteUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/update`, data);
  }
}