import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {
  AvailableLanguage,
  isLanguageSupported,
  supportedLanguages,
} from 'src/app/core/utils/i18n-utils';
import {LocalStoredValue} from '../../../core/utils/local-stored-value';

@Component({
  selector: 'app-language-selector',
  templateUrl: './language-selector.component.html',
})
export class LanguageSelectorComponent implements OnInit {
  selectedLanguage = new LocalStoredValue<string>(
    'en',
    'selectedLanguage',
    isLanguageSupported,
  );

  supportedLanguages = supportedLanguages;

  constructor(private translateService: TranslateService) {}

  ngOnInit(): void {
    this.updateSelectedLanguage();
  }

  setSelectedLanguage(language: AvailableLanguage) {
    this.selectedLanguage.value = language.code;
    this.updateSelectedLanguage();
  }

  updateSelectedLanguage() {
    this.translateService.use(this.selectedLanguage.value);
  }
}
