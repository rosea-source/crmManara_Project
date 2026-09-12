import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { SettingsService } from '../../../core/services/settings.service';

@Component({
  selector: 'app-sidebar-admin',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-admin.component.html'
})
export class SidebarAdminComponent implements OnInit {

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