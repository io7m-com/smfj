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

import com.io7m.jcoords.core.conversion.CAxis;
import com.io7m.jcoords.core.conversion.CAxisSystem;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFComponentType;
import com.io7m.smfj.core.SMFCoordinateSystem;
import com.io7m.smfj.core.SMFFaceWindingOrder;
import com.io7m.smfj.core.SMFPartialLogged;
import com.io7m.smfj.core.SMFSchemaIdentifier;
import com.io7m.smfj.core.SMFSchemaName;
import com.io7m.smfj.validation.api.SMFSchema;
import com.io7m.smfj.validation.api.SMFSchemaAttribute;
import com.io7m.smfj.validation.api.SMFSchemaParserType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import org.apache.commons.io.input.BrokenInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SMFSchemaParserContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SMFSchemaParserContract.class);
  }

  private static void showErrors(
    final SMFPartialLogged<SMFSchema> r)
  {
    if (r.isFailed()) {
      r.errors().forEach(e -> LOG.error("{}", e.fullMessage()));
    }
  }

  protected abstract SMFSchemaParserType create(
    Path path,
    InputStream stream);

  private SMFSchemaParserType resourceParser(
    final String name)
    throws Exception
  {
    final String path = "/com/io7m/smfj/tests/validation/api/" + name;
    final InputStream stream =
      Objects.requireNonNull(SMFSchemaParserContract.class.getResourceAsStream(
        path), "Stream");
    return this.create(Paths.get(path), stream);
  }

  @Test
  public final void testIOError()
  {
    boolean caught = false;
    try {
      try (SMFSchemaParserType parser = this.create(
        Paths.get("/invalid"),
        new BrokenInputStream())) {

        final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
        showErrors(r);
        Assertions.assertFalse(r.isSucceeded());
        Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
          "I/O error")));
      }
    } catch (final IOException e) {
      LOG.debug("caught: ", e);
      caught = true;
    }
    Assertions.assertTrue(caught);
  }

  @Test
  public final void testInvalidUnknown0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-unknown-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Unrecognized schema statement")));
    }
  }

  @Test
  public final void testInvalidRequireVertices0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-require-vertices-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse vertices requirement")));
    }
  }

  @Test
  public final void testInvalidRequireVertices1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-require-vertices-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse vertices requirement")));
    }
  }

  @Test
  public final void testInvalidRequireTriangles0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-require-triangles-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse triangle requirement")));
    }
  }

  @Test
  public final void testInvalidRequireTriangles1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-require-triangles-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse triangle requirement")));
    }
  }

  @Test
  public final void testInvalidAttribute0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-attribute-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Incorrect number of arguments")));
    }
  }

  @Test
  public final void testInvalidAttribute1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-attribute-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse requirement")));
    }
  }

  @Test
  public final void testInvalidAttribute2()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-attribute-2.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse attribute name")));
    }
  }

  @Test
  public final void testInvalidAttribute3()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-attribute-3.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Unrecognized type")));
    }
  }

  @Test
  public final void testInvalidAttribute4()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-attribute-4.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse component count")));
    }
  }

  @Test
  public final void testInvalidAttribute5()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-attribute-5.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse component size")));
    }
  }

  @Test
  public final void testInvalidEmpty()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser("empty.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Empty file: Must begin with an smf-schema version declaration")));
    }
  }

  @Test
  public final void testInvalidMissingIdentifier()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "missing-ident.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Must specify a schema identifier")));
    }
  }

  @Test
  public final void testInvalidSchema0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-schema-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Incorrect number of arguments")));
    }
  }

  @Test
  public final void testInvalidVersion0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-version-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.fullMessage().contains(
        "Unparseable version declaration")));
    }
  }

  @Test
  public final void testInvalidVersion1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-version-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.fullMessage().contains(
        "Unparseable version declaration")));
    }
  }

  @Test
  public final void testInvalidVersion2()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-version-2.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.fullMessage().contains(
        "Unparseable version declaration")));
    }
  }

  @Test
  public final void testInvalidSchema1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-schema-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.fullMessage().contains(
        "NumberFormatException")));
    }
  }

  @Test
  public final void testInvalidBadCoordinateSystem0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-coords-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse coordinate system")));
    }
  }

  @Test
  public final void testInvalidBadCoordinateSystem1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-coords-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse coordinate system")));
    }
  }

  @Test
  public final void testInvalidBadCoordinateSystem2()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-coords-2.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse coordinate system")));
    }
  }

  @Test
  public final void testInvalidBadCoordinateSystem3()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "invalid-coords-3.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertFalse(r.isSucceeded());
      Assertions.assertTrue(r.errors().stream().anyMatch(e -> e.message().contains(
        "Could not parse coordinate system")));
    }
  }

  @Test
  public final void testValidMinimal()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "valid-minimal.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertTrue(r.isSucceeded());
      final SMFSchema schema = r.get();
      Assertions.assertEquals(
        SMFSchemaIdentifier.of(
          SMFSchemaName.of("com.io7m.smf.example"), 1, 2),
        schema.schemaIdentifier());
      Assertions.assertEquals(
        Optional.empty(),
        schema.requiredCoordinateSystem());
      Assertions.assertEquals(Map.of(), schema.requiredAttributes());
    }
  }

  @Test
  public final void testValidAttribute0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "valid-attribute-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertTrue(r.isSucceeded());
      final SMFSchema schema = r.get();

      final SMFAttributeName a_name = SMFAttributeName.of("x");
      Assertions.assertEquals(1L, schema.requiredAttributes().size());
      final SMFSchemaAttribute a = schema.requiredAttributes().get(a_name);
      Assertions.assertEquals(a_name, a.name());
      Assertions.assertEquals(
        SMFComponentType.ELEMENT_TYPE_FLOATING,
        a.requiredComponentType().get());
      Assertions.assertEquals(4L, a.requiredComponentCount().getAsInt());
      Assertions.assertEquals(32L, a.requiredComponentSize().getAsInt());
    }
  }

  @Test
  public final void testValidAttribute1()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "valid-attribute-1.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertTrue(r.isSucceeded());
      final SMFSchema schema = r.get();

      final SMFAttributeName a_name = SMFAttributeName.of("x");
      Assertions.assertEquals(1L, schema.requiredAttributes().size());
      final SMFSchemaAttribute a = schema.requiredAttributes().get(a_name);
      Assertions.assertEquals(a_name, a.name());
      Assertions.assertEquals(Optional.empty(), a.requiredComponentType());
      Assertions.assertEquals(OptionalInt.empty(), a.requiredComponentCount());
      Assertions.assertEquals(OptionalInt.empty(), a.requiredComponentSize());
    }
  }

  @Test
  public final void testValidAttribute2()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "valid-attribute-2.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertTrue(r.isSucceeded());
      final SMFSchema schema = r.get();

      final SMFAttributeName a_name = SMFAttributeName.of("x");
      Assertions.assertEquals(1L, schema.optionalAttributes().size());
      final SMFSchemaAttribute a = schema.optionalAttributes().get(a_name);
      Assertions.assertEquals(a_name, a.name());
      Assertions.assertEquals(
        SMFComponentType.ELEMENT_TYPE_FLOATING,
        a.requiredComponentType().get());
      Assertions.assertEquals(4L, a.requiredComponentCount().getAsInt());
      Assertions.assertEquals(32L, a.requiredComponentSize().getAsInt());
    }
  }

  @Test
  public final void testValidCoords0()
    throws Exception
  {
    try (SMFSchemaParserType parser = this.resourceParser(
      "valid-coords-0.smfs")) {
      final SMFPartialLogged<SMFSchema> r = parser.parseSchema();
      showErrors(r);
      Assertions.assertTrue(r.isSucceeded());
      final SMFSchema schema = r.get();
      Assertions.assertEquals(
        SMFSchemaIdentifier.of(
          SMFSchemaName.of("com.io7m.smf.example"), 1, 2),
        schema.schemaIdentifier());
      Assertions.assertEquals(
        Optional.of(SMFCoordinateSystem.of(
          CAxisSystem.of(
            CAxis.AXIS_POSITIVE_X,
            CAxis.AXIS_POSITIVE_Y,
            CAxis.AXIS_NEGATIVE_Z),
          SMFFaceWindingOrder.FACE_WINDING_ORDER_COUNTER_CLOCKWISE)),
        schema.requiredCoordinateSystem());
      Assertions.assertEquals(Map.of(), schema.requiredAttributes());
    }
  }
}
