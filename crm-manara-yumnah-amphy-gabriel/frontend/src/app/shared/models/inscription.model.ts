import { Enfant } from './enfant.model';
import { Session } from './session.model';

export interface Inscription {
  id: number;
  enfant: Enfant;
  session: Session;
  statutPaiement: string;
}