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
import {deleteCounterparty} from '@/lib/api/client';
import {Button} from '@/components/ui/button';
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';

interface DeleteCounterpartyDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  counterparty: CounterpartyDto | null;
}

export const DeleteCounterpartyDialog = ({
  isOpen,
  onClose,
  onConfirm,
  counterparty,
}: DeleteCounterpartyDialogProps) => {
  const [isDeleting, setIsDeleting] = useState(false);

  const handleDelete = async () => {
    if (!counterparty) return;

    setIsDeleting(true);
    try {
      await deleteCounterparty(counterparty.participantId);
      onConfirm();
    } catch (error) {
      console.error('Failed to delete counterparty:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  if (!counterparty) return null;

  return (
    <AlertDialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Delete Chat</AlertDialogTitle>
          <AlertDialogDescription>
            Are you sure you want to delete the chat with{' '}
            <span className="font-medium">{counterparty.participantId}</span>?
            This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel
            disabled={isDeleting}
            className="hover:cursor-pointer">
            Cancel
          </AlertDialogCancel>
          <Button
            variant="destructive"
            onClick={handleDelete}
            disabled={isDeleting}>
            {isDeleting ? 'Deleting...' : 'Delete'}
          </Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
