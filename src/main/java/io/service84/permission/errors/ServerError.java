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

package io.service84.permission.errors;

import java.util.function.Supplier;

public class ServerError extends Error {
  public static final long serialVersionUID = 1L;

  public static Supplier<ServerError> supplier() {
    return new Supplier<>() {
      @Override
      public ServerError get() {
        return new ServerError();
      }
    };
  }

  public ServerError() {}

  public ServerError(String message) {
    super(message);
  }

  public ServerError(String message, Throwable cause) {
    super(message, cause);
  }

  public ServerError(Throwable cause) {
    super(cause);
  }
}
