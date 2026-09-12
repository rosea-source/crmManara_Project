// src/app/shared/components/sidebar-parent/sidebar-parent.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { SettingsService } from '../../../core/services/settings.service';

@Component({
  selector: 'app-sidebar-parent',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-parent.component.html'
})
export class SidebarParentComponent implements OnInit {

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