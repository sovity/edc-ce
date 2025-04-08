/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import React from 'react';
import {cn} from '@/lib/utils/css-utils';
import {cva, type VariantProps} from 'class-variance-authority';

const spinnerVariants = cva('relative', {
  variants: {
    size: {
      sm: 'h-6 w-6',
      default: 'h-11 w-11',
      lg: 'h-14 w-14',
    },
  },
  defaultVariants: {
    size: 'default',
  },
});

export type LoadingSpinnerProps = VariantProps<typeof spinnerVariants>;

const LoadingSpinner = ({size}: LoadingSpinnerProps) => {
  return (
    <svg
      width="38"
      height="38"
      viewBox="0 0 38 38"
      xmlns="http://www.w3.org/2000/svg"
      className={cn(spinnerVariants({size}))}>
      <defs>
        <linearGradient x1="8.042%" y1="0%" x2="65.682%" y2="23.865%" id="a">
          <stop stopColor="currentColor" stopOpacity="0" offset="0%" />
          <stop stopColor="currentColor" stopOpacity=".631" offset="63.146%" />
          <stop stopColor="currentColor" offset="100%" />
        </linearGradient>
      </defs>
      <g fill="none" fillRule="evenodd">
        <g transform="translate(1 1)">
          <path
            d="M36 18c0-9.94-8.06-18-18-18"
            id="Oval-2"
            stroke="url(#a)"
            strokeWidth="2">
            <animateTransform
              attributeName="transform"
              type="rotate"
              from="0 18 18"
              to="360 18 18"
              dur="0.9s"
              repeatCount="indefinite"
            />
          </path>
          <circle fill="currentColor" cx="36" cy="18" r="1">
            <animateTransform
              attributeName="transform"
              type="rotate"
              from="0 18 18"
              to="360 18 18"
              dur="0.9s"
              repeatCount="indefinite"
            />
          </circle>
        </g>
      </g>
    </svg>
  );
};

const CenteredLoadingSpinner = ({
  className,
  size,
  ...props
}: React.HTMLAttributes<HTMLDivElement> & LoadingSpinnerProps) => {
  return (
    <div
      className={cn(
        'flex h-full w-full items-center justify-center',
        className,
      )}
      {...props}>
      <LoadingSpinner size={size} />
    </div>
  );
};

export {CenteredLoadingSpinner, LoadingSpinner, spinnerVariants};
