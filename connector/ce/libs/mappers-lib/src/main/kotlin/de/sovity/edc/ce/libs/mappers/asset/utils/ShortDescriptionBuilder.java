/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.asset.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import de.sovity.edc.runtime.simple_di.Service;
import org.jsoup.Jsoup;

@Service
public class ShortDescriptionBuilder {

    public String buildShortDescription(String descriptionMarkdown) {
        if (descriptionMarkdown == null) {
            return null;
        }

        var text = extractMarkdownText(descriptionMarkdown);
        return abbreviate(text, 300);
    }

    public String extractMarkdownText(String markdown) {
        var options = new MutableDataSet();
        var parser = Parser.builder(options).build();
        var renderer = HtmlRenderer.builder(options).build();
        var document = parser.parse(markdown);
        var html = renderer.render(document);
        return Jsoup.parse(html).text();
    }

    String abbreviate(String text, int maxCharacters) {
        if (text == null) {
            return null;
        }
        return text.substring(0, Math.min(maxCharacters, text.length()));
    }
}
