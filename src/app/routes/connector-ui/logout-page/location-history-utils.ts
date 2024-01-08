import {Location} from '@angular/common';
import {Injectable} from '@angular/core';
import {PreviousRouteListener} from './previous-route-listener';

/**
 * Required because:
 *  - The logout action is currently a "page"/route instead of a navigation entry with a click/enter handler.
 *  - This page should not be in location history or the back button won't work.
 *  - For that we need to replace the logout page's state with the correct URL to return to.
 *  - For that we need that URL in the first place.
 */
@Injectable()
export class LocationHistoryUtils {
  constructor(
    private location: Location,
    private previousRouteListener: PreviousRouteListener,
  ) {}

  replaceStateWithPreviousUrl(opts: {skipUrlsStartingWith: string}) {
    const goodReplacementUrl = this.getGoodReplacementUrl(opts);
    this.location.replaceState(goodReplacementUrl);
  }

  getGoodReplacementUrl(opts: {skipUrlsStartingWith: string}): string {
    const urlsToTry: (string | null)[] = [
      this.previousRouteListener.currentUrl,
      this.previousRouteListener.previousUrl,

      // Fallback to dashboard
      '/',
    ];

    const url = urlsToTry.find(
      (url) => url && !url.startsWith(opts.skipUrlsStartingWith),
    );

    return url ?? urlsToTry[urlsToTry.length - 1]!!;
  }
}
