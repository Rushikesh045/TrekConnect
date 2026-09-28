import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Role } from '../../../core/models/auth.model';

@Component({
  selector: 'app-role-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './role-selector.component.html',
  styleUrl: './role-selector.component.scss'
})
export class RoleSelectorComponent {
  @Input() selectedRole: Role = 'USER';
  @Input() allowAdminRole: boolean = true;  // Set false on register page to prevent self-registration as Admin
  @Input() label: string = 'Sign in as';
  @Output() roleChange = new EventEmitter<Role>();

  private allRoles: { value: Role; label: string; icon: string }[] = [
    { value: 'USER', label: 'Trekker', icon: 'ri-user-heart-line' },
    { value: 'ORGANIZER', label: 'Organizer', icon: 'ri-flag-2-line' },
    { value: 'ADMIN', label: 'Platform Admin', icon: 'ri-shield-star-line' }
  ];

  get roles(): { value: Role; label: string; icon: string }[] {
    return this.allowAdminRole ? this.allRoles : this.allRoles.filter(r => r.value !== 'ADMIN');
  }

  selectRole(role: Role): void {
    this.selectedRole = role;
    this.roleChange.emit(role);
  }
}
