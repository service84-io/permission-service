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

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import io.service84.permission.persistence.model.Scope;

@Repository("7A248A98-EB62-42F3-AA94-7FC84C0A54C5")
public interface ScopeRepository
    extends JpaRepository<Scope, UUID>, JpaSpecificationExecutor<Scope> {
  Optional<Scope> findByNamespaceAndName(String namespace, String name);

  Optional<Scope> findByQualifiedScope(String qualifiedScope);
}
