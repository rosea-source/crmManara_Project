export interface SessionDetailDTO {
  id: number;
  lieu?: string;
  dateDebut: string;
  dateFin?: string;
  heureDebut: string;
  heureFin: string;
  capaciteMax: number;
  statut: string;

  activiteTitre: string;
  animateurNom: string;

  inscrits: number;
}