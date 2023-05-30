import {HttpErrorResponse} from '@angular/common/http';
import {OperatorFunction, concat, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';

/**
 * Wraps potentially fetched value with state and potential errors.
 */
export class Fetched<T> {
  constructor(
    public state: FetchedState,
    public dataOrUndefined: T | undefined,
    public errorOrUndefined: FetchError | undefined,
  ) {}

  get isLoading(): boolean {
    return this.state === 'loading' || this.state === 'not-loaded';
  }

  get isReady(): boolean {
    return this.state === 'ready';
  }

  get isError(): boolean {
    return this.state === 'error';
  }

  get data(): T {
    if (this.state !== 'ready') {
      throw new Error(`Fetched not ready!`);
    }
    return this.dataOrUndefined!;
  }

  get error(): FetchError {
    if (this.state !== 'error') {
      throw new Error(`Fetched not in error state!`);
    }
    return this.errorOrUndefined!;
  }

  /**
   * Map data if present but keep state
   * @param mapFn mapping fn
   */
  map<R>(mapFn: (t: T) => R): Fetched<R> {
    return new Fetched<R>(
      this.state,
      this.isReady ? mapFn(this.data) : undefined,
      this.errorOrUndefined,
    );
  }

  /**
   * Get data or fall back to default value
   * @param defaultValue value to fall back to if no data is present
   */
  orElse<R>(defaultValue: R): T | R {
    return this.isReady ? this.data : defaultValue;
  }

  static empty<T>(): Fetched<T> {
    return new Fetched<T>('not-loaded', undefined, undefined);
  }

  static loading<T>(): Fetched<T> {
    return new Fetched<T>('loading', undefined, undefined);
  }

  static ready<T>(data: T): Fetched<T> {
    return new Fetched<T>('ready', data, undefined);
  }

  static error<T>(failureMessage: string, error: any): Fetched<T> {
    return Fetched.error2(mapFetchError(failureMessage, error));
  }

  static error2<T>(fetchError: FetchError): Fetched<T> {
    return new Fetched<T>('error', undefined, fetchError);
  }

  /**
   * RXJS Operator: Wraps request into multiple emissions that track state.
   *
   * @param opts adit
   */
  static wrap<T>(opts: {
    failureMessage: string;
  }): OperatorFunction<T, Fetched<T>> {
    return (obs) =>
      concat(
        of(Fetched.loading<T>()),
        obs.pipe(
          map((data) => Fetched.ready(data)),
          catchError((err) => {
            console.error(opts.failureMessage, err);
            return of(Fetched.error<T>(opts.failureMessage, err));
          }),
        ),
      );
  }

  /**
   * RXJS Operator: Map fetched value
   *
   * @param mapFn mapping fn applied to data if present
   */
  static map<T, R>(
    mapFn: (value: T) => R,
  ): OperatorFunction<Fetched<T>, Fetched<R>> {
    return (obs) => obs.pipe(map((fetched) => fetched.map(mapFn)));
  }

  /**
   * RXJS Operator: Get value or fall back to default value
   *
   * @param defaultValue value to fall back to if no data is present
   */
  static orElse<T, R>(defaultValue: R): OperatorFunction<Fetched<T>, T | R> {
    return (obs) => obs.pipe(map((fetched) => fetched.orElse(defaultValue)));
  }
}

/**
 * States a potentially fetched value can assume.
 */
export type FetchedState = 'not-loaded' | 'loading' | 'ready' | 'error';

/**
 * Errors are paired with a user-friendly action-specific failure messages
 * since stack traces might have useless technical error messages.
 */
export interface FetchError {
  type: 'error' | '401';
  failureMessage: string;
  failureIcon: string;
  error: any;
}

export function mapFetchError(failureMessage: string, error: any): FetchError {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 401) {
      return {
        type: '401',
        failureIcon: 'refresh',
        failureMessage: 'Session most likely expired. Please refresh browser.',
        error,
      };
    }
  }

  return {
    type: 'error',
    failureIcon: 'error',
    failureMessage,
    error,
  };
}
