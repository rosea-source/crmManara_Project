import { Parent } from './parent.model';

export interface Enfant {
  id: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  parent: Parent;
  allergies?: string;
  notesMedicales?: string;
}