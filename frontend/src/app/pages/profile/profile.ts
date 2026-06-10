import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AppShell } from '../../components/app-shell/app-shell';
import { AuthService } from '../../services/auth.service';
import { formatLocalDate, getInitials } from '../../utils/formatters';

@Component({
  selector: 'app-profile',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AppShell, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  private readonly authService = inject(AuthService);

  readonly user = this.authService.currentUser;
  readonly role = this.authService.session;
  readonly weeklyProductCount = computed(() => this.role()?.weeklyProductCount ?? 0);
  readonly vipRequiredProducts = computed(() => this.role()?.vipRequiredProducts ?? 10);
  readonly vipRemainingProducts = computed(() => this.role()?.vipRemainingProducts ?? this.vipRequiredProducts());
  readonly usedOffersCount = computed(() => this.role()?.usedOffersCount ?? 0);
  readonly isVip = computed(() => this.role()?.vip ?? false);
  readonly vipProgressPercent = computed(() => {
    if (this.vipRequiredProducts() === 0) return 0;
    return Math.min(100, Math.round((this.weeklyProductCount() / this.vipRequiredProducts()) * 100));
  });
  readonly initials = computed(() => {
    const user = this.user();
    return getInitials(user ? `${user.nombre} ${user.apellido}` : '');
  });
  readonly fullName = computed(() => {
    const user = this.user();
    return user ? `${user.nombre} ${user.apellido}`.trim() : '';
  });
  readonly roleLabel = computed(() => {
    const role = this.role()?.role;

    if (role === 'admin') return 'Administrador';
    if (role === 'client') return 'Cliente';
    return 'Invitado';
  });

  formatDate(value: string): string {
    return formatLocalDate(value);
  }
}
