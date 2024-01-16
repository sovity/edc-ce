import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CatalogDataOfferMapped} from '../catalog-page/mapping/catalog-page-result-mapped';

@Component({
  selector: 'broker-data-offer-list',
  templateUrl: './broker-data-offer-list.component.html',
  styleUrls: ['./broker-data-offer-list.component.scss'],
})
export class BrokerDataOfferList {
  @Input()
  dataOffers: CatalogDataOfferMapped[] = [];
  columnsToDisplay = ['organizationName', 'name', 'description', 'status'];

  @Output()
  dataOfferClick = new EventEmitter<CatalogDataOfferMapped>();

  onRowClick(clickedOffer: CatalogDataOfferMapped) {
    this.dataOfferClick.emit(clickedOffer);
  }
}
