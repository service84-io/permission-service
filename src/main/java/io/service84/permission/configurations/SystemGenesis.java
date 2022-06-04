/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.service84.permission.configurations;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.service84.library.keyvaluepersistence.exceptions.KeyNotFound;
import io.service84.library.keyvaluepersistence.services.KeyValueService;
import io.service84.permission.errors.ServerError;
import io.service84.permission.persistence.model.Scope;
import io.service84.permission.persistence.model.SubjectScope;
import io.service84.permission.persistence.repository.ScopeRepository;
import io.service84.permission.persistence.repository.SubjectScopeRepository;

@Service("E823A5ED-E64C-4AFA-BB19-C7F4B1E89D2B")
public class SystemGenesis {
  private static final Logger logger = LoggerFactory.getLogger(SystemGenesis.class);

  private static String GenesisDone = "GenesisDone";

  private static UUID GenesisUserID = UUID.fromString("AB43ACF4-E19A-4142-A5EA-49D69B3672A7");
  private static UUID GenesisKeyID = UUID.fromString("CD9A3ED2-9190-4B27-86CC-9EC8F5341692");

  private static String PermissionNamespace = "permission";
  private static String CreateAnyScope = "create_any_scope";
  private static String GetAnyScope = "get_any_scope";
  private static String GrantAnySubjectScope = "grant_any_subject_scope";

  @Autowired private ScopeRepository scopeRepository;
  @Autowired private SubjectScopeRepository subjectScopeRepository;
  @Autowired private KeyValueService kvService;

  private void createScope(String namespace, String name) {
    Scope scope = new Scope(namespace, name);

    try {
      scope = scopeRepository.save(scope);
    } catch (DataIntegrityViolationException e) {
      scope =
          scopeRepository
              .findByNamespaceAndName(namespace, name)
              .orElseThrow(ServerError.supplier());
    }

    try {
      SubjectScope userSubjectScope = new SubjectScope(GenesisUserID, scope);
      subjectScopeRepository.save(userSubjectScope);
    } catch (DataIntegrityViolationException e) {
      subjectScopeRepository
          .findBySubjectAndScope(GenesisUserID, scope)
          .orElseThrow(ServerError.supplier());
    }

    try {
      SubjectScope apiKeySubjectScope = new SubjectScope(GenesisKeyID, scope);
      subjectScopeRepository.save(apiKeySubjectScope);
    } catch (DataIntegrityViolationException e) {
      subjectScopeRepository
          .findBySubjectAndScope(GenesisKeyID, scope)
          .orElseThrow(ServerError.supplier());
    }
  }

  private Boolean isGenesisDone() {
    try {
      return kvService.getValue(GenesisDone, Boolean.class);
    } catch (KeyNotFound e) {
      return Boolean.FALSE;
    }
  }

  private void markGenesisDone() {
    kvService.setValue(GenesisDone, Boolean.TRUE);
  }

  @PostConstruct
  public void systemGenesis() {
    logger.debug("systemGenesis");
    if (!isGenesisDone()) {
      createScope(PermissionNamespace, CreateAnyScope);
      createScope(PermissionNamespace, GetAnyScope);
      createScope(PermissionNamespace, GrantAnySubjectScope);

      markGenesisDone();
    }
  }
}
