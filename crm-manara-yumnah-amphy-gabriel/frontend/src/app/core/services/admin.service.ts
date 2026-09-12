import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Activite } from '../../shared/models/activite.model';
import { Session } from '../../shared/models/session.model';
import { Inscription } from '../../shared/models/inscription.model';
import { User } from '../../shared/models/user.model';
import { SessionDetailDTO } from '@shared/models/SessionDetailDTO';
import { SessionUpdateDTO } from '@shared/models/SessionUpdateDTO';
import { Event } from '../../shared/models/event.model'; 

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}
  getDashboard(): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/dashboard`);
  }
  // ================= SESSIONS =================

  getSessions(): Observable<Session[]> {
    return this.http.get<Session[]>(`${this.apiUrl}/sessions`);
  }

 getSession(id: number): Observable<SessionDetailDTO> {
  return this.http.get<SessionDetailDTO>(`${this.apiUrl}/sessions/${id}`);
  }

  createSession(session: any): Observable<any>  {
    return this.http.post<Session>(`${this.apiUrl}/sessions`, session);
  }

 updateSession(id: number, payload: SessionUpdateDTO) {
  return this.http.put(`${this.apiUrl}/sessions/${id}`, payload);
}
  deleteSession(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/sessions/${id}`);
  }
 getSessionsList(): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/sessions/list`);
  }

getAnimateurs(): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/animateurs/list`);
}

getSessionById(id: number): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/sessions/list`).pipe(
    map((sessions: any[]) => sessions.find(s => s.id === id))
  );
}
getAttentes(sessionId: number): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/sessions/${sessionId}/attentes`);
}

getInscrits(sessionId: number): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/sessions/${sessionId}/inscrits`);
}

acceptInscription(id: number): Observable<any> {
  return this.http.post(`${this.apiUrl}/inscriptions/${id}/accept`, {});
}

refuseInscription(id: number): Observable<any> {
  return this.http.post(`${this.apiUrl}/inscriptions/${id}/refuse`, {});
}

retirerInscription(id: number): Observable<any> {
  return this.http.delete<void>(`${this.apiUrl}/inscriptions/${id}/retirer`);
}

  // ================= ACTIVITES =================

  getActivites(): Observable<Activite[]> {
    return this.http.get<Activite[]>(`${this.apiUrl}/activites`);
  }

  createActivite(a: Activite): Observable<Activite> {
    return this.http.post<Activite>(`${this.apiUrl}/activites`, a);
  }

  updateActivite(id: number, a: Activite): Observable<Activite> {
    return this.http.put<Activite>(`${this.apiUrl}/activites/${id}`, a);
  }

  deleteActivite(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/activites/${id}`);
  }

  // ================= INSCRIPTIONS =================

  getInscriptions(): Observable<Inscription[]> {
    return this.http.get<Inscription[]>(`${this.apiUrl}/inscriptions`);
  }

 

  // ================= USERS =================

  getUsersList(): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/users/list`);
}

getUserDetail(id: number): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/users/${id}/detail`);
}

createUser(user: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/users/create`, user);
}
deleteUser(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
}

updateUser(id: number, payload: any): Observable<any> {
  return this.http.put(`${this.apiUrl}/users/${id}/update`, payload);
}
  // ================= ÉVÉNEMENTS =================

  getEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/events`);
  }

  createEvent(event: Event): Observable<Event> {
    return this.http.post<Event>(`${this.apiUrl}/events`, event);
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/events/${id}`);
  }

  // ================= INCIDENTS =================

  getIncidents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/incidents`);
  }

  // ================= PLANNING =================

  /**
   * Récupère le planning hebdomadaire (sessions groupées par jour)
   */
  getPlanning(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/planning`);
  }

  /**
   * Pour les paramètres (optionnel si tu stockes en DB)
   */
  getSettings(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/settings`);
  }

}