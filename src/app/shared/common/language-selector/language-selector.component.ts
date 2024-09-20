import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {LocalStoredValue} from '../../../core/utils/local-stored-value';

interface AvailableLanguage {
  code: string;
  name: string;
}

@Component({
  selector: 'app-language-selector',
  templateUrl: './language-selector.component.html',
})
export class LanguageSelectorComponent implements OnInit {
  supportedLanguages: AvailableLanguage[] = [
    {code: 'en', name: 'English'},
    {code: 'de', name: 'Deutsch'},
  ];

  selectedLanguage = new LocalStoredValue<string>(
    'en',
    'selectedLanguage',
    (value): value is string =>
      this.supportedLanguages.map((it) => it.code).includes(value as string),
  );

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
