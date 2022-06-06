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

import java.util.UUID;

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
import io.service84.permission.services.ScopeService;
import io.service84.permission.services.Translator;
import io.service84.services.permission.api.ScopeApiDelegate;
import io.service84.services.permission.dto.ScopeDTO;
import io.service84.services.permission.dto.ScopeDataDTO;
import io.service84.services.permission.dto.ScopePageDTO;

@Service("03FBBD4D-F742-481E-96A4-69AB117611D8")
public class ScopeDelegate implements ScopeApiDelegate {
  private static Logger logger = LoggerFactory.getLogger(ScopeDelegate.class);

  @Autowired private ScopeService scopeService;
  @Autowired private AuthenticationService authenticationService;
  @Autowired private RequestService requestService;
  @Autowired private Translator translator;

  @Override
  public ResponseEntity<ScopeDTO> createScope(ScopeDataDTO body, String authentication) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Scope scope = scopeService.createScope(body.getNamespace(), body.getName());
      ResponseEntity<ScopeDTO> result = translator.translate(scope, HttpStatus.CREATED);
      logger.info("Created");
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
  public ResponseEntity<Void> deleteScope(UUID id, String authentication) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Scope scope = scopeService.getScope(id);
      scopeService.deleteScope(scope);
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

  @Override
  public ResponseEntity<ScopeDTO> getScope(UUID id, String authentication) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Scope scope = scopeService.getScope(id);
      ResponseEntity<ScopeDTO> result = translator.translate(scope, HttpStatus.OK);
      logger.info("OK");
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
  public ResponseEntity<ScopeDTO> getScopeByQualifiedName(String name, String authentication) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Scope scope = scopeService.getScope(name);
      ResponseEntity<ScopeDTO> result = translator.translate(scope, HttpStatus.OK);
      logger.info("OK");
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
  public ResponseEntity<ScopePageDTO> getScopes(
      String authentication, String pageIndex, Integer pageSize) {
    try {
      logger.info(
          "{} {} {}",
          authenticationService.getSubject(),
          requestService.getMethod(),
          requestService.getURL());
      Pageable pageable = translator.getPageable(pageIndex, pageSize);
      Page<Scope> scopePage = scopeService.getScopes(pageable);
      ResponseEntity<ScopePageDTO> result = translator.translateScopePage(scopePage, HttpStatus.OK);
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
}
