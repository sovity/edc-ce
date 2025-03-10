/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
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
      items: [
        {
          testId: 'nav-link-dashboard',
          path: 'dashboard',
          icon: 'data_usage',
          title: 'Dashboard',
        },
        {
          testId: 'nav-link-catalog',
          path: 'catalog-browser',
          icon: 'sim_card',
          title: 'catalog_browser_page.title',
        },
        {
          testId: 'nav-link-contracts',
          path: 'contracts',
          icon: 'assignment_turned_in',
          title: 'contract_agreement_page.title',
        },
        {
          testId: 'nav-link-transfer-history',
          path: 'transfer-history',
          icon: 'assignment',
          title: 'transfer_history_page.title',
        },
      ],
    },
    {
      title: 'Provide',
      items: [
        {
          testId: 'nav-link-create-data-offer',
          path: 'create-asset',
          icon: 'post_add',
          title: 'create_data_offer_page.title',
        },
        {
          testId: 'nav-link-assets',
          path: 'my-assets',
          icon: 'upload',
          title: 'asset_list_page.title',
        },
        {
          testId: 'nav-link-policies',
          path: 'policies',
          icon: 'policy',
          title: 'policy_definition_page.title',
        },
        {
          testId: 'nav-link-data-offers',
          path: 'data-offers',
          icon: 'rule',
          title: 'contract_definition_page.title',
        },
      ],
    },

    {
      items: [
        {
          testId: 'nav-link-logout',
          path: 'logout',
          icon: 'logout',
          title: 'logout_page.title',
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
