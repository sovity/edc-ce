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

import {useState} from 'react';
import type {CounterpartyDto} from '@/lib/api/models/counterparty-dto';
import {ChatSidebar} from '@/components/chat-sidebar';
import {ChatWindow} from '@/components/chat-window';
import {AddCounterpartyDialog} from '@/components/add-counterparty-dialog';
import {DeleteCounterpartyDialog} from '@/components/delete-counterparty-dialog';
import {Button} from '@/components/ui/button';
import {Menu} from 'lucide-react';

const ChatPage = () => {
  const [counterparties, setCounterparties] = useState<CounterpartyDto[]>([]);
  const [selectedCounterparty, setSelectedCounterparty] =
    useState<CounterpartyDto | null>(null);
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [counterpartyToDelete, setCounterpartyToDelete] =
    useState<CounterpartyDto | null>(null);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  // Close sidebar when a chat is selected on mobile
  const handleCounterpartySelect = (counterparty: CounterpartyDto) => {
    setSelectedCounterparty(counterparty);
    setIsSidebarOpen(false);
  };

  const handleAddCounterparty = (newCounterparty: CounterpartyDto) => {
    setCounterparties((prev) => [...prev, newCounterparty]);
    setSelectedCounterparty(newCounterparty);
    setIsAddDialogOpen(false);
    setIsSidebarOpen(false);
  };

  const handleDeleteClick = (counterparty: CounterpartyDto) => {
    setCounterpartyToDelete(counterparty);
    setIsDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = () => {
    if (counterpartyToDelete) {
      // Update the UI immediately after successful deletion from the server
      const updatedCounterparties = counterparties.filter(
        (c) => c.participantId !== counterpartyToDelete.participantId,
      );

      setCounterparties(updatedCounterparties);

      // If the deleted counterparty was selected, select another one
      if (
        selectedCounterparty?.participantId ===
        counterpartyToDelete.participantId
      ) {
        setSelectedCounterparty(
          updatedCounterparties.length > 0 ? updatedCounterparties[0] : null,
        );
      }

      // Close the dialog and reset state
      setIsDeleteDialogOpen(false);
      setCounterpartyToDelete(null);
    }
  };

  // Toggle sidebar for mobile view
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  return (
    <div className="bg-background flex h-screen overflow-hidden">
      {/* Sidebar - responsive with overlay on mobile */}
      <div
        className={`fixed inset-y-0 left-0 z-40 transform transition-transform duration-300 ease-in-out md:relative md:translate-x-0 ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full'} `}>
        {/* Overlay - lighter background */}
        <div
          className="absolute inset-0 bg-black/30 md:hidden"
          onClick={() => setIsSidebarOpen(false)}></div>

        {/* Sidebar content */}
        <div className="bg-background relative h-full w-80 max-w-[80vw] md:w-80 lg:w-96">
          <ChatSidebar
            selectedCounterparty={selectedCounterparty}
            onCounterpartySelect={handleCounterpartySelect}
            onAddClick={() => setIsAddDialogOpen(true)}
            onDeleteClick={handleDeleteClick}
            onCloseSidebar={() => setIsSidebarOpen(false)}
          />
        </div>
      </div>

      {/* Main chat area */}
      <div className="flex flex-1 flex-col">
        {selectedCounterparty ? (
          <ChatWindow
            selectedCounterparty={selectedCounterparty}
            onOpenSidebar={toggleSidebar}
          />
        ) : (
          <div className="flex flex-1 items-center justify-center p-4">
            {/* Mobile sidebar toggle button - for empty state */}
            <Button
              variant="ghost"
              size="icon"
              onClick={toggleSidebar}
              className="absolute top-4 left-4 md:hidden">
              <Menu className="h-5 w-5" />
              <span className="sr-only">Open sidebar</span>
            </Button>

            <div className="text-center">
              <h2 className="mb-2 text-2xl font-semibold">No chat selected</h2>
              <p className="text-muted-foreground">
                <span className="hidden md:inline">
                  Select a chat from the sidebar or create a new one
                </span>
                <span className="md:hidden">
                  Open the sidebar to select or create a chat
                </span>
              </p>
              <Button
                onClick={toggleSidebar}
                variant="outline"
                className="mt-4 md:hidden">
                Open Sidebar
              </Button>
            </div>
          </div>
        )}
      </div>

      <AddCounterpartyDialog
        isOpen={isAddDialogOpen}
        onClose={() => setIsAddDialogOpen(false)}
        onAdd={handleAddCounterparty}
      />

      <DeleteCounterpartyDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => setIsDeleteDialogOpen(false)}
        onConfirm={handleDeleteConfirm}
        counterparty={counterpartyToDelete}
      />
    </div>
  );
};

export default ChatPage;
