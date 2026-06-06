import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Login } from './login';
import { AuthService } from '../../services/auth.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  const authService = {
    login: vi.fn(() => of(false)),
    enterAsGuest: vi.fn(),
  };

  beforeEach(async () => {
    authService.login.mockClear();
    authService.enterAsGuest.mockClear();

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('trims credentials before validating and submitting', () => {
    component.loginForm.setValue({
      dni: ' 99999999 ',
      password: ' Admin123 ',
    });

    component.login();

    expect(authService.login).toHaveBeenCalledWith('99999999', 'Admin123');
  });
});
