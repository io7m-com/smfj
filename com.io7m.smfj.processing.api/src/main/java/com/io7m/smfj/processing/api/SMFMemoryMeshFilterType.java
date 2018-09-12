/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.smfj.processing.api;

import javaslang.collection.List;
import javaslang.control.Validation;

/**
 * A filter that transforms an in-memory mesh.
 */

public interface SMFMemoryMeshFilterType
{
  /**
   * @return The name of the filter
   */

  String name();

  /**
   * @return The syntax of the filter
   */

  String syntax();

  /**
   * Evaluate the filter on the given mesh.
   *
   * @param context The filtering context
   * @param m       A mesh
   *
   * @return A filtered mesh, or a list or reasons why the filtering did not work
   */

  Validation<List<SMFProcessingError>, SMFMemoryMesh> filter(
    SMFFilterCommandContext context,
    SMFMemoryMesh m);
}
