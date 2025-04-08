/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Inter, Roboto_Mono} from 'next/font/google';
import DialogsProvider from '@/components/dialogs-provider';
import {QueryClientProvider} from '@/components/query-client-provider';
import {TailwindIndicator} from '@/components/dev-utilities/tailwind-indicator';
import {Toaster} from '@/components/ui/toaster';
import {TITLE_TEMPLATE} from '@/lib/title-template';
import {cn} from '@/lib/utils/css-utils';
import '@/styles/globals.css';
import {NextIntlClientProvider} from 'next-intl';
import {PublicEnvScript} from 'next-runtime-env';
import React from 'react';

export const metadata = {
  title: {
    template: TITLE_TEMPLATE,
    default: 'EDC',
  },
  icons: [{rel: 'icon', url: '/favicon.svg'}],
};

const inter = Inter({
  subsets: ['latin'],
  variable: '--font-sans',
});

const robotoMono = Roboto_Mono({
  subsets: ['latin'],
  variable: '--font-mono',
});

export const dynamic = 'force-dynamic';

export default function RootLayout({children}: {children: React.ReactNode}) {
  return (
    <html lang="en" className="h-full">
      <head>
        <PublicEnvScript />
      </head>
      <body
        className={cn('h-full font-sans', inter.variable, robotoMono.variable)}>
        <QueryClientProvider>
          <NextIntlClientProvider>
            {children}
            <TailwindIndicator />
            <DialogsProvider />
          </NextIntlClientProvider>
        </QueryClientProvider>
        <Toaster />
      </body>
    </html>
  );
}
