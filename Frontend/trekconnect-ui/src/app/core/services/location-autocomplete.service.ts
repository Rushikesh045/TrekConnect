import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

/**
 * Angular Service providing free Location Autocomplete Suggestions.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Provides dynamic location suggestions as organizers type into the location search input box.
 */
@Injectable({
  providedIn: 'root'
})
export class LocationAutocompleteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/locations/suggest`;

  /**
   * Fetches location suggestions for a search query string.
   */
  getSuggestions(query: string): Observable<string[]> {
    console.log(`[LocationAutocompleteService] Fetching location suggestions for query: ${query}`);
    return this.http.get<string[]>(`${this.apiUrl}?q=${encodeURIComponent(query)}`);
  }
}
