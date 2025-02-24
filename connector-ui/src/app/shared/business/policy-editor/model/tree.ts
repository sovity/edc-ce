/**
 * Tree data structure with a generic value type.
 *
 * The tree is mutable, but the TreeNode structure inside is immutable for better
 * change detection.
 */
export class Tree<T> {
  constructor(public root: TreeNode<T>, private _nextId: number) {}

  remove(path: string[]): void {
    this.transform((node) => {
      if (this.isEqualPath(node, path)) {
        return null;
      }
      return node;
    });
  }

  replaceTree<I>(
    path: string[],
    value: I,
    generatorFn: TreeGeneratorFn<I, T>,
  ): TreeNode<T> {
    const parentPath = path.slice(0, -1);

    const newNode = Tree.recursiveFoldNodes({
      parentPath,
      original: value,
      generatorFn,
      idFactory: () => this.nextId(),
    });

    this.transform((node) => {
      if (!this.isEqualPath(node, path)) {
        return node;
      }

      return newNode;
    });

    return newNode;
  }

  push(parentPath: string[], value: T): TreeNode<T> {
    const id = this.nextId();
    const newNode: TreeNode<T> = {
      id,
      path: [...parentPath, id],
      value: value,
      children: [],
    };
    this.pushNode(parentPath, newNode);
    return newNode;
  }

  pushTree<I>(
    parentPath: string[],
    value: I,
    generatorFn: TreeGeneratorFn<I, T>,
  ): TreeNode<T> {
    const newNode = Tree.recursiveFoldNodes({
      parentPath,
      original: value,
      generatorFn,
      idFactory: () => this.nextId(),
    });
    this.pushNode(parentPath, newNode);
    return newNode;
  }

  private pushNode(parentPath: string[], newNode: TreeNode<T>): void {
    this.transform((node) => {
      if (!this.isEqualPath(node, parentPath)) {
        return node;
      }

      return {
        ...node,
        children: [...node.children, newNode],
      };
    });
  }

  private isEqualPath(node: TreeNode<T>, path: string[]) {
    return node.path.join('.') === path.join('.');
  }

  private transform(fn: (node: TreeNode<T>) => TreeNode<T> | null): void {
    const transformNode = (node: TreeNode<T>): TreeNode<T> | null => {
      const transformed = fn(node);
      if (!transformed) {
        return null;
      }
      return {
        ...transformed,
        children: transformed.children
          .map(transformNode)
          .filter((it) => it != null) as TreeNode<T>[],
      };
    };
    this.root = transformNode(this.root)!;
  }

  private nextId() {
    return String(this._nextId++);
  }

  static ofTreeLikeStructure<T, R>(opts: {
    root: T;
    generatorFn: TreeGeneratorFn<T, R>;
  }): Tree<R> {
    let currentId = 0;
    const nextId = () => String(currentId++);
    const rootNode = Tree.recursiveFoldNodes({
      parentPath: [],
      original: opts.root,
      generatorFn: opts.generatorFn,
      idFactory: nextId,
    });
    return new Tree<R>(rootNode, currentId);
  }

  private static recursiveFoldNodes<T, R>(opts: {
    parentPath: string[];
    original: T;
    generatorFn: TreeGeneratorFn<T, R>;
    idFactory: () => string;
  }): TreeNode<R> {
    const id = opts.idFactory();
    const {value, children} = opts.generatorFn(opts.original, id);
    const path = [...opts.parentPath, id];
    const childrenMapped = children.map((child) =>
      Tree.recursiveFoldNodes({
        parentPath: path,
        original: child,
        generatorFn: opts.generatorFn,
        idFactory: opts.idFactory,
      }),
    );
    return {
      id,
      path,
      value,
      children: childrenMapped,
    };
  }
}

export interface TreeNode<T> {
  path: string[];
  id: string;
  value: T;
  children: TreeNode<T>[];
}

/**
 * Mapper between a tree-like structure a dedicated TreeNode value type
 */
export type TreeGeneratorFn<T, R> = (
  value: T,
  id: string,
) => {value: R; children: T[]};
