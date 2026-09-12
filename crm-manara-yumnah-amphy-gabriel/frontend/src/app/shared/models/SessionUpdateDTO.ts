export interface SessionUpdateDTO {
  id: number;
  lieu?: string;
  dateDebut: string;
  dateFin?: string;
  heureDebut: string;
  heureFin: string;
  capaciteMax?: number;
  statut: string;
}