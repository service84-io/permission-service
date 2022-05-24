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

import static io.service84.library.standardpersistence.services.SpecificationHelper.simpleTrue;

import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import io.service84.library.authutils.services.AuthenticationService;
import io.service84.permission.exceptions.EntityNotFound;
import io.service84.permission.exceptions.InsufficientPermission;
import io.service84.permission.persistence.model.Scope;
import io.service84.permission.persistence.model.SubjectScope;
import io.service84.permission.persistence.model.SubjectScope_;
import io.service84.permission.persistence.repository.SubjectScopeRepository;

@Service("B1CF9A90-272C-4A91-9CE9-9283C9C72132")
public class SubjectScopeService {
  private static final Logger logger = LoggerFactory.getLogger(SubjectScopeService.class);

  private static String GetAnySubjectScopes = "permission:get_any_subject_scopes";
  private static String GrantAnySubjectScope = "permission:grant_any_subject_scope";
  private static String RevokeAnySubjectScope = "permission:revoke_any_subject_scope";

  @Autowired private SubjectScopeRepository repository;
  @Autowired private AuthenticationService authenticationService;

  public Page<SubjectScope> getSubjectScopes(
      List<UUID> subjects, List<Scope> scope, Pageable pageable) throws InsufficientPermission {
    logger.debug("getSubjectScopes");
    List<String> subjectScopes = authenticationService.getScopes();

    if (subjectScopes.contains(GetAnySubjectScopes)) {
      return repository.findAll(subjectSelector(subjects).and(scopeSelector(scope)), pageable);
    }
    throw new InsufficientPermission();
  }

  public SubjectScope grantSubjectScope(UUID subject, Scope scope) throws InsufficientPermission {
    logger.debug("grantSubjectScope");
    List<String> subjectScopes = authenticationService.getScopes();

    if (subjectScopes.contains(GrantAnySubjectScope)) {
      SubjectScope subjectScope = new SubjectScope(subject, scope);
      subjectScope = repository.save(subjectScope);
      return subjectScope;
    }
    throw new InsufficientPermission();
  }

  public void revokeSubjectScope(UUID subject, Scope scope)
      throws EntityNotFound, InsufficientPermission {
    logger.debug("revokeSubjectScope");
    List<String> subjectScopes = authenticationService.getScopes();

    if (!subjectScopes.contains(RevokeAnySubjectScope)) {
      throw new InsufficientPermission();
    }
    SubjectScope subjectScope =
        repository.findBySubjectAndScope(subject, scope).orElseThrow(EntityNotFound.supplier());
    repository.delete(subjectScope);
  }

  @SuppressWarnings("serial")
  private Specification<SubjectScope> scopeSelector(List<Scope> scopes) {
    if (scopes == null || scopes.isEmpty()) {
      return simpleTrue();
    }
    return new Specification<>() {
      @Override
      public Predicate toPredicate(
          Root<SubjectScope> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = root.get(SubjectScope_.scope).in(scopes);
        return predicate;
      }
    };
  }

  @SuppressWarnings("serial")
  private Specification<SubjectScope> subjectSelector(List<UUID> subjects) {
    if (subjects == null || subjects.isEmpty()) {
      return simpleTrue();
    }
    return new Specification<>() {
      @Override
      public Predicate toPredicate(
          Root<SubjectScope> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = root.get(SubjectScope_.subject).in(subjects);
        return predicate;
      }
    };
  }
}
