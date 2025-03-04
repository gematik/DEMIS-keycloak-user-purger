package de.gematik.demis.idp.spi.userpurger;

/*-
 * #%L
 * keycloak-user-purger
 * %%
 * Copyright (C) 2025 gematik GmbH
 * %%
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Will purge all users which are flagged as temporary and haven't logged in for more than 48 hours.
 */
@Slf4j
@Component
public class UserPurger {

  protected static final String PORTAL_REALM_NAME = "PORTAL";
  protected static final String USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY = "accountIsTemporary";
  protected static final String USER_ATTRIBUTE_LAST_LOGIN_DATE = "lastLoginDate";
  private static final String VALUE_ACCOUNT_IS_TEMPORARY = "true";
  private static final int MILLISECONDS_IN_HOUR = 3600000;
  private static final int BUFFER_SIZE = 100;

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPurger.class);

  @Value("${purge.after.hours}")
  private int purgeAfterHours;

  private KeycloakClient keycloakClient;

  /**
   * Will purge all users which are flagged as temporary and haven't logged in for more than 48
   * hours.
   */
  public UserPurger(KeycloakClient keycloakClient) {
    this.keycloakClient = keycloakClient;
  }

  public void purgeUsers() {
    RealmResource realmResource = keycloakClient.getKeycloak().realm(PORTAL_REALM_NAME);
    UsersResource users = realmResource.users();
    int numberOfUsers = users.count() + BUFFER_SIZE;
    List<UserRepresentation> allUsers = users.list(0, numberOfUsers);
    LOGGER.info("Found {} users to process.", allUsers.size());
    for (UserRepresentation user : allUsers) {
      determineUsersToPurge(users, user);
    }
  }

  private void determineUsersToPurge(UsersResource users, UserRepresentation user) {
    if (shouldBePurged(user)) {
      LOGGER.info("Purging user {}", user.getId());
      users.delete(user.getId());
      LOGGER.info("User {} purged", user.getId());
    }
  }

  private boolean shouldBePurged(UserRepresentation user) {
    Map<String, List<String>> userAttributes = user.getAttributes();
    if (userAttributes == null || userAttributes.isEmpty()) {
      log.info("No attributes found for user {}", user.getId());
      return false;
    }

    List<String> isTemporary = userAttributes.get(USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY);
    if (isTemporary != null
        && isTemporary.size() == 1
        && isTemporary.getFirst().equalsIgnoreCase(VALUE_ACCOUNT_IS_TEMPORARY)) {
      List<String> lastLoginDates = userAttributes.get(USER_ATTRIBUTE_LAST_LOGIN_DATE);
      if (lastLoginDates != null && lastLoginDates.size() == 1) {
        long lastLoginDate = Long.parseLong(lastLoginDates.getFirst());
        return System.currentTimeMillis() - lastLoginDate
            > (long) purgeAfterHours * MILLISECONDS_IN_HOUR;
      } else {
        // purge user when attribute lastLoginDate is not annotated
        return true;
      }
    }
    return false;
  }
}
