/*
 * Copyright © 2016 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.smfj.core.SMFPartialLogged;
import com.io7m.smfj.core.SMFSchemaIdentifier;
import com.io7m.smfj.core.SMFSchemaName;
import com.io7m.smfj.processing.api.SMFFilterCommandContext;
import com.io7m.smfj.processing.api.SMFMemoryMesh;
import com.io7m.smfj.processing.api.SMFMemoryMeshFilterType;
import com.io7m.smfj.processing.api.SMFProcessingError;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.smfj.processing.api.SMFFilterCommandParsing.errorExpectedGotValidation;

/**
 * A filter that checks the existence and type of an attribute.
 */

public final class SMFMemoryMeshFilterSchemaCheck
  implements SMFMemoryMeshFilterType
{
  /**
   * The command name.
   */

  public static final String NAME = "schema-check";

  private static final String SYNTAX =
    "<schema-id> <schema-major> <schema-minor>";

  private final SMFSchemaIdentifier config;

  private SMFMemoryMeshFilterSchemaCheck(
    final SMFSchemaIdentifier in_config)
  {
    this.config = Objects.requireNonNull(in_config, "Config");
  }

  /**
   * Create a new filter.
   *
   * @param in_config The configuration
   *
   * @return A new filter
   */

  public static SMFMemoryMeshFilterType create(
    final SMFSchemaIdentifier in_config)
  {
    return new SMFMemoryMeshFilterSchemaCheck(in_config);
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

  public static SMFPartialLogged<SMFMemoryMeshFilterType> parse(
    final Optional<URI> file,
    final int line,
    final List<String> text)
  {
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(text, "text");

    if (text.size() == 3) {
      try {
        final SMFSchemaName schema = SMFSchemaName.of(text.get(0));
        final int major = Integer.parseUnsignedInt(text.get(1));
        final int minor = Integer.parseUnsignedInt(text.get(2));
        return SMFPartialLogged.succeeded(create(
          SMFSchemaIdentifier.builder()
            .setName(schema)
            .setVersionMajor(major)
            .setVersionMinor(minor).build()));
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
  public SMFPartialLogged<SMFMemoryMesh> filter(
    final SMFFilterCommandContext context,
    final SMFMemoryMesh m)
  {
    Objects.requireNonNull(context, "Context");
    Objects.requireNonNull(m, "Mesh");

    final Optional<SMFSchemaIdentifier> received_opt =
      m.header().schemaIdentifier();
    if (received_opt.isPresent()) {
      final SMFSchemaIdentifier received = received_opt.get();
      if (Objects.equals(received, this.config)) {
        return SMFPartialLogged.succeeded(m);
      }
    }

    final StringBuilder sb = new StringBuilder(128);
    sb.append("Incorrect schema identifier.");
    sb.append(System.lineSeparator());
    sb.append("Expected: ");
    sb.append(this.config.toHumanString());
    sb.append(System.lineSeparator());
    sb.append("Received: ");
    if (received_opt.isPresent()) {
      final SMFSchemaIdentifier received = received_opt.get();
      sb.append(received.toHumanString());
    } else {
      sb.append("No schema ID");
    }
    sb.append(System.lineSeparator());

    return SMFPartialLogged.failed(
      SMFProcessingError.of(sb.toString(), Optional.empty()));
  }
}
