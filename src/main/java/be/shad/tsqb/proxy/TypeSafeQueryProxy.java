package be.shad.tsqb.proxy;

import be.shad.tsqb.data.TypeSafeQueryProxyData;

/**
 * The interface which is additionally implemented by the proxied entities.
 */
public interface TypeSafeQueryProxy {

    /**
     * Retrieve all relevant data of this proxy.
     * <p>
     * It is expected this method name will not clash with any
     * existing domain object wherever this library may be used.
     * <p>
     * This could in theory be a restriction, but won't be in practice.
     */
    TypeSafeQueryProxyData getTypeSafeProxyData();
    
}
