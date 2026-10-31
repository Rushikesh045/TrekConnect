import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastNotificationComponent } from './shared/components/toast-notification/toast-notification.component';
import { TrekBotComponent } from './shared/components/trek-bot/trek-bot.component';
import { WebSocketService } from './core/services/websocket.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastNotificationComponent, TrekBotComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'TrekConnect Platform';
  private wsService = inject(WebSocketService);

  ngOnInit(): void {
    // Initialize real-time WebSocket connection to TrekConnect STOMP broker
    this.wsService.init();
  }
}
