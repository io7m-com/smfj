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

package com.io7m.smfj.processing.main;

import com.io7m.jnull.NullCheck;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFHeader;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.processing.api.SMFAttributeArrayType;
import com.io7m.smfj.processing.api.SMFFilterCommandContext;
import com.io7m.smfj.processing.api.SMFMemoryMesh;
import com.io7m.smfj.processing.api.SMFMemoryMeshFilterType;
import com.io7m.smfj.processing.api.SMFProcessingError;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.SortedMap;
import javaslang.control.Validation;

import java.net.URI;
import java.util.Optional;

import static com.io7m.smfj.processing.api.SMFFilterCommandChecks.checkAttributeExists;
import static com.io7m.smfj.processing.api.SMFFilterCommandParsing.errorExpectedGotValidation;
import static javaslang.control.Validation.invalid;
import static javaslang.control.Validation.valid;

/**
 * A filter that resamples attribute data.
 */

public final class SMFMemoryMeshFilterAttributeResample implements
  SMFMemoryMeshFilterType
{
  /**
   * The command name.
   */

  public static final String NAME = "resample";

  private static final String SYNTAX = "<name> <size>";

  private final SMFAttributeName attribute;
  private final int size;

  private SMFMemoryMeshFilterAttributeResample(
    final SMFAttributeName in_attribute,
    final int in_size)
  {
    this.attribute = NullCheck.notNull(in_attribute, "Attribute");
    this.size = in_size;
  }

  /**
   * Attempt to parse a command.
   *
   * @param file The file, if any
   * @param line The line
   * @param text The text
   *
   * @return A parsed command or a list of parse errors
   */

  public static Validation<List<SMFParseError>, SMFMemoryMeshFilterType> parse(
    final Optional<URI> file,
    final int line,
    final List<String> text)
  {
    NullCheck.notNull(file, "file");
    NullCheck.notNull(text, "text");

    if (text.length() == 2) {
      try {
        final SMFAttributeName  name = SMFAttributeName.of(text.get(0));
        final int size = Integer.parseUnsignedInt(text.get(1));
        return valid(create(name, size));
      } catch (final IllegalArgumentException e) {
        return errorExpectedGotValidation(file, line, makeSyntax(), text);
      }
    }
    return errorExpectedGotValidation(file, line, makeSyntax(), text);
  }

  private static String makeSyntax()
  {
    return NAME + " " + SYNTAX;
  }

  /**
   * Create a new filter.
   *
   * @param in_attribute The name of the attribute that will be resampled
   * @param in_size      The new size in bits
   *
   * @return A new filter
   */

  public static SMFMemoryMeshFilterType create(
    final SMFAttributeName in_attribute,
    final int in_size)
  {
    return new SMFMemoryMeshFilterAttributeResample(in_attribute, in_size);
  }

  @Override
  public String name()
  {
    return NAME;
  }

  @Override
  public String syntax()
  {
    return makeSyntax();
  }

  @Override
  public Validation<List<SMFProcessingError>, SMFMemoryMesh> filter(
    final SMFFilterCommandContext context,
    final SMFMemoryMesh m)
  {
    NullCheck.notNull(context, "Context");
    NullCheck.notNull(m, "Mesh");

    final SMFHeader header = m.header();

    final SortedMap<SMFAttributeName, SMFAttribute> by_name =
      m.header().attributesByName();
    Seq<SMFProcessingError> errors = List.empty();
      errors = checkAttributeExists(errors, by_name, this.attribute);

    if (errors.isEmpty()) {
      try {
        final SMFAttribute original =
          by_name.get(this.attribute).get();
        final SMFAttribute resampled =
          original.withComponentSizeBits(this.size);
        final List<SMFAttribute> ordered =
          header.attributesInOrder().replace(original, resampled);
        final SMFHeader new_header =
          header.withAttributesInOrder(ordered);
        return valid(
          SMFMemoryMesh.builder()
            .from(m)
            .setHeader(new_header)
            .build());
      } catch (final UnsupportedOperationException e) {
        errors = errors.append(
          SMFProcessingError.of(e.getMessage(), Optional.of(e)));
      }
    }

    return invalid(List.ofAll(errors));
  }
}
