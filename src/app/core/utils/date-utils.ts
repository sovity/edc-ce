/**
 * Takes the year/month/day information of a local date and creates a new Date object from it.
 * Hour offset context is removed.
 * Can be used to ensure dates are displayed identically across different timezones when stringified in JSON payloads.
 * @param date date to convert
 */
import {format} from 'date-fns-tz';

export function toGmtZeroHourDate(date: Date): Date {
  return new Date(format(date, 'yyyy-MM-dd'));
}
