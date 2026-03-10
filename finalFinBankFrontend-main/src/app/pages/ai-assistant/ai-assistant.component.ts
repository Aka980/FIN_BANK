import { Component, OnInit } from '@angular/core';
import { LoanResponse } from '../../models/loan.model';
import { AiAssistantService } from '../../services/ai-assistant.service';
import { LoanService } from '../../services/loan.service';

type MessageRole = 'user' | 'assistant' | 'system';

interface AssistantMessage {
  role: MessageRole;
  text: string;
  timestamp: Date;
}

@Component({
  selector: 'app-ai-assistant',
  standalone: false,
  templateUrl: './ai-assistant.component.html',
  styleUrl: './ai-assistant.component.css'
})
export class AiAssistantComponent implements OnInit {
  loans: LoanResponse[] = [];
  selectedLoanId: number | null = null;

  prompt = '';
  messages: AssistantMessage[] = [];

  loadingLoans = false;
  checkingConnection = false;
  generatingSummary = false;
  sending = false;

  infoMessage = '';
  errorMessage = '';

  constructor(
    private readonly loanService: LoanService,
    private readonly aiAssistantService: AiAssistantService
  ) { }

  ngOnInit(): void {
    this.checkServiceConnection();
    this.loadCustomerLoans();
  }

  onLoanChange(rawValue: string | number | null): void {
    const parsedLoanId = Number(rawValue);
    this.selectedLoanId = Number.isFinite(parsedLoanId) ? parsedLoanId : null;
    this.errorMessage = '';
  }

  requestLoanSummary(): void {
    if (this.selectedLoanId === null || this.generatingSummary || this.sending) {
      return;
    }

    this.errorMessage = '';
    this.generatingSummary = true;

    this.aiAssistantService.getLoanSummary(this.selectedLoanId).subscribe({
      next: (summary) => {
        this.messages = [...this.messages, {
          role: 'assistant',
          text: summary,
          timestamp: new Date()
        }];
        this.generatingSummary = false;
      },
      error: (error: unknown) => {
        this.generatingSummary = false;
        this.errorMessage = this.getErrorMessage(error);
      }
    });
  }

  sendPrompt(): void {
    const message = this.prompt.trim();
    if (!message || this.selectedLoanId === null || this.sending) {
      return;
    }

    this.errorMessage = '';
    this.messages = [...this.messages, {
      role: 'user',
      text: message,
      timestamp: new Date()
    }];
    this.prompt = '';
    this.sending = true;

    this.aiAssistantService.chatWithLoan(this.selectedLoanId, message).subscribe({
      next: (response) => {
        this.messages = [...this.messages, {
          role: 'assistant',
          text: response,
          timestamp: new Date()
        }];
        this.sending = false;
      },
      error: (error: unknown) => {
        this.sending = false;
        this.errorMessage = this.getErrorMessage(error);
        this.messages = [...this.messages, {
          role: 'system',
          text: this.errorMessage,
          timestamp: new Date()
        }];
      }
    });
  }

  onPromptEnter(event: Event): void {
    const keyboardEvent = event as KeyboardEvent;

    if (keyboardEvent.shiftKey) {
      return;
    }

    keyboardEvent.preventDefault();
    this.sendPrompt();
  }

  private checkServiceConnection(): void {
    this.checkingConnection = true;
    this.infoMessage = 'Checking AI service connectivity...';

    this.aiAssistantService.testConnection().subscribe({
      next: () => {
        this.checkingConnection = false;
        this.infoMessage = 'AI service connected.';
      },
      error: () => {
        this.checkingConnection = false;
        this.infoMessage = 'AI service is unavailable right now. You can retry in a few moments.';
      }
    });
  }

  private loadCustomerLoans(): void {
    this.loadingLoans = true;

    this.loanService.getLoans().subscribe({
      next: (loans) => {
        this.loans = [...loans].sort((a, b) => b.applicationNo - a.applicationNo);
        this.selectedLoanId = this.loans.length > 0 ? this.loans[0].applicationNo : null;
        this.loadingLoans = false;
      },
      error: (error) => {
        this.loadingLoans = false;
        this.errorMessage = this.getErrorMessage(error);
      }
    });
  }

  private getErrorMessage(error: unknown): string {
    const response = error as { status?: number; error?: unknown; message?: string };

    if (response.status === 401 || response.status === 403) {
      return 'Authorization failed. Please login again and retry.';
    }

    if (response.status === 0) {
      return 'Unable to connect to backend services. Ensure API gateway and ai-service are running.';
    }

    if (typeof response.error === 'string' && response.error.trim().length > 0) {
      return response.error;
    }

    if (response.message && response.message.trim().length > 0) {
      return response.message;
    }

    return 'Failed to get AI response.';
  }
}
