import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { AppShell } from '../../components/app-shell/app-shell';
import { CodeService, Code } from '../../services/code.service';
import { formatLocalDate, formatPercent, formatSoles } from '../../utils/formatters';

type CodeVisibility = 'GLOBAL' | 'PRIVATE';

@Component({
  selector: 'app-admin-codes',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AppShell, ReactiveFormsModule],
  templateUrl: './admin-codes.html',
  styleUrl: './admin-codes.css',
})
export class AdminCodes implements OnInit {
  private readonly codeService = inject(CodeService);

  readonly codes = signal<Code[]>([]);
  readonly editingCodeId = signal<number | null>(null);
  readonly pendingDeleteCode = signal<Code | null>(null);
  readonly showForm = signal(false);
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly activeCodesCount = computed(() =>
    this.codes().filter((code) => code.isActive && !code.isUsed).length
  );

  readonly codeForm = new FormGroup({
    title: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    description: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    valueCode: new FormControl(10, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    discountCode: new FormControl(5, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    visibility: new FormControl<CodeVisibility>('GLOBAL', { nonNullable: true, validators: [Validators.required] }),
    isActive: new FormControl(true, { nonNullable: true }),
    isUsed: new FormControl(false, { nonNullable: true }),
    expiration: new FormControl(this.dateFromNow(30), { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.loadCodes();
  }

  loadCodes(): void {
    this.isLoading.set(true);
    this.codeService.getCodes().subscribe({
      next: (data) => {
        this.codes.set(data);
        this.errorMessage.set('');
        this.isLoading.set(false);
      },
      error: () => {
        this.codes.set([]);
        this.errorMessage.set('No se pudieron cargar los sellos. Revisa que el servidor esté activo.');
        this.isLoading.set(false);
      },
    });
  }

  saveCode(): void {
    if (this.codeForm.invalid) {
      this.codeForm.markAllAsTouched();
      this.errorMessage.set('Completa los campos requeridos antes de guardar el sello.');
      this.successMessage.set('');
      return;
    }

    const formValue = this.codeForm.getRawValue();
    const payload: Partial<Code> = {
      title: formValue.title.trim(),
      description: formValue.description.trim(),
      valueCode: Number(formValue.valueCode),
      discountCode: Number(formValue.discountCode),
      visibility: formValue.visibility,
      isActive: formValue.isActive,
      isUsed: formValue.isUsed,
      expiration: formValue.expiration,
    };

    const id = this.editingCodeId();
    const request = id !== null
      ? this.codeService.updateCode(id, payload)
      : this.codeService.createCode({ ...payload, createdAt: this.today() });

    this.isSaving.set(true);
    request.pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: () => {
        this.successMessage.set(id !== null ? 'Sello actualizado con éxito.' : 'Sello creado con éxito.');
        this.errorMessage.set('');
        this.cancelForm();
        this.loadCodes();
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set(id !== null ? 'No se pudo actualizar el sello.' : 'No se pudo crear el sello.');
      },
    });
  }

  openNewForm(): void {
    this.resetForm();
    this.showForm.set(true);
  }

  editCode(code: Code): void {
    this.editingCodeId.set(code.id);
    this.showForm.set(true);
    this.pendingDeleteCode.set(null);
    this.codeForm.patchValue({
      title: code.title,
      description: code.description,
      valueCode: code.valueCode,
      discountCode: code.discountCode,
      visibility: code.visibility as CodeVisibility,
      isActive: code.isActive,
      isUsed: code.isUsed,
      expiration: code.expiration,
    });
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  requestDelete(code: Code): void {
    this.pendingDeleteCode.set(code);
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  cancelDelete(): void {
    this.pendingDeleteCode.set(null);
  }

  confirmDelete(): void {
    const code = this.pendingDeleteCode();
    if (!code) return;

    this.isSaving.set(true);
    this.codeService.deleteCode(code.id).pipe(
      finalize(() => this.isSaving.set(false))
    ).subscribe({
      next: () => {
        this.pendingDeleteCode.set(null);
        this.successMessage.set('Sello eliminado.');
        this.errorMessage.set('');
        this.loadCodes();
      },
      error: () => {
        this.successMessage.set('');
        this.errorMessage.set('No se pudo eliminar el sello.');
      },
    });
  }

  cancelForm(): void {
    this.showForm.set(false);
    this.editingCodeId.set(null);
    this.pendingDeleteCode.set(null);
    this.codeForm.reset({
      title: '',
      description: '',
      valueCode: 10,
      discountCode: 5,
      visibility: 'GLOBAL',
      isActive: true,
      isUsed: false,
      expiration: this.dateFromNow(30),
    });
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  resetForm(): void {
    this.editingCodeId.set(null);
    this.pendingDeleteCode.set(null);
    this.codeForm.reset({
      title: '',
      description: '',
      valueCode: 10,
      discountCode: 5,
      visibility: 'GLOBAL',
      isActive: true,
      isUsed: false,
      expiration: this.dateFromNow(30),
    });
  }

  hasError(field: keyof AdminCodes['codeForm']['controls']): boolean {
    const control = this.codeForm.controls[field];

    return control.invalid && control.touched;
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

  private today(): string {
    return this.toDateInputValue(new Date());
  }

  private dateFromNow(days: number): string {
    const date = new Date();
    date.setDate(date.getDate() + days);

    return this.toDateInputValue(date);
  }

  private toDateInputValue(date: Date): string {
    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);

    return localDate.toISOString().slice(0, 10);
  }
}
