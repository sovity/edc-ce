--
--  Copyright (c) 2024 sovity GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       sovity GmbH - SQL Script
--
--

update edc_asset_property
set property_value = (jsonb(property_value) #- '{"https://w3id.org/mobilitydcat-ap/mobilityDataStandard","@id"}' ||
                      to_jsonb(json_build_object('https://w3id.org/mobilitydcat-ap/mobilityDataStandard',
                                                 json_build_object(
                                                         'https://w3id.org/mobilitydcat-ap/mobility-data-standard',
                                                                  jsonb(property_value)->'https://w3id.org/mobilitydcat-ap/mobilityDataStandard'->'@id'))))::text
where property_name = 'http://www.w3.org/ns/dcat#distribution' and jsonb(property_value)->'https://w3id.org/mobilitydcat-ap/mobilityDataStandard'->'@id' is not null;
