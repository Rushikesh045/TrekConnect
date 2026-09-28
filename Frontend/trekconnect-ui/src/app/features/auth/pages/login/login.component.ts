import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { TokenService } from '../../../../core/services/token.service';
import { Role } from '../../../../core/models/auth.model';
import { AuthCardLayoutComponent } from '../../../../shared/components/auth-card-layout/auth-card-layout.component';
import { FormInputComponent } from '../../../../shared/components/form-input/form-input.component';
import { ActionButtonComponent } from '../../../../shared/components/action-button/action-button.component';
import { RoleSelectorComponent } from '../../../../shared/components/role-selector/role-selector.component';

/**
 * Login Page Component for TrekConnect Platform.
 * 
 * WHY THIS COMPONENT WAS CREATED:
 * Provides a modern, responsive user login interface with role switching (Trekker/Organizer/Admin),
 * reactive validation, password show/hide toggles, error toast alerts, and role-based routing.
 * Utilizes reusable UI template components (AuthCardLayout, FormInput, ActionButton, RoleSelector).
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AuthCardLayoutComponent,
    FormInputComponent,
    ActionButtonComponent,
    RoleSelectorComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  private tokenService = inject(TokenService);
  private router = inject(Router);

  // Selected identity role: 'USER' (default), 'ORGANIZER', or 'ADMIN'
  selectedRole: Role = 'USER';
  isLoading = false;
  rememberMe = false;

  /**
   * Reactive login form group with required and email pattern validators.
   */
  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  /**
   * Handles role selection tab changes.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Updates component's selectedRole property when user clicks 'Trekker', 'Organizer', or 'Admin'.
   */
  onRoleChange(role: Role): void {
    this.selectedRole = role;
  }

  /**
   * Handles login form submission.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Validates form fields, invokes AuthService.login(), displays success/error toast alerts,
   * and routes user to their appropriate dashboard based on their assigned role.
   */
  onSubmit(): void {
    // Step 1: Check if form validation passes
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    // Step 2: Set loading spinner state and extract form values
    this.isLoading = true;
    const { email, password } = this.loginForm.value;

    // Step 3: Trigger login API request to Auth Service with selected identity role
    this.authService.login({
      email: email!,
      password: password!,
      deviceInfo: navigator.userAgent,
      role: this.selectedRole
    }).subscribe({
      next: (res) => {
        // Step 4: Strict Role Matching Validation
        if (this.selectedRole !== res.user.role) {
          this.isLoading = false;
          this.tokenService.clearTokens(); // Directly clear tokens instead of calling unsubscribed logout()
          this.notificationService.showError(
            `Role mismatch: You selected "${this.selectedRole}" but this account is registered as "${res.user.role}". Please switch to the correct role tab.`,
            'Authentication Failed'
          );
          return;
        }

        // Step 5: On success, stop loading & show success notification toast
        this.isLoading = false;
        this.notificationService.showSuccess(
          `Welcome back, ${res.user.email}! Signed in as ${res.user.role}.`,
          'Login Successful'
        );

        // Step 6: Perform role-based navigation redirect
        if (res.user.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (res.user.role === 'ORGANIZER') {
          this.router.navigate(['/organizer/dashboard']);
        } else {
          this.router.navigate(['/user/home']);
        }
      },
      error: (err) => {
        // Step 7: On failure, stop loading & display server error message
        this.isLoading = false;
        const msg = err?.error?.message || 'Invalid email, password, or role for selected tab. Please try again.';
        this.notificationService.showError(msg, 'Authentication Failed');
      }
    });
  }

  /**
   * Getter for email field validation error text.
   */
  get emailError(): string {
    const control = this.loginForm.get('email');
    if (control?.touched && control.errors) {
      if (control.errors['required']) return 'Email address is required';
      if (control.errors['email']) return 'Please enter a valid email address';
    }
    return '';
  }

  /**
   * Getter for password field validation error text.
   */
  get passwordError(): string {
    const control = this.loginForm.get('password');
    if (control?.touched && control.errors) {
      if (control.errors['required']) return 'Password is required';
      if (control.errors['minlength']) return 'Password must be at least 6 characters';
    }
    return '';
  }
}
