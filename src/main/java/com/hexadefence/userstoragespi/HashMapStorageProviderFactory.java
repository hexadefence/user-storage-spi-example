package com.hexadefence.userstoragespi;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class HashMapStorageProviderFactory implements UserStorageProviderFactory<HashMapStorageProvider> {

    public static final String PROVIDER_NAME = "hashmap-user-store";
    HashMapUserStore userStore = new HashMapUserStore();

    @Override
    public HashMapStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new HashMapStorageProvider(session, model, userStore);
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
}
