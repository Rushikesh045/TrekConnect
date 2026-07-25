import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-action-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './action-button.component.html',
  styleUrl: './action-button.component.scss'
})
export class ActionButtonComponent {
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() variant: 'primary' | 'secondary' | 'outline' = 'primary';
  @Input() isLoading = false;
  @Input() isDisabled = false;
  @Input() fullWidth = true;
  @Input() iconClass = '';

  @Output() btnClick = new EventEmitter<Event>();

  onClick(event: Event): void {
    if (!this.isDisabled && !this.isLoading) {
      this.btnClick.emit(event);
    }
  }
}
