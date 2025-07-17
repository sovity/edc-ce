-- add indices to improve performance
create index policy_monitor_lease_id on edc_policy_monitor (lease_id);
create index transfer_process_lease_id on edc_transfer_process (lease_id);
create index contract_negotiation_lease_id on edc_contract_negotiation (lease_id);
create unique index if not exists contract_agreement_id_uindex on public.edc_contract_agreement using btree (agr_id);
create unique index if not exists contract_negotiation_id_uindex on public.edc_contract_negotiation using btree (id);
create unique index if not exists edc_policydefinitions_id_uindex on public.edc_policydefinitions using btree (policy_id);
create unique index if not exists lease_lease_id_uindex on public.edc_lease using btree (lease_id);
create unique index if not exists transfer_process_id_uindex on public.edc_transfer_process using btree (transferprocess_id);

-- fix empty profiles from V11 -> V13 migration
update edc_policydefinitions set profiles = '[]'::json where profiles is null;

-- for user managed vault secrets
create table sovity_vault_secret
(
  key         varchar primary key      not null,
  description varchar                  not null,
  updated_at  timestamp with time zone not null
);
