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

import type React from 'react';

import {useState, useEffect, useRef} from 'react';
import type {CounterpartyDto} from '@/lib/api/models/counterparty-dto';
import type {MessageDto} from '@/lib/api/models/message-dto';
import {MessageDirectionDto} from '@/lib/api/models/message-direction-dto';
import {MessageStatusDto} from '@/lib/api/models/message-status-dto';
import {getAllMessages, sendMessage} from '@/lib/api/client';
import {Button} from '@/components/ui/button';
import {Input} from '@/components/ui/input';
import {ScrollArea} from '@/components/ui/scroll-area';
import {Skeleton} from '@/components/ui/skeleton';
import {Send, CheckCircle2, AlertCircle, Clock, Menu} from 'lucide-react';
import {cn} from '@/lib/utils';
import {useQuery} from '@tanstack/react-query';
import {ChatTimestamp} from '@/components/chat-timestamp';

interface ChatWindowProps {
  selectedCounterparty: CounterpartyDto;
  onOpenSidebar?: () => void;
}

export const ChatWindow = ({
  selectedCounterparty,
  onOpenSidebar,
}: ChatWindowProps) => {
  const [messages, setMessages] = useState<MessageDto[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const pageQuery = useQuery({
    queryKey: ['messages', selectedCounterparty.participantId],
    queryFn: async () =>
      await getAllMessages(selectedCounterparty.participantId),
    refetchOnWindowFocus: true,
    refetchInterval: 1000,
  });

  useEffect(() => {
    if (pageQuery.data) {
      setMessages(pageQuery.data);
    }
  }, [pageQuery.data]);

  // Scroll to bottom when messages change
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({behavior: 'smooth'});
  };

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim()) return;

    const messageToSend = newMessage;
    setNewMessage('');

    try {
      await sendMessage(selectedCounterparty.participantId, {
        message: messageToSend,
      });
      // The message will be fetched from the backend in the next polling cycle
    } catch (error) {
      console.error('Failed to send message:', error);
    }
  };

  const getMessageStatusIcon = (status: MessageStatusDto) => {
    switch (status) {
      case MessageStatusDto.SENDING:
        return <Clock className="text-muted-foreground h-3 w-3" />;
      case MessageStatusDto.OK:
        return <CheckCircle2 className="h-3 w-3 text-green-500" />;
      case MessageStatusDto.ERROR:
        return <AlertCircle className="h-3 w-3 text-red-500" />;
      default:
        return null;
    }
  };

  return (
    <div className="flex h-full flex-col">
      <div className="flex h-20 items-center border-b p-4">
        {onOpenSidebar && (
          <Button
            variant="ghost"
            size="icon"
            onClick={onOpenSidebar}
            className="mr-2 hover:cursor-pointer md:hidden">
            <Menu className="h-5 w-5" />
            <span className="sr-only">Open sidebar</span>
          </Button>
        )}
        <div className="min-w-0 flex-1">
          <h2 className="truncate font-semibold">
            {selectedCounterparty.participantId}
          </h2>
          <p className="text-muted-foreground truncate text-sm">
            {selectedCounterparty.connectorEndpoint}
          </p>
        </div>
      </div>

      <ScrollArea className="flex-1 p-2 sm:p-4">
        {pageQuery.isPending ? (
          <div className="space-y-4">
            {Array.from({length: 5}).map((_, i) => (
              <div
                key={i}
                className={cn(
                  'flex',
                  i % 2 === 0 ? 'justify-start' : 'justify-end',
                )}>
                <Skeleton
                  className={cn(
                    'h-16 rounded-lg',
                    i % 2 === 0 ? 'w-2/3' : 'w-1/2',
                  )}
                />
              </div>
            ))}
          </div>
        ) : messages.length === 0 ? (
          <div className="flex h-full items-center justify-center">
            <div className="text-center">
              <h3 className="font-medium">No messages yet</h3>
              <p className="text-muted-foreground mt-1 text-sm">
                Send a message to start the conversation
              </p>
            </div>
          </div>
        ) : (
          <div className="space-y-3">
            {messages.map((message) => (
              <div
                key={message.messageId}
                className={cn(
                  'flex',
                  message.messageDirection === MessageDirectionDto.OUTGOING
                    ? 'justify-end'
                    : 'justify-start',
                )}>
                <div
                  className={cn(
                    'max-w-[85%] rounded-lg p-3 sm:max-w-[70%]',
                    message.messageDirection === MessageDirectionDto.OUTGOING
                      ? 'bg-primary text-primary-foreground'
                      : 'bg-muted',
                  )}>
                  <div className="text-sm break-words sm:text-base">
                    {message.message}
                  </div>
                  <div className="mt-1 flex items-center justify-end space-x-1">
                    <span className="text-xs opacity-70">
                      <ChatTimestamp date={message.createdAt} />
                    </span>
                    {message.messageDirection ===
                      MessageDirectionDto.OUTGOING && (
                      <span>{getMessageStatusIcon(message.status)}</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>
        )}
      </ScrollArea>

      <form
        onSubmit={handleSendMessage}
        className="flex items-center space-x-2 border-t p-2 sm:p-4">
        <Input
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Type a message..."
          className="flex-1"
          disabled={pageQuery.isPending}
        />
        <Button
          type="submit"
          size="icon"
          disabled={!newMessage.trim() || pageQuery.isPending}>
          <Send className="h-4 w-4" />
          <span className="sr-only">Send</span>
        </Button>
      </form>
    </div>
  );
};
