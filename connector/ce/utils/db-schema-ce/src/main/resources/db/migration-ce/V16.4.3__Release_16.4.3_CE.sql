-- Create indices to reduce database load
create index if not exists data_plane_lease_id on edc_data_plane (lease_id);
create index if not exists data_plane_instance_lease_id on edc_data_plane_instance (lease_id);
create index if not exists contract_negotiation_agreement_id on edc_contract_negotiation (agreement_id);

-- Delete all leases that are not referenced by any other table
delete from edc_lease l
where not exists (select 1 from edc_data_plane dp where dp.lease_id = l.lease_id)
  and not exists (select 1 from edc_contract_negotiation cn where cn.lease_id = l.lease_id)
  and not exists (select 1 from edc_data_plane_instance dpi where dpi.lease_id = l.lease_id)
  and not exists (select 1 from edc_policy_monitor pm where pm.lease_id = l.lease_id)
  and not exists (select 1 from edc_transfer_process tp where tp.lease_id = l.lease_id);
