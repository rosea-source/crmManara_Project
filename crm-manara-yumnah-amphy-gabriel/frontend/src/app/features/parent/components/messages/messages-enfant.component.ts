// src/app/features/parent/components/messages-enfant/messages-enfant.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ParentService, MessagesEnfantDTO, MessageEnfantDTO } from '../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-messages-enfant',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe, SidebarParentComponent],
  templateUrl: './messages-enfant.component.html'
})
export class MessagesEnfantComponent implements OnInit {

  private parentService = inject(ParentService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);          // ← ajout
  private cdr = inject(ChangeDetectorRef);

  data: MessagesEnfantDTO | null = null;
  filteredMessages: MessageEnfantDTO[] = [];
  selectedSessionId: number | null = null;
  loading = true;
  enfantId!: number;

  ngOnInit(): void {
    this.enfantId = Number(this.route.snapshot.paramMap.get('enfantId'));

    if (!this.enfantId || this.enfantId === 0) {
      console.error('ID enfant invalide dans la route');
      this.loading = false;
      return;
    }

    this.parentService.getMessagesEnfant(this.enfantId).subscribe({
      next: (data) => {
        this.data = data;
        this.filteredMessages = data.messages ?? [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur messages:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterSession(sessionId: number | null): void {
    this.selectedSessionId = sessionId;
    this.filteredMessages = sessionId === null
      ? (this.data?.messages ?? [])
      : (this.data?.messages ?? []).filter(m => m.session?.id === sessionId);
    this.cdr.detectChanges();
  }

  // ← navigation vers la page contact au lieu d'un appel API
  contacterAdmin(): void {
    this.router.navigate(['/parent/contact']);
  }

  repondre(msg: MessageEnfantDTO): void {
    const sujet = encodeURIComponent(`Réponse - ${msg.session?.activite?.titre ?? ''}`);
    const corps = encodeURIComponent(
      `Bonjour,\n\nJe vous contacte concernant mon enfant ${this.data?.enfant?.prenom ?? ''}.\n\n`
    );
    window.open(`https://mail.google.com/mail/?view=cm&fs=1&su=${sujet}&body=${corps}`, '_blank');
  }
}