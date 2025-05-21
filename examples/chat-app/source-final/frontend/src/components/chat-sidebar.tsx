/*
 * Copyright 2025 sovity GmbH
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
 */
'use client';

import type {CounterpartyDto} from '@/lib/api/models/counterparty-dto';
import {Button} from '@/components/ui/button';
import {ScrollArea} from '@/components/ui/scroll-area';
import {Skeleton} from '@/components/ui/skeleton';
import {PlusCircle, Trash2, CircleDot, X} from 'lucide-react';
import {cn} from '@/lib/utils';
import {useQuery} from '@tanstack/react-query';
import {listCounterparties} from '@/lib/api/client';
import {getStatusColor} from '@/lib/utils/color-utils';
import {FixedScrollArea} from '@/components/fixed-scroll-area';

interface ChatSidebarProps {
  selectedCounterparty: CounterpartyDto | null;
  onCounterpartySelect: (counterparty: CounterpartyDto) => void;
  onAddClick: () => void;
  onDeleteClick: (counterparty: CounterpartyDto) => void;
  onCloseSidebar?: () => void;
  className?: string;
}

export const ChatSidebar = ({
  selectedCounterparty,
  onCounterpartySelect,
  onAddClick,
  onDeleteClick,
  onCloseSidebar,
  className,
}: ChatSidebarProps) => {
  const pageQuery = useQuery({
    queryKey: ['counterparties'],
    queryFn: async () => await listCounterparties(),
    retry: 3,
    refetchOnWindowFocus: true,
    refetchInterval: 1000,
  });

  return (
    <div className={cn('flex h-full flex-col border-r', className)}>
      <div className="flex h-20 items-center justify-between border-b p-4">
        <h2 className="text-xl font-semibold">Chats</h2>
        {onCloseSidebar && (
          <Button
            variant="ghost"
            size="icon"
            onClick={onCloseSidebar}
            className="hover:cursor-pointer md:hidden">
            <X className="h-5 w-5" />
            <span className="sr-only">Close sidebar</span>
          </Button>
        )}
      </div>

      <FixedScrollArea className="flex-1">
        {pageQuery.isPending || pageQuery.data === undefined ? (
          <div className="space-y-3 p-4">
            {Array.from({length: 5}).map((_, i) => (
              <div key={i} className="flex items-center space-x-4">
                <Skeleton className="h-12 w-12 rounded-full" />
                <div className="space-y-2">
                  <Skeleton className="h-4 w-[150px]" />
                  <Skeleton className="h-4 w-[100px]" />
                </div>
              </div>
            ))}
          </div>
        ) : pageQuery.data.length === 0 ? (
          <div className="p-8 text-center">
            <p className="text-muted-foreground mb-4">No chats yet</p>
            <Button onClick={onAddClick} variant="outline" className="mx-auto">
              <PlusCircle className="mr-2 h-4 w-4" />
              Start a new chat
            </Button>
          </div>
        ) : (
          <div className="p-2">
            {pageQuery.data.map((counterparty) => (
              <div
                key={counterparty.participantId}
                className={cn(
                  'hover:bg-accent group flex w-full cursor-pointer items-center justify-between rounded-md p-3',
                  selectedCounterparty?.participantId ===
                    counterparty.participantId && 'bg-accent',
                )}
                onClick={() => onCounterpartySelect(counterparty)}>
                <div className="flex w-full items-center space-x-3 overflow-hidden">
                  <CircleDot
                    className={cn(
                      'h-3 w-3',
                      getStatusColor(counterparty.status),
                    )}
                  />
                  <div className="overflow-hidden">
                    <div className="truncate font-medium">
                      {counterparty.participantId}
                    </div>
                    <div className="text-muted-foreground truncate text-xs">
                      {counterparty.connectorEndpoint}
                    </div>
                  </div>
                </div>
                <Button
                  variant="ghost"
                  size="icon"
                  className="opacity-0 transition-opacity group-hover:opacity-100 hover:text-red-700"
                  onClick={(e) => {
                    e.stopPropagation();
                    onDeleteClick(counterparty);
                  }}>
                  <Trash2 className="h-4 w-4" />
                  <span className="sr-only">Delete</span>
                </Button>
              </div>
            ))}
          </div>
        )}
      </FixedScrollArea>

      <div className="border-t p-4">
        <Button onClick={onAddClick} className="w-full">
          <PlusCircle className="mr-2 h-4 w-4" />
          New Chat
        </Button>
      </div>
    </div>
  );
};
