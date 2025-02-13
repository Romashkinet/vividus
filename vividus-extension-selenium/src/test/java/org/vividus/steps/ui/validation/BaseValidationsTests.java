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

package org.vividus.steps.ui.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.vividus.testdouble.TestLocatorType.SEARCH;
import static org.vividus.ui.action.search.IElementAction.NOT_SET_CONTEXT;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.vividus.selenium.IWebDriverProvider;
import org.vividus.steps.ComparisonRule;
import org.vividus.ui.State;
import org.vividus.ui.action.ISearchActions;
import org.vividus.ui.action.search.Locator;
import org.vividus.ui.action.search.SearchParameters;
import org.vividus.ui.context.IUiContext;
import org.vividus.ui.validation.matcher.ExistsMatcher;
import org.vividus.ui.validation.matcher.ExpectedConditionsMatcher;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("MethodCount")
class BaseValidationsTests
{
    private static final String SOME_ELEMENT = "Some element";
    private static final String PATTERN_ELEMENTS = "Number %1$s of elements found by '%2$s'";
    private static final String BUSINESS_DESCRIPTION = "Test business description";
    private static final String XPATH_INT = ".//xpath=1";
    private static final int INT_ARG = 1;
    private static final String EQUAL_TO_MATCHER = "number of elements is a value equal to <1>";
    private static final String AT_LEAST_ONE_ELEMENT_ASSERTION = "There is at least one element with attributes ' "
            + "Search: './/xpath=1'; Visibility: VISIBLE;'";
    private static final String NUMBER_OF_ELEMENTS_IS_GREATER_THAN_0 = "number of elements is a value greater"
            + " than <0>";

    @Mock private WebDriver mockedWebDriver;
    @Mock private SearchContext mockedSearchContext;
    @Mock private WebElement mockedWebElement;
    @Mock private ExpectedCondition<?> mockedExpectedCondition;
    @Mock private IWebDriverProvider mockedWebDriverProvider;
    @Mock private IUiContext uiContext;
    @Mock private ISearchActions searchActions;
    @Mock private IDescriptiveSoftAssert softAssert;
    @InjectMocks private BaseValidations baseValidations;

    private List<WebElement> webElements;
    private BaseValidations spy;

    @SuppressWarnings("unchecked")
    @Test
    void testAssertIfElementExistsWithLocator()
    {
        webElements = List.of(mockedWebElement);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator attributes = mock(Locator.class);
        when(searchActions.findElements(mockedSearchContext, attributes)).thenReturn(webElements);
        when(attributes.toString()).thenReturn("attributes");
        when(softAssert.assertThat(eq(SOME_ELEMENT), anyString(), eq(webElements),
                any(Matcher.class))).thenReturn(true);
        WebElement foundElement = baseValidations.assertIfElementExists(SOME_ELEMENT, attributes);
        assertEquals(mockedWebElement, foundElement);
    }

