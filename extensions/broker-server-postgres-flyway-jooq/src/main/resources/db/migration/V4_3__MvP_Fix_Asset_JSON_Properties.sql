-- Maps JSON Values to String
-- '{"a": "b", "c": [1, 2], "d": true}'::jsonb becomes '{"a": "b", "c": "[1, 2]", "d": "true"}'::jsonb
create or replace function pg_temp.migrate_asset_properties(asset_properties jsonb) returns jsonb as
$$
begin
return (select jsonb_object_agg(key, case when jsonb_typeof(value) = 'string' then value #>> '{}' else value::text end)
        from jsonb_each(asset_properties));
end;
$$
language plpgsql;

-- Fix existing data offer asssets
update data_offer
set asset_properties = pg_temp.migrate_asset_properties(asset_properties);
