/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {ChartConfiguration} from 'chart.js';

export interface DonutChartData {
  labels: string[];
  datasets: ChartConfiguration<'doughnut'>['data']['datasets'];
  options: ChartConfiguration<'doughnut'>['options'];

  isEmpty: boolean;
  emptyMessage: string;

  totalLabel: string;
  totalValue: number;
}
