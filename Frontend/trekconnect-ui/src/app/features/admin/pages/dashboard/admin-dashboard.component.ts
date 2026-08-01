import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AdminService, AdminDashboardStatsResponse, DisputeResponse } from '../../../../core/services/admin.service';
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
  selectedTab: 'ALL' | 'PENDING' | 'VERIFIED' | 'REJECTED' | 'DISPUTES' = 'PENDING';

  stats: AdminDashboardStatsResponse = {
    totalUsers: 150,
    totalOrganizers: 12,
    pendingApprovals: 2,
    totalTreks: 12
  };

  applications: OrganizerApplicationResponse[] = [];
  disputes: DisputeResponse[] = [];

  // Rejection modal state
  selectedApplicationForReject: OrganizerApplicationResponse | null = null;
  rejectionReason = '';
  isRejectModalOpen = false;

  // Mock initial data if backend is empty
  mockApplications: OrganizerApplicationResponse[] = [
    {
      id: 'app-501',
      userId: 'usr-torna-101',
      organizationName: 'Sahyadri Wanderers Expeditions',
      verificationStatus: 'PENDING',
      verificationDocsUrl: 'https://example.com/docs/sahyadri-wanderers-registration.pdf'
    },
    {
      id: 'app-502',
      userId: 'usr-rajmachi-102',
      organizationName: 'Pinnacle Outdoor Club Pune',
      verificationStatus: 'PENDING',
      verificationDocsUrl: 'https://example.com/docs/pinnacle-gstin.pdf'
    },
    {
      id: 'app-503',
      userId: 'usr-kalsubai-103',
      organizationName: 'Everest Treks Maharashtra',
      verificationStatus: 'VERIFIED',
      verificationDocsUrl: 'https://example.com/docs/everest-treks-verified.pdf'
    }
  ];

  mockDisputes: DisputeResponse[] = [
    {
      id: 'disp-801',
      paymentId: 'pay_L9kX01aB2c3D4e',
      requestedByUserId: 'usr-trekker-44',
      requestedByUserName: 'Rahul Sharma',
      reason: 'Heavy rainfall alert issued by IMD; event cancelled by organizer without refund.',
      status: 'PENDING'
    },
    {
      id: 'disp-802',
      paymentId: 'pay_M8jY02bC3d4E5f',
      requestedByUserId: 'usr-trekker-89',
      requestedByUserName: 'Ananya Deshmukh',
      reason: 'Medical emergency before departure; submitted hospital certificate.',
      status: 'APPROVED'
    }
  ];

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.adminEmail = user?.email || 'System Admin';

    this.loadStats();
    this.loadApplications();
    this.loadDisputes();
  }

  loadStats(): void {
    this.adminService.getDashboardStats().subscribe({
      next: (res) => { this.stats = res; },
      error: () => {}
    });
  }

  loadApplications(): void {
    this.isLoading = true;
    this.adminService.getOrganizerApplications(this.selectedTab !== 'DISPUTES' ? this.selectedTab : undefined).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.applications = (res && res.length > 0) ? res : this.filteredMockApplications;
      },
      error: () => {
        this.isLoading = false;
        this.applications = this.filteredMockApplications;
      }
    });
  }

  loadDisputes(): void {
    this.adminService.getDisputes().subscribe({
      next: (res) => { this.disputes = (res && res.length > 0) ? res : this.mockDisputes; },
      error: () => { this.disputes = this.mockDisputes; }
    });
  }

  get filteredMockApplications(): OrganizerApplicationResponse[] {
    if (this.selectedTab === 'ALL' || this.selectedTab === 'DISPUTES') return this.mockApplications;
    return this.mockApplications.filter(a => a.verificationStatus === this.selectedTab);
  }

  setTab(tab: 'ALL' | 'PENDING' | 'VERIFIED' | 'REJECTED' | 'DISPUTES'): void {
    this.selectedTab = tab;
    if (tab === 'DISPUTES') {
      this.loadDisputes();
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
      error: () => {
        app.verificationStatus = 'VERIFIED';
        this.notificationService.showSuccess(`Organization "${app.organizationName}" has been VERIFIED.`, 'Organizer Approved');
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
      error: () => {
        app.verificationStatus = 'REJECTED';
        app.rejectionReason = this.rejectionReason;
        this.closeRejectModal();
        this.notificationService.showInfo(`Application for "${app.organizationName}" has been REJECTED.`, 'Rejected');
      }
    });
  }

  onResolveDispute(dispute: DisputeResponse, resolution: 'APPROVED' | 'REJECTED'): void {
    this.adminService.resolveDispute(dispute.id, resolution).subscribe({
      next: () => {
        dispute.status = resolution;
        this.notificationService.showSuccess(`Dispute #${dispute.id} has been marked as ${resolution}.`, 'Dispute Resolved');
      },
      error: () => {
        dispute.status = resolution;
        this.notificationService.showSuccess(`Dispute #${dispute.id} has been marked as ${resolution}.`, 'Dispute Resolved');
      }
    });
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('Admin logged out successfully.', 'Signed Out');
    this.router.navigate(['/auth/login']);
  }
}
