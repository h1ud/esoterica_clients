import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { AppFooter } from '../../components/app-footer/app-footer';

@Component({
  selector: 'app-register',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink, AppFooter],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly errorMessage = signal('');
  readonly isSubmitting = signal(false);
  readonly registerForm = new FormGroup({
    nombre: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)],
    }),
    apellido: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)],
    }),
    dni: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[0-9]{8}$/)],
    }),
    fechaNacimiento: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6)],
    }),
  });

  register(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.errorMessage.set('Completa los campos requeridos con datos válidos.');
      return;
    }

    this.isSubmitting.set(true);

    this.authService.register(this.registerForm.getRawValue()).pipe(
      finalize(() => this.isSubmitting.set(false))
    ).subscribe({
      next: (wasCreated) => {
        if (!wasCreated) {
          this.errorMessage.set('Ya existe una cuenta con ese DNI. Inicia sesión o usa otro DNI.');
        } else {
          this.errorMessage.set('');
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.errorMessage.set('No se pudo conectar con el servidor. Intenta nuevamente.');
      }
    });
  }

  hasError(field: keyof Register['registerForm']['controls']): boolean {
    const control = this.registerForm.controls[field];

    return control.invalid && control.touched;
  }
}
