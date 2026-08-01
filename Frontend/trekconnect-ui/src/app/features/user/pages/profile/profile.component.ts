import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UserService, UserProfileResponse } from '../../../../core/services/user.service';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';

/**
 * User Profile Page Component.
 * 
 * WHY THIS COMPONENT WAS CREATED:
 * Allows authenticated trekkers to view and update their profile details (Full Name, Phone, Bio, Profile Avatar),
 * view active membership badges, and launch the Organizer Application process.
 */
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private tokenService = inject(TokenService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  isLoading = true;
  isSaving = false;
  userEmail = '';
  userRole = '';
  profileData: UserProfileResponse | null = null;

  profileForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
    phone: ['', [Validators.pattern('^[0-9+\\-\\s]{8,15}$')]],
    bio: ['', [Validators.maxLength(1000)]],
    profilePicUrl: ['', [Validators.maxLength(500)]]
  });

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.userEmail = user?.email || 'Trekker';
    this.userRole = this.tokenService.getRole() || 'USER';

    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.userService.getProfile().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.profileData = res;
        this.profileForm.patchValue({
          name: res.name || '',
          phone: res.phone || '',
          bio: res.bio || '',
          profilePicUrl: res.profilePicUrl || ''
        });
      },
      error: () => {
        this.isLoading = false;
        // Fallback default values
        this.profileForm.patchValue({
          name: this.userEmail.split('@')[0]
        });
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    const { name, phone, bio, profilePicUrl } = this.profileForm.value;

    this.userService.updateProfile({
      name: name!,
      phone: phone || undefined,
      bio: bio || undefined,
      profilePicUrl: profilePicUrl || undefined
    }).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.profileData = res;
        this.notificationService.showSuccess('Your profile has been saved successfully!', 'Profile Updated');
      },
      error: (err) => {
        this.isSaving = false;
        const msg = err?.error?.message || 'Failed to update profile. Please try again.';
        this.notificationService.showError(msg, 'Update Failed');
      }
    });
  }
}
