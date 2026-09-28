import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { Role } from '../../../../core/models/auth.model';
import { AuthCardLayoutComponent } from '../../../../shared/components/auth-card-layout/auth-card-layout.component';
import { FormInputComponent } from '../../../../shared/components/form-input/form-input.component';
import { ActionButtonComponent } from '../../../../shared/components/action-button/action-button.component';
import { RoleSelectorComponent } from '../../../../shared/components/role-selector/role-selector.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    AuthCardLayoutComponent,
    FormInputComponent,
    ActionButtonComponent,
    RoleSelectorComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  selectedRole: Role = 'USER';
  isLoading = false;

  registerForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onRoleChange(role: Role): void {
    this.selectedRole = role;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const { name, email, password } = this.registerForm.value;

    this.authService.register({
      name: name!,
      email: email!,
      password: password!,
      role: this.selectedRole
    }).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.notificationService.showSuccess(
          `Welcome to TrekConnect, ${res.user.email}! Your account has been created successfully.`,
          'Registration Successful'
        );
        // Role-based redirect
        if (res.user.role === 'ORGANIZER') {
          this.router.navigate(['/organizer/dashboard']);
        } else {
          this.router.navigate(['/user/home']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err?.error?.message || 'Registration failed. Please check your inputs.';
        this.notificationService.showError(msg, 'Registration Error');
      }
    });
  }

  get nameError(): string {
    const control = this.registerForm.get('name');
    if (control?.touched && control.errors) {
      if (control.errors['required']) return 'Full name is required';
      if (control.errors['minlength']) return 'Name must be at least 2 characters';
    }
    return '';
  }

  get emailError(): string {
    const control = this.registerForm.get('email');
    if (control?.touched && control.errors) {
      if (control.errors['required']) return 'Email address is required';
      if (control.errors['email']) return 'Please enter a valid email address';
    }
    return '';
  }

  get passwordError(): string {
    const control = this.registerForm.get('password');
    if (control?.touched && control.errors) {
      if (control.errors['required']) return 'Password is required';
      if (control.errors['minlength']) return 'Password must be at least 8 characters';
    }
    return '';
  }
}
