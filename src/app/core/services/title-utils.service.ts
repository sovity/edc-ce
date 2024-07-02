import {Injectable} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router,} from '@angular/router';
import {concat, of} from 'rxjs';
import {filter, map, shareReplay} from 'rxjs/operators';

@Injectable()
export class TitleUtilsService {
  routeData$ = this.routeDone$().pipe(
    map(() => this.getRouteDataRecursively(), shareReplay(1)),
  );

  title$ = this.routeData$.pipe(map((data) => data.title));

  constructor(
    private router: Router,
    private titleService: Title,
    private activatedRoute: ActivatedRoute,
  ) {}

  startUpdatingTitleFromRouteData(defaultTitle: string) {
    this.title$.subscribe((title) => {
      let fullTitle = title ?? defaultTitle;
      this.titleService.setTitle(fullTitle);
    });
  }

  private routeDone$() {
    return concat(
      of({}),
      this.router.events.pipe(
        filter((event) => event instanceof NavigationEnd),
      ),
    );
  }

  private getRouteDataRecursively(): any {
    let snapshot: ActivatedRouteSnapshot | null = this.activatedRoute.snapshot;
    let data = {};
    while (snapshot) {
      data = {...data, ...snapshot.data};
      snapshot = snapshot.firstChild;
    }
    return data;
  }
}
