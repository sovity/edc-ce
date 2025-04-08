/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect} from 'react';
import ContactSupport from './contact-support';
import {Button} from './ui/button';

interface ErrorPageProps {
  error: Error & {digest?: string};
  reset: () => void;
}

const ErrorPage = ({error, reset}: ErrorPageProps) => {
  useEffect(() => {
    // ToDo: Send the error to some service?
    console.error(error);
  }, [error]);

  const overTitle = error.name ?? 'Error';

  return (
    <div className="h-full">
      <main className="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8">
        <div className="text-center">
          <p
            className="text-base font-semibold text-brand-blue"
            data-testid="error-over-title">
            {overTitle}
          </p>
          <h1
            className="mt-4 text-2xl font-bold tracking-tight text-gray-900 sm:text-5xl"
            data-testid="error-title">
            Error occurred
          </h1>
          <p
            className="mt-6 max-w-[48rem] text-base leading-7 text-gray-600"
            data-testid="error-msg">
            {error.message}
          </p>
          <p className="mt-1 text-xs text-gray-600">
            {error.digest ? (
              <span>Digest: {error.digest}</span>
            ) : (
              <span>No digest</span>
            )}
          </p>
          <div className="mt-10 flex items-center justify-center gap-x-6">
            <Button dataTestId={'try-again'} size="sm" onClick={() => reset()}>
              Try again
            </Button>
            <ContactSupport />
          </div>
        </div>
      </main>
    </div>
  );
};

export default ErrorPage;
