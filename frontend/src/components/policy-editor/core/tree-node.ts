/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {associateByR} from '@/lib/utils/map-utils';
import {patchKey, patchObj, withoutKey} from '@/lib/utils/object-utils';

export interface TreeNode<T> {
  path: string[];
  id: string;
  value: T;
  children: Record<string, TreeNode<T>>;
}

export const newTreeEmpty = <T>(value: T): TreeNode<T> => {
  return {
    path: [],
    id: '$',
    value,
    children: {},
  };
};

export const newTreeFold = <I, T>(opts: {
  value: I;
  foldFn: TreeGeneratorFn<I, T>;
  idFactory: IdFactory;
}): TreeNode<T> => {
  return recursiveFoldNodes<I, T>({
    parentPath: null,
    value: opts.value,
    foldFn: opts.foldFn,
    idFactory: opts.idFactory,
  });
};

export const findNode = <T>(tree: TreeNode<T>, path: string[]): TreeNode<T> => {
  let node = tree;
  path.forEach((key) => {
    const child = node.children[key];
    if (child == null) {
      throw new Error('Node not found');
    }
    node = child;
  });
  return node;
};

/**
 * Appends another child to the tree.
 */
export const withAppendedTree = <I, T>(opts: {
  rootNode: TreeNode<T>;
  parentPath: string[];
  value: I;
  foldFn: TreeGeneratorFn<I, T>;
  idFactory: IdFactory;
}): TreeNode<T> => {
  const newNode = recursiveFoldNodes({
    parentPath: opts.parentPath,
    value: opts.value,
    foldFn: opts.foldFn,
    idFactory: opts.idFactory,
  });

  return withReplacedNode({
    root: opts.rootNode,
    path: opts.parentPath,
    replaceNode: (parent) => ({
      ...parent,
      children: {
        ...parent.children,
        [newNode.id]: newNode,
      },
    }),
  });
};

/**
 * Removes the node. Cannot remove root
 */
export const withRemovedNode = <T>(opts: {
  root: TreeNode<T>;
  path: string[];
}): TreeNode<T> => {
  if (opts.path.length === 0) {
    throw new Error('Cannot remove root node');
  }

  return withReplacedNode({
    root: opts.root,
    path: opts.path.slice(0, opts.path.length - 1),
    replaceNode: (parent) =>
      withRemovedChild(parent, opts.path[opts.path.length - 1]!),
  });
};

const withReplacedNode = <T>(opts: {
  root: TreeNode<T>;
  path: string[];
  replaceNode: (current: TreeNode<T>) => TreeNode<T>;
}) => {
  const updateInternal = (
    node: TreeNode<T>,
    remainingPath: string[],
  ): TreeNode<T> => {
    if (remainingPath.length === 0) {
      return opts.replaceNode(node);
    }

    return patchObj(node, (it) => ({
      children: patchKey(it.children, remainingPath[0]!, (child) =>
        updateInternal(child!, remainingPath.slice(1)),
      ),
    }));
  };

  return updateInternal(opts.root, opts.path);
};

const withRemovedChild = <T>(
  tree: TreeNode<T>,
  childId: string,
): TreeNode<T> => {
  return patchObj(tree, (it) => ({
    children: withoutKey(it.children, childId),
  }));
};

const recursiveFoldNodes = <I, T>(opts: {
  parentPath: string[] | null;
  value: I;
  foldFn: TreeGeneratorFn<I, T>;
  idFactory: IdFactory;
}): TreeNode<T> => {
  const id = opts.parentPath == null ? '$' : opts.idFactory();
  const path = opts.parentPath == null ? [] : [...opts.parentPath, id];
  const {value, children} = opts.foldFn(opts.value, id);
  const childrenMapped = children.map((child) =>
    recursiveFoldNodes({
      parentPath: path,
      value: child,
      foldFn: opts.foldFn,
      idFactory: opts.idFactory,
    }),
  );
  return {
    id,
    path,
    value,
    children: associateByR(childrenMapped, (it) => it.id),
  };
};

export type IdFactory = () => string;

/**
 * Mapper between a tree-like structure a dedicated TreeNode value type
 */
export type TreeGeneratorFn<I, T> = (
  value: I,
  id: string,
) => {value: T; children: I[]};
