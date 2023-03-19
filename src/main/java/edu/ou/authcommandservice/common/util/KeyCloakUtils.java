package edu.ou.authcommandservice.common.util;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class KeyCloakUtils {
    /**
     * Change password
     *
     * @param keycloak keycloak instance
     * @param realm realm
     * @param keyCloakId keycloak id
     * @param newPassword new password
     */
    public static void changePassword(
            Keycloak keycloak,
            String realm,
            String keyCloakId,
            String newPassword
    ){
        final UserResource userResource = keycloak.realm(realm).users().get(keyCloakId);
        final UserRepresentation userRepresentation = userResource.toRepresentation();
        final CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);
        userResource.resetPassword(credential);
        userResource.update(userRepresentation);
    }
}
