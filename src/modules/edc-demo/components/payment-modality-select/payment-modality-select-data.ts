import {PaymentModalitySelectItem} from './payment-modality-select-item';

export const PAYMENT_MODALITY_SELECT_DATA: PaymentModalitySelectItem[] = [
  {
    id: 'FREE',
    comment: "To express that the exchange of resource is free",
    label: "free"
  },

  {
    id: 'FIXED_PRICE',
    comment: "To express that the exchange of resource is with a fixed price",
    label: "fixed price"
  },

  {
    id: 'NEGOTIATION_BASIS',
    comment: "To express that the exchange of resource is negotiation-based.",
    label: "negotiation basis"
  }
];
