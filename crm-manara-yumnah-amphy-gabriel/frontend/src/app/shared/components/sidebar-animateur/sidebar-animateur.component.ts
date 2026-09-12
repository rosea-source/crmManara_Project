// src/app/shared/components/sidebar-animateur/sidebar-animateur.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { SettingsService } from '../../../core/services/settings.service';

@Component({
  selector: 'app-sidebar-animateur',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-animateur.component.html'
})
export class SidebarAnimateurComponent implements OnInit {

  private auth = inject(AuthService);
  private settings = inject(SettingsService);

  isCompact = false;

  ngOnInit() {
    this.settings.sidebar$.subscribe(v => this.isCompact = v);
  }

  toggleCompact() {
    this.settings.setSidebar(!this.isCompact);
  }

  logout() {
    this.auth.logout();
  }
}