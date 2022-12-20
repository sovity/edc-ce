import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {first, map, switchMap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {AssetEntryDto, AssetService,} from "../../../mgmt-api-client";
import {AssetEditorDialog} from "../asset-editor-dialog/asset-editor-dialog.component";
import {Asset} from "../../models/asset";
import {ConfirmationDialogComponent, ConfirmDialogModel} from "../confirmation-dialog/confirmation-dialog.component";
import {NotificationService} from "../../services/notification.service";

@Component({
  selector: 'edc-demo-asset-viewer',
  templateUrl: './asset-viewer.component.html',
  styleUrls: ['./asset-viewer.component.scss']
})
export class AssetViewerComponent implements OnInit {

  filteredAssets$: Observable<Asset[]> = of([]);
  searchText = '';
  isTransferring = false;
  private fetch$ = new BehaviorSubject(null);

  constructor(private assetService: AssetService,
              private notificationService: NotificationService,
              private readonly dialog: MatDialog) {
  }

  private showError(error: string) {
    this.notificationService.showError("This asset cannot be deleted");
    console.error(error);
  }

  ngOnInit(): void {
    this.filteredAssets$ = this.fetch$
      .pipe(
        switchMap(() => {
          const assets$ = this.assetService.getAllAssets().pipe(map(assets => assets.map(asset => new Asset(asset.properties!))));
          return !!this.searchText ?
            assets$.pipe(map(assets => assets.filter(asset => asset.name.includes(this.searchText))))
            :
            assets$;
        }));
  }

  isBusy() {
    return this.isTransferring;
  }

  onSearch() {
    this.fetch$.next(null);
  }

  onDelete(asset: Asset) {

    const dialogData = ConfirmDialogModel.forDelete("asset", `"${asset.name}"`)
    const ref = this.dialog.open(ConfirmationDialogComponent, {maxWidth: "20%", data: dialogData});

    ref.afterClosed().subscribe(res => {
      if (res) {
        this.assetService.removeAsset(asset.id).subscribe(() => this.fetch$.next(null),
          err => this.showError(err),
          () => this.notificationService.showInfo("Successfully deleted")
        );
      }
    });

  }

  onCreate() {
    const dialogRef = this.dialog.open(AssetEditorDialog);
    dialogRef.afterClosed().pipe(first()).subscribe((result: { assetEntryDto?: AssetEntryDto }) => {
      const newAsset = result?.assetEntryDto;
      if (newAsset) {
        this.assetService.createAsset(newAsset).subscribe(() => this.fetch$.next(null), error => this.showError(error), () => this.notificationService.showInfo("Successfully created"));
      }
    });
  }
}
