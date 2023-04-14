export interface PropertyGridField {
  icon: string;
  label: string;
  text: string;
  url?: string;
  onclick?: () => void;
  additionalClasses?: string;
}
