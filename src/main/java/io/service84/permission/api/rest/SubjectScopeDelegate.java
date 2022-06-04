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

package io.service84.permission.api.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.service84.library.authutils.services.AuthenticationService;
import io.service84.library.exceptionalresult.models.ExceptionalException;
import io.service84.library.standardservice.services.RequestService;
import io.service84.permission.api.rest.exceptionalresults.InsufficientPermissionResult;
import io.service84.permission.api.rest.exceptionalresults.InternalServerError;
import io.service84.permission.api.rest.exceptionalresults.NotFound;
import io.service84.permission.exceptions.EntityNotFound;
import io.service84.permission.exceptions.InsufficientPermission;
import io.service84.permission.persistence.model.Scope;
import io.service84.permission.persistence.model.SubjectScope;
import io.service84.permission.services.ScopeService;
import io.service84.permission.services.SubjectScopeService;
import io.service84.permission.services.Translator;
import io.service84.services.permission.api.SubjectScopeApiDelegate;
import io.service84.services.permission.dto.SubjectScopeDTO;
import io.service84.services.permission.dto.SubjectScopeDataDTO;
import io.service84.services.permission.dto.SubjectScopePageDTO;

@Service("3FD8C631-77AF-4887-8487-940592B1697B")
public class SubjectScopeDelegate implements SubjectScopeApiDelegate {
  private static Logger logger = LoggerFactory.getLogger(SubjectScopeDelegate.class);

  @Autowired private ScopeService scopeService;
  @Autowired private SubjectScopeService subjectScopeService;
  @Autowired private AuthenticationService authenticationService;
  @Autowired private RequestService requestService;
  @Autowired private Translator translator;

  @Override
  public ResponseEntity<SubjectScopePageDTO> getSubjectScopes(
      String authentication,
      List<UUID> subjects,
      List<String> qualifiedScopes,
      String pageIndex,
      Integer pageSize) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Pageable pageable = translator.getPageable(pageIndex, pageSize);
      qualifiedScopes = ObjectUtils.defaultIfNull(qualifiedScopes, Collections.emptyList());
      List<Scope> scopes = new ArrayList<>();

      for (String qualifiedScope : qualifiedScopes) {
        scopes.add(scopeService.getScope(qualifiedScope));
      }

      Page<SubjectScope> subjectScopePage =
          subjectScopeService.getSubjectScopes(subjects, scopes, pageable);
      ResponseEntity<SubjectScopePageDTO> result =
          translator.translateSubjectScopePage(subjectScopePage, HttpStatus.OK);
      logger.info("OK");
      return result;
    } catch (InsufficientPermission e) {
      logger.info("Insufficient Permission");
      throw new InsufficientPermissionResult();
    } catch (ExceptionalException e) {
      throw e;
    } catch (Throwable t) {
      logger.error(t.getMessage(), t);
      throw new InternalServerError();
    }
  }

  @Override
  public ResponseEntity<SubjectScopeDTO> grantSubjectScope(
      SubjectScopeDataDTO body, String authentication) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Scope scope = scopeService.getScope(body.getScopeId());
      SubjectScope subjectScope = subjectScopeService.grantSubjectScope(body.getSubject(), scope);
      ResponseEntity<SubjectScopeDTO> result =
          translator.translate(subjectScope, HttpStatus.CREATED);
      logger.info("Created");
      return result;
    } catch (EntityNotFound e) {
      logger.info("Entity Not Found");
      throw new NotFound();
    } catch (InsufficientPermission e) {
      logger.info("Insufficient Permission");
      throw new InsufficientPermissionResult();
    } catch (ExceptionalException e) {
      throw e;
    } catch (Throwable t) {
      logger.error(t.getMessage(), t);
      throw new InternalServerError();
    }
  }

  @Override
  public ResponseEntity<Void> revokeSubjectScope(SubjectScopeDataDTO body, String authentication) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Scope scope = scopeService.getScope(body.getScopeId());
      subjectScopeService.revokeSubjectScope(body.getSubject(), scope);
      ResponseEntity<Void> result = translator.translate(HttpStatus.NO_CONTENT);
      logger.info("No Content");
      return result;
    } catch (EntityNotFound e) {
      logger.info("Entity Not Found");
      throw new NotFound();
    } catch (InsufficientPermission e) {
      logger.info("Insufficient Permission");
      throw new InsufficientPermissionResult();
    } catch (ExceptionalException e) {
      throw e;
    } catch (Throwable t) {
      logger.error(t.getMessage(), t);
      throw new InternalServerError();
    }
  }
}
