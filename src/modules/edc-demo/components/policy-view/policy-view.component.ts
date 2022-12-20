import {Component, OnInit} from '@angular/core';
import {IdResponseDto, PolicyDefinitionResponseDto, PolicyService} from "../../../mgmt-api-client";
import {BehaviorSubject, Observable, Observer, of} from "rxjs";
import {first, map, switchMap} from "rxjs/operators";
import {MatDialog} from "@angular/material/dialog";
import {NewPolicyDialogComponent} from "../new-policy-dialog/new-policy-dialog.component";
import {NotificationService} from "../../services/notification.service";
import {ConfirmationDialogComponent, ConfirmDialogModel} from "../confirmation-dialog/confirmation-dialog.component";

@Component({
  selector: 'app-policy-view',
  templateUrl: './policy-view.component.html',
  styleUrls: ['./policy-view.component.scss']
})
export class PolicyViewComponent implements OnInit {

  filteredPolicies$: Observable<PolicyDefinitionResponseDto[]> = of([]);
  searchText: string = '';
  private fetch$ = new BehaviorSubject(null);
  private readonly errorOrUpdateSubscriber: Observer<IdResponseDto>;

  constructor(private policyService: PolicyService,
              private notificationService: NotificationService,
              private readonly dialog: MatDialog) {

    this.errorOrUpdateSubscriber = {
      next: x => this.fetch$.next(null),
      error: err => this.showError(err),
      complete: () => {
        this.notificationService.showInfo("Successfully completed")
      },
    }

  }

  ngOnInit(): void {
    this.filteredPolicies$ = this.fetch$.pipe(
      switchMap(() => {
        const policyDefinitions = this.policyService.getAllPolicies();
        return !!this.searchText ?
          policyDefinitions.pipe(map(policies => policies.filter(policy => this.isFiltered(policy, this.searchText))))
          :
          policyDefinitions;
      }));
  }

  onSearch() {
    this.fetch$.next(null);
  }

  onCreate() {
    const dialogRef = this.dialog.open(NewPolicyDialogComponent)
    dialogRef.afterClosed().pipe(first()).subscribe((result: PolicyDefinitionResponseDto) => {
      if (result) {
        this.policyService.createPolicy(result).subscribe(this.errorOrUpdateSubscriber);
      }
    })
  }

  /**
   * simple full-text search - serialize to JSON and see if "searchText"
   * is contained
   */
  private isFiltered(policy: PolicyDefinitionResponseDto, searchText: string) {
    return JSON.stringify(policy).includes(searchText);
  }

  delete(policy: PolicyDefinitionResponseDto) {

    let policyId = policy.id!;
    const dialogData = ConfirmDialogModel.forDelete("policy", policyId);

    const ref = this.dialog.open(ConfirmationDialogComponent, {maxWidth: '20%', data: dialogData});

    ref.afterClosed().subscribe(res => {
      if (res) {
        this.policyService.deletePolicy(policyId).subscribe(this.errorOrUpdateSubscriber);
      }
    });
  }

  private showError(error: Error) {
    console.error(error)
    this.notificationService.showError('This policy cannot be deleted');
  }
}
