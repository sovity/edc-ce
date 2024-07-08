-- Fix invalid DB state
delete
from "user"
where organization_mds_id is null;
