import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../services/auth.service';

interface SidebarLink {
  label: string;
  route: string;
  icon: string;
  exact?: boolean;
}

@Component({
  selector: 'app-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'sidebar-host',
  },
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly session = this.authService.session;
  readonly homeRoute = computed(() => {
    const role = this.session()?.role;

    return !role || role === 'guest' ? '/offers' : '/dashboard';
  });
  readonly showAccountPanel = computed(() => {
    const role = this.session()?.role;

    return role === 'admin' || role === 'client';
  });
  readonly userName = computed(() => this.session()?.name ?? 'Invitado');
  readonly userInitials = computed(() => this.createInitials(this.userName()));
  readonly roleLabel = computed(() => {
    const role = this.session()?.role;

    if (role === 'admin') return 'Administrador';
    if (role === 'client') return 'Cliente';
    return 'Invitado';
  });
  readonly links = computed<SidebarLink[]>(() => {
    const role = this.session()?.role;
    const items: SidebarLink[] = [];

    if (role !== 'guest') {
      items.push({ label: 'Panel de Control', route: '/dashboard', icon: '▦', exact: true });
    }

    if (role === 'client') {
      items.push(
        { label: 'Ofertas', route: '/offers', icon: '%' },
        { label: 'Perfil', route: '/profile', icon: '◉' }
      );
    }

    if (role === 'admin') {
      items.push(
        { label: 'Clientes', route: '/admin-clients', icon: '◎' },
        { label: 'Menú', route: '/admin-menu', icon: '≡' },
        { label: 'Códigos', route: '/admin-codes', icon: '#' },
        { label: 'Ofertas', route: '/offers', icon: '%' },
        { label: 'Perfil', route: '/profile', icon: '◉' }
      );
    }

    if (role === 'guest' || !role) {
      items.push({ label: 'Ofertas', route: '/offers', icon: '%' });
    }

    return items;
  });

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
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
