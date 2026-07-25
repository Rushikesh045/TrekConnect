import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-card-layout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth-card-layout.component.html',
  styleUrl: './auth-card-layout.component.scss'
})
export class AuthCardLayoutComponent {
  @Input() title = 'Welcome Back';
  @Input() subtitle = 'Sign in to access your expeditions and bookings';
  @Input() heroTitle = 'Explore India’s Most Breathtaking Treks';
  @Input() heroSubtitle = 'Connect with verified organizers, book seamless adventures, and embark on unforgettable journeys.';
}
