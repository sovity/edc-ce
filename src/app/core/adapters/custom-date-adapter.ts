import {NativeDateAdapter} from '@angular/material/core';
import {isValid, parse as parseDate} from 'date-fns';
import {format as formateDate} from 'date-fns-tz';

export class CustomDateAdapter extends NativeDateAdapter {
  parse(value: any): Date | null {
    if (typeof value === 'string' && value.indexOf('/') > -1) {
      const parsedDate = parseDate(value, 'dd/MM/yyyy', new Date());
      return isValid(parsedDate) ? parsedDate : null;
    }

    const timestamp = typeof value === 'number' ? value : Date.parse(value);
    return isNaN(timestamp) ? null : new Date(timestamp);
  }

  format(date: Date, displayFormat: Object): string {
    return formateDate(date, 'dd/MM/yyyy', {
      timeZone: 'UTC',
    });
  }
}
