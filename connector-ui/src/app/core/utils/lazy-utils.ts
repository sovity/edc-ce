import {TranslateService} from '@ngx-translate/core';

export class LazyTranslation<T> {
  private value: T | null = null;
  private language: string | null = null;
  constructor(
    private translateService: TranslateService,
    private generate: (translationService: TranslateService) => T,
  ) {}

  getValue(): T {
    if (
      this.value == null ||
      this.language !== this.translateService.currentLang
    ) {
      this.language = this.translateService.currentLang;
      this.value = this.generate(this.translateService);
    }
    return this.value;
  }
}
