export interface AvailableLanguage {
  code: string;
  name: string;
}

export const supportedLanguages: AvailableLanguage[] = [
  {code: 'en', name: 'English'},
  {code: 'de', name: 'Deutsch'},
];

export const isLanguageSupported = (value: unknown): value is string =>
  supportedLanguages.map((it) => it.code).includes(value as string);
