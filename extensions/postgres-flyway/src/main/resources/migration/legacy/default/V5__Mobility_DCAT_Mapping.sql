--
--  Copyright (c) 2023 sovity GmbH
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


-- "http://www.w3.org/ns/dcat#distribution": {
--   "http://www.w3.org/ns/dcat#mediaType": "previously http://www.w3.org/ns/dcat#mediaType",
--   "https://w3id.org/mobilitydcat-ap/mobilityDataStandard": {
--     "@id": "previously http://w3id.org/mds#dataModel"
--   }
-- },
with data as (
    select asset_id,
           (select property_value from edc_asset_property p where p.property_name = 'http://www.w3.org/ns/dcat#mediaType' and p.asset_id_fk = a.asset_id) as media_type,
           (select property_value from edc_asset_property p where p.property_name = 'http://w3id.org/mds#dataModel' and p.asset_id_fk = a.asset_id) as data_model
    from edc_asset a
)
insert into edc_asset_property (asset_id_fk, property_name, property_type, property_value) (
    select asset_id,
           'http://www.w3.org/ns/dcat#distribution',
           'java.util.LinkedHashMap',
           jsonb_build_object(
                   'http://www.w3.org/ns/dcat#mediaType', media_type,
                   'https://w3id.org/mobilitydcat-ap/mobilityDataStandard', jsonb_build_object('@id', data_model)
           )::text
    from data
    where media_type is not null or data_model is not null
);
delete from edc_asset_property where property_name = 'http://www.w3.org/ns/dcat#mediaType';
delete from edc_asset_property where property_name = 'http://w3id.org/mds#dataModel';


-- "https://w3id.org/mobilitydcat-ap/mobilityTheme": {
--   "https://w3id.org/mobilitydcat-ap/mobility-theme/data-content-category": "previously http://w3id.org/mds#dataCategory",
--   "https://w3id.org/mobilitydcat-ap/mobility-theme/data-content-sub-category": "previously http://w3id.org/mds#dataSubcategory"
-- },
with data as (
    select asset_id,
           (select property_value from edc_asset_property p where p.property_name = 'http://w3id.org/mds#dataCategory' and p.asset_id_fk = a.asset_id) as data_category,
           (select property_value from edc_asset_property p where p.property_name = 'http://w3id.org/mds#dataSubcategory' and p.asset_id_fk = a.asset_id) as data_subcategory
    from edc_asset a
)
insert into edc_asset_property (asset_id_fk, property_name, property_type, property_value) (
    select asset_id,
           'https://w3id.org/mobilitydcat-ap/mobilityTheme',
           'java.util.LinkedHashMap',
           jsonb_build_object(
                'https://w3id.org/mobilitydcat-ap/mobility-theme/data-content-category', data_category,
                'https://w3id.org/mobilitydcat-ap/mobility-theme/data-content-sub-category', data_subcategory
           )::text
    from data
    where data_category is not null or data_subcategory is not null
);
delete from edc_asset_property where property_name = 'http://w3id.org/mds#dataCategory';
delete from edc_asset_property where property_name = 'http://w3id.org/mds#dataSubcategory';


-- "https://w3id.org/mobilitydcat-ap/georeferencingMethod": "previously http://w3id.org/mds#geoReferenceMethod",
update edc_asset_property
set property_name = 'https://w3id.org/mobilitydcat-ap/georeferencingMethod'
where property_name = 'http://w3id.org/mds#geoReferenceMethod';

-- "https://w3id.org/mobilitydcat-ap/transportMode": "previously http://w3id.org/mds#transportMode"
update edc_asset_property
set property_name = 'https://w3id.org/mobilitydcat-ap/transportMode'
where property_name = 'http://w3id.org/mds#transportMode';
