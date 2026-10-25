import { Injectable, inject } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { environment } from '../environments/environment';
import { NotificationService } from './notification.service';
import { TokenService } from './token.service';

export interface WsNotification {
  id?: string;
  userId?: string;
  type?: 'SUCCESS' | 'INFO' | 'WARNING' | 'ERROR';
  title?: string;
  message: string;
  createdAt?: string;
}

export interface SeatUpdateMessage {
  eventId: string;
  availableSeats: number;
  totalSeats: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private notificationService = inject(NotificationService);
  private tokenService = inject(TokenService);

  private stompClient: Client | null = null;
  private isConnected = false;
  private activeSubscriptions = new Map<string, StompSubscription>();

  /**
   * Initialize STOMP client over native WebSocket.
   */
  init(): void {
    if (this.stompClient && this.isConnected) {
      return;
    }

    const wsUrl = environment.wsUrl || 'ws://localhost:8081/ws';

    this.stompClient = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 8000,
      heartbeatIncoming: 15000,
      heartbeatOutgoing: 15000,
      debug: (msg: string) => {
        // Optional quiet debug output
        // console.debug('[STOMP]', msg);
      },
      onConnect: () => {
        this.isConnected = true;
        console.log('[WebSocketService] Connected to TrekConnect Real-Time Broker.');
        this.autoSubscribeCurrentUser();
      },
      onDisconnect: () => {
        this.isConnected = false;
        this.activeSubscriptions.clear();
        console.log('[WebSocketService] Disconnected from TrekConnect Broker.');
      },
      onStompError: (frame) => {
        console.warn('[WebSocketService] STOMP Broker Error:', frame.headers['message']);
      },
      onWebSocketError: (event) => {
        // Quietly fail or retry if backend is starting up or offline
        console.debug('[WebSocketService] WebSocket transport connection notice.');
      }
    });

    try {
      this.stompClient.activate();
    } catch (err) {
      console.warn('[WebSocketService] Could not activate WebSocket client:', err);
    }
  }

  /**
   * Subscribe automatically to the authenticated user's notification channel.
   */
  private autoSubscribeCurrentUser(): void {
    const user = this.tokenService.getUser();
    if (!user || !user.id) return;

    this.subscribeToNotifications(user.id);
  }

  /**
   * Subscribe to real-time personal alerts for user.
   */
  subscribeToNotifications(userId: string): void {
    if (!this.stompClient || !this.isConnected) return;

    const topic = `/topic/notifications/${userId}`;
    if (this.activeSubscriptions.has(topic)) return;

    try {
      const sub = this.stompClient.subscribe(topic, (message: IMessage) => {
        try {
          const payload: WsNotification = JSON.parse(message.body);
          this.handleIncomingNotification(payload);
        } catch {
          this.notificationService.showInfo(message.body, 'New Notification');
        }
      });
      this.activeSubscriptions.set(topic, sub);
      console.log(`[WebSocketService] Subscribed to notifications channel: ${topic}`);
    } catch (err) {
      console.warn(`[WebSocketService] Failed to subscribe to ${topic}`, err);
    }
  }

  /**
   * Subscribe to real-time seat availability updates for a specific event batch.
   */
  subscribeToSeatUpdates(eventId: string, onUpdate: (seats: SeatUpdateMessage) => void): StompSubscription | null {
    if (!this.stompClient || !this.isConnected) return null;

    const topic = `/topic/events/${eventId}/seats`;
    try {
      return this.stompClient.subscribe(topic, (message: IMessage) => {
        try {
          const data: SeatUpdateMessage = JSON.parse(message.body);
          onUpdate(data);
        } catch (e) {
          console.error('[WebSocketService] Failed to parse seat update', e);
        }
      });
    } catch (err) {
      console.warn(`[WebSocketService] Failed to subscribe to seat updates for ${eventId}`, err);
      return null;
    }
  }

  /**
   * Subscribe to booking status changes for user.
   */
  subscribeToBookingUpdates(userId: string, onUpdate: (data: any) => void): StompSubscription | null {
    if (!this.stompClient || !this.isConnected) return null;

    const topic = `/topic/user/${userId}/booking-updates`;
    try {
      return this.stompClient.subscribe(topic, (message: IMessage) => {
        try {
          const data = JSON.parse(message.body);
          onUpdate(data);
        } catch (e) {
          console.error('[WebSocketService] Failed to parse booking update', e);
        }
      });
    } catch (err) {
      console.warn(`[WebSocketService] Failed to subscribe to booking updates for ${userId}`, err);
      return null;
    }
  }

  private handleIncomingNotification(payload: WsNotification): void {
    const title = payload.title || 'Live Notification';
    const message = payload.message || '';

    switch (payload.type) {
      case 'SUCCESS':
        this.notificationService.showSuccess(message, title);
        break;
      case 'WARNING':
        this.notificationService.showWarning(message, title);
        break;
      case 'ERROR':
        this.notificationService.showError(message, title);
        break;
      case 'INFO':
      default:
        this.notificationService.showInfo(message, title);
        break;
    }
  }

  /**
   * Disconnect client and clear all subscriptions.
   */
  disconnect(): void {
    if (this.stompClient) {
      try {
        this.stompClient.deactivate();
      } catch (e) {
        console.warn('[WebSocketService] Error during deactivation', e);
      }
      this.isConnected = false;
      this.activeSubscriptions.clear();
    }
  }
}
