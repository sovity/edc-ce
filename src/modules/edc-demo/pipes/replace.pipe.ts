import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'replace'})
export class ReplacePipe implements PipeTransform {
  transform(value: string | undefined, replaceMap: { [key: string]: string; }): string | undefined {
    if(!value || !replaceMap)
    {
      return value;
    }

    let result = value;
    for(let [key,value] of Object.entries(replaceMap)){
      result = result.replace(new RegExp(`\{\{${key}\}\}`, 'g'), value);
    }

    return result;
  }
}
