/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type RefObject, useEffect, useState} from 'react';

export const useWidthObserver = (
  ref: RefObject<Element>,
  initialWidth: number,
): number => {
  const [width, setWidth] = useState(initialWidth);

  useEffect(() => {
    const handleResize = () => {
      setWidth(ref.current?.clientWidth ?? initialWidth);
    };

    if (ref.current) {
      handleResize();
    }

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [initialWidth, ref]);

  return width;
};
