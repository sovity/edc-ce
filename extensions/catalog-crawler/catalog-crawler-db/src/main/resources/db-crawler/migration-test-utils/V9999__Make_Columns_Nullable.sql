do
$$
    declare
        r record;
    begin
        for r in (select 'alter table "' || table_schema || '"."' || table_name || '" alter column "' || column_name ||
                         '" drop not null;' as command
                  from information_schema.columns
                  where table_schema not in ('pg_catalog', 'information_schema') -- exclude system schemas
                    and table_name in ('connector', 'organization', 'user') -- only selected AP tables
                    and is_nullable = 'no')
            loop
                execute r.command;
            end loop;
    end
$$;
