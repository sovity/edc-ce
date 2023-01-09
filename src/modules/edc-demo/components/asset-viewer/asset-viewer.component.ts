import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {AssetService,} from "../../../edc-dmgmt-client";
import {AssetEditorDialog} from "../asset-editor-dialog/asset-editor-dialog.component";
import {Asset} from "../../models/asset";
import {ConfirmationDialogComponent, ConfirmDialogModel} from "../confirmation-dialog/confirmation-dialog.component";
import {NotificationService} from "../../services/notification.service";
import {AppConfigService} from "../../../app/app-config.service";
import {AssetPropertyMapper} from "../../services/asset-property-mapper";

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
  themeClassString: any;

  constructor(private assetService: AssetService,
              private notificationService: NotificationService,
              private readonly dialog: MatDialog,
              private appConfigService: AppConfigService,
              private assetPropertyMapper: AssetPropertyMapper) {
  }

  private showError(error: string) {
    this.notificationService.showError("This asset cannot be deleted");
    console.error(error);
  }

  ngOnInit(): void {
    this.themeClass();
    this.filteredAssets$ = this.fetch$
      .pipe(
        switchMap(() => {
          let assets$ = this.assetService.getAllAssets()
            .pipe(map(assets => assets.map(asset => this.assetPropertyMapper.readProperties(asset.properties))));

          if (this.searchText) {
            assets$ = assets$.pipe(map(assets => assets.filter(asset => asset.name?.includes(this.searchText))))
          }

          return assets$;
        }));
  }

  themeClass() {
    this.themeClassString = this.appConfigService.getConfig()?.theme;
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
    this.dialog.open(AssetEditorDialog, {panelClass: this.themeClassString});
  }
}