    @Test
    void shouldRecordFailedAssertionInCaseOfNullContext()
    {
        Locator attributes = mock(Locator.class);
        assertNull(baseValidations.assertIfElementExists(SOME_ELEMENT, attributes));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void shouldNotSearchIfContextNullAssertIfElementExists()
    {
        Locator locator = new Locator(SEARCH, XPATH_INT);
        assertNull(baseValidations.assertIfElementExists(BUSINESS_DESCRIPTION, null, locator));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void shouldNotSearchIfContextNullAssertIfElementsExist()
    {
        Locator locator = new Locator(SEARCH, XPATH_INT);
        assertEquals(List.of(), baseValidations.assertIfElementsExist(BUSINESS_DESCRIPTION, locator));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void shouldNotSearchIfContextNullAssertIfExactNumberOfElementsFound()
    {
        Locator locator = new Locator(SEARCH, XPATH_INT);
        assertEquals(false, baseValidations.assertIfExactNumberOfElementsFound(BUSINESS_DESCRIPTION, null,
                locator, 1));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void shouldNotSearchIfContextNullAssertIfAtLeastNumberOfElementsExist()
    {
        Locator locator = new Locator(SEARCH, XPATH_INT);
        assertEquals(List.of(), baseValidations.assertIfAtLeastNumberOfElementsExist(BUSINESS_DESCRIPTION, null,
                locator, 1));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void shouldNotSearchIfContextNullAssertIfAtLeastOneElementExists()
    {
        Locator locator = new Locator(SEARCH, XPATH_INT);
        assertEquals(null, baseValidations.assertIfAtLeastOneElementExists(BUSINESS_DESCRIPTION, null, locator));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void shouldNotSearchIfContextNullAssertIfNumberOfElementsFound()
    {
        Locator locator = new Locator(SEARCH, XPATH_INT);
        assertEquals(List.of(), baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, locator, 0,
                ComparisonRule.EQUAL_TO));
        verify(softAssert).recordFailedAssertion(NOT_SET_CONTEXT);
        verifyNoMoreInteractions(softAssert);
        verifyNoInteractions(searchActions);
    }

    @Test
    void testAssertIfElementDoesNotExistWithLocator()
    {
        spy = Mockito.spy(baseValidations);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        SearchParameters searchParameters = new SearchParameters();
        Locator attributes = new Locator(SEARCH, searchParameters);
        when(searchActions.findElements(mockedSearchContext, attributes)).thenReturn(List.of());
        assertTrue(spy.assertIfElementDoesNotExist(BUSINESS_DESCRIPTION, attributes));
        assertFalse(searchParameters.isWaitForElement());
    }

    @Test
    void testAssertIfElementDoesNotExistWhenNoFailedAssertionRecordingIsNeeded()
    {
        spy = Mockito.spy(baseValidations);
        webElements = List.of(mockedWebElement);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        SearchParameters searchParameters = new SearchParameters();
        Locator attributes = new Locator(SEARCH, searchParameters);
        when(searchActions.findElements(mockedSearchContext, attributes)).thenReturn(webElements);
        assertFalse(spy.assertIfElementDoesNotExist(BUSINESS_DESCRIPTION, attributes));
        assertFalse(searchParameters.isWaitForElement());
        verifyNoInteractions(softAssert);
    }

    @Test
    void testAssertElementStateNullWebElement()
    {
        State state = mock(State.class);
        boolean result = baseValidations.assertElementState(BUSINESS_DESCRIPTION, state, null);
        verifyNoInteractions(state);
        assertFalse(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAssertWebElementStateSuccess()
    {
        when(mockedWebDriverProvider.get()).thenReturn(mockedWebDriver);
        String mockedExpectedConditionToString = mockedExpectedCondition.toString();
        State state = mock(State.class);
        doReturn(mockedExpectedCondition).when(state).getExpectedCondition(mockedWebElement);
        when(softAssert.assertThat(eq(BUSINESS_DESCRIPTION), eq(mockedExpectedConditionToString),
                eq(mockedWebDriver), argThat(matcher -> matcher instanceof ExpectedConditionsMatcher)))
                .thenReturn(Boolean.TRUE);
        assertTrue(baseValidations.assertElementState(BUSINESS_DESCRIPTION, state, mockedWebElement));
    }

    @Test
    void testAssertElementStateResultNotBoolean()
    {
        spy = Mockito.spy(baseValidations);
        when(mockedWebDriverProvider.get()).thenReturn(mockedWebDriver);
        String mockedExpectedConditionToString = mockedExpectedCondition.toString();
        State state = mock(State.class);
        doReturn(mockedExpectedCondition).when(state).getExpectedCondition(mockedWebElement);
        spy.assertElementState(BUSINESS_DESCRIPTION, state, mockedWebElement);
        verify(softAssert).assertThat(eq(BUSINESS_DESCRIPTION), eq(mockedExpectedConditionToString),
                eq(mockedWebDriver), argThat(matcher -> matcher instanceof ExpectedConditionsMatcher));
    }

    @Test
    void testAssertIfExactNumberOfElementsFound()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        String systemDescription = String.format(PATTERN_ELEMENTS, INT_ARG, locator);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        mockAssertingWebElements(webElements);
        baseValidations.assertIfExactNumberOfElementsFound(BUSINESS_DESCRIPTION, locator, INT_ARG);
        verify(softAssert).assertThat(eq(BUSINESS_DESCRIPTION), eq(systemDescription),
                eq(webElements), argThat(e -> EQUAL_TO_MATCHER.equals(e.toString())));
    }

    @Test
    void testAssertIfExactNumberOfElementsFoundNullList()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        String systemDescription = String.format(PATTERN_ELEMENTS, INT_ARG, locator);
        mockAssertingWebElements(webElements);
        assertFalse(
                baseValidations.assertIfExactNumberOfElementsFound(BUSINESS_DESCRIPTION, locator, INT_ARG));
        verify(softAssert).assertThat(eq(BUSINESS_DESCRIPTION), eq(systemDescription),
                eq(webElements), argThat(e -> EQUAL_TO_MATCHER.equals(e.toString())));
    }

    @Test
    void testAssertIfElementsExist()
    {
        webElements = List.of(mockedWebElement);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        baseValidations.assertIfElementsExist(BUSINESS_DESCRIPTION, locator);
        verify(softAssert).assertThat(eq(BUSINESS_DESCRIPTION), eq(AT_LEAST_ONE_ELEMENT_ASSERTION), eq(webElements),
                argThat(e -> NUMBER_OF_ELEMENTS_IS_GREATER_THAN_0.equals(e.toString())));
        assertEquals(1, webElements.size());
    }

    @Test
    void testAssertIfAtLeastOneElementExists()
    {
        webElements = List.of(mockedWebElement);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        WebElement element = baseValidations.assertIfAtLeastOneElementExists(BUSINESS_DESCRIPTION, locator);
        verify(softAssert).assertThat(eq(BUSINESS_DESCRIPTION), eq(AT_LEAST_ONE_ELEMENT_ASSERTION), eq(webElements),
                argThat(e -> NUMBER_OF_ELEMENTS_IS_GREATER_THAN_0.equals(e.toString())));
        assertEquals(mockedWebElement, element);
    }

    @Test
    void testAssertNoElementFound()
    {
        List<WebElement> webElements = List.of();
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator attributes = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, attributes)).thenReturn(webElements);
        when(softAssert.assertThat(eq(BUSINESS_DESCRIPTION),
                eq("An element with attributes: ' Search: './/xpath=1'; Visibility: VISIBLE;'"), eq(webElements),
                argThat(matcher -> matcher instanceof ExistsMatcher))).thenReturn(false);
        WebElement element = baseValidations.assertIfElementExists(BUSINESS_DESCRIPTION, attributes);
        assertNull(element);
    }

    @Test
    void testAssertIfAtLeastNumberOfElementsExistSuccess()
    {
        mockAssertingWebElements(webElements);
        List<WebElement> result = testAssertIfAtLeastNumberOfElementsExist(true);
        assertEquals(webElements, result);
    }

    @Test
    void testAssertIfAtLeastNumberOfElementsExistFailed()
    {
        mockAssertingWebElements(webElements);
        List<WebElement> result = testAssertIfAtLeastNumberOfElementsExist(false);
        assertTrue(result.isEmpty());
    }

    private List<WebElement> testAssertIfAtLeastNumberOfElementsExist(boolean assertionResult)
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        int leastCount = 1;
        String systemDescription = String.format("There are at least %d elements with attributes '%s'", leastCount,
                locator);
        when(softAssert.assertThat(eq(BUSINESS_DESCRIPTION), eq(systemDescription), eq(webElements),
                argThat(e -> "number of elements is a value equal to or greater than <1>".equals(e.toString()))))
                .thenReturn(assertionResult);
        return baseValidations.assertIfAtLeastNumberOfElementsExist(BUSINESS_DESCRIPTION, locator, leastCount);
    }

    @Test
    void testAssertIfAtLeastOneElementExistsNull()
    {
        spy = Mockito.spy(baseValidations);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        WebElement element = spy.assertIfAtLeastOneElementExists(BUSINESS_DESCRIPTION, locator);
        verify(spy).assertIfAtLeastOneElementExists(BUSINESS_DESCRIPTION, mockedSearchContext, locator);
        assertNull(element);
    }

    @Test
    void testAssertIfNumberOfElementsFoundWithoutContext()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        testAssertIfNumberOfElementsFound(
                attributes -> baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, attributes, 1,
                        ComparisonRule.EQUAL_TO), true, List.of(mockedWebElement));
    }

