import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

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

  readonly closed = output<void>();
  readonly navigated = output<void>();
  readonly session = this.authService.session;
  readonly homeRoute = computed(() => {
    const role = this.session()?.role;

    return !role || role === 'guest' ? '/offers' : '/dashboard';
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

  close(): void {
    this.closed.emit();
  }

  notifyNavigation(): void {
    this.navigated.emit();
  }
}
