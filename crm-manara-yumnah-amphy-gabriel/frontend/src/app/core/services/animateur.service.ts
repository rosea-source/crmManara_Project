// src/app/core/services/animateur.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ─── Dashboard ───────────────────────────────────────────────────────────────
export interface AnimateurDashboardDTO {
  prenomAnimateur: string;
  sessionsActives: number;
  enfantsInscrits: number;
  prochaineSession: string;
}

// ─── Activités ────────────────────────────────────────────────────────────────
export interface ActiviteItemDTO {
  titre: string;
  contenuHtml: string;
  badgeLabel: string;
  iconClass: string;
  borderClass: string;
  badgeClass: string;
  dateHeure: string;
  heureLabel: string;
  detailsUrl: string;
}
export type ActivitesParJour = Record<string, ActiviteItemDTO[]>;

// ─── Présences ────────────────────────────────────────────────────────────────
export interface PresenceDTO {
  id: number;
  enfantNom: string;
  activiteTitre: string;
  sessionDate: string;        // ← champ exact retourné par le DTO Java
  statut: 'Présent' | 'Absent' | 'Retard';
  noteAnimateur: string;
}

export interface PresenceSaveRequest {
  inscriptionId: number;
  statut: string;
  note: string;
}

export interface PresenceItem {
  inscriptionId: number;
  nomComplet: string;
}

// ─── Sessions ────────────────────────────────────────────────────────────────
export interface SessionDTO {
  id: number;
  titre: string;
  dateDebut: string;
  dateFin?: string;
  heureDebut: string;
  heureFin: string;
  lieu?: string;
  statut: string;
}

// ─── Zone Enfant ─────────────────────────────────────────────────────────────
export interface EnfantSimpleDTO {
  id: number;
  nomComplet: string;
}

export interface MessageEnfantDTO {
  id: number;
  contenu: string;
  fichierNom?: string;
  fichierChemin?: string;
  dateEnvoi: string;
  enfantNom?: string;
  lu: boolean;
}

export interface ZoneEnfantDTO {
  sessionId: number;
  titreActivite: string;
  dateDebut: string;
  enfants: EnfantSimpleDTO[];
  messages: MessageEnfantDTO[];
}

// ─── Incidents ────────────────────────────────────────────────────────────────
export interface IncidentHistoryDTO {
  id: number;
  enfantNomComplet: string;
  activiteTitre: string;
  description: string;
  gravite: string;
  dateLabel: string;
  badgeClass: string;
  etatParentLabel: string;
  etatParentClass: string;
}

export interface EnfantOption {
  id: number;
  prenom: string;
  nom: string;
  sessionId: number;
}

export interface SessionOption {
  id: number;
  titre: string;
  dateDebut: string;
  heureDebut: string;
  lieu: string;
}

export interface IncidentFormDataDTO {
  enfants: EnfantOption[];
  sessions: SessionOption[];
}

export interface IncidentReportForm {
  enfantId: number;
  sessionId: number;
  description: string;
  gravite: string;
  destinataire: string;
}

// ─── Service ─────────────────────────────────────────────────────────────────
@Injectable({ providedIn: 'root' })
export class AnimateurService {

  private apiUrl = 'http://localhost:8080/api/animateur';

  constructor(private http: HttpClient) {}

  // Dashboard
  getDashboard(): Observable<AnimateurDashboardDTO> {
    return this.http.get<AnimateurDashboardDTO>(`${this.apiUrl}/dashboard`);
  }

  // Activités
  getActivites(): Observable<ActivitesParJour> {
    return this.http.get<ActivitesParJour>(`${this.apiUrl}/activites`);
  }

  // Présences
  getHistoriquePresences(): Observable<PresenceDTO[]> {
    return this.http.get<PresenceDTO[]>(`${this.apiUrl}/presences/historique`);
  }

  getParticipants(sessionId: number): Observable<PresenceItem[]> {
    return this.http.get<PresenceItem[]>(`${this.apiUrl}/sessions/${sessionId}/participants`);
  }

  savePresences(sessionId: number, presences: PresenceSaveRequest[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/sessions/${sessionId}/presences`, presences);
  }

  // Sessions
  getSessions(): Observable<SessionDTO[]> {
    return this.http.get<SessionDTO[]>(`${this.apiUrl}/sessions`);
  }

  // Zone enfant
  getZoneEnfant(sessionId: number): Observable<ZoneEnfantDTO> {
    return this.http.get<ZoneEnfantDTO>(`${this.apiUrl}/sessions/${sessionId}/zone-enfant`);
  }

  posterMessage(sessionId: number, formData: FormData): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/sessions/${sessionId}/zone-enfant/poster`, formData);
  }

  supprimerMessage(messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/messages/${messageId}`);
  }

  // Incidents
  getIncidentHistory(): Observable<IncidentHistoryDTO[]> {
    return this.http.get<IncidentHistoryDTO[]>(`${this.apiUrl}/incidents/history`);
  }

  getIncidentFormData(): Observable<IncidentFormDataDTO> {
    return this.http.get<IncidentFormDataDTO>(`${this.apiUrl}/incidents/form-data`);
  }

  reportIncident(form: IncidentReportForm): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/incidents/report`, form);
  }
  deleteIncident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/incidents/${id}`);
  }
}