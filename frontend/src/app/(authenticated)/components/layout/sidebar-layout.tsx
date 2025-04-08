/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
import Image from 'next/image';
import Link from 'next/link';
import {Footer} from '@/components/footer';
import {Sheet, SheetContent} from '@/components/ui/sheet';
import {UserDropdownMenu} from '@/components/user-dropdown-menu/user-dropdown-menu';
import {urls} from '@/lib/urls';
import {Bars3Icon} from '@heroicons/react/24/outline';
import BreadcrumbComponent from './breadcrumb';
import SidebarNavigationMenu from './sidebar-navigation-menu';

export interface SidebarLayoutProps {
  children?: React.ReactNode;
}

export default function SidebarLayout({children}: SidebarLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="h-full">
      <Sheet open={sidebarOpen} onOpenChange={(open) => setSidebarOpen(open)}>
        <SheetContent
          side="left"
          className="flex h-full flex-col justify-between pb-6">
          <div className="flex grow flex-col gap-y-5 overflow-y-auto bg-white px-2 pb-2">
            <div className="flex h-16 shrink-0 items-center">
              <Link
                href={urls.rootPage()}
                onClick={() => setSidebarOpen(false)}>
                <Image
                  src={'/sovity_logo.svg'}
                  alt={'Sovity Logo'}
                  priority
                  width={115}
                  height={32}
                />
              </Link>
            </div>
            <SidebarNavigationMenu onClick={() => setSidebarOpen(false)} />
          </div>
          <Footer />
        </SheetContent>
      </Sheet>

      {/* Static sidebar for desktop */}
      <div className="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-72 lg:flex-col">
        <div className="flex grow flex-col gap-y-5 overflow-y-auto border-r border-gray-200 bg-white px-6">
          <div className="flex h-16 shrink-0 items-center">
            <Link href={urls.rootPage()}>
              <Image
                src={'/sovity_logo.svg'}
                alt={'Sovity Logo'}
                priority
                width={115}
                height={32}
              />
            </Link>
          </div>
          <SidebarNavigationMenu />
          <Footer />
        </div>
      </div>

      <div className="fixed top-0 z-40 flex w-full items-center gap-x-6 bg-white px-4 py-4 shadow-sm sm:px-6 lg:hidden">
        <button
          data-testid={'btn-open-sidebar'}
          type="button"
          className="-m-2.5 p-2.5 text-gray-700 lg:hidden"
          onClick={() => setSidebarOpen(true)}>
          <span className="sr-only">Open sidebar</span>
          <Bars3Icon className="h-6 w-6" aria-hidden="true" />
        </button>
        <div className="flex-1">
          <BreadcrumbComponent />
        </div>
        <UserDropdownMenu />
      </div>
      <div className="fixed top-0 z-40 hidden w-full items-center justify-between bg-white py-2 pl-[19.5rem] pr-6 shadow-sm lg:flex">
        <BreadcrumbComponent />
        <UserDropdownMenu />
      </div>

      <main className="h-full lg:pl-72">
        <div className="h-full px-4 pt-[4.5rem] sm:px-6 lg:px-8 lg:pt-14">
          {children}
        </div>
      </main>
    </div>
  );
}
