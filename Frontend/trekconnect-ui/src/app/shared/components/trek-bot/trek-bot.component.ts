import { Component, OnInit, OnDestroy, inject, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AiService, ChatMessage, TrekBotChatResponse, TrekContextPayload } from '../../../core/services/ai.service';

interface DisplayMessage {
  role: 'user' | 'assistant';
  content: string;
  groundedIn?: string;
  confidence?: number;
  timestamp: Date;
}

@Component({
  selector: 'app-trek-bot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trek-bot.component.html',
  styleUrls: ['./trek-bot.component.scss']
})
export class TrekBotComponent implements OnInit, OnDestroy, AfterViewChecked {
  private aiService = inject(AiService);
  private subs = new Subscription();

  @ViewChild('chatScrollContainer') private chatScrollContainer!: ElementRef;

  isOpen = false;
  isLoading = false;
  userPrompt = '';
  activeContext: TrekContextPayload | null = null;

  messages: DisplayMessage[] = [
    {
      role: 'assistant',
      content: 'Hello adventurer! 🏔️ I am **TrekBot**, your AI mountain expedition guide. Ask me anything about trail difficulty, mandatory monsoon/winter gear, packing checklists, meal inclusions, or safety protocols!',
      groundedIn: 'TrekConnect Outdoor Knowledge Base',
      confidence: 1.0,
      timestamp: new Date()
    }
  ];

  suggestedFollowUps: string[] = [
    '🎒 What should I pack for this trek?',
    '🧗 Is this suitable for beginners?',
    '🍲 What meals and gear are included?',
    '🌧️ What safety rules apply in heavy rain?'
  ];

  private shouldScrollToBottom = false;

  ngOnInit(): void {
    this.subs.add(
      this.aiService.isTrekBotOpen$.subscribe((open) => {
        this.isOpen = open;
        if (open) {
          this.shouldScrollToBottom = true;
        }
      })
    );

    this.subs.add(
      this.aiService.activeTrekContext$.subscribe((ctx) => {
        if (ctx) {
          this.activeContext = ctx;
          // Add a contextual greeting if not already present
          this.messages.push({
            role: 'assistant',
            content: `Loaded context for **${ctx.title}** (${ctx.difficulty || 'Moderate'}, ${ctx.altitudeFt ? ctx.altitudeFt.toLocaleString('en-IN') + ' ft' : 'Mountain'}). How can I help you prepare for this expedition?`,
            groundedIn: ctx.title,
            confidence: 1.0,
            timestamp: new Date()
          });
          this.shouldScrollToBottom = true;
        }
      })
    );
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.shouldScrollToBottom = true;
    }
  }

  closeChat(): void {
    this.isOpen = false;
    this.aiService.closeTrekBot();
  }

  sendMessage(presetPrompt?: string): void {
    const text = presetPrompt || this.userPrompt;
    if (!text || !text.trim() || this.isLoading) return;

    const trimmed = text.trim();
    this.userPrompt = '';

    // Append user message
    this.messages.push({
      role: 'user',
      content: trimmed,
      timestamp: new Date()
    });
    this.shouldScrollToBottom = true;
    this.isLoading = true;

    // Build conversation history for RAG context
    const history: ChatMessage[] = this.messages.slice(-6).map((m) => ({
      role: m.role,
      content: m.content
    }));

    this.aiService.chatWithTrekBot({
      trekId: this.activeContext?.trekId,
      trekTitle: this.activeContext?.title || undefined,
      userMessage: trimmed,
      conversationHistory: history,
      trekContext: this.activeContext || undefined
    }).subscribe({
      next: (res: TrekBotChatResponse) => {
        this.isLoading = false;
        this.messages.push({
          role: 'assistant',
          content: res.reply,
          groundedIn: res.groundedIn,
          confidence: res.confidenceScore,
          timestamp: new Date()
        });

        if (res.suggestedFollowUps && res.suggestedFollowUps.length > 0) {
          this.suggestedFollowUps = res.suggestedFollowUps;
        }
        this.shouldScrollToBottom = true;
      },
      error: () => {
        this.isLoading = false;
        this.messages.push({
          role: 'assistant',
          content: 'Sorry, I encountered a temporary connection issue. Please check your network or ask another question!',
          timestamp: new Date()
        });
        this.shouldScrollToBottom = true;
      }
    });
  }

  selectFollowUp(question: string): void {
    this.sendMessage(question);
  }

  clearChat(): void {
    this.messages = [
      {
        role: 'assistant',
        content: 'Chat refreshed! How can I help you plan your next mountain summit?',
        groundedIn: 'TrekBot Assistant',
        confidence: 1.0,
        timestamp: new Date()
      }
    ];
    this.shouldScrollToBottom = true;
  }

  private scrollToBottom(): void {
    try {
      if (this.chatScrollContainer) {
        this.chatScrollContainer.nativeElement.scrollTop = this.chatScrollContainer.nativeElement.scrollHeight;
      }
    } catch (err) {
      // scroll container safety
    }
  }

  formatMessageContent(msg: DisplayMessage): string {
    if (!msg || !msg.content) return '';

    // If user message, strictly escape all HTML tags to prevent XSS
    if (msg.role === 'user') {
      return this.escapeHtml(msg.content);
    }

    // Assistant message: escape first, then convert markdown syntax
    let formatted = this.escapeHtml(msg.content);

    // Convert ### Headers
    formatted = formatted.replace(/^###\s+(.*$)/gim, '<h4 class="md-heading">$1</h4>');
    formatted = formatted.replace(/^##\s+(.*$)/gim, '<h3 class="md-heading">$1</h3>');

    // Convert **bold**
    formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

    // Convert *italic*
    formatted = formatted.replace(/\*(.*?)\*/g, '<em>$1</em>');

    // Convert `code`
    formatted = formatted.replace(/`([^`]+)`/g, '<code class="md-code">$1</code>');

    // Convert list bullets
    formatted = formatted.replace(/^[•\-\*]\s+(.*$)/gim, '<div class="md-bullet"><span class="bullet-dot">•</span> <span>$1</span></div>');

    // Convert newlines to breaks
    formatted = formatted.replace(/\n/g, '<br/>');

    // Clean up redundant breaks after headings
    formatted = formatted.replace(/<\/h4><br\/>/g, '</h4>');
    formatted = formatted.replace(/<\/h3><br\/>/g, '</h3>');
    formatted = formatted.replace(/<\/div><br\/>/g, '</div>');

    return formatted;
  }

  private escapeHtml(str: string): string {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
