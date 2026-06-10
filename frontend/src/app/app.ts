import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

// Componente raíz de la aplicación.
// <router-outlet> es el punto de entrada del Angular Router: aquí se renderiza
// la ruta activa (login, dashboard, admin-codes, etc.) según app.routes.ts
@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('Esotérica');
}
