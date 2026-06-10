import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../services/auth.service';

type FooterVariant = 'public' | 'portal';

interface FooterLink {
  label: string;
  route: string;
}

@Component({
  selector: 'app-footer',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  host: {
    class: 'app-footer-host',
  },
  templateUrl: './app-footer.html',
  styleUrl: './app-footer.css',
})
export class AppFooter {
  private readonly authService = inject(AuthService);

  readonly compact = input(false);
  readonly variant = input<FooterVariant>('public');
  readonly subtitle = computed(() => {
    if (this.variant() === 'public') return 'Carta, cuenta y acceso del atelier';

    const role = this.authService.session()?.role;

    if (role === 'admin') return 'Panel administrativo del obrador';
    if (role === 'client') return 'Beneficios, perfil y carta disponible';
    return 'Carta pública disponible sin iniciar sesión';
  });
  readonly links = computed<FooterLink[]>(() => {
    if (this.variant() === 'public') {
      return [
        { label: 'Carta', route: '/offers' },
        { label: 'Crear cuenta', route: '/register' },
        { label: 'Acceso', route: '/login' },
      ];
    }

    const role = this.authService.session()?.role;

    if (role === 'admin') {
      return [
        { label: 'Panel', route: '/dashboard' },
        { label: 'Clientes', route: '/admin-clients' },
        { label: 'Menú', route: '/admin-menu' },
        { label: 'Sellos', route: '/admin-codes' },
        { label: 'Carta', route: '/offers' },
      ];
    }

    if (role === 'client') {
      return [
        { label: 'Panel', route: '/dashboard' },
        { label: 'Ofertas', route: '/offers' },
        { label: 'Perfil', route: '/profile' },
      ];
    }

    return [
      { label: 'Carta', route: '/offers' },
      { label: 'Crear cuenta', route: '/register' },
    ];
  });
}
