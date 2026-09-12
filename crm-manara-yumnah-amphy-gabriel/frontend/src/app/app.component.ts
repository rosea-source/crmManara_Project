import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { SettingsService } from './core/services/settings.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  template: `<router-outlet></router-outlet>`
})
export class AppComponent implements OnInit {

  private authService = inject(AuthService);
  private settings = inject(SettingsService);
  

  ngOnInit() {
    this.authService.loadUser();
    // just injecting settings triggers loadFromStorage()
  }
}