/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.core.SMFAttributeName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Useful methods for implementing filter commands.
 */

public final class SMFFilterCommandChecks
{
  private SMFFilterCommandChecks()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Check that the given attribute exists.
   *
   * @param errors     The current list of errors
   * @param attributes The available attributes
   * @param name       The attribute name
   *
   * @return The list of errors plus an error indicating that the attribute does not exist
   */

  public static List<SMFProcessingError> checkAttributeExists(
    final List<SMFProcessingError> errors,
    final Map<SMFAttributeName, SMFAttribute> attributes,
    final SMFAttributeName name)
  {
    if (!attributes.containsKey(name)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Mesh does not contain the given attribute.");
      sb.append(System.lineSeparator());
      sb.append("  Attribute: ");
      sb.append(name.value());
      sb.append(System.lineSeparator());
      sb.append("  Existing:  ");
      sb.append(System.lineSeparator());

      final Iterator<Map.Entry<SMFAttributeName, SMFAttribute>> iter =
        attributes.entrySet().iterator();

      while (iter.hasNext()) {
        final Map.Entry<SMFAttributeName, SMFAttribute> entry = iter.next();
        sb.append("  ");
        sb.append(entry.getKey().value());
        sb.append(System.lineSeparator());
      }

      final var errorsResult = new ArrayList<>(errors);
      errorsResult.add(SMFProcessingError.of(sb.toString(), Optional.empty()));
      return List.copyOf(errorsResult);
    }

    return errors;
  }

  /**
   * Check that the given attribute does not exist.
   *
   * @param errors     The current list of errors
   * @param attributes The available attributes
   * @param name       The attribute name
   *
   * @return The list of errors plus an error indicating that the attribute already exists
   */

  public static List<SMFProcessingError> checkAttributeNonexistent(
    final List<SMFProcessingError> errors,
    final Map<SMFAttributeName, SMFAttribute> attributes,
    final SMFAttributeName name)
  {
    if (attributes.containsKey(name)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Mesh already contains an attribute with the given name.");
      sb.append(System.lineSeparator());
      sb.append("  Attribute: ");
      sb.append(name.value());
      sb.append(System.lineSeparator());
      sb.append("  Existing:  ");
      sb.append(System.lineSeparator());

      final Iterator<Map.Entry<SMFAttributeName, SMFAttribute>> iter =
        attributes.entrySet().iterator();

      while (iter.hasNext()) {
        final Map.Entry<SMFAttributeName, SMFAttribute> entry = iter.next();
        sb.append("  ");
        sb.append(entry.getKey().value());
        sb.append(System.lineSeparator());
      }

      final var errorsResult = new ArrayList<>(errors);
      errorsResult.add(SMFProcessingError.of(sb.toString(), Optional.empty()));
      return List.copyOf(errorsResult);
    }

    return errors;
  }
}
