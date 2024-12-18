import {Injectable} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';

/**
 * Required because:
 *  - The logout action is currently a "page"/route instead of a navigation entry with a click/enter handler.
 *  - This page should not be in location history or the back button won't work.
 *  - For that we need to replace the logout page's state with the correct URL to return to.
 *  - For that we need that URL in the first place.
 */
@Injectable()
export class PreviousRouteListener {
  previousUrl: string | null = null;
  currentUrl: string | null = null;

  constructor(private router: Router) {
    this.startListeningToUrlChanges();
  }

  private startListeningToUrlChanges() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.previousUrl = this.currentUrl;
        this.currentUrl = event.url;
      }
    });
  }
}
