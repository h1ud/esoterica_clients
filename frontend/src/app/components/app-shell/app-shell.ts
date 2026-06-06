import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Sidebar } from '../sidebar/sidebar';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, Sidebar],
  host: {
    class: 'app-shell-host',
  },
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.css',
})
export class AppShell {
  private readonly authService = inject(AuthService);

  readonly eyebrow = input('');
  readonly title = input.required<string>();
  readonly description = input('');
  readonly isPublicSession = computed(() => {
    const role = this.authService.session()?.role;

    return !role || role === 'guest';
  });
}
