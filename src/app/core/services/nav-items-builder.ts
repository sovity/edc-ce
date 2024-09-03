import {Injectable} from '@angular/core';
import {routes} from 'src/app/routes/connector-ui/connector-ui-routing.module';
import {ActiveFeatureSet} from '../config/active-feature-set';
import {NavItemGroup} from './models/nav-item-group';

/**
 * Builds NavItems from Angular routes
 */
@Injectable({
  providedIn: 'root',
})
export class NavItemsBuilder {
  private navItemGroups: NavItemGroup[] = [
    {
      items: [{path: 'dashboard', icon: 'data_usage', title: 'Dashboard'}],
    },
    {
      title: 'Providing',
      items: [
        {path: 'create-asset', icon: 'post_add', title: 'Create Data Offer'},
        {path: 'my-assets', icon: 'upload', title: 'Assets'},
        {path: 'policies', icon: 'policy', title: 'Policies'},
        {
          path: 'data-offers',
          icon: 'rule',
          title: 'Data Offers',
        },
      ],
    },
    {
      title: 'Consuming',
      items: [
        {path: 'catalog-browser', icon: 'sim_card', title: 'Catalog Browser'},
        {path: 'contracts', icon: 'assignment_turned_in', title: 'Contracts'},
        {
          path: 'transfer-history',
          icon: 'assignment',
          title: 'Transfer History',
        },
      ],
    },
    {
      items: [
        {
          path: 'logout',
          icon: 'logout',
          title: 'Logout',
          requiresFeature: 'logout-button',
        },
      ],
    },
  ];

  constructor(private activeFeatureSet: ActiveFeatureSet) {}

  buildNavItemGroups(): NavItemGroup[] {
    const allNavItemRoutesExist = this.navItemGroups
      .flatMap((navItemGroup) =>
        navItemGroup.items.map((navItem) => navItem.path),
      )
      .every((path) => routes.find((route) => route.path === path));

    if (!allNavItemRoutesExist) {
      throw new Error('All nav item routes must exist in the routes array');
    }

    return this.navItemGroups
      .map((group) => ({
        ...group,
        items: group.items.filter((item) => {
          return item.requiresFeature
            ? this.activeFeatureSet.has(item.requiresFeature)
            : true;
        }),
      }))
      .filter((group) => group.items.length > 0);
  }
}
