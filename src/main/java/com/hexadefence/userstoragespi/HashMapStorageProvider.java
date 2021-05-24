package com.hexadefence.userstoragespi;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.HashMap;
import java.util.Map;

public class HashMapStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator {

    private HashMapUserStore hashMapUserStore;
    KeycloakSession keycloakSession;
    ComponentModel componentModel;
    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    public HashMapStorageProvider(KeycloakSession session, ComponentModel model,
                                  HashMapUserStore hashMapUserStore) {
        this.hashMapUserStore = hashMapUserStore;
        this.keycloakSession = session;
        this.componentModel = model;
    }

    @Override
    public void close() {

    }


    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        UserModel adapter = loadedUsers.get(username);
        if (adapter == null) {
            User user = hashMapUserStore.getUser(username);
            if (user != null) {
                adapter = createAdapter(realm, username);
                loadedUsers.put(username, adapter);
            }
        }
        return adapter;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(PasswordCredentialModel.TYPE);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {

        try {
            String password = hashMapUserStore.getUser(user.getUsername()).getPassword();
            return credentialType.equals(PasswordCredentialModel.TYPE) && password != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType())) return false;

        try {
            String password = hashMapUserStore.getUser(user.getUsername()).getPassword();
            if (password == null) return false;
            return password.equals(credentialInput.getChallengeResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private UserModel createAdapter(RealmModel realm, String username) {
        return new AbstractUserAdapter(keycloakSession, realm, componentModel) {
            @Override
            public String getUsername() {
                return username;
            }
        };
    }
}
