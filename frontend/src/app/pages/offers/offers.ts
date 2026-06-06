import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';

import { AppShell } from '../../components/app-shell/app-shell';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { CodeActivation, CodeService, Code } from '../../services/code.service';
import { MenuItem, MenuService } from '../../services/menu.service';
import { ClientSummary } from '../../models/user';
import { formatLocalDate, formatPercent, formatSoles } from '../../utils/formatters';

@Component({
  selector: 'app-offers',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AppShell, RouterLink, ReactiveFormsModule],
  templateUrl: './offers.html',
  styleUrl: './offers.css',
})
export class Offers implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly clientService = inject(ClientService);
  private readonly codeService = inject(CodeService);
  private readonly menuService = inject(MenuService);
  private readonly offerImages = [
    {
      keywords: ['cheesecake', 'queso', 'tarta'],
      label: 'Tarta premium',
      url: 'https://images.unsplash.com/photo-1524351199678-941a58a3df50?auto=format&fit=crop&w=920&q=80',
    },
    {
      keywords: ['brownie', 'friday', 'viernes'],
      label: 'Brownies artesanales',
      url: 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=920&q=80',
    },
    {
      keywords: ['cafe', 'café', 'combo', 'dulce'],
      label: 'Café y postre artesanal',
      url: 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=920&q=80',
    },
    {
      keywords: ['macaron', 'macarons'],
      label: 'Macarons artesanales',
      url: 'https://images.unsplash.com/photo-1569864358642-9d1684040f43?auto=format&fit=crop&w=920&q=80',
    },
  ];

  readonly isGuest = this.authService.isGuest;
  readonly offers = signal<Code[]>([]);
  readonly activations = signal<CodeActivation[]>([]);
  readonly menuItems = signal<MenuItem[]>([]);
  readonly clientProgress = signal<ClientSummary | null>(null);
  readonly selectedOffer = signal<Code | null>(null);
  readonly isLoading = signal(false);
  readonly isActivating = signal(false);
  readonly loadError = signal('');
  readonly successMessage = signal('');
  readonly productQuantity = new FormControl(1, {
    nonNullable: true,
    validators: [Validators.required, Validators.min(1), Validators.max(50)],
  });
  readonly availableOffers = computed(() =>
    this.offers().filter((offer) => this.isOfferAvailable(offer))
  );
  readonly featuredMenuItems = computed(() =>
    this.menuItems().filter((item) => item.isFeatured).slice(0, 3)
  );
  readonly regularMenuItems = computed(() =>
    this.menuItems().filter((item) => !item.isFeatured)
  );
  readonly vipProgressPercent = computed(() => {
    const progress = this.clientProgress();
    if (!progress || progress.vipRequiredProducts <= 0) return 0;

    return Math.min(100, Math.round((progress.weeklyProductCount / progress.vipRequiredProducts) * 100));
  });
  readonly productsRemainingLabel = computed(() => {
    const progress = this.clientProgress();
    if (!progress) return '';
    if (progress.vip) return 'VIP activo';

    return `${progress.vipRemainingProducts} productos para VIP`;
  });
  ngOnInit(): void {
    if (!this.authService.hasSession()) {
      this.authService.enterAsGuest();
    }

    this.loadOffers();
  }

  loadOffers(): void {
    this.isLoading.set(true);

    if (this.isGuest()) {
      this.loadPublicCatalog();
      return;
    }

    forkJoin({
      offers: this.codeService.getCodes(),
      client: this.clientService.getMe(),
      activations: this.codeService.getMyActivations(),
      menu: this.menuService.getMenu(),
    }).subscribe({
      next: ({ offers, client, activations, menu }) => {
        this.offers.set(offers);
        this.menuItems.set(menu);
        this.clientProgress.set(client);
        this.activations.set(activations);
        this.loadError.set('');
        this.isLoading.set(false);
      },
      error: () => {
        this.authService.enterAsGuest();
        this.loadPublicCatalog();
      },
    });
  }

  openActivation(offer: Code): void {
    this.selectedOffer.set(offer);
    this.productQuantity.setValue(1);
    this.productQuantity.markAsUntouched();
    this.loadError.set('');
    this.successMessage.set('');
  }

  closeActivation(): void {
    if (this.isActivating()) return;
    this.selectedOffer.set(null);
  }

  activateSelectedOffer(): void {
    const offer = this.selectedOffer();
    if (!offer) return;

    if (this.productQuantity.invalid) {
      this.productQuantity.markAsTouched();
      this.loadError.set('Elige una cantidad válida de productos para activar el sello.');
      return;
    }

    this.isActivating.set(true);
    this.codeService.activateCode(offer.id, this.productQuantity.value).pipe(
      finalize(() => this.isActivating.set(false))
    ).subscribe({
      next: (result) => {
        this.clientProgress.set(result.client);
        this.activations.update((activations) => [result.activation, ...activations]);
        this.authService.syncClientSession(result.client);
        this.selectedOffer.set(null);
        this.loadError.set('');
        this.successMessage.set(result.vipUnlocked
          ? 'El círculo VIP quedó encendido para tu cuenta.'
          : 'Oferta activada: tu progreso semanal subió.'
        );
      },
      error: () => {
        this.successMessage.set('');
        this.loadError.set('No se pudo activar la oferta. Revisa el servidor o intenta otra vez.');
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

  formatActivationTotal(offer: Code): string {
    return formatSoles(Number(offer.discountCode) * this.productQuantity.value);
  }

  formatPrice(value: number): string {
    return formatSoles(value);
  }

  offerImage(offer: Code): string {
    return this.offerVisual(offer).url;
  }

  offerImageAlt(offer: Code): string {
    return `${this.offerVisual(offer).label} para ${offer.title}`;
  }

  selectedOfferProductsLabel(): string {
    return this.productQuantity.value === 1 ? '1 producto' : `${this.productQuantity.value} productos`;
  }

  private isOfferAvailable(offer: Code): boolean {
    return offer.isActive
      && !offer.isUsed
      && offer.expiration >= this.today()
      && !this.activations().some((activation) => activation.codeId === offer.id);
  }

  private loadPublicCatalog(): void {
    forkJoin({
      offers: this.codeService.getCodes(),
      menu: this.menuService.getMenu(),
    }).subscribe({
      next: ({ offers, menu }) => {
        this.offers.set(offers);
        this.menuItems.set(menu);
        this.clientProgress.set(null);
        this.activations.set([]);
        this.loadError.set('');
        this.isLoading.set(false);
      },
      error: () => {
        this.offers.set([]);
        this.menuItems.set([]);
        this.loadError.set('No se pudieron cargar las ofertas ni la carta. Revisa que el servidor esté activo.');
        this.isLoading.set(false);
      },
    });
  }

  private today(): string {
    const date = new Date();
    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);

    return localDate.toISOString().slice(0, 10);
  }

  private offerVisual(offer: Code): { label: string; url: string } {
    const haystack = `${offer.title} ${offer.description}`.toLowerCase();
    const match = this.offerImages.find((visual) =>
      visual.keywords.some((keyword) => haystack.includes(keyword))
    );

    return match ?? this.offerImages[0];
  }
}
