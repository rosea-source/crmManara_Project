import { Inscription } from './inscription.model';

export interface Presence {
  id: number;
  inscription: Inscription;
  statut: string;
  noteAnimateur?: string;
}