import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarAdminComponent } from '../../../../shared/components/sidebar-admin/sidebar-admin.component';
import { SettingsService } from '../../../../core/services/settings.service';

@Component({
  selector: 'app-parametres',
  standalone: true,
  imports: [FormsModule, SidebarAdminComponent],
  templateUrl: './parametres.component.html'
})
export class ParametresComponent implements OnInit {

  private settings = inject(SettingsService);

  theme: 'light' | 'dark' = 'light';
  compactSidebar = false;
  animations = true; 
  saved = false;
  isCompact = false;
  isOpen = true;

  ngOnInit() {
    this.settings.theme$.subscribe(t => this.theme = t);
    this.settings.sidebar$.subscribe(s => this.compactSidebar = s);
    this.settings.animations$.subscribe(a => this.animations = a);
  }

  updateColor(color: string) {
    this.settings.setColor(color);
  }

  applyTheme() {
    this.settings.setTheme(this.theme);
  }

  toggleSidebar() {
    this.settings.setSidebar(this.compactSidebar);
  }

  toggleAnimations() {
    this.settings.setAnimations(this.animations);
  }

  saveSettings() {
    this.saved = true;
    setTimeout(() => this.saved = false, 2000);
  }
}