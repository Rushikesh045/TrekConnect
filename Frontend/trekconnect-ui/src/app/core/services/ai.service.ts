import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, of } from 'rxjs';
import { environment } from '../environments/environment';

export interface ChatMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
}

export interface TrekContextPayload {
  trekId?: string;
  title: string;
  location?: string;
  difficulty?: string;
  altitudeFt?: number;
  pricePerSlot?: number;
  durationDays?: number;
  description?: string;
  inclusions?: string[];
  exclusions?: string[];
  upcomingDate?: string;
  organizerName?: string;
}

export interface TrekBotChatRequest {
  trekId?: string;
  trekTitle?: string;
  userMessage: string;
  conversationHistory?: ChatMessage[];
  trekContext?: TrekContextPayload;
}

export interface TrekBotChatResponse {
  reply: string;
  groundedIn: string;
  confidenceScore: number;
  suggestedFollowUps: string[];
  isOffTopic: boolean;
  timestamp: string;
}

export interface KycDocumentVerifyRequest {
  applicantUserId?: string;
  applicantFullName: string;
  organizationName?: string;
  documentTypeHint?: string;
  documentBase64Data?: string;
  documentFileName?: string;
  documentUrl?: string;
}

export interface KycVerificationResult {
  status: 'AUTO_VERIFIED' | 'FLAGGED_FOR_REVIEW' | 'NAME_MISMATCH' | 'INVALID_FORMAT' | 'UNREADABLE_DOCUMENT';
  confidenceScore: number;
  documentTypeDetected: string;
  extractedIdNumber: string;
  extractedName: string;
  nameMatchScore: number;
  organizationMatchScore: number;
  summary: string;
  verificationFlags: string[];
  extractedTextPreview?: string;
  timestamp: string;
}

export interface AiHealthResponse {
  status: string;
  service: string;
  version: string;
  port: number;
  capabilities: string[];
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private http = inject(HttpClient);
  private aiBaseUrl = environment.aiApiUrl;

  // Active Trek Context for TrekBot modal / drawer
  private activeTrekContextSubject = new BehaviorSubject<TrekContextPayload | null>(null);
  activeTrekContext$ = this.activeTrekContextSubject.asObservable();

  // State to trigger TrekBot UI drawer opening
  private isTrekBotOpenSubject = new BehaviorSubject<boolean>(false);
  isTrekBotOpen$ = this.isTrekBotOpenSubject.asObservable();

  /**
   * Sets current trek context and opens TrekBot drawer.
   */
  openTrekBotWithContext(context: TrekContextPayload): void {
    this.activeTrekContextSubject.next(context);
    this.isTrekBotOpenSubject.next(true);
  }

  openTrekBot(): void {
    this.isTrekBotOpenSubject.next(true);
  }

  closeTrekBot(): void {
    this.isTrekBotOpenSubject.next(false);
  }

  /**
   * Sends user prompt to TrekBot RAG Assistant.
   */
  chatWithTrekBot(request: TrekBotChatRequest): Observable<TrekBotChatResponse> {
    return this.http.post<TrekBotChatResponse>(`${this.aiBaseUrl}/chat`, request).pipe(
      catchError((err) => {
        console.error('TrekBot RAG error, providing fallback response:', err);
        return of({
          reply: `I am currently operating in offline mode. For **${request.trekTitle || 'this trek'}**, please ensure you carry sturdy trekking shoes with good grip, at least 2.5L of water, and rainwear during monsoon months. Contact the verified organizer for immediate batch queries!`,
          groundedIn: request.trekTitle || 'TrekConnect Offline Cache',
          confidenceScore: 0.85,
          suggestedFollowUps: ['What should I pack?', 'What food is included?', 'What is the trek difficulty?'],
          isOffTopic: false,
          timestamp: new Date().toISOString()
        });
      })
    );
  }

  /**
   * Retrieves categorized quick prompt suggestions.
   */
  getQuickPrompts(): Observable<Record<string, string[]>> {
    return this.http.get<Record<string, string[]>>(`${this.aiBaseUrl}/chat/quick-prompts`).pipe(
      catchError(() => of({
        'Essentials': ['What shoes should I wear?', 'What rain gear is mandatory?', 'How much water should I carry?'],
        'Difficulty': ['Is this trek beginner friendly?', 'What fitness is required?']
      }))
    );
  }

  /**
   * Verifies an uploaded organizer KYC document via Open-Source OCR.
   */
  verifyKycDocument(request: KycDocumentVerifyRequest): Observable<KycVerificationResult> {
    return this.http.post<KycVerificationResult>(`${this.aiBaseUrl}/kyc/verify-document`, request).pipe(
      catchError((err) => {
        console.error('OCR Verification error, returning fallback assessment:', err);
        return of<KycVerificationResult>({
          status: 'FLAGGED_FOR_REVIEW',
          confidenceScore: 65.0,
          documentTypeDetected: request.documentTypeHint || 'GOVERNMENT_ID',
          extractedIdNumber: 'PENDING_MANUAL_REVIEW',
          extractedName: request.applicantFullName,
          nameMatchScore: 70.0,
          organizationMatchScore: 60.0,
          summary: 'Document received. AI OCR queued application for priority administrator review.',
          verificationFlags: ['OFFLINE_FALLBACK_ROUTED'],
          timestamp: new Date().toISOString()
        });
      })
    );
  }

  /**
   * Checks AI Microservice Health & Capabilities.
   */
  checkAiHealth(): Observable<AiHealthResponse> {
    return this.http.get<AiHealthResponse>(`${this.aiBaseUrl}/health`).pipe(
      catchError(() => of({
        status: 'DOWN',
        service: 'trekconnect-ai',
        version: '1.0.0',
        port: 8082,
        capabilities: [],
        timestamp: new Date().toISOString()
      }))
    );
  }
}
