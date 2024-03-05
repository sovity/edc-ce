
# Updates checklist

This is a checklist about the workarounds that we had to use and may cause trouble in the future.
These are not easily testable and require a manual check.

---

After each EDC version update

- [ ]  Check if `org.eclipse.edc.spi.types.domain.asset.Asset.toBuilder` added a new
  field and adjust the builder in `de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetBuilder.fromEditMetadataRequest` accordingly

## Context

### Asset.toBuilder

A list of the element that may break when updating the EDC version.

In `de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetBuilder.fromEditMetadataRequest`

When re-creating the asset, we can't re-use the `Asset.toBuilder()` as it doesn't allow us to remove properties.

We must therefore re-build the asset using the same content as that `.toBuilder()`.

If the Eclipse EDC adds a field in this builder, we will miss it and any write to the JsonLd via the web API
will remove that hypothetical new field.

#### Workaround

On the EDC version update, check that `org.eclipse.edc.spi.types.domain.asset.Asset.toBuilder` doesn't set more
fields than what we set. If a new field was added, add it to this function too.
