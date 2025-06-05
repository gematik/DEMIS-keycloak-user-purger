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
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class UserPurgerTest {

  private TestUsersUtil testUsersUtil = new TestUsersUtil();

  @Test
  void shouldDelete105Users() {
    UsersResource usersResource = testUsersUtil.prepare105Testusers();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(usersResource, times(105)).delete(argumentCaptor.capture());
    List<String> capturedUserIds = argumentCaptor.getAllValues();
    assertThat(capturedUserIds).isNotNull();
    Set<String> capturedDistinctUserIds = new HashSet<>(capturedUserIds);
    assertThat(capturedDistinctUserIds).hasSize(105);
  }

  @Test
  void shouldDelete205UsersNewUsersRegisterDuringRun() {
    UsersResource usersResource =
        testUsersUtil.prepare205TestUsersSimulateNewUsersAreAddedDuringRun();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(usersResource, times(205)).delete(argumentCaptor.capture());
    List<String> capturedUserIds = argumentCaptor.getAllValues();
    assertThat(capturedUserIds).isNotNull();
    Set<String> capturedDistinctUserIds = new HashSet<>(capturedUserIds);
    assertThat(capturedDistinctUserIds).hasSize(205);
  }

  private KeycloakClient prepareMockObjects(UsersResource usersResource) {
    KeycloakClient keycloakClient = Mockito.mock(KeycloakClient.class);
    RealmResource realmResource = prepareRealmResource(keycloakClient);
    when(realmResource.users()).thenReturn(usersResource);
    return keycloakClient;
  }

  private RealmResource prepareRealmResource(KeycloakClient keycloakClient) {
    Keycloak keycloak = mock(Keycloak.class);
    when(keycloakClient.getKeycloak()).thenReturn(keycloak);

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm(UserPurger.PORTAL_REALM_NAME)).thenReturn(realmResource);
    return realmResource;
  }

  @Test
  void shouldNotDeleteUsersWithoutAttributes() {
    UsersResource usersResource = testUsersUtil.prepareForTestUserWithNoAttributes();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    assertFourDistinctUserIds(usersResource);
  }

  private void assertFourDistinctUserIds(UsersResource usersResource) {
    assertNDistinctUserIds(usersResource, 4);
  }

  private void assertNDistinctUserIds(UsersResource usersResource, int numberOfDistinctUsers) {
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(usersResource, times(numberOfDistinctUsers)).delete(argumentCaptor.capture());
    List<String> capturedUserIds = argumentCaptor.getAllValues();
    assertThat(capturedUserIds).isNotNull();
    Set<String> capturedDistinctUserIds = new HashSet<>(capturedUserIds);
    assertThat(capturedDistinctUserIds).hasSize(numberOfDistinctUsers);
  }

  @Test
  void shouldNotDeleteUsersWithoutAttributeAccountIsTemporary() {
    UsersResource usersResource =
        testUsersUtil.prepareForTestUserWithoutAttributeAccountIsTemporary();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    assertFourDistinctUserIds(usersResource);
  }

  @Test
  void shouldNotDeleteUsersWithAttributeAccountIsTemporaryFalse() {
    UsersResource usersResource =
        testUsersUtil.prepareForTestUserWithAttributeAccountIsTemporaryFalse();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    assertFourDistinctUserIds(usersResource);
  }

  @Test
  void shouldDeleteUsersWithoutAttributeLastLoginDate() {
    UsersResource usersResource = testUsersUtil.prepareForTestUserWithoutAttributeLastLoginDate();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    assertNDistinctUserIds(usersResource, 5);
  }

  @Test
  void shouldNotDeletePermanentUserWithoutAttributeLastLoginDate() {
    UsersResource usersResource =
        testUsersUtil.prepareForTestPermanentUserWithoutAttributeLastLoginDate();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);

    UserPurger userPurger = new UserPurger(keycloakClient);
    userPurger.purgeUsers();

    assertFourDistinctUserIds(usersResource);
  }

  @Test
  void shouldNotDeleteUsersWithLastLoginDateLessThan48Hours()
      throws NoSuchFieldException, IllegalAccessException {
    UsersResource usersResource =
        testUsersUtil.prepareForTestUserWithLastLoginDateLessThan48Hours();
    KeycloakClient keycloakClient = prepareMockObjects(usersResource);
    UserPurger userPurger = new UserPurger(keycloakClient);
    setPurgeAfterHours(userPurger, 48);

    userPurger.purgeUsers();

    assertFourDistinctUserIds(usersResource);
  }

  private void setPurgeAfterHours(UserPurger userPurger, int hours)
      throws NoSuchFieldException, IllegalAccessException {
    Class<?> personClass = userPurger.getClass();
    Field nameField = personClass.getDeclaredField("purgeAfterHours");
    nameField.setAccessible(true);
    nameField.set(userPurger, hours);
  }
}
