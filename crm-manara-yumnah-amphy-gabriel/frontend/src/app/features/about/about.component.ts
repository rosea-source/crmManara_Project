// src/app/features/about/about.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterModule, FooterComponent, NavbarComponent],
  templateUrl: './about.component.html'
})
export class AboutComponent {

  valeurs = [
    { icon: 'bi-heart-fill',   titre: 'Bienveillance', desc: 'Chaque enfant est accueilli avec chaleur, respect et attention individuelle.' },
    { icon: 'bi-shield-check', titre: 'Sécurité',      desc: 'Un environnement sécurisant et inclusif où chaque enfant peut s\'épanouir.' },
    { icon: 'bi-stars',        titre: 'Excellence',    desc: 'Des programmes enrichissants animés par des professionnels qualifiés.' },
    { icon: 'bi-people-fill',  titre: 'Communauté',    desc: 'Tisser des liens forts entre familles, animateurs et enfants.' },
  ];

  equipe = [
    { prenom: 'Yumnah', nom: 'Hamphy', role: 'Directrice générale',       initiales: 'YH', color: '#c0392b' },
    { prenom: 'Karim',  nom: 'Benali', role: 'Responsable des activités', initiales: 'KB', color: '#1a2332' },
    { prenom: 'Sarah',  nom: 'Dupont', role: 'Coordinatrice pédagogique', initiales: 'SD', color: '#f0a500' },
  ];
}