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
  @Output() roleChange = new EventEmitter<Role>();

  roles: { value: Role; label: string; icon: string }[] = [
    { value: 'USER', label: 'Trekker', icon: 'ri-user-heart-line' },
    { value: 'ORGANIZER', label: 'Organizer', icon: 'ri-flag-2-line' },
    { value: 'ADMIN', label: 'Platform Admin', icon: 'ri-shield-star-line' }
  ];

  selectRole(role: Role): void {
    this.selectedRole = role;
    this.roleChange.emit(role);
  }
}
