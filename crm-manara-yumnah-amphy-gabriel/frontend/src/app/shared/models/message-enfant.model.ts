import { Session } from './session.model';
import { Enfant } from './enfant.model';
import { Animateur } from './animateur.model';

export interface MessageEnfant {
  id: number;
  contenu: string;
  fichierNom?: string;
  fichierChemin?: string;
  dateEnvoi: string;
  session: Session;
  enfant?: Enfant;
  animateur: Animateur;
  lu: boolean;
}