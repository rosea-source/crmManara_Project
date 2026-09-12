export interface User {
  id?: number;
  prenom: string;
  nom: string;
  email: string;
  password: string;
  role?: string;
  telephone?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  role: string;
  prenom: string;
}