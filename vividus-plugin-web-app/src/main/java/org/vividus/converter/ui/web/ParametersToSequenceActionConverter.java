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

import java.lang.reflect.Type;
import java.util.function.Supplier;

import javax.inject.Named;

import org.jbehave.core.steps.ParameterConverters.AbstractParameterConverter;
import org.jbehave.core.steps.Parameters;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.vividus.converter.PointConverter;
import org.vividus.converter.ui.StringToLocatorConverter;
import org.vividus.steps.ui.web.model.SequenceAction;
import org.vividus.steps.ui.web.model.SequenceActionType;

@Named
public class ParametersToSequenceActionConverter extends AbstractParameterConverter<Parameters, SequenceAction>
{
    private final StringToLocatorConverter stringToLocatorConverter;
    private final PointConverter pointConverter;

    public ParametersToSequenceActionConverter(StringToLocatorConverter stringToLocatorConverter,
                                               PointConverter pointConverter)
    {
        this.stringToLocatorConverter = stringToLocatorConverter;
        this.pointConverter = pointConverter;
    }

    @Override
    public SequenceAction convertValue(Parameters parameters, Type type)
    {
        SequenceActionType actionType = parameters.valueAs("type", SequenceActionType.class);
        String argumentAsString = argumentAs(parameters, String.class);
        if (argumentAsString.isEmpty() && actionType.isNullable())
        {
            return new SequenceAction(actionType, null);
        }
        Type argumentType = actionType.getArgumentType();
        return new SequenceAction(actionType, convertArgument(argumentAsString,
                argumentType, () -> argumentAs(parameters, argumentType)));
    }

    private <T> T argumentAs(Parameters parameters, Type type)
    {
        return parameters.valueAs("argument", type);
    }

    private Object convertArgument(String argumentValue, Type argumentType, Supplier<Object> defaultConverter)
    {
        if (argumentType.equals(WebElement.class))
        {
            return stringToLocatorConverter.convertValue(argumentValue, null);
        }
        else if (argumentType.equals(Point.class))
        {
            return pointConverter.convertValue(argumentValue, null);
        }
        return defaultConverter.get();
    }
}
