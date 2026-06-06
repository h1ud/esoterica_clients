import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { adminGuard, sessionGuard } from './session.guard';

describe('sessionGuard', () => {
  const authService = {
    hasSession: vi.fn(),
    isGuest: vi.fn(),
    isAdmin: vi.fn(),
  };

  beforeEach(() => {
    authService.hasSession.mockReset();
    authService.isGuest.mockReset();
    authService.isAdmin.mockReset();

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
      ],
    });
  });

  it('redirects guests away from private session pages', () => {
    authService.hasSession.mockReturnValue(true);
    authService.isGuest.mockReturnValue(true);

    const result = runSessionGuard();

    expect(serializeUrl(result)).toBe('/offers');
  });

  it('allows authenticated non-guest sessions', () => {
    authService.hasSession.mockReturnValue(true);
    authService.isGuest.mockReturnValue(false);

    expect(runSessionGuard()).toBe(true);
  });

  it('redirects clients away from admin pages', () => {
    authService.isAdmin.mockReturnValue(false);

    const result = runAdminGuard();

    expect(serializeUrl(result)).toBe('/dashboard');
  });

  function runSessionGuard(): boolean | UrlTree {
    return TestBed.runInInjectionContext(() => sessionGuard({} as never, {} as never)) as boolean | UrlTree;
  }

  function runAdminGuard(): boolean | UrlTree {
    return TestBed.runInInjectionContext(() => adminGuard({} as never, {} as never)) as boolean | UrlTree;
  }

  function serializeUrl(result: boolean | UrlTree): string {
    const router = TestBed.inject(Router);

    return result instanceof UrlTree ? router.serializeUrl(result) : String(result);
  }
});