    @Test
    void testAssertIfNumberOfElementsFoundWithContext()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        testAssertIfNumberOfElementsFound(
                attributes -> baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, attributes, 1,
                        ComparisonRule.EQUAL_TO), true, List.of(mockedWebElement));
    }

    @Test
    void testAssertIfNumberOfElementsFoundWithoutContextNotDesiredQuantity()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        testAssertIfNumberOfElementsFound(
                attributes -> baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, attributes, 1,
                        ComparisonRule.EQUAL_TO), false, List.of());
    }

    @Test
    void testAssertIfNumberOfElementsFoundWithContextNotDesiredQuantity()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        testAssertIfNumberOfElementsFound(
                attributes -> baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, attributes, 1,
                        ComparisonRule.EQUAL_TO), false, List.of());
    }

    @Test
    void testAssertElementExists()
    {
        webElements = List.of(mockedWebElement);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        when(softAssert.recordAssertion(true, BUSINESS_DESCRIPTION + " is found by the locator " + locator))
                .thenReturn(true);
        Optional<WebElement> element = baseValidations.assertElementExists(BUSINESS_DESCRIPTION, locator);
        assertTrue(element.isPresent());
        assertEquals(mockedWebElement, element.get());
    }

    @Test
    void testAssertElementsNumber()
    {
        List<WebElement> elements = List.of(mockedWebElement);
        mockAssertingWebElements(elements);
        when(softAssert.assertThat(eq(BUSINESS_DESCRIPTION), eq(elements.size()),
                argThat(e -> "a value equal to <1>".equals(e.toString())))).thenReturn(true);
        assertTrue(baseValidations.assertElementsNumber(BUSINESS_DESCRIPTION, elements, ComparisonRule.EQUAL_TO, 1));
    }

    @Test
    void testAssertElementExistsMoreThanOne()
    {
        webElements = List.of(mockedWebElement, mockedWebElement);
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        Optional<WebElement> element = baseValidations.assertElementExists(BUSINESS_DESCRIPTION, locator);
        assertTrue(element.isEmpty());
        verify(softAssert).recordFailedAssertion(
                "The number of elements found by the locator " + locator + " is 2, but expected 1");
    }

    @Test
    void testAssertElementExistsEmptyElements()
    {
        List<WebElement> elements = List.of();
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        Locator locator = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(elements);
        when(softAssert.recordAssertion(false,
                BUSINESS_DESCRIPTION + " is not found by the locator " + locator.toString())).thenReturn(false);
        Optional<WebElement> element = baseValidations.assertElementExists(BUSINESS_DESCRIPTION, locator);
        assertTrue(element.isEmpty());
    }

    @Test
    void shouldFailIfExpectedNumberOfElementsIsLessThanZero()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> baseValidations
                .assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, null, 0, ComparisonRule.LESS_THAN));
        assertEquals("Invalid input rule: the number of elements can not be less than 0", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "EQUAL_TO, number of elements is a value equal to <0>, is equal to 0",
            "LESS_THAN_OR_EQUAL_TO, number of elements is a value less than or equal to <0>, is less than or equal to 0"
    })
    void shouldNotWaitForElementsIfExpectedNumberOfElementsIsEqualToOrLessThanOrEqualToZero(ComparisonRule rule,
            String matcherAsString, String comparison)
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        testAssertIfNumberOfElementsFound(
                attributes -> baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, attributes, 0, rule),
                false, List.of(), comparison, matcherAsString, false);
    }

    @Test
    void shouldWaitForElementIfExpectedNumberOfElementsIsGreaterThanOrEqualToZero()
    {
        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        testAssertIfNumberOfElementsFound(
                attributes -> baseValidations.assertIfNumberOfElementsFound(BUSINESS_DESCRIPTION, attributes, 0,
                        ComparisonRule.GREATER_THAN_OR_EQUAL_TO), true, List.of(), "is greater than or equal to 0",
                "number of elements is a value equal to or greater than <0>", true);
    }

    @Test
    void shouldAssertNumberOfElementsFound()
    {
        spy = Mockito.spy(baseValidations);
        Locator locator = mock(Locator.class);
        List<WebElement> webElements = List.of(mockedWebElement);

        when(uiContext.getSearchContext()).thenReturn(mockedSearchContext);
        when(searchActions.findElements(mockedSearchContext, locator)).thenReturn(webElements);
        doReturn(true).when(spy).assertElementsNumber(BUSINESS_DESCRIPTION, webElements, ComparisonRule.GREATER_THAN,
                1);

        assertEquals(webElements,
                spy.assertNumberOfElementsFound(BUSINESS_DESCRIPTION, locator, 1, ComparisonRule.GREATER_THAN));
    }

    private void testAssertIfNumberOfElementsFound(Function<Locator, List<WebElement>> actualCall, boolean checkPassed,
            List<WebElement> foundElements)
    {
        testAssertIfNumberOfElementsFound(actualCall, checkPassed, foundElements, "is equal to 1", EQUAL_TO_MATCHER,
                true);
    }

    private void testAssertIfNumberOfElementsFound(Function<Locator, List<WebElement>> actualCall,
            boolean checkPassed, List<WebElement> foundElements, String comparison, String marcher, boolean wait)
    {
        Locator attributes = new Locator(SEARCH, XPATH_INT);
        when(searchActions.findElements(mockedSearchContext, attributes)).thenReturn(foundElements);
        when(softAssert.assertThat(eq(BUSINESS_DESCRIPTION),
                eq("Number of elements found by ' Search: './/xpath=1'; Visibility: VISIBLE;' " + comparison),
                eq(foundElements), argThat(m -> marcher.equals(m.toString())))).thenReturn(checkPassed);
        assertTrue(attributes.getSearchParameters().isWaitForElement());
        assertEquals(foundElements, actualCall.apply(attributes));
        assertEquals(wait, attributes.getSearchParameters().isWaitForElement());
    }

    private void mockAssertingWebElements(List<WebElement> elements)
    {
        doAnswer(a ->
        {
            BooleanSupplier supplier = a.getArgument(1, BooleanSupplier.class);
            return supplier.getAsBoolean();
        }).when(uiContext).withAssertingWebElements(eq(elements), any());
    }
}
