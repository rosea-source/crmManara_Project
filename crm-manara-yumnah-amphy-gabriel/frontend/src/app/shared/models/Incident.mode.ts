export interface Incident {
  id: number;
  enfantNomComplet: string;
  activiteTitre: string;
  description: string;
  gravite: string;
  dateHeure: string; // LocalDateTime arrive en ISO string
  vuParParent: boolean;
}