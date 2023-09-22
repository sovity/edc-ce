import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from '../../../../environments/environment';
import {ContractOffer} from '../models/contract-offer';
import {NegotiationResult} from '../models/negotiation-result';
import {TransferProcessStates} from '../models/transfer-process-states';
import {NotificationService} from '../notification.service';
import {
  ContractNegotiationService as ContractNegotiationApiService,
  ContractNegotiationDto,
  NegotiationInitiateRequestDto,
} from './legacy-managent-api-client';

interface RunningTransferProcess {
  processId: string;
  assetId?: string;
  state: TransferProcessStates;
}

@Injectable({providedIn: 'root'})
export class ContractNegotiationService {
  runningTransferProcesses: RunningTransferProcess[] = [];
  // contractOfferId, NegotiationResult
  runningNegotiations: Map<string, NegotiationResult> = new Map<
    string,
    NegotiationResult
  >();
  // contractOfferId, contractAgreementId
  finishedNegotiations: Map<string, ContractNegotiationDto> = new Map<
    string,
    ContractNegotiationDto
  >();
  private pollingHandleNegotiation?: any;

  constructor(
    private contractNegotiationService: ContractNegotiationApiService,
    private router: Router,
    private notificationService: NotificationService,
  ) {
    if (!environment.production) {
      // Test data on local dev
      this.runningNegotiations.set(
        'offer:6db25605-cd8c-4528-ade2-6a90349d06ac',
        {} as any,
      );
      this.finishedNegotiations.set(
        'offer:6db25605-cd8c-4528-ade1-6a90389d06ac',
        {} as any,
      );
    }
  }

  isBusy(contractOffer: ContractOffer) {
    return (
      this.runningNegotiations.get(contractOffer.id) !== undefined ||
      !!this.runningTransferProcesses.find(
        (tp) => tp.assetId === contractOffer.asset.assetId,
      )
    );
  }

  isNegotiated(contractOffer: ContractOffer) {
    return this.finishedNegotiations.get(contractOffer.id) !== undefined;
  }

  getState(contractOffer: ContractOffer): string {
    const transferProcess = this.runningTransferProcesses.find(
      (tp) => tp.assetId === contractOffer.asset.assetId,
    );
    if (transferProcess) {
      return TransferProcessStates[transferProcess.state];
    }

    const negotiation = this.runningNegotiations.get(contractOffer.id);
    if (negotiation) {
      return 'negotiating';
    }

    return '';
  }

  negotiate(contractOffer: ContractOffer) {
    const initiateRequest: NegotiationInitiateRequestDto = {
      connectorAddress: contractOffer.asset.connectorEndpoint!,

      offer: {
        offerId: contractOffer.id,
        assetId: contractOffer.asset.assetId,
        policy: contractOffer.policy,
      },
      connectorId: 'my-connector',
      protocol: 'ids-multipart',
    };

    const finishedNegotiationStates = ['CONFIRMED', 'DECLINED', 'ERROR'];

    this.initiateNegotiation(initiateRequest).subscribe(
      (negotiationId) => {
        this.finishedNegotiations.delete(initiateRequest.offer.offerId);
        this.runningNegotiations.set(initiateRequest.offer.offerId, {
          id: negotiationId,
          offerId: initiateRequest.offer.offerId,
        });

        if (!this.pollingHandleNegotiation) {
          // there are no active negotiations
          this.pollingHandleNegotiation = setInterval(() => {
            for (const negotiation of this.runningNegotiations.values()) {
              this.getNegotiationState(negotiation.id).subscribe(
                (updatedNegotiation) => {
                  if (
                    finishedNegotiationStates.includes(updatedNegotiation.state)
                  ) {
                    let offerId = negotiation.offerId;
                    this.runningNegotiations.delete(offerId);
                    if (updatedNegotiation.state === 'CONFIRMED') {
                      this.finishedNegotiations.set(
                        offerId,
                        updatedNegotiation,
                      );
                      this.notificationService.showInfo(
                        'Contract Negotiation complete!',
                        'Show me!',
                        () => {
                          this.router.navigate(['/contracts']);
                        },
                      );
                    }
                  }

                  if (this.runningNegotiations.size === 0) {
                    clearInterval(this.pollingHandleNegotiation);
                    this.pollingHandleNegotiation = undefined;
                  }
                },
              );
            }
          }, 1000);
        }
      },
      (error) => {
        console.error(error);
        this.notificationService.showError('Error starting negotiation');
      },
    );
  }
  private initiateNegotiation(
    initiateDto: NegotiationInitiateRequestDto,
  ): Observable<string> {
    return this.contractNegotiationService
      .initiateContractNegotiation(initiateDto, 'body', false)
      .pipe(map((t) => t.id!));
  }

  private getNegotiationState(id: string): Observable<ContractNegotiationDto> {
    return this.contractNegotiationService.getNegotiation(id);
  }
}
