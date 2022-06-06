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

package io.service84.permission.services;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.service84.library.authutils.services.AuthenticationService;
import io.service84.permission.errors.ServerError;
import io.service84.permission.exceptions.EntityNotFound;
import io.service84.permission.exceptions.InsufficientPermission;
import io.service84.permission.persistence.model.Scope;
import io.service84.permission.persistence.repository.ScopeRepository;

@Service("47296D66-7D2D-4C90-9B30-C4FF9898828D")
public class ScopeService {
  private static final Logger logger = LoggerFactory.getLogger(ScopeService.class);

  private static String CreateAnyScope = "permission:create_any_scope";
  private static String DeleteAnyScope = "permission:delete_any_scope";
  private static String GetAnyScope = "permission:get_any_scope";
  private static String GetAnyScopes = "permission:get_any_scopes";

  @Autowired private ScopeRepository repository;
  @Autowired private AuthenticationService authenticationService;

  public Scope createScope(String namespace, String name) throws InsufficientPermission {
    logger.debug("createScope");
    List<String> subjectScopes = authenticationService.getScopes();

    if (subjectScopes.contains(CreateAnyScope)) {
      Scope scope = new Scope(namespace, name);

      try {
        scope = repository.save(scope);
      } catch (DataIntegrityViolationException e) {
        scope =
            repository.findByNamespaceAndName(namespace, name).orElseThrow(ServerError.supplier());
      }

      return scope;
    }

    throw new InsufficientPermission();
  }

  public void deleteScope(Scope scope) throws InsufficientPermission {
    logger.debug("deleteScope");
    List<String> subjectScopes = authenticationService.getScopes();

    if (!subjectScopes.contains(DeleteAnyScope)) {
      throw new InsufficientPermission();
    }

    repository.delete(scope);
  }

  public Scope getScope(String qualifiedScope) throws EntityNotFound, InsufficientPermission {
    logger.debug("getScope");
    List<String> subjectScopes = authenticationService.getScopes();

    if (subjectScopes.contains(GetAnyScope)) {
      return repository.findByQualifiedScope(qualifiedScope).orElseThrow(EntityNotFound.supplier());
    }

    throw new InsufficientPermission();
  }

  public Scope getScope(UUID id) throws EntityNotFound, InsufficientPermission {
    logger.debug("getScope");
    List<String> subjectScopes = authenticationService.getScopes();

    if (subjectScopes.contains(GetAnyScope)) {
      return repository.findById(id).orElseThrow(EntityNotFound.supplier());
    }

    throw new InsufficientPermission();
  }

  public Page<Scope> getScopes(Pageable pageable) throws InsufficientPermission {
    logger.debug("getScopes");
    List<String> subjectScopes = authenticationService.getScopes();

    if (subjectScopes.contains(GetAnyScopes)) {
      return repository.findAll(pageable);
    }

    throw new InsufficientPermission();
  }
}
