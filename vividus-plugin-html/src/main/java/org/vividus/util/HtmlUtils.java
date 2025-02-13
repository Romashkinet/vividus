/*
 * Copyright 2019-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class HtmlUtils
{
    private HtmlUtils()
    {
    }

    public static Elements getElements(String html, String cssSelector)
    {
        return getElements("", html, cssSelector);
    }

    public static Elements getElements(String baseUri, String html, String cssSelector)
    {
        Document document = Jsoup.parse(html, baseUri);
        return document.select(cssSelector);
    }
}
