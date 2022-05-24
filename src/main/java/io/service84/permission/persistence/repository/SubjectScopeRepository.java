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

package io.service84.permission.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import io.service84.permission.persistence.model.Scope;
import io.service84.permission.persistence.model.SubjectScope;

@Repository("E3B7467D-3DD8-48B5-ABB5-76D15845B386")
public interface SubjectScopeRepository
    extends JpaRepository<SubjectScope, UUID>, JpaSpecificationExecutor<SubjectScope> {
  List<SubjectScope> findAllBySubjectAndScopeIn(String subject, List<Scope> scopes);

  Optional<SubjectScope> findBySubjectAndScope(UUID subject, Scope scope);
}
