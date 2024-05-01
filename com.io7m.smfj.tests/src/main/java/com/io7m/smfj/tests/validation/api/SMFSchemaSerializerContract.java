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

package com.io7m.smfj.tests.validation.api;

import com.io7m.smfj.core.SMFPartialLogged;
import com.io7m.smfj.validation.api.SMFSchema;
import com.io7m.smfj.validation.api.SMFSchemaParserType;
import com.io7m.smfj.validation.api.SMFSchemaSerializerType;
import com.io7m.smfj.validation.api.SMFSchemaVersion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.SortedSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SMFSchemaSerializerContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SMFSchemaSerializerContract.class);
  }

  protected abstract SMFSchemaParserType createParser(
    Path path,
    InputStream stream);

  protected abstract SortedSet<SMFSchemaVersion> serializerVersions();

  protected abstract SMFSchemaSerializerType createSerializer(
    SMFSchemaVersion version,
    Path path,
    OutputStream stream);

  private SMFSchemaParserType resourceParser(
    final String name)
    throws Exception
  {
    final String path = "/com/io7m/smfj/tests/validation/api/" + name;
    final InputStream stream =
      Objects.requireNonNull(SMFSchemaParserContract.class.getResourceAsStream(
        path), "Stream");
    return this.createParser(Paths.get(path), stream);
  }

  @Test
  public void testRoundTrip()
    throws Exception
  {
    final SMFSchema schema;
    try (SMFSchemaParserType parser = this.resourceParser("all.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      Assertions.assertTrue(r.isSucceeded());
      schema = r.get();
    }

    final SortedSet<SMFSchemaVersion> versions = this.serializerVersions();
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      final Path out_path = Paths.get("/output");
      try (SMFSchemaSerializerType serial =
             this.createSerializer(versions.last(), out_path, out)) {
        serial.serializeSchema(schema);
      }

      try (InputStream in = new ByteArrayInputStream(out.toByteArray())) {
        try (SMFSchemaParserType parser = this.createParser(out_path, in)) {
          final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
          Assertions.assertTrue(r.isSucceeded());
          final SMFSchema written_schema = r.get();
          Assertions.assertEquals(schema, written_schema);
        }
      }
    }
  }
}
