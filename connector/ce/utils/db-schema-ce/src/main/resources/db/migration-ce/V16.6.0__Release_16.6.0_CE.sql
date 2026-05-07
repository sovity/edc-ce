-- remove duplicate indices
drop index if exists contract_agreement_id_uindex;
drop index if exists contract_negotiation_id_uindex;
drop index if exists edc_policydefinitions_id_uindex;
drop index if exists lease_lease_id_uindex;
drop index if exists transfer_process_id_uindex;

-- fix incorrectly stored sovity class name in resource manifest
update edc_transfer_process
set resource_manifest = replace(resource_manifest::text, 'SovityObjectStorageResourceDefinition', 'ObjectStorageResourceDefinition')::json
where resource_manifest::text like '%SovityObjectStorageResourceDefinition%';
