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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

/** Generates test users for different use cases. */
public class TestUsersUtil {

  private static final int PAGE_SIZE = 100;

  /**
   * Generates 105 test users that should be deleted.
   *
   * @return 105 test users that should be deleted.
   */
  public UsersResource prepare105Testusers() {
    UsersResource usersResource = mock(UsersResource.class);
    List<UserRepresentation> testUsers = generateTestUsers(105, TestScenarios.HAPPY_PATH);
    when(usersResource.list(0, PAGE_SIZE)).thenReturn(testUsers.subList(0, PAGE_SIZE));
    when(usersResource.list(100, PAGE_SIZE)).thenReturn(testUsers.subList(100, 105));
    List<UserRepresentation> noUsers = generateTestUsers(0, TestScenarios.HAPPY_PATH);
    when(usersResource.list(105, PAGE_SIZE)).thenReturn(noUsers);
    return usersResource;
  }

  public UsersResource prepare205TestUsersSimulateNewUsersAreAddedDuringRun() {
    UsersResource usersResource = mock(UsersResource.class);
    List<UserRepresentation> testUsers = generateTestUsers(205, TestScenarios.HAPPY_PATH);
    when(usersResource.list(0, PAGE_SIZE)).thenReturn(testUsers.subList(0, PAGE_SIZE));
    when(usersResource.list(100, PAGE_SIZE)).thenReturn(testUsers.subList(97, 197));
    when(usersResource.list(200, PAGE_SIZE)).thenReturn(testUsers.subList(197, 205));
    List<UserRepresentation> noUsers = generateTestUsers(0, TestScenarios.HAPPY_PATH);
    when(usersResource.list(205, PAGE_SIZE)).thenReturn(noUsers);
    return usersResource;
  }

  private List<UserRepresentation> generateTestUsers(int count, TestScenarios testScenarios) {
    List<UserRepresentation> users = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      UserRepresentation user;
      if (i == 0) {
        user = handleFirstUser(testScenarios);
      } else {
        user = generateUserForHappyPath();
      }
      users.add(user);
    }
    return users;
  }

  /**
   * Generates five test users with the first test user having no user attributes.
   *
   * @return Five test users with the first test user having no user attributes.
   */
  public UsersResource prepareForTestUserWithNoAttributes() {
    return prepareForTest(TestScenarios.USER_HAS_NO_ATTRIBUTES);
  }

  private UsersResource prepareForTest(TestScenarios testScenarios) {
    UsersResource usersResource = mock(UsersResource.class);
    List<UserRepresentation> users = generateTestUsers(5, testScenarios);
    when(usersResource.list(0, PAGE_SIZE)).thenReturn(users);
    return usersResource;
  }

  /**
   * Generates five test users with the first test user having no user attribute accountIsTemporary.
   *
   * @return Five test users with the first test user having no user attribute accountIsTemporary.
   */
  public UsersResource prepareForTestUserWithoutAttributeAccountIsTemporary() {
    return prepareForTest(TestScenarios.USER_HAS_NO_ATTRIBUTE_ACCOUNT_IS_TEMPORARY);
  }

  private UserRepresentation handleFirstUser(TestScenarios testScenarios) {
    return switch (testScenarios) {
      case HAPPY_PATH -> generateUserForHappyPath();
      case USER_HAS_NO_ATTRIBUTES -> generateUserWithoutAttributes();
      case USER_HAS_NO_ATTRIBUTE_ACCOUNT_IS_TEMPORARY ->
          generateUserWithoutAttributeAccountIsTemporary();
      case USER_ACCOUNT_IS_PERMANENT -> generateUserWithAttributeAccountIsTemporaryFalse();
      case USER_HAS_NO_ATTRIBUTE_LAST_LOGINDATE -> generateUserWithoutAttributeLastLoginDate();
      case USER_ACCOUNT_IS_PERMANENT_AND_HAS_NO_ATTRIBUTE_LAST_LOGIN_DATE ->
          generatePermanentUserWithoutAttributeLastLoginDate();
      case USER_HAS_LOGGED_IN_WITHIN_48_HOURS -> generateUserWithLastLoginDateLessThan48Hours();
    };
  }

  private UserRepresentation generateUserForHappyPath() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());
    when(user.getAttributes()).thenReturn(generateAttributesForDeletion());
    return user;
  }

  private UserRepresentation generateUserWithoutAttributes() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());
    when(user.getAttributes()).thenReturn(Map.of());
    return user;
  }

  private Map<String, List<String>> generateAttributesForDeletion() {
    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put(UserPurger.USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY, List.of("true"));
    attributes.put(UserPurger.USER_ATTRIBUTE_LAST_LOGIN_DATE, List.of("1720582235000"));
    return attributes;
  }

  private UserRepresentation generateUserWithoutAttributeAccountIsTemporary() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());

    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put(UserPurger.USER_ATTRIBUTE_LAST_LOGIN_DATE, List.of("1720582235000"));
    when(user.getAttributes()).thenReturn(attributes);
    return user;
  }

  public UsersResource prepareForTestUserWithAttributeAccountIsTemporaryFalse() {
    return prepareForTest(TestScenarios.USER_ACCOUNT_IS_PERMANENT);
  }

  private UserRepresentation generateUserWithAttributeAccountIsTemporaryFalse() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());

    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put(UserPurger.USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY, List.of("false"));
    attributes.put(UserPurger.USER_ATTRIBUTE_LAST_LOGIN_DATE, List.of("1720582235000"));
    when(user.getAttributes()).thenReturn(attributes);
    return user;
  }

  public UsersResource prepareForTestUserWithoutAttributeLastLoginDate() {
    return prepareForTest(TestScenarios.USER_HAS_NO_ATTRIBUTE_LAST_LOGINDATE);
  }

  public UsersResource prepareForTestPermanentUserWithoutAttributeLastLoginDate() {
    return prepareForTest(
        TestScenarios.USER_ACCOUNT_IS_PERMANENT_AND_HAS_NO_ATTRIBUTE_LAST_LOGIN_DATE);
  }

  private UserRepresentation generateUserWithoutAttributeLastLoginDate() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());

    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put(UserPurger.USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY, List.of("true"));
    when(user.getAttributes()).thenReturn(attributes);
    return user;
  }

  private UserRepresentation generatePermanentUserWithoutAttributeLastLoginDate() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());

    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put(UserPurger.USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY, List.of("false"));
    when(user.getAttributes()).thenReturn(attributes);
    return user;
  }

  public UsersResource prepareForTestUserWithLastLoginDateLessThan48Hours() {
    return prepareForTest(TestScenarios.USER_HAS_LOGGED_IN_WITHIN_48_HOURS);
  }

  private UserRepresentation generateUserWithLastLoginDateLessThan48Hours() {
    UserRepresentation user = mock(UserRepresentation.class);
    when(user.getId()).thenReturn("user_" + UUID.randomUUID());

    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put(UserPurger.USER_ATTRIBUTE_ACCOUNT_IS_TEMPORARY, List.of("true"));
    attributes.put(
        UserPurger.USER_ATTRIBUTE_LAST_LOGIN_DATE, List.of(System.currentTimeMillis() + ""));
    when(user.getAttributes()).thenReturn(attributes);
    return user;
  }
}
