import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  private themeSubject = new BehaviorSubject<'light' | 'dark'>('light');
  theme$ = this.themeSubject.asObservable();

  private sidebarSubject = new BehaviorSubject<boolean>(false);
  sidebar$ = this.sidebarSubject.asObservable();

  private animationSubject = new BehaviorSubject<boolean>(true);
  animations$ = this.animationSubject.asObservable();

  constructor() {
    this.loadFromStorage();
  }

  // COLOR
  setColor(color: string) {
    document.documentElement.style.setProperty('--primary-accent', color);
    localStorage.setItem('manara-color', color);
  }

  // THEME
  setTheme(theme: 'light' | 'dark') {
    this.themeSubject.next(theme);

    if (theme === 'dark') {
      document.body.setAttribute('data-theme', 'dark');
    } else {
      document.body.removeAttribute('data-theme');
    }

    localStorage.setItem('manara-theme', theme);
  }

  // SIDEBAR
  setSidebar(compact: boolean) {
    this.sidebarSubject.next(compact);
    localStorage.setItem('manara-sidebar', compact ? 'compact' : 'normal');
  }

  // ANIMATIONS
  setAnimations(enabled: boolean) {
    this.animationSubject.next(enabled);
    localStorage.setItem('manara-animations', enabled ? 'on' : 'off');

    if (enabled) {
      document.body.classList.remove('no-animations');
    } else {
      document.body.classList.add('no-animations');
    }
  }

  // LOAD
  private loadFromStorage() {
    const savedColor = localStorage.getItem('manara-color');
    if (savedColor) this.setColor(savedColor);

    const savedTheme = localStorage.getItem('manara-theme') as 'light' | 'dark';
    if (savedTheme) this.setTheme(savedTheme);

    const savedSidebar = localStorage.getItem('manara-sidebar');
    if (savedSidebar === 'compact') this.setSidebar(true);

    const savedAnimations = localStorage.getItem('manara-animations');
    if (savedAnimations === 'off') this.setAnimations(false);
  }
}