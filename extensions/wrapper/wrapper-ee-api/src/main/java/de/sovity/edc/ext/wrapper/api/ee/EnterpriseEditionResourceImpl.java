package de.sovity.edc.ext.wrapper.api.ee;

import de.sovity.edc.ext.wrapper.api.ee.model.ConnectorLimits;
import de.sovity.edc.ext.wrapper.api.ee.model.StoredFile;

import java.util.List;
import java.util.Map;

public class EnterpriseEditionResourceImpl implements EnterpriseEditionResource {
    @Override
    public ConnectorLimits connectorLimits() {
        return new ConnectorLimits(-1, -1);
    }

    @Override
    public List<StoredFile> listStoredFiles() {
        return null;
    }

    @Override
    public StoredFile createStoredFileAsset(String storedFileId,
                                            Map<String, String> assetProperties) {
        return null;
    }
}
