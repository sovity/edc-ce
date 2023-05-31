import {formatDistanceToNow} from 'date-fns';

/**
 * Formats date as "{n} {timeUnit} ago" or "in {n} {timeUnit}s".
 * @param date date
 */
export function formatDateAgo(date?: Date | null): string {
  if (!date) {
    return '';
  }

  return formatDistanceToNow(date, {addSuffix: true});
}
