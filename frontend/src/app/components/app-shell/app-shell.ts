import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Sidebar } from '../sidebar/sidebar';
import { AppFooter } from '../app-footer/app-footer';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Sidebar, AppFooter],
  host: {
    class: 'app-shell-host',
    '(document:keydown.escape)': 'closeSidebar()',
  },
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.css',
})
export class AppShell {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly eyebrow = input('');
  readonly title = input.required<string>();
  readonly description = input('');
  readonly isSidebarOpen = signal(false);
  readonly session = this.authService.session;
  readonly isPublicSession = computed(() => {
    const role = this.session()?.role;

    return !role || role === 'guest';
  });
  readonly userName = computed(() => this.session()?.name ?? 'Invitado');
  readonly userInitials = computed(() => this.createInitials(this.userName()));
  readonly roleLabel = computed(() => {
    const role = this.session()?.role;

    if (role === 'admin') return 'Administrador';
    if (role === 'client') return 'Cliente';
    return 'Invitado';
  });

  toggleSidebar(): void {
    this.isSidebarOpen.update((isOpen) => !isOpen);
  }

  closeSidebar(): void {
    this.isSidebarOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.closeSidebar();
    this.router.navigate(['/login']);
  }

  private createInitials(name: string): string {
    const initials = name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0])
      .join('')
      .toUpperCase();

    return initials || 'E';
  }
}
