import type en from '../messages/en.json';

declare type TranslationKey = keyof typeof en;

declare module 'next-intl' {
  interface AppConfig {
    Messages: typeof en;
  }
}
