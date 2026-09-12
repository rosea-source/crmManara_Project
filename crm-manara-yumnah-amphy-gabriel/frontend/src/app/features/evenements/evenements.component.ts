import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { Event } from '../../shared/models/event.model';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-evenements',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, FooterComponent],
  templateUrl: './evenements.component.html'
})
export class EvenementsComponent implements OnInit {
  private http = inject(HttpClient);
  events: Event[] = [];
   private cdr = inject(ChangeDetectorRef);

 ngOnInit() {
  this.http.get<Event[]>('http://localhost:8080/api/evenements').subscribe({
    next: (data) => {
      this.events = data;
      this.cdr.detectChanges();
    },
    error: (err) => console.error(err)
  });
}

}