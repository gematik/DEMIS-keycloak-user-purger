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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class KeycloakClientTest {

  private static MockedStatic<KeycloakBuilder> KEYCLOAK_BUILDER;
  private static KeycloakBuilder MOCK_KEYCLOAK_BUILDER;
  private static Keycloak MOCK_KEYCLOAK;

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

  @Autowired private KeycloakClient keycloakClient;

  @BeforeAll
  public static void beforeClass() {
    MOCK_KEYCLOAK_BUILDER = Mockito.mock(KeycloakBuilder.class);
    KEYCLOAK_BUILDER = Mockito.mockStatic(KeycloakBuilder.class);
    KEYCLOAK_BUILDER.when(KeycloakBuilder::builder).thenReturn(MOCK_KEYCLOAK_BUILDER);
    when(MOCK_KEYCLOAK_BUILDER.serverUrl(any())).thenReturn(MOCK_KEYCLOAK_BUILDER);
    when(MOCK_KEYCLOAK_BUILDER.realm(any())).thenReturn(MOCK_KEYCLOAK_BUILDER);
    when(MOCK_KEYCLOAK_BUILDER.clientId(any())).thenReturn(MOCK_KEYCLOAK_BUILDER);
    when(MOCK_KEYCLOAK_BUILDER.clientSecret(any())).thenReturn(MOCK_KEYCLOAK_BUILDER);
    when(MOCK_KEYCLOAK_BUILDER.username(any())).thenReturn(MOCK_KEYCLOAK_BUILDER);
    when(MOCK_KEYCLOAK_BUILDER.password(any())).thenReturn(MOCK_KEYCLOAK_BUILDER);
    MOCK_KEYCLOAK = mock(Keycloak.class);
    when(MOCK_KEYCLOAK_BUILDER.build()).thenReturn(MOCK_KEYCLOAK);
  }

  @AfterAll
  public static void afterClass() {
    if (KEYCLOAK_BUILDER != null) {
      KEYCLOAK_BUILDER.close();
    }
  }

  @Test
  void shouldCreateAndGetKeycloakClient() {
    verify(MOCK_KEYCLOAK_BUILDER).serverUrl(keycloakUrl);
    verify(MOCK_KEYCLOAK_BUILDER).realm(keycloakRealmName);
    verify(MOCK_KEYCLOAK_BUILDER).clientId(keycloakClientId);
    verify(MOCK_KEYCLOAK_BUILDER).clientSecret(keycloakClientSecret);
    verify(MOCK_KEYCLOAK_BUILDER).username(keycloakAdminUsername);
    verify(MOCK_KEYCLOAK_BUILDER).password(keycloakAdminPassword);
    verify(MOCK_KEYCLOAK_BUILDER).build();

    assertThat(keycloakClient.getKeycloak()).isNotNull().isEqualTo(MOCK_KEYCLOAK);
  }
}
