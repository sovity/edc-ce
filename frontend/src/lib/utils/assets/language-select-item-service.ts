/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type UiSelectItemGroup} from '@/model/ui-select-item-group';
import {type UiSelectItemOption} from '@/model/ui-select-item-option';
import {LANGUAGE_SELECT_DATA} from './language-select-data';
import {type LanguageSelectItem} from './language-select-item';

export function getLanguageSelectItemById(id: string): LanguageSelectItem {
  const item = LANGUAGE_SELECT_DATA.find(
    (item) =>
      item.id === id ||
      item.idShort === id ||
      item.sameAs === id ||
      item.label === id,
  );

  if (!item) {
    return {id, label: id};
  }

  return item;
}

export const languageSelectItemGroups = (): UiSelectItemGroup[] => {
  const highlightItemIds = [
    'https://w3id.org/idsa/code/MULTI_LINGUAL',
    'https://w3id.org/idsa/code/DE',
    'https://w3id.org/idsa/code/EN',
  ];

  const highlightedItems = LANGUAGE_SELECT_DATA.filter((it) =>
    highlightItemIds.includes(it.id),
  );

  const otherItems = LANGUAGE_SELECT_DATA.filter(
    (it) => !highlightItemIds.includes(it.id),
  );

  const toItem = (item: LanguageSelectItem): UiSelectItemOption => ({
    id: item.id,
    label: item.label,
  });

  return [
    {
      heading: '',
      items: highlightedItems.map(toItem),
    },
    {
      heading: '',
      items: otherItems.map(toItem),
    },
  ];
};
