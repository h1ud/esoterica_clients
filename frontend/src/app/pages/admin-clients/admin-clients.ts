import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';

import { AppShell } from '../../components/app-shell/app-shell';
import { AuthService } from '../../services/auth.service';
import { AdminClientUpdate, ClientService } from '../../services/client.service';
import { SettingsService } from '../../services/settings.service';
import { ClientSummary } from '../../models/user';
import { formatLocalDate } from '../../utils/formatters';

type EditableRole = 'client' | 'admin';

@Component({
  selector: 'app-admin-clients',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AppShell, ReactiveFormsModule],
  templateUrl: './admin-clients.html',
  styleUrl: './admin-clients.css',
})
export class AdminClients implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly clientService = inject(ClientService);
  private readonly settingsService = inject(SettingsService);

  readonly clients = signal<ClientSummary[]>([]);
  readonly editingClientId = signal<number | null>(null);
  readonly pendingDeleteClient = signal<ClientSummary | null>(null);
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly currentSession = this.authService.session;

  readonly vipClientsCount = computed(() => this.clients().filter((client) => client.vip).length);
  readonly clientUsersCount = computed(() => this.clients().filter((client) => client.role === 'client').length);
  readonly totalWeeklyProducts = computed(() =>
    this.clients().reduce((total, client) => total + client.weeklyProductCount, 0)
  );

  readonly settingsForm = new FormGroup({
    vipRequiredProducts: new FormControl(10, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1), Validators.max(200)],
    }),
  });

  readonly clientForm = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dni: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[0-9]{8}$/)],
    }),
    birthdayDate: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    role: new FormControl<EditableRole>('client', { nonNullable: true, validators: [Validators.required] }),
    weeklyProductCount: new FormControl(0, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(0), Validators.max(999)],
    }),
    vip: new FormControl(false, { nonNullable: true }),
  });

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);
    forkJoin({
      clients: this.clientService.getClients(),
      settings: this.settingsService.getVipSettings(),
    }).subscribe({
      next: ({ clients, settings }) => {
        this.clients.set(clients);
        this.settingsForm.patchValue({ vipRequiredProducts: settings.vipRequiredProducts });
        this.errorMessage.set('');
        this.isLoading.set(false);
      },
      error: () => {
        this.clients.set([]);
        this.errorMessage.set('No se pudo consultar el círculo de clientes.');
        this.isLoading.set(false);
      },
    });
  }

  saveSettings(): void {
    if (this.settingsForm.invalid) {
      this.settingsForm.markAllAsTouched();
      this.errorMessage.set('La meta VIP debe ser mayor a cero.');
      return;
    }

    this.isSaving.set(true);
    this.settingsService.updateVipSettings(this.settingsForm.controls.vipRequiredProducts.value).pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: () => {
        this.successMessage.set('Meta VIP actualizada.');
        this.errorMessage.set('');
        this.loadData();
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set('No se pudo actualizar la meta VIP.');
      },
    });
  }

  editClient(client: ClientSummary): void {
    this.editingClientId.set(client.id);
    this.pendingDeleteClient.set(null);
    this.clientForm.reset({
      name: client.name,
      dni: client.dni,
      birthdayDate: client.birthdayDate,
      role: client.role,
      weeklyProductCount: client.weeklyProductCount,
      vip: client.vip,
    });
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  cancelForm(): void {
    this.editingClientId.set(null);
    this.clientForm.reset({
      name: '',
      dni: '',
      birthdayDate: '',
      role: 'client',
      weeklyProductCount: 0,
      vip: false,
    });
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  saveClient(): void {
    const id = this.editingClientId();
    if (id === null) return;

    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      this.errorMessage.set('Completa los datos del cliente antes de guardar.');
      return;
    }

    const value = this.clientForm.getRawValue();
    const payload: AdminClientUpdate = {
      name: value.name.trim(),
      dni: value.dni.trim(),
      birthdayDate: value.birthdayDate,
      role: value.role,
      weeklyProductCount: Number(value.weeklyProductCount),
      vip: value.vip,
    };

    this.isSaving.set(true);
    this.clientService.updateClient(id, payload).pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: (updatedClient) => {
        this.clients.update((clients) =>
          clients.map((client) => client.id === updatedClient.id ? updatedClient : client)
        );
        this.successMessage.set('Cliente actualizado.');
        this.errorMessage.set('');
        this.cancelForm();
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set('No se pudo actualizar el cliente.');
      },
    });
  }

  requestDelete(client: ClientSummary): void {
    this.pendingDeleteClient.set(client);
    this.editingClientId.set(null);
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  cancelDelete(): void {
    this.pendingDeleteClient.set(null);
  }

  confirmDelete(): void {
    const client = this.pendingDeleteClient();
    if (!client || this.isCurrentSession(client)) return;

    this.isSaving.set(true);
    this.clientService.deleteClient(client.id).pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: () => {
        this.clients.update((clients) => clients.filter((item) => item.id !== client.id));
        this.pendingDeleteClient.set(null);
        this.successMessage.set('Cliente retirado del círculo.');
        this.errorMessage.set('');
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set('No se pudo eliminar el cliente.');
      },
    });
  }

  resetClientForm(): void {
    this.editingClientId.set(null);
    this.clientForm.reset({
      name: '',
      dni: '',
      birthdayDate: '',
      role: 'client',
      weeklyProductCount: 0,
      vip: false,
    });
  }

  hasClientError(field: keyof AdminClients['clientForm']['controls']): boolean {
    const control = this.clientForm.controls[field];

    return control.invalid && control.touched;
  }

  formatDate(value: string | null): string {
    return value ? formatLocalDate(value) : 'Sin fecha';
  }

  roleLabel(role: string): string {
    return role === 'admin' ? 'Administrador' : 'Cliente';
  }

  progressPercent(client: ClientSummary): number {
    if (client.vipRequiredProducts <= 0) return 100;

    return Math.min(100, Math.round((client.weeklyProductCount / client.vipRequiredProducts) * 100));
  }

  isCurrentSession(client: ClientSummary): boolean {
    return this.currentSession()?.id === client.id;
  }
}
