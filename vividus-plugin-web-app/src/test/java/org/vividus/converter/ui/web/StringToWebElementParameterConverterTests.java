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

package org.vividus.converter.ui.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebElement;
import org.vividus.ui.action.SearchActions;
import org.vividus.ui.web.util.ElementUtil;

@ExtendWith(MockitoExtension.class)
class StringToWebElementParameterConverterTests
{
    @Mock private SearchActions searchActions;
    @Mock private ElementUtil elementUtil;
    @InjectMocks private StringToWebElementParameterConverter converter;

    @Test
    void shouldConvertStringToWebElement()
    {
        String locator = "locator";
        Optional<WebElement> expected = Optional.of(mock(WebElement.class));
        when(elementUtil.getElement(locator, searchActions)).thenReturn(() -> expected);
        assertEquals(expected, converter.convertValue(locator, null).get());
    }
}
