/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

export interface FormGroupProps {
  title: string;
  subTitle?: string;
  children: React.ReactNode;
}

const FormGroup = ({title, subTitle, children}: FormGroupProps) => {
  return (
    <div className="grid grid-cols-1 gap-x-8 gap-y-8 md:grid-cols-3">
      <div className="px-4 sm:px-0">
        <h2 className="text-base font-semibold leading-7 text-gray-900">
          {title}
        </h2>
        {subTitle ? (
          <p className="mt-1 text-sm leading-6 text-gray-600">{subTitle}</p>
        ) : null}
      </div>
      <div className="space-y-8 px-4 py-2 sm:rounded-xl md:col-span-2">
        {children}
      </div>
    </div>
  );
};

export default FormGroup;
