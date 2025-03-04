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

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Establishes a connection to Keyclaok. */
@Component
public class KeycloakClient {

  @Value("${keycloak.url}")
  private String keycloakUrl;

  @Value("${keycloak.realm.name}")
  private String keycloakRealmName;

  @Value("${keycloak.portal.client.id}")
  private String keycloakClientId;

  @Value("${keycloak.portal.client.secret}")
  private String keycloakClientSecret;

  @Value("${keycloak.portal.admin.username}")
  private String keycloakAdminUsername;

  @Value("${keycloak.portal.admin.password}")
  private String keycloakAdminPassword;

  private Keycloak keycloak;

  /** Establishes a connection to Keycloak and stores the handle in instance variable keycloak. */
  @PostConstruct
  public void init() {
    this.keycloak =
        KeycloakBuilder.builder()
            .serverUrl(keycloakUrl)
            .realm(keycloakRealmName)
            .clientId(keycloakClientId)
            .clientSecret(keycloakClientSecret)
            .username(keycloakAdminUsername)
            .password(keycloakAdminPassword)
            .build();
  }

  public Keycloak getKeycloak() {
    return keycloak;
  }
}
