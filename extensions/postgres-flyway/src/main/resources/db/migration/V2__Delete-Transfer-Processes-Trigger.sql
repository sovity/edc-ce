-- Required for reasonably fast ON DELETE CASCADE from edc_transfer_process
create index data_request_transfer_process_id_idx
    on edc_data_request (transfer_process_id);
-- Speed up sort + limit query
-- Include transferprocess_id to enable index-only scan
create index transfer_process_created_at_idx
    on edc_transfer_process (created_at) include (transferprocess_id);

-- Delete oldest row when table size exceeds 3000 rows
-- The row count should mostly stabilize slightly above 3000, as the reltuples data in pg_class is only updated by VACUUM
-- Unfortunately, I was not able to get conclusive results on the behavior under concurrent inserts
-- One problem is that the table might still grow over time, if concurrent inserts can delete the same row
-- To avoid this, we could delete two rows instead of just one
-- Then the table would shrink until the next auto-vacuum detects that it is below 3000 rows again
create function transfer_process_delete_old_rows() returns trigger as $$
begin
    delete from edc_transfer_process o
        using (
            select i2.transferprocess_id
            from edc_transfer_process i2
            order by i2.created_at
            limit 2
        ) i,
            (
                -- Hack to avoid count(*), which takes several hundred milliseconds
                -- Not perfectly accurate, but close enough
                -- Idea taken from: https://www.cybertec-postgresql.com/en/postgresql-count-made-fast/
                select pgc.reltuples::bigint as count
                from pg_catalog.pg_class pgc
                where pgc.relname = 'edc_transfer_process'
            ) c
    where i.transferprocess_id = o.transferprocess_id and c.count > 3000;

    return null;
end;
$$ language plpgsql;

create trigger delete_old_rows after insert
    on edc_transfer_process
    for each row
execute function transfer_process_delete_old_rows();