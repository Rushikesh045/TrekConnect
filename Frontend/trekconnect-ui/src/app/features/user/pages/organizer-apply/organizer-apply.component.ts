import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrganizerService, OrganizerApplicationResponse } from '../../../../core/services/organizer.service';
import { NotificationService } from '../../../../core/services/notification.service';

/**
 * Organizer Application Onboarding Component.
 * 
 * WHY THIS COMPONENT WAS CREATED:
 * Allows trekkers to submit an application to become a verified Trek Organizer on TrekConnect.
 * Displays existing application verification status ('PENDING', 'VERIFIED', 'REJECTED') if submitted.
 */
@Component({
  selector: 'app-organizer-apply',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './organizer-apply.component.html',
  styleUrl: './organizer-apply.component.scss'
})
export class OrganizerApplyComponent implements OnInit {
  private fb = inject(FormBuilder);
  private organizerService = inject(OrganizerService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  isLoading = true;
  isSubmitting = false;
  existingApplication: OrganizerApplicationResponse | null = null;

  // Dual Document Option State: 'LOCAL' or 'URL'
  docSourceType: 'LOCAL' | 'URL' = 'LOCAL';
  uploadedFileName = '';
  uploadedFileSize = '';
  selectedFilePreview: string | null = null;

  applyForm = this.fb.group({
    organizationName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
    contactPhone: ['', [Validators.required]],
    cityLocation: ['', [Validators.required]],
    licenseNumber: [''],
    verificationDocsUrl: ['', [Validators.maxLength(5000000)]] // Allow Base64 Data URLs as well as web URLs
  });

  setDocSourceType(type: 'LOCAL' | 'URL'): void {
    this.docSourceType = type;
  }

  onLocalFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.uploadedFileName = file.name;
    this.uploadedFileSize = (file.size / (1024 * 1024)).toFixed(2) + ' MB';

    const reader = new FileReader();
    reader.onload = () => {
      const dataUrl = reader.result as string;
      this.selectedFilePreview = dataUrl;
      this.applyForm.patchValue({ verificationDocsUrl: dataUrl });
      this.notificationService.showSuccess(`Document "${file.name}" loaded successfully!`, 'File Selected');
    };
    reader.readAsDataURL(file);
  }

  removeSelectedFile(): void {
    this.uploadedFileName = '';
    this.uploadedFileSize = '';
    this.selectedFilePreview = null;
    this.applyForm.patchValue({ verificationDocsUrl: '' });
  }

  ngOnInit(): void {
    this.checkApplicationStatus();
  }

  checkApplicationStatus(): void {
    this.isLoading = true;
    this.organizerService.getMyApplicationStatus().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.existingApplication = res;
        this.applyForm.patchValue({
          organizationName: res.organizationName,
          contactPhone: res.contactPhone || '',
          cityLocation: res.cityLocation || '',
          licenseNumber: res.licenseNumber || '',
          verificationDocsUrl: res.verificationDocsUrl || ''
        });
      },
      error: () => {
        this.isLoading = false;
        this.existingApplication = null;
      }
    });
  }

  onSubmit(): void {
    if (this.applyForm.invalid) {
      this.applyForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const { organizationName, contactPhone, cityLocation, licenseNumber, verificationDocsUrl } = this.applyForm.value;

    this.organizerService.applyForOrganizer({
      organizationName: organizationName!,
      contactPhone: contactPhone || undefined,
      cityLocation: cityLocation || undefined,
      licenseNumber: licenseNumber || undefined,
      verificationDocsUrl: verificationDocsUrl || undefined
    }).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        this.existingApplication = res;
        this.notificationService.showSuccess(
          `Your application for "${res.organizationName}" has been submitted for Admin verification.`,
          'Application Submitted'
        );
      },
      error: (err) => {
        this.isSubmitting = false;
        const msg = err?.error?.message || 'Failed to submit application. Please try again.';
        this.notificationService.showError(msg, 'Submission Failed');
      }
    });
  }
}
