package com.fasterxml.transistore.basic;

import com.fasterxml.clustermate.api.DecodableRequestPath;
import com.fasterxml.clustermate.api.RequestPathBuilder;
import com.fasterxml.clustermate.api.RequestPathStrategy;

/**
 * {@link RequestPathStrategy} implementation for
 * Basic TransiStore configuration.
 *<p>
 * Paths are mapped as follows:
 *<ul>
 * <li>Store entries under ".../store/":
 *  <ul>
 *    <li>".../store/entry" for single-entry access</li>
 *    <li>".../store/entries" for range (ordered multi-entry) access</li>
 *    <li>".../store/status" for store status (admin interface)</li>
 *    <li>".../store/findEntry" for redirecting single-entry</li>
 *    <li>".../store/findList" for redirecting range access</li>
 *  </ul>
 * <li>Node status entries under ".../node/":
 *  <ul>
 *    <li>".../node/status" for basic status</li>
 *  </ul>
 *  <ul>
 *    <li>".../node/metrics" for various metrics</li>
 *  </ul>
 *</ul>
 * <li>Sync entries under ".../sync/":
 *  <ul>
 *    <li>".../sync/list" for accessing metadata for changes</li>
 *    <li>".../sync/pull" for pulling entries to sync</li>
 *  </ul>
 *</ul>
 */
public class BasicTSPaths extends RequestPathStrategy<BasicTSPath>
{
    protected final static String FIRST_SEGMENT_STORE = "store";
    protected final static String FIRST_SEGMENT_NODE = "node";
    protected final static String FIRST_SEGMENT_SYNC = "sync";

    protected final static String SECOND_SEGMENT_STORE_ENTRY = "entry";
    protected final static String SECOND_SEGMENT_STORE_ENTRY_INFO = "entryInfo";
    protected final static String SECOND_SEGMENT_STORE_ENTRIES = "entries";
    protected final static String SECOND_SEGMENT_STORE_STATUS = "status";
    protected final static String SECOND_SEGMENT_STORE_FIND_ENTRY = "findEntry";
    protected final static String SECOND_SEGMENT_STORE_FIND_ENTRIES = "findEntries";

    protected final static String SECOND_SEGMENT_NODE_STATUS = "status";
    protected final static String SECOND_SEGMENT_NODE_METRICS = "metrics";

    protected final static String SECOND_SEGMENT_SYNC_LIST = "list";
    protected final static String SECOND_SEGMENT_SYNC_PULL = "pull";

    /*
    /**********************************************************************
    /* Path building, external store requests
    /**********************************************************************
     */

    @Override
    public <B extends RequestPathBuilder<B>> B appendPath(B basePath, BasicTSPath type)
    {
        switch (type) {
        case NODE_METRICS:
            return appendNodeMetricsPath(basePath);
        case NODE_STATUS:
            return appendNodeStatusPath(basePath);

        case STORE_ENTRY:
            return appendStoreEntryPath(basePath);
        case STORE_ENTRY_INFO:
            return appendStoreEntryInfoPath(basePath);
        case STORE_ENTRIES:
            return appendStoreListPath(basePath);

        case STORE_FIND_ENTRY:
            return _storePath(basePath).addPathSegment(SECOND_SEGMENT_STORE_FIND_ENTRY);
        case STORE_FIND_LIST:
            return _storePath(basePath).addPathSegment(SECOND_SEGMENT_STORE_FIND_ENTRIES);
        case STORE_STATUS:
            return  _storePath(basePath).addPathSegment(SECOND_SEGMENT_STORE_STATUS);

        case SYNC_LIST:
            return appendSyncListPath(basePath);
        case SYNC_PULL:
            return appendSyncPullPath(basePath);
        }
        throw new IllegalStateException();
    }

    /*
    /**********************************************************************
    /* Methods for building basic content access paths
    /**********************************************************************å
     */

    @Override
    public <B extends RequestPathBuilder<B>> B appendStoreEntryPath(B basePath) {
        return _storePath(basePath).addPathSegment(SECOND_SEGMENT_STORE_ENTRY);
    }

    @Override
    public <B extends RequestPathBuilder<B>> B appendStoreEntryInfoPath(B basePath) {
        return _storePath(basePath).addPathSegment(SECOND_SEGMENT_STORE_ENTRY_INFO);
    }

    @Override
    public <B extends RequestPathBuilder<B>> B appendStoreListPath(B basePath) {
        return _storePath(basePath).addPathSegment(SECOND_SEGMENT_STORE_ENTRIES);
    }

    /*
    /**********************************************************************
    /* Path building, server-side sync requests
    /**********************************************************************
     */

    @Override
    public <B extends RequestPathBuilder<B>> B appendSyncListPath(B basePath) {
        return _syncPath(basePath).addPathSegment(SECOND_SEGMENT_SYNC_LIST);
    }

    @Override
    public <B extends RequestPathBuilder<B>> B appendSyncPullPath(B basePath) {
        return _syncPath(basePath).addPathSegment(SECOND_SEGMENT_SYNC_PULL);
    }

    @Override
    public <B extends RequestPathBuilder<B>> B appendNodeStatusPath(B basePath) {
        return _nodePath(basePath).addPathSegment(SECOND_SEGMENT_NODE_STATUS);
    }

    @Override
    public <B extends RequestPathBuilder<B>> B appendNodeMetricsPath(B basePath) {
        return _nodePath(basePath).addPathSegment(SECOND_SEGMENT_NODE_METRICS);
    }

    /*
    /**********************************************************************
    /* Path matching (decoding)
    /**********************************************************************
     */

    @Override
    public BasicTSPath matchPath(DecodableRequestPath pathDecoder)
    {
        String full = pathDecoder.getPath();
        if (pathDecoder.matchPathSegment(FIRST_SEGMENT_STORE)) {
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_STORE_ENTRY)) {
                return BasicTSPath.STORE_ENTRY;
            }
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_STORE_ENTRIES)) {
                return BasicTSPath.STORE_ENTRIES;
            }
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_STORE_ENTRY_INFO)) {
                return BasicTSPath.STORE_ENTRY_INFO;
            }
        } else if (pathDecoder.matchPathSegment(FIRST_SEGMENT_NODE)) {
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_NODE_STATUS)) {
                return BasicTSPath.NODE_STATUS;
            }
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_NODE_METRICS)) {
                return BasicTSPath.NODE_METRICS;
            }
        } else if (pathDecoder.matchPathSegment(FIRST_SEGMENT_SYNC)) {
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_SYNC_LIST)) {
                return BasicTSPath.SYNC_LIST;
            }
            if (pathDecoder.matchPathSegment(SECOND_SEGMENT_SYNC_PULL)) {
                return BasicTSPath.SYNC_PULL;
            }
        }
        // if no match, need to reset
        pathDecoder.setPath(full);
        return null;
    }
    
    /*
    /**********************************************************************
    /* Internal methods
    /**********************************************************************
     */
    
    protected <B extends RequestPathBuilder<B>> B _storePath(B nodeRoot) {
        return nodeRoot.addPathSegment(FIRST_SEGMENT_STORE);
    }

    protected <B extends RequestPathBuilder<B>> B _nodePath(B nodeRoot) {
        return nodeRoot.addPathSegment(FIRST_SEGMENT_NODE);
    }

    protected <B extends RequestPathBuilder<B>> B _syncPath(B nodeRoot) {
        return nodeRoot.addPathSegment(FIRST_SEGMENT_SYNC);
    }
}
