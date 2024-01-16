/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.api.filtering;

import de.sovity.edc.ext.brokerserver.api.model.CnfFilter;
import de.sovity.edc.ext.brokerserver.api.model.CnfFilterAttribute;
import de.sovity.edc.ext.brokerserver.api.model.CnfFilterItem;
import de.sovity.edc.ext.brokerserver.api.model.CnfFilterValue;
import de.sovity.edc.ext.brokerserver.api.model.CnfFilterValueAttribute;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryFields;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQuerySelectedFilterQuery;
import de.sovity.edc.ext.brokerserver.dao.utils.JsonDeserializationUtils;
import de.sovity.edc.ext.brokerserver.utils.CollectionUtils2;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class CatalogFilterService {
    private final CatalogFilterAttributeDefinitionService catalogFilterAttributeDefinitionService;

    private final Comparator<String> caseInsensitiveEmptyStringLast = (s1, s2) -> {
        int result = s1.compareToIgnoreCase(s2);
        if (s1.isEmpty() && !s2.isEmpty()) {
            return 1;
        } else if (!s1.isEmpty() && s2.isEmpty()) {
            return -1;
        } else {
            return result;
        }
    };


    /**
     * Currently supported filters for the catalog page.
     *
     * @return attribute definitions
     */
    private List<CatalogFilterAttributeDefinition> getAvailableFilters() {
        return List.of(
            catalogFilterAttributeDefinitionService.buildDataSpaceFilter(),
            catalogFilterAttributeDefinitionService.forField(
                fields -> fields.getDataOfferTable().DATA_CATEGORY,
                "dataCategory",
                "Data Category"
            ),
            catalogFilterAttributeDefinitionService.forField(
                fields -> fields.getDataOfferTable().DATA_SUBCATEGORY,
                "dataSubcategory",
                "Data Subcategory"
            ),
            catalogFilterAttributeDefinitionService.forField(
                fields -> fields.getDataOfferTable().DATA_MODEL,
                "dataModel",
                "Data Model"
            ),
            catalogFilterAttributeDefinitionService.forField(
                fields -> fields.getDataOfferTable().TRANSPORT_MODE,
                "transportMode",
                "Transport Mode"
            ),
            catalogFilterAttributeDefinitionService.forField(
                fields -> fields.getDataOfferTable().GEO_REFERENCE_METHOD,
                "geoReferenceMethod",
                "Geo Reference Method"
            ),
            catalogFilterAttributeDefinitionService.forField(
                CatalogQueryFields::getOrganizationName,
                "curatorOrganizationName",
                "Organization Name"
            ),
            catalogFilterAttributeDefinitionService.buildConnectorEndpointFilter()
        );
    }

    public List<CatalogQueryFilter> getCatalogQueryFilters(CnfFilterValue cnfFilterValue) {
        var values = getCnfFilterValuesMap(cnfFilterValue);
        return getAvailableFilters().stream()
            .map(filter -> new CatalogQueryFilter(
                filter.name(),
                filter.valueGetter(),
                getQueryFilter(filter, values.get(filter.name()))
            ))
            .toList();
    }

    private CatalogQuerySelectedFilterQuery getQueryFilter(CatalogFilterAttributeDefinition filter, List<String> values) {
        if (CollectionUtils2.isNotEmpty(values)) {
            return fields -> filter.filterApplier().filterDataOffers(fields, values);
        }
        return null;
    }

    public CnfFilter buildAvailableFilters(String filterValuesJson) {
        var filterValues = JsonDeserializationUtils.read2dStringList(filterValuesJson);
        var filterAttributes = zipAvailableFilters(getAvailableFilters(), filterValues)
            .map(availableFilter -> new CnfFilterAttribute(
                availableFilter.definition().name(),
                availableFilter.definition().label(),
                buildAvailableFilterValues(availableFilter)
            ))
            .toList();
        return new CnfFilter(filterAttributes);
    }

    private List<CnfFilterItem> buildAvailableFilterValues(AvailableFilter availableFilter) {
        return availableFilter.availableValues().stream()
            .sorted(caseInsensitiveEmptyStringLast)
            .map(value -> new CnfFilterItem(value, value))
            .toList();
    }

    private Stream<AvailableFilter> zipAvailableFilters(List<CatalogFilterAttributeDefinition> availableFilters, List<List<String>> filterValues) {
        Validate.isTrue(
            availableFilters.size() == filterValues.size(),
            "Number of available filters and filter values must match: %d != %d",
            availableFilters.size(),
            filterValues.size()
        );
        return Stream.iterate(0, i -> i + 1)
            .limit(availableFilters.size())
            .map(i -> new AvailableFilter(availableFilters.get(i), filterValues.get(i)));
    }

    private record AvailableFilter(CatalogFilterAttributeDefinition definition, List<String> availableValues) {
    }

    private Map<String, List<String>> getCnfFilterValuesMap(CnfFilterValue cnfFilterValue) {
        if (cnfFilterValue == null || cnfFilterValue.getSelectedAttributeValues() == null) {
            return Map.of();
        }
        return cnfFilterValue.getSelectedAttributeValues().stream()
            .filter(it -> it.getId() != null && CollectionUtils2.isNotEmpty(it.getSelectedIds()))
            .collect(toMap(CnfFilterValueAttribute::getId, CnfFilterValueAttribute::getSelectedIds));
    }
}
