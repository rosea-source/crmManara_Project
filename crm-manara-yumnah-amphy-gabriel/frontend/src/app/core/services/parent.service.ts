// src/app/core/services/parent.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ─── DTOs ──────────────────────────────────────────────────────────────────

export interface EnfantDTO {
  id?: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  allergies?: string;
  notesMedicales?: string;
}

export interface ActiviteDTO {
  id: number;
  titre: string;
  description?: string;
}

export interface SessionDTO {
  id: number;
  activite: ActiviteDTO;
  dateDebut: string;
  dateFin: string;
  heureDebut?: string;   
  heureFin?: string;     
  lieu?: string;         
  statut?: string;       
  capacite?: number;     
  capaciteMax?: number;  
}

export interface InscriptionDTO {
  id: number;
  enfant: EnfantDTO;
  session: SessionDTO;
  statutPaiement: string;
}
export interface AnimateurSimpleDTO {
  prenom: string;
  nom: string;
  email: string;
}
export interface MessageEnfantDTO {
  id: number;
  contenu: string;
  dateEnvoi: string;
  lu: boolean;
  fichierNom?: string;
  fichierChemin?: string;
  session?: {
    id: number;
    activite: { id: number; titre: string; };
  };
  animateur?: AnimateurSimpleDTO;
}

export interface MessagesEnfantDTO {
  enfant: EnfantDTO;
  messages: MessageEnfantDTO[];
  sessions: SessionDTO[];
  totalPresences: number;
  totalAbsences: number;
  totalSessions: number;
}

export interface IncidentDTO {
  id: number;
  description: string;
  date: string;
  enfantPrenom: string;
  vuParParent: boolean;
}

export interface DashboardDTO {
  email: string;
  nbr_enfants: number;
  nbr_inscription: number;
  enfants: EnfantDTO[];
  inscriptions: InscriptionDTO[];
}



export interface InscriptionRequest {
  sessionId: number;
  enfantId: number;
}

// ─── Service ───────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class ParentService {

  private apiUrl = 'http://localhost:8080/api/parent';

  constructor(private http: HttpClient) {}

  // ── Dashboard ─────────────────────────────────────────────────────────────

  getDashboard(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(`${this.apiUrl}/dashboard`);
  }

  // ── Enfants ───────────────────────────────────────────────────────────────

  getChildren(): Observable<EnfantDTO[]> {
    return this.http.get<EnfantDTO[]>(`${this.apiUrl}/children`);
  }

  addChild(enfant: EnfantDTO): Observable<EnfantDTO> {
    return this.http.post<EnfantDTO>(`${this.apiUrl}/children`, enfant);
  }

  updateChild(id: number, enfant: EnfantDTO): Observable<EnfantDTO> {
    return this.http.put<EnfantDTO>(`${this.apiUrl}/children/${id}`, enfant);
  }

  // ── Activités ─────────────────────────────────────────────────────────────

  getActivites(): Observable<ActiviteDTO[]> {
    return this.http.get<ActiviteDTO[]>(`${this.apiUrl}/activities`);
  }

  getSessionsByActivite(activiteId: number): Observable<SessionDTO[]> {
    return this.http.get<SessionDTO[]>(`${this.apiUrl}/activities/${activiteId}/sessions`);
  }

  getRegistrationData(sessionId: number): Observable<{ session: SessionDTO; enfants: EnfantDTO[] }> {
    return this.http.get<{ session: SessionDTO; enfants: EnfantDTO[] }>(
      `${this.apiUrl}/activities/sessions/${sessionId}/register`
    );
  }

  registerToSession(request: InscriptionRequest): Observable<InscriptionDTO> {
    return this.http.post<InscriptionDTO>(`${this.apiUrl}/activities/register`, request);
  }

  // ── Planning ──────────────────────────────────────────────────────────────

  getPlanning(): Observable<{ enfants: EnfantDTO[]; inscriptions: InscriptionDTO[] }> {
    return this.http.get<{ enfants: EnfantDTO[]; inscriptions: InscriptionDTO[] }>(
      `${this.apiUrl}/planning`
    );
  }

  // ── Messages enfant ───────────────────────────────────────────────────────

  getMessagesEnfant(enfantId: number): Observable<MessagesEnfantDTO> {
    return this.http.get<MessagesEnfantDTO>(`${this.apiUrl}/enfants/${enfantId}/messages`);
  }

  // ── Incidents ─────────────────────────────────────────────────────────────

  getIncidents(): Observable<IncidentDTO[]> {
    return this.http.get<IncidentDTO[]>(`${this.apiUrl}/incidents`);
  }

  contactAdmin(enfantId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/contact-admin/${enfantId}`, {});
  }
  deleteChild(id: number): Observable<any> {
  return this.http.delete(`${this.apiUrl}/children/${id}`);
  }
}