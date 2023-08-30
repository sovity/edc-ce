export interface PolicyCard {
  id: string;
  isRegular: boolean;
  irregularities: string[];
  constraints: PolicyCardConstraint[];

  objectForJson: any;
}

export interface PolicyCardConstraint {
  left: string;
  operator: string;
  right: string;
}
