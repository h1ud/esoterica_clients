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
  { path: '', component: Login },
  { path: 'login', redirectTo: '', pathMatch: 'full' },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard, canActivate: [sessionGuard] },
  { path: 'offers', component: Offers },
  { path: 'profile', component: Profile, canActivate: [sessionGuard] },
  { path: 'admin-codes', component: AdminCodes, canActivate: [sessionGuard, adminGuard] },
  { path: 'admin-clients', component: AdminClients, canActivate: [sessionGuard, adminGuard] },
  { path: 'admin-menu', component: AdminMenu, canActivate: [sessionGuard, adminGuard] },
  { path: '**', redirectTo: '' },
];
