import { User } from './user.model';

export interface Parent {
  id: number;
  adresse?: string;
  user: User;
}