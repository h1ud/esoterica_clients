import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { AppShell } from '../../components/app-shell/app-shell';
import { MenuItem, MenuService } from '../../services/menu.service';
import { formatSoles } from '../../utils/formatters';

@Component({
  selector: 'app-admin-menu',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AppShell, ReactiveFormsModule],
  templateUrl: './admin-menu.html',
  styleUrl: './admin-menu.css',
})
export class AdminMenu implements OnInit {
  private readonly menuService = inject(MenuService);

  readonly menuItems = signal<MenuItem[]>([]);
  readonly editingItemId = signal<number | null>(null);
  readonly pendingDeleteItem = signal<MenuItem | null>(null);
  readonly showForm = signal(false);
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly availableCount = computed(() =>
    this.menuItems().filter((item) => item.isAvailable).length
  );
  readonly featuredCount = computed(() =>
    this.menuItems().filter((item) => item.isFeatured).length
  );
  readonly categories = computed(() =>
    Array.from(new Set(this.menuItems().map((item) => item.category))).sort()
  );

  readonly menuForm = new FormGroup({
    title: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    description: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    category: new FormControl('Postres', { nonNullable: true, validators: [Validators.required] }),
    price: new FormControl(10, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    imageUrl: new FormControl('', { nonNullable: true }),
    isAvailable: new FormControl(true, { nonNullable: true }),
    isFeatured: new FormControl(false, { nonNullable: true }),
  });

  ngOnInit(): void {
    this.loadMenu();
  }

  loadMenu(): void {
    this.isLoading.set(true);
    this.menuService.getAdminMenu().subscribe({
      next: (items) => {
        this.menuItems.set(items);
        this.errorMessage.set('');
        this.isLoading.set(false);
      },
      error: () => {
        this.menuItems.set([]);
        this.errorMessage.set('No se pudo abrir el grimorio del menú.');
        this.isLoading.set(false);
      },
    });
  }

  saveMenuItem(): void {
    if (this.menuForm.invalid) {
      this.menuForm.markAllAsTouched();
      this.errorMessage.set('Completa nombre, descripción, categoría y precio.');
      this.successMessage.set('');
      return;
    }

    const value = this.menuForm.getRawValue();
    const payload: Partial<MenuItem> = {
      title: value.title.trim(),
      description: value.description.trim(),
      category: value.category.trim(),
      price: Number(value.price),
      imageUrl: value.imageUrl.trim(),
      isAvailable: value.isAvailable,
      isFeatured: value.isFeatured,
    };

    const id = this.editingItemId();
    const request = id === null
      ? this.menuService.createMenuItem(payload)
      : this.menuService.updateMenuItem(id, payload);

    this.isSaving.set(true);
    request.pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: () => {
        this.successMessage.set(id === null ? 'Producto agregado al menú.' : 'Producto actualizado.');
        this.errorMessage.set('');
        this.cancelForm();
        this.loadMenu();
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set(id === null ? 'No se pudo agregar el producto.' : 'No se pudo actualizar el producto.');
      },
    });
  }

  openNewForm(): void {
    this.resetForm();
    this.showForm.set(true);
  }

  editMenuItem(item: MenuItem): void {
    this.editingItemId.set(item.id);
    this.showForm.set(true);
    this.pendingDeleteItem.set(null);
    this.menuForm.reset({
      title: item.title,
      description: item.description,
      category: item.category,
      price: item.price,
      imageUrl: item.imageUrl ?? '',
      isAvailable: item.isAvailable,
      isFeatured: item.isFeatured,
    });
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  requestDelete(item: MenuItem): void {
    this.pendingDeleteItem.set(item);
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  cancelDelete(): void {
    this.pendingDeleteItem.set(null);
  }

  confirmDelete(): void {
    const item = this.pendingDeleteItem();
    if (!item) return;

    this.isSaving.set(true);
    this.menuService.deleteMenuItem(item.id).pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: () => {
        this.pendingDeleteItem.set(null);
        this.successMessage.set('Producto retirado del menú.');
        this.errorMessage.set('');
        this.loadMenu();
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set('No se pudo retirar el producto.');
      },
    });
  }

  cancelForm(): void {
    this.showForm.set(false);
    this.editingItemId.set(null);
    this.pendingDeleteItem.set(null);
    this.menuForm.reset({
      title: '',
      description: '',
      category: 'Postres',
      price: 10,
      imageUrl: '',
      isAvailable: true,
      isFeatured: false,
    });
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  resetForm(): void {
    this.editingItemId.set(null);
    this.pendingDeleteItem.set(null);
    this.menuForm.reset({
      title: '',
      description: '',
      category: 'Postres',
      price: 10,
      imageUrl: '',
      isAvailable: true,
      isFeatured: false,
    });
  }

  hasError(field: keyof AdminMenu['menuForm']['controls']): boolean {
    const control = this.menuForm.controls[field];

    return control.invalid && control.touched;
  }

  formatPrice(value: number): string {
    return formatSoles(value);
  }
}
