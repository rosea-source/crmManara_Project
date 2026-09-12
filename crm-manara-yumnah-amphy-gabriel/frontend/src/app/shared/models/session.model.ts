import { Activite } from './activite.model';
import { Animateur } from './animateur.model';

export interface Session {
  id: number;

  activite: Activite;
  animateur: Animateur;

  dateDebut: string;
  dateFin?: string;

  heureDebut: string;
  heureFin: string;

  lieu?: string;
  capaciteMax: number;

  statut: 'Prévue' | 'En cours' | 'Terminée' | 'Annulée' | 'Fermée';

  // frontend only (calculé)
  inscrits?: number;
}