// Configuración del Angular Router.
// Cada ruta se mapea a un componente y puede tener guards que controlan el acceso.
// Las rutas se renderizan dentro de <router-outlet> en app.html.

import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Offers } from './pages/offers/offers';
import { Profile } from './pages/profile/profile';
import { AdminCodes } from './pages/admin-codes/admin-codes';
import { AdminClients } from './pages/admin-clients/admin-clients';
import { AdminMenu } from './pages/admin-menu/admin-menu';
import { sessionGuard, adminGuard } from './guards/session.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },         // Raíz → redirige a login
  { path: 'login', component: Login },                           // Página de inicio de sesión
  { path: 'register', component: Register },                     // Registro de nuevo usuario
  { path: 'dashboard', component: Dashboard, canActivate: [sessionGuard] },       // Panel principal (requiere sesión)
  { path: 'offers', component: Offers },                         // Carta y ofertas (público)
  { path: 'profile', component: Profile, canActivate: [sessionGuard] },           // Perfil del usuario (requiere sesión)
  { path: 'admin-codes', component: AdminCodes, canActivate: [sessionGuard, adminGuard] },    // Admin: gestión de códigos
  { path: 'admin-clients', component: AdminClients, canActivate: [sessionGuard, adminGuard] }, // Admin: gestión de clientes
  { path: 'admin-menu', component: AdminMenu, canActivate: [sessionGuard, adminGuard] },       // Admin: gestión del menú
  { path: '**', redirectTo: 'login' },                           // Ruta comodín: cualquier otra → login
];
