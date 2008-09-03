/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.util.keystore;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.cert.Certificate;

import javax.security.auth.callback.CallbackHandler;

import org.apache.log4j.Logger;

/** A keystore container for PCKS#11 keystores i.e. the java PKCS#11 wrapper
 * 
 * @version $Id$
 */
public class KeyStoreContainerP11 extends KeyStoreContainerBase {
    private static final Logger log = Logger.getLogger(KeyStoreContainerP11.class);

    /** The name of Suns textcallbackhandler (for pkcs11) implementation */
    private static final String SUNTEXTCBHANDLERCLASS = "com.sun.security.auth.callback.TextCallbackHandler";

    private KeyStoreContainerP11( KeyStore _keyStore,
                                  String _providerName ) throws Exception, IOException{
        super( _keyStore, _providerName, _providerName );
        load();
    }
    protected void load() throws Exception, IOException {
        this.keyStore.load(null, null);
    }

    /** Use KeyStoreContainer.getInstance to get an instance of this class
     * @see KeyStoreContainer#getInstance(String, String, String, String)
     */
    static KeyStoreContainer getInstance(final String slot,
                                         final String libName,
                                         final boolean isIx,
                                         final String attributesFile,
                                         final KeyStore.ProtectionParameter protectionParameter) throws Exception, IOException {
        Provider provider = KeyTools.getP11Provider(slot, libName, isIx, attributesFile);
        final String providerName = provider.getName();
        log.debug("Adding provider with name: "+providerName);
        Security.addProvider(provider);

        return getInstance(providerName, protectionParameter);
    }
    /** Use KeyStoreContainer.getInstance to get an instance of this class
     * @see KeyStoreContainer#getInstance(String, String, String, String)
     */
    static KeyStoreContainer getInstance(final String providerName,
                                         final KeyStore.ProtectionParameter protectionParameter) throws Exception, IOException {
        // Make a default password callback handler, if we don't specify one on the command line
        KeyStore.ProtectionParameter pp = protectionParameter;
        if (pp == null) {
            CallbackHandler cbh = null;
            try {
                // We will construct the PKCS11 text callback handler (sun.security...) using reflection, because 
                // the sun class does not exist on other JDKs than sun, and we want to be able to compile everything on i.e. IBM JDK.
                //   return new SunPKCS11(new ByteArrayInputStream(baos.toByteArray()));
                final Class<?> implClass = Class.forName(SUNTEXTCBHANDLERCLASS);
                cbh = (CallbackHandler)implClass.newInstance();
            } catch (Exception e) {
                IOException ioe = new IOException("Error constructing pkcs11 text callback handler: "+e.getMessage());
                ioe.initCause(e);
                throw ioe;
            } 
            // The above code replaces the single line:
            //final CallbackHandler cbh = new TextCallbackHandler();        	
            pp = new CallbackHandlerProtection(cbh);        	
        }
        Provider provider = Security.getProvider(providerName);
        KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", provider, pp);
        final KeyStore keyStore = builder.getKeyStore();
        return new KeyStoreContainerP11( keyStore, providerName );
    }

    @Override
    public byte[] storeKeyStore() throws Exception, IOException {
        char[] authCode = getPassPhraseLoadSave();
        this.keyStore.store(null, authCode);
        return new byte[0];
    }
    @Override
    void setKeyEntry(String alias, Key key, Certificate chain[]) throws IOException, Exception {
        this.keyStore.setKeyEntry(alias, key, null, chain);
    }
    @Override
    public Key getKey(String alias) throws Exception, IOException {
        return this.keyStore.getKey(alias, null);
    }
    @Override
    public char[] getPassPhraseGetSetEntry() {
        return null;
    }

}
