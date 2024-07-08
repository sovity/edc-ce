do
$$
    declare
        r record;
    begin
        for r in (select 'alter table "' || c.table_schema || '"."' || c.table_name || '" alter column "' || c.column_name ||
                         '" drop not null;' as command
                  from information_schema.columns c
                  where c.table_schema not in ('pg_catalog', 'information_schema') -- exclude system schemas
                    and c.table_name in ('connector', 'organization', 'user') -- only selected AP tables
                    and c.is_nullable = 'NO'
                    and not exists (SELECT tc.constraint_type
                         FROM information_schema.table_constraints AS tc
                                  JOIN information_schema.key_column_usage AS kcu
                                       ON tc.constraint_name = kcu.constraint_name
                                           AND tc.table_schema = kcu.table_schema
                         WHERE tc.table_schema = c.table_schema
                           and tc.table_name = c.table_name
                           AND kcu.column_name = c.column_name
                           AND tc.constraint_type = 'PRIMARY KEY')) -- exclude primary keys
            loop
                execute r.command;
            end loop;
    end
$$;
