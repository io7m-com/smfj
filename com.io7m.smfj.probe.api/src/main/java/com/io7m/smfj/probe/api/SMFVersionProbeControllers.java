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

package com.io7m.smfj.probe.api;

import com.io7m.jlexing.core.LexicalPositions;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.smfj.core.SMFErrorType;
import com.io7m.smfj.core.SMFPartialLogged;
import com.io7m.smfj.core.SMFWarningType;
import com.io7m.smfj.parser.api.SMFParseError;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.io7m.smfj.parser.api.SMFParseErrors.errorWithMessage;

/**
 * Functions for implementing version probe controllers.
 */

final class SMFVersionProbeControllers
{
  private SMFVersionProbeControllers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Run the given version probes.
   *
   * @param streams A stream supplier
   * @param probes  A set of probe providers
   *
   * @return The result of probing
   */

  static SMFPartialLogged<SMFVersionProbed> probe(
    final Supplier<InputStream> streams,
    final Iterable<SMFVersionProbeProviderType> probes)
  {
    final List<SMFWarningType> warnings = new ArrayList<>();
    final List<SMFErrorType> errors = new ArrayList<>();
    for (final SMFVersionProbeProviderType probe : probes) {
      try (InputStream stream = streams.get()) {
        final var r = probe.probe(stream);
        if (r.isFailed()) {
          errors.addAll(r.errors());
          warnings.addAll(r.warnings());
        } else {
          return r;
        }
      } catch (final IOException e) {
        errors.add(SMFParseError.of(
          LexicalPositions.zero(),
          e.getMessage(),
          Optional.of(e)));
      }
    }

    if (errors.isEmpty()) {
      errors.add(errorWithMessage("No format providers available."));
    }
    return SMFPartialLogged.failed(errors, warnings);
  }
}
