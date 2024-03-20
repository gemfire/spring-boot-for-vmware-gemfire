/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * The {@link OnMissingPropertyCondition} class is a {@link SpringBootCondition}, Spring {@link Condition} type
 * asserting whether the specified, declared properties are missing.
 *
 * @author John Blum
 * @see ConditionOutcome
 * @see SpringBootCondition
 * @see Condition
 * @see ConditionContext
 * @see AnnotationAttributes
 * @see PropertyResolver
 * @see AnnotatedTypeMetadata
 * @see ConditionalOnMissingProperty
 * @since 1.0.0
 */
public class OnMissingPropertyCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

		String annotationName = ConditionalOnMissingProperty.class.getName();

		Collection<AnnotationAttributes> annotationAttributesCollection =
			toAnnotationAttributesFromMultiValueMap(metadata.getAllAnnotationAttributes(annotationName));

		PropertyResolver propertyResolver = getPropertyResolver(context);

		Collection<String> allMatchingProperties = new ArrayList<>();

		annotationAttributesCollection.forEach(annotationAttributes -> {

			List<String> propertyNames = collectPropertyNames(annotationAttributes);

			allMatchingProperties.addAll(findMatchingProperties(propertyResolver, propertyNames));
		});

		return determineConditionOutcome(allMatchingProperties);
	}

	@SuppressWarnings("unchecked")
	private <T extends Collection<AnnotationAttributes>> T toAnnotationAttributesFromMultiValueMap(
			MultiValueMap<String, Object> map) {

		List<AnnotationAttributes> annotationAttributesList = new ArrayList<>();

		map.forEach((key, value) -> {
			for (int index = 0, size = value.size(); index < size; index++) {

				AnnotationAttributes annotationAttributes =
					resolveAnnotationAttributes(annotationAttributesList, index);

				annotationAttributes.put(key, value.get(index));
			}
		});

		return (T) annotationAttributesList;
	}

	private AnnotationAttributes resolveAnnotationAttributes(List<AnnotationAttributes> annotationAttributesList,
			int index) {

		if (index < annotationAttributesList.size()) {
			return annotationAttributesList.get(index);
		}
		else {
			AnnotationAttributes newAnnotationAttributes = new AnnotationAttributes();
			annotationAttributesList.add(newAnnotationAttributes);
			return newAnnotationAttributes;
		}
	}

	private PropertyResolver getPropertyResolver(ConditionContext context) {
		return context.getEnvironment();
	}

	private List<String> collectPropertyNames(AnnotationAttributes annotationAttributes) {

		String prefix = getPrefix(annotationAttributes);

		String[] names = getNames(annotationAttributes);

		return Arrays.stream(names).map(name -> prefix + name).collect(Collectors.toList());
	}

	private String[] getNames(AnnotationAttributes annotationAttributes) {

		String[] names = annotationAttributes.getStringArray("name");
		String[] values = annotationAttributes.getStringArray("value");

		Assert.isTrue(names.length > 0 || values.length > 0,
			String.format("The name or value attribute of @%s is required",
				ConditionalOnMissingProperty.class.getSimpleName()));

		// TODO remove; not needed when using @AliasFor.
		/*
		Assert.isTrue(names.length * values.length == 0,
			String.format("The name and value attributes of @%s are exclusive",
				ConditionalOnMissingProperty.class.getSimpleName()));
		*/

		return names.length > 0 ? names : values;
	}

	private String getPrefix(AnnotationAttributes annotationAttributes) {

		String prefix = annotationAttributes.getString("prefix");

		return StringUtils.hasText(prefix) ? prefix.trim().endsWith(".") ? prefix.trim() : prefix.trim() + "." : "";
	}

	private Collection<String> findMatchingProperties(PropertyResolver propertyResolver, List<String> propertyNames) {
		return propertyNames.stream().filter(propertyResolver::containsProperty).collect(Collectors.toSet());
	}

	private ConditionOutcome determineConditionOutcome(Collection<String> matchingProperties) {

		if (!matchingProperties.isEmpty()) {

			return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnMissingProperty.class)
				.found("property already defined", "properties already defined")
				.items(matchingProperties));
		}

		return ConditionOutcome.match();
	}
}
