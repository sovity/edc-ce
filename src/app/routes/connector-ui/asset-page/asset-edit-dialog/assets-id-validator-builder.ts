import {Injectable} from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
} from '@angular/forms';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AssetService} from '../../../../core/services/asset.service';

@Injectable({
  providedIn: 'root',
})
export class AssetsIdValidatorBuilder {
  constructor(private assetServiceMapped: AssetService) {}

  assetIdDoesNotExistsValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return this.fetchAssetIds().pipe(
        map((assetIds) => {
          if (assetIds.has(control.value)) {
            return {idAlreadyExists: true};
          }
          return null;
        }),
      );
    };
  }

  private fetchAssetIds(): Observable<Set<string>> {
    return this.assetServiceMapped
      .fetchAssets()
      .pipe(map((assets) => new Set(assets.map((asset) => asset.assetId))));
  }
}
