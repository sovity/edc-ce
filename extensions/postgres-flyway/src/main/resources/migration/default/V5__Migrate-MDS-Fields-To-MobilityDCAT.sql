-- Migrates MDS keys to MobilityDCAT vocabulary
create
or replace function pg_temp.migrate_asset_property_name(asset_property_key text) returns text as
$$
begin
return case asset_property_key
    -- This list only contains properties that are directly mappable
    -- Properties that require a new nested JSON structure are not included
           when 'http://w3id.org/mds#dataCategory' then 'https://w3id.org/mobilitydcat-ap/mobility-theme/data-content-category'
           when 'http://w3id.org/mds#dataSubcategory' then 'https://w3id.org/mobilitydcat-ap/mobility-theme/data-content-sub-category'
           when 'http://w3id.org/mds#dataModel' then 'https://w3id.org/mobilitydcat-ap/mobility-data-standard'
           when 'http://w3id.org/mds#geoReferenceMethod' then 'https://w3id.org/mobilitydcat-ap/georeferencing-method'
           when 'http://w3id.org/mds#transportMode' then 'https://w3id.org/mobilitydcat-ap/transport-mode'
           else asset_property_key
    end;
end;
$$
language plpgsql;

update edc_asset_property
set property_name  = pg_temp.migrate_asset_property_name(property_name);
