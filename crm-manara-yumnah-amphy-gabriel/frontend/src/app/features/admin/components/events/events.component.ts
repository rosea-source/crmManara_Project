import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AdminService } from '../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../shared/components/sidebar-admin/sidebar-admin.component';
import { Event } from '../../../../shared/models/event.model';
import {  ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './events.component.html'
})
export class EventsComponent implements OnInit {
  private adminService = inject(AdminService);
  private http = inject(HttpClient);
    private cdr = inject(ChangeDetectorRef);

  events: Event[] = [];
  showForm = false;
  loading = false;
  errorMessage = '';
  selectedFile: File | null = null;

  newEvent: Partial<Event> = {
    title: '',
    description: '',
    date: '',
    imageUrl: ''
  };

  ngOnInit() {
    this.loadEvents();
  }

  // events.component.ts
loadEvents() {
  this.adminService.getEvents().subscribe({
    next: (data) => {
      this.events = data;
      this.cdr.detectChanges(); // ← ajoute ça
    }
  });
}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (!this.newEvent.title || !this.newEvent.date) {
      this.errorMessage = 'Le titre et la date sont obligatoires.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      this.http.post<string>(
        'http://localhost:8080/api/admin/events/upload',
        formData,
        { withCredentials: true, responseType: 'text' as 'json' }
      ).subscribe({
        next: (imageUrl) => {
          this.newEvent.imageUrl = 'http://localhost:8080' + imageUrl;
          this.saveEvent();
        },
        error: () => {
          this.errorMessage = 'Erreur upload image';
          this.loading = false;
        }
      });
    } else {
      this.saveEvent();
    }
    }
    saveEvent() {
    this.adminService.createEvent(this.newEvent as Event).subscribe({
        next: () => {
        this.loading = false;
        this.showForm = false;
        this.newEvent = { title: '', description: '', date: '', imageUrl: '' };
        this.selectedFile = null;
        setTimeout(() => this.loadEvents(), 300); // ← petit délai
        },
        error: () => {
        this.errorMessage = 'Erreur lors de la création.';
        this.loading = false;
        }
    });
    }

    onDelete(id: number) {
    if (!confirm('Supprimer cet événement ?')) return;
    this.adminService.deleteEvent(id).subscribe({
        next: () => setTimeout(() => this.loadEvents(), 300), // ← petit délai
        error: (err) => console.error(err)
    });
    }
  toggleForm() {
    this.showForm = !this.showForm;
    this.errorMessage = '';
    this.selectedFile = null;
  }
}