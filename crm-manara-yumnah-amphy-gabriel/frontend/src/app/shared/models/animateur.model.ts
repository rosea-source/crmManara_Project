import { User } from './user.model';

export interface Animateur {
  id: number;
  user: User;
  diplome?: string;
  specialite?: string;
  dateEmbauche?: string;
}