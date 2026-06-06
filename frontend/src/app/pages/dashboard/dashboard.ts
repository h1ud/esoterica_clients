import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AppShell } from '../../components/app-shell/app-shell';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { CodeActivation, CodeService, Code } from '../../services/code.service';
import { ClientSummary } from '../../models/user';
import { formatLocalDate, formatPercent, formatSoles } from '../../utils/formatters';

@Component({
  selector: 'app-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AppShell, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly clientService = inject(ClientService);
  private readonly codeService = inject(CodeService);

  readonly isGuest = this.authService.isGuest;
  readonly currentUser = this.authService.currentUser;
  readonly codes = signal<Code[]>([]);
  readonly activations = signal<CodeActivation[]>([]);
  readonly clientProgress = signal<ClientSummary | null>(null);
  readonly isLoading = signal(false);
  readonly loadError = signal('');
  readonly activeCodes = computed(() =>
    this.codes().filter((code) => this.isOfferAvailable(code))
  );
  readonly featuredOffers = computed(() => this.activeCodes().slice(0, 3));
  readonly usedCodesCount = computed(() =>
    this.clientProgress()?.usedOffersCount ?? this.authService.session()?.usedOffersCount ?? 0
  );
  readonly weeklyProductCount = computed(() =>
    this.clientProgress()?.weeklyProductCount ?? this.authService.session()?.weeklyProductCount ?? 0
  );
  readonly vipRequiredProducts = computed(() =>
    this.clientProgress()?.vipRequiredProducts ?? this.authService.session()?.vipRequiredProducts ?? 10
  );
  readonly vipRemainingProducts = computed(() =>
    this.clientProgress()?.vipRemainingProducts ?? this.authService.session()?.vipRemainingProducts ?? this.vipRequiredProducts()
  );
  readonly isVip = computed(() =>
    this.clientProgress()?.vip ?? this.authService.session()?.vip ?? false
  );
  readonly vipProgressPercent = computed(() => {
    const required = this.vipRequiredProducts();
    if (required <= 0) return 100;

    return Math.min(100, Math.round((this.weeklyProductCount() / required) * 100));
  });
  readonly shellDescription = computed(() => {
    if (this.isGuest()) {
      return 'Asómate al umbral del obrador y crea una cuenta para despertar beneficios.';
    }

    return 'Tu grimorio de beneficios, compras semanales y señales hacia el círculo VIP.';
  });

  ngOnInit(): void {
    if (this.isGuest()) return;

    this.isLoading.set(true);
    forkJoin({
      codes: this.codeService.getCodes(),
      client: this.clientService.getMe(),
      activations: this.codeService.getMyActivations(),
    }).subscribe({
      next: ({ codes, client, activations }) => {
        this.codes.set(codes);
        this.clientProgress.set(client);
        this.activations.set(activations);
        this.loadError.set('');
        this.isLoading.set(false);
      },
      error: () => {
        this.codes.set([]);
        this.loadError.set('No se pudieron cargar las promociones. Revisa que el servidor esté activo.');
        this.isLoading.set(false);
      },
    });
  }

  formatDate(value: string): string {
    return formatLocalDate(value);
  }

  formatDiscount(value: number): string {
    return formatSoles(value);
  }

  formatValue(value: number): string {
    return formatPercent(value);
  }

  private isOfferAvailable(code: Code): boolean {
    return code.isActive
      && !code.isUsed
      && code.expiration >= this.today()
      && !this.activations().some((activation) => activation.codeId === code.id);
  }

  private today(): string {
    const date = new Date();
    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);

    return localDate.toISOString().slice(0, 10);
  }
}
