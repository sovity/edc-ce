export interface PropertyGridField {
  icon: string;

  /**
   * Title of Property
   */
  label: string;

  /**
   * Adds "title"-Attribute to Label HTML Element
   */
  labelTitle?: string;

  /**
   * Property Value
   */
  text: string;

  url?: string;
  onclick?: () => void;
  additionalClasses?: string;

  copyButton?: boolean;
  tooltip?: string | null;
  textIconAfter?: string | null;
}
