/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import * as React from 'react';
import {cn} from '@/lib/utils/css-utils';
import {Slot} from '@radix-ui/react-slot';
import {cva, type VariantProps} from 'class-variance-authority';
import {Loader2} from 'lucide-react';

const buttonVariants = cva(
  'inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50',
  {
    variants: {
      variant: {
        default: 'bg-primary text-primary-foreground hover:bg-primary/90',
        secondary:
          'bg-secondary text-secondary-foreground hover:bg-accent hover:text-accent-foreground border-[0.07rem] border-primary',
        destructive:
          'bg-destructive text-destructive-foreground hover:bg-destructive/90',
        ghost: 'hover:bg-accent hover:text-accent-foreground',
        outline:
          'border border-input bg-background hover:bg-accent hover:text-accent-foreground',
        destructiveOutline:
          'border border-destructive bg-background text-destructive hover:bg-accent',
        link: 'text-brand-blue underline-offset-4 hover:underline',
        fakeLink: 'text-brand-blue',
      },
      size: {
        default: 'h-10 px-4 py-2',
        sm: 'h-9 rounded-md px-3',
        lg: 'h-11 rounded-md px-8',
        icon: 'h-10 w-10',
        smallIcon: 'h-8 w-8',
        wide: 'w-12 h-6',
        lgWide: 'w-14 h-6',
        fit: '',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  },
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  dataTestId: string;
  asChild?: boolean;
  isLoading?: boolean;
  loadingText?: string;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      className,
      variant,
      size,
      asChild = false,
      isLoading,
      loadingText,
      dataTestId,
      ...props
    },
    ref,
  ) => {
    const Comp = asChild ? Slot : 'button';

    return isLoading ? (
      <Comp
        className={cn(buttonVariants({variant, size, className}))}
        ref={ref}
        data-testid={dataTestId}
        {...props}>
        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
        {loadingText ?? 'Please wait'}
      </Comp>
    ) : (
      <Comp
        data-testid={dataTestId}
        className={cn(buttonVariants({variant, size, className}))}
        ref={ref}
        {...props}
      />
    );
  },
);
Button.displayName = 'Button';

export {Button, buttonVariants};
