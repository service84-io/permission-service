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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.service84.library.standardpersistence.services.PaginationTranslator;
import io.service84.permission.persistence.model.Scope;
import io.service84.permission.persistence.model.SubjectScope;
import io.service84.services.permission.dto.MetadataDTO;
import io.service84.services.permission.dto.ScopeDTO;
import io.service84.services.permission.dto.ScopePageDTO;
import io.service84.services.permission.dto.SubjectScopeDTO;
import io.service84.services.permission.dto.SubjectScopePageDTO;

@Service("874783D7-CEB4-4FA1-858B-C6D7378CBBE7")
public class Translator extends PaginationTranslator {
  private static final Logger logger = LoggerFactory.getLogger(Translator.class);

  public static class MetadataPDSDTO extends MetadataDTO implements CursorPaginationDataStandard {}

  private UUID id(Scope scope) {
    return scope == null ? null : scope.getId();
  }

  public ResponseEntity<Void> translate(HttpStatus status) {
    logger.debug("translate");
    return new ResponseEntity<>(status);
  }

  public ScopeDTO translate(Scope scope) {
    logger.debug("translate");
    if (scope == null) {
      return null;
    }

    ScopeDTO dto = new ScopeDTO();
    dto.setId(scope.getId());
    dto.setNamespace(scope.getNamespace());
    dto.setName(scope.getName());
    return dto;
  }

  public ResponseEntity<ScopeDTO> translate(Scope scope, HttpStatus status) {
    logger.debug("translate");
    return new ResponseEntity<>(translate(scope), status);
  }

  public SubjectScopeDTO translate(SubjectScope subjectScope) {
    logger.debug("translate");
    if (subjectScope == null) {
      return null;
    }

    SubjectScopeDTO dto = new SubjectScopeDTO();
    dto.setId(subjectScope.getId());
    dto.setSubject(subjectScope.getSubject());
    dto.setScopeId(id(subjectScope.getScope()));
    return dto;
  }

  public ResponseEntity<SubjectScopeDTO> translate(SubjectScope subjectScope, HttpStatus status) {
    logger.debug("translate");
    return new ResponseEntity<>(translate(subjectScope), status);
  }

  public List<ScopeDTO> translateScopeList(List<Scope> list) {
    logger.debug("translateScopeList");
    return list.stream().map(e -> translate(e)).collect(Collectors.toList());
  }

  public ScopePageDTO translateScopePage(Page<Scope> page) {
    logger.debug("translateScopePage");
    ScopePageDTO dto = new ScopePageDTO();
    dto.setMetadata(cursorMetadata(page, MetadataPDSDTO.class));
    dto.setContent(translateScopeList(page.getContent()));
    return dto;
  }

  public ResponseEntity<ScopePageDTO> translateScopePage(Page<Scope> page, HttpStatus status) {
    logger.debug("translateScopePage");
    return new ResponseEntity<>(translateScopePage(page), status);
  }

  public List<SubjectScopeDTO> translateSubjectScopeList(List<SubjectScope> list) {
    logger.debug("translateSubjectScopeList");
    return list.stream().map(e -> translate(e)).collect(Collectors.toList());
  }

  public SubjectScopePageDTO translateSubjectScopePage(Page<SubjectScope> page) {
    logger.debug("translateSubjectScopePage");
    SubjectScopePageDTO dto = new SubjectScopePageDTO();
    dto.setMetadata(cursorMetadata(page, MetadataPDSDTO.class));
    dto.setContent(translateSubjectScopeList(page.getContent()));
    return dto;
  }

  public ResponseEntity<SubjectScopePageDTO> translateSubjectScopePage(
      Page<SubjectScope> page, HttpStatus status) {
    logger.debug("translateSubjectScopePage");
    return new ResponseEntity<>(translateSubjectScopePage(page), status);
  }
}
