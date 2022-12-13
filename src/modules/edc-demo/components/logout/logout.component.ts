import {Component, Inject, OnInit} from '@angular/core';
import {DOCUMENT} from "@angular/common";
import {AppConfigService} from "../../../app/app-config.service";

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(
      private appConfigService: AppConfigService,
      @Inject(DOCUMENT) private document: Document) { }

  ngOnInit(): void {
    this.document.location.href = this.appConfigService.getConfig()?.logoutUrl!;
  }

}
