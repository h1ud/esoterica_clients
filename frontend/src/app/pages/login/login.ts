import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../services/auth.service';

const DNI_PATTERN = /^[0-9]{8}$/;

@Component({
  selector: 'app-login',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly errorMessage = signal('');
  readonly isSubmitting = signal(false);
  readonly loginForm = new FormGroup({
    dni: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(DNI_PATTERN)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  login(): void {
    this.normalizeCredentials();

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.errorMessage.set(this.validationMessage());
      return;
    }

    this.isSubmitting.set(true);

    const { dni, password } = this.loginForm.getRawValue();
    this.authService.login(dni, password).pipe(
      finalize(() => this.isSubmitting.set(false))
    ).subscribe({
      next: (isValid) => {
        if (!isValid) {
          this.errorMessage.set('Credenciales incorrectas. Revisa tu DNI y contraseña.');
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

  guestLogin(): void {
    this.authService.enterAsGuest();
    this.router.navigate(['/offers']);
  }

  hasError(field: keyof Login['loginForm']['controls']): boolean {
    const control = this.loginForm.controls[field];

    return control.invalid && control.touched;
  }

  private normalizeCredentials(): void {
    const { dni, password } = this.loginForm.getRawValue();

    this.loginForm.patchValue({
      dni: dni.trim(),
      password: password.trim(),
    }, { emitEvent: false });
  }

  private validationMessage(): string {
    const dni = this.loginForm.controls.dni;
    const password = this.loginForm.controls.password;

    if (dni.hasError('required')) return 'Ingresa tu DNI.';
    if (dni.hasError('pattern')) return 'El DNI debe tener 8 dígitos, sin espacios ni guiones.';
    if (password.hasError('required')) return 'Ingresa tu contraseña.';

    return 'Completa tus credenciales para entrar.';
  }
}
