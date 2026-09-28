import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AdminService, AdminDashboardStatsResponse, DisputeResponse, RegisteredUserResponse } from '../../../../core/services/admin.service';
import { OrganizerApplicationResponse } from '../../../../core/services/organizer.service';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';

/**
 * ADMIN Role Verification, Dispute Oversight & System Management Dashboard Component.
 */
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  private tokenService = inject(TokenService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  isLoading = true;
  adminEmail = '';
  selectedTab: 'ALL' | 'PENDING' | 'VERIFIED' | 'REJECTED' | 'USERS' | 'DISPUTES' = 'PENDING';

  stats: AdminDashboardStatsResponse = {
    totalUsers: 150,
    totalOrganizers: 12,
    pendingApprovals: 2,
    totalTreks: 12
  };

  applications: OrganizerApplicationResponse[] = [];
  disputes: DisputeResponse[] = [];
  registeredUsers: RegisteredUserResponse[] = [];

  // Rejection modal state
  selectedApplicationForReject: OrganizerApplicationResponse | null = null;
  rejectionReason = '';
  isRejectModalOpen = false;

  // Create Admin User modal state
  isCreateAdminModalOpen = false;
  isCreatingAdmin = false;
  newAdminName = '';
  newAdminEmail = '';
  newAdminRole = 'ADMIN';
  newAdminPassword = '';
  createdAdminResult: any = null;

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.adminEmail = user?.email || 'System Admin';

    this.loadStats();
    this.loadApplications();
    this.loadDisputes();
    this.loadRegisteredUsers();
  }

  openCreateAdminModal(): void {
    this.newAdminName = '';
    this.newAdminEmail = '';
    this.newAdminRole = 'ADMIN';
    this.newAdminPassword = '';
    this.createdAdminResult = null;
    this.isCreateAdminModalOpen = true;
  }

  closeCreateAdminModal(): void {
    this.isCreateAdminModalOpen = false;
    this.createdAdminResult = null;
  }

  submitCreateAdmin(): void {
    if (!this.newAdminName.trim() || !this.newAdminEmail.trim()) {
      this.notificationService.showError('Name and Email are required.', 'Validation Error');
      return;
    }

    this.isCreatingAdmin = true;
    this.adminService.createAdminUser({
      name: this.newAdminName.trim(),
      email: this.newAdminEmail.trim(),
      role: this.newAdminRole,
      password: this.newAdminPassword.trim() || undefined
    }).subscribe({
      next: (res) => {
        this.isCreatingAdmin = false;
        this.createdAdminResult = res;
        this.notificationService.showSuccess(`Account for ${res.email} created successfully!`, 'User Account Provisioned');
        this.loadRegisteredUsers();
        this.loadStats();
      },
      error: (err) => {
        this.isCreatingAdmin = false;
        const msg = err?.error?.message || 'Failed to create user account. Please check details.';
        this.notificationService.showError(msg, 'Creation Failed');
      }
    });
  }

  loadStats(): void {
    this.adminService.getDashboardStats().subscribe({
      next: (res) => { this.stats = res; },
      error: (err) => console.error('Error loading admin stats', err)
    });
  }

  loadApplications(): void {
    this.isLoading = true;
    this.adminService.getOrganizerApplications(this.selectedTab !== 'DISPUTES' && this.selectedTab !== 'USERS' ? this.selectedTab : undefined).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.applications = res || [];
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading organizer applications', err);
        this.applications = [];
      }
    });
  }

  loadDisputes(): void {
    this.adminService.getDisputes().subscribe({
      next: (res) => { this.disputes = res || []; },
      error: (err) => {
        console.error('Error loading disputes', err);
        this.disputes = [];
      }
    });
  }

  loadRegisteredUsers(): void {
    this.adminService.getAllRegisteredUsers().subscribe({
      next: (res) => { this.registeredUsers = res || []; },
      error: (err) => {
        console.error('Error loading registered users', err);
        this.registeredUsers = [];
      }
    });
  }

  setTab(tab: 'ALL' | 'PENDING' | 'VERIFIED' | 'REJECTED' | 'USERS' | 'DISPUTES'): void {
    this.selectedTab = tab;
    if (tab === 'DISPUTES') {
      this.loadDisputes();
    } else if (tab === 'USERS') {
      this.loadRegisteredUsers();
    } else {
      this.loadApplications();
    }
  }

  onVerify(app: OrganizerApplicationResponse): void {
    this.adminService.verifyOrganizer(app.id).subscribe({
      next: () => {
        app.verificationStatus = 'VERIFIED';
        this.notificationService.showSuccess(`Organization "${app.organizationName}" has been VERIFIED.`, 'Organizer Approved');
        this.loadStats();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Failed to verify organizer. Please try again.';
        this.notificationService.showError(msg, 'Verification Failed');
      }
    });
  }

  openRejectModal(app: OrganizerApplicationResponse): void {
    this.selectedApplicationForReject = app;
    this.rejectionReason = '';
    this.isRejectModalOpen = true;
  }

  closeRejectModal(): void {
    this.isRejectModalOpen = false;
    this.selectedApplicationForReject = null;
    this.rejectionReason = '';
  }

  confirmReject(): void {
    if (!this.rejectionReason || this.rejectionReason.trim().length < 5) {
      this.notificationService.showError('Please provide a rejection reason of at least 5 characters.', 'Reason Required');
      return;
    }

    const app = this.selectedApplicationForReject;
    if (!app) return;

    this.adminService.rejectOrganizer(app.id, this.rejectionReason).subscribe({
      next: () => {
        app.verificationStatus = 'REJECTED';
        app.rejectionReason = this.rejectionReason;
        this.closeRejectModal();
        this.notificationService.showInfo(`Application for "${app.organizationName}" has been REJECTED.`, 'Rejected');
        this.loadStats();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Failed to reject organizer. Please try again.';
        this.notificationService.showError(msg, 'Rejection Failed');
        this.closeRejectModal();
      }
    });
  }

  onResolveDispute(dispute: DisputeResponse, resolution: 'APPROVED' | 'REJECTED'): void {
    this.adminService.resolveDispute(dispute.id, resolution).subscribe({
      next: () => {
        dispute.status = resolution;
        const action = resolution === 'APPROVED' ? 'approved for refund' : 'dismissed';
        this.notificationService.showSuccess(`Dispute #${dispute.id.substring(0, 8)} has been ${action}.`, 'Dispute Resolved');
      },
      error: (err) => {
        const msg = err?.error?.message || 'Failed to resolve dispute. Please try again.';
        this.notificationService.showError(msg, 'Action Failed');
      }
    });
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('Admin logged out successfully.', 'Signed Out');
    this.router.navigate(['/auth/login']);
  }
}
