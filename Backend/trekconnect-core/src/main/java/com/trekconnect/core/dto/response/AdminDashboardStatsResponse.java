package com.trekconnect.core.dto.response;

/**
 * Response DTO returning platform summary statistics for Admin Dashboard.
 */
public class AdminDashboardStatsResponse {

    private long totalUsers;
    private long totalOrganizers;
    private long pendingApprovals;
    private long totalTreks;

    public AdminDashboardStatsResponse() {
    }

    public AdminDashboardStatsResponse(long totalUsers, long totalOrganizers, long pendingApprovals, long totalTreks) {
        this.totalUsers = totalUsers;
        this.totalOrganizers = totalOrganizers;
        this.pendingApprovals = pendingApprovals;
        this.totalTreks = totalTreks;
    }

    public static AdminDashboardStatsResponseBuilder builder() {
        return new AdminDashboardStatsResponseBuilder();
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalOrganizers() {
        return totalOrganizers;
    }

    public void setTotalOrganizers(long totalOrganizers) {
        this.totalOrganizers = totalOrganizers;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public long getTotalTreks() {
        return totalTreks;
    }

    public void setTotalTreks(long totalTreks) {
        this.totalTreks = totalTreks;
    }

    public static class AdminDashboardStatsResponseBuilder {
        private long totalUsers;
        private long totalOrganizers;
        private long pendingApprovals;
        private long totalTreks;

        public AdminDashboardStatsResponseBuilder totalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public AdminDashboardStatsResponseBuilder totalOrganizers(long totalOrganizers) {
            this.totalOrganizers = totalOrganizers;
            return this;
        }

        public AdminDashboardStatsResponseBuilder pendingApprovals(long pendingApprovals) {
            this.pendingApprovals = pendingApprovals;
            return this;
        }

        public AdminDashboardStatsResponseBuilder totalTreks(long totalTreks) {
            this.totalTreks = totalTreks;
            return this;
        }

        public AdminDashboardStatsResponse build() {
            return new AdminDashboardStatsResponse(totalUsers, totalOrganizers, pendingApprovals, totalTreks);
        }
    }
}
