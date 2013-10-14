package com.fasterxml.transistore.clustertest.leveldb_ahc;

import java.io.File;

import com.fasterxml.clustermate.client.StoreClientBootstrapper;
import com.fasterxml.storemate.store.backend.StoreBackendConfig;
import com.fasterxml.transistore.client.BasicTSClientConfig;
import com.fasterxml.transistore.clustertest.base.single.ByteRangesTestBase;

public class ByteRangesTest extends ByteRangesTestBase
{
    @Override
    protected StoreBackendConfig createBackendConfig(File dataDir) {
        return this.levelDBBackendConfig(dataDir);
    }

    @Override
    protected StoreClientBootstrapper<?, ?, ?, ?> createClientBootstrapper(BasicTSClientConfig clientConfig) {
        return bootstrapperWithAHC(clientConfig);
    }
}
