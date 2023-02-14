# Extension: policy-always-true

Creates Policy Definition `Always True` / `always-true`.

Also adds `always-true` policy to get this done.

## Why does this extension exist?

When creating a contract definition one must specify both an access policy and a contract policy.

While testing EDC data transfers, access policies and contract policies matter less important, however, one is forced to
create one to use one.

Instead of being forced to create policy definitions, the default policy definition created by this extension with
ID `always-true` adds a usable policy definition out-of-the-box, so one can use policies where they actually matter.
