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

package com.io7m.smfj.tests.format.binary;

import com.io7m.jfsm.core.FSMTransitionException;
import com.io7m.smfj.core.SMFFormatVersion;
import com.io7m.smfj.core.SMFHeader;
import com.io7m.smfj.core.SMFTriangles;
import com.io7m.smfj.format.binary.SMFFormatBinary;
import com.io7m.smfj.parser.api.SMFParserEventsType;
import com.io7m.smfj.parser.api.SMFParserSequentialType;
import com.io7m.smfj.serializer.api.SMFSerializerType;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SMFFormatBinarySequentialTrianglesTest extends SMFBinaryTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SMFFormatBinarySequentialTrianglesTest.class);
  }

  @Rule public final ExpectedException expected = ExpectedException.none();

  @Test
  public void testTriangles8(
    final @Mocked SMFParserEventsType events)
  {
    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 8L));
    final SMFHeader header = header_b.build();

    new StrictExpectations()
    {{
      events.onStart();
      events.onVersionReceived(SMFFormatVersion.of(1, 0));
      events.onHeaderParsed(header);

      events.onDataTrianglesStart();
      events.onDataTriangle(0L, 1L, 2L);
      events.onDataTriangle(0L, 2L, 3L);
      events.onDataTrianglesFinish();
    }};

    final SMFParserSequentialType p = this.parserSequentialFor(events, out -> {
      out.putBytes(SMFFormatBinary.magicNumber());
      out.putU32(1L);
      out.putU32(0L);
      out.putBytes(header_s.buffer());

      out.putU8(0L);
      out.putU8(1L);
      out.putU8(2L);

      out.putU8(0L);
      out.putU8(2L);
      out.putU8(3L);
    });

    p.parseHeader();
    p.parseData();
  }

  @Test
  public void testTriangles16(
    final @Mocked SMFParserEventsType events)
  {
    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 16L));
    final SMFHeader header = header_b.build();

    new StrictExpectations()
    {{
      events.onStart();
      events.onVersionReceived(SMFFormatVersion.of(1, 0));
      events.onHeaderParsed(header);

      events.onDataTrianglesStart();
      events.onDataTriangle(0L, 1L, 2L);
      events.onDataTriangle(0L, 2L, 3L);
      events.onDataTrianglesFinish();
    }};

    final SMFParserSequentialType p = this.parserSequentialFor(events, out -> {
      out.putBytes(SMFFormatBinary.magicNumber());
      out.putU32(1L);
      out.putU32(0L);
      out.putBytes(header_s.buffer());

      out.putU16(0L);
      out.putU16(1L);
      out.putU16(2L);

      out.putU16(0L);
      out.putU16(2L);
      out.putU16(3L);
    });

    p.parseHeader();
    p.parseData();
  }

  @Test
  public void testTriangles32(
    final @Mocked SMFParserEventsType events)
  {
    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 32L));
    final SMFHeader header = header_b.build();

    new StrictExpectations()
    {{
      events.onStart();
      events.onVersionReceived(SMFFormatVersion.of(1, 0));
      events.onHeaderParsed(header);

      events.onDataTrianglesStart();
      events.onDataTriangle(0L, 1L, 2L);
      events.onDataTriangle(0L, 2L, 3L);
      events.onDataTrianglesFinish();
    }};

    final SMFParserSequentialType p = this.parserSequentialFor(events, out -> {
      out.putBytes(SMFFormatBinary.magicNumber());
      out.putU32(1L);
      out.putU32(0L);
      out.putBytes(header_s.buffer());

      out.putU32(0L);
      out.putU32(1L);
      out.putU32(2L);

      out.putU32(0L);
      out.putU32(2L);
      out.putU32(3L);
    });

    p.parseHeader();
    p.parseData();
  }

  @Test
  public void testSerializeTriangles8()
    throws IOException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Path path = Paths.get("/data");
    final SMFFormatVersion version = SMFFormatVersion.of(1, 0);

    final SMFSerializerType serializer =
      new SMFFormatBinary().serializerCreate(version, path, out);

    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 8L));
    final SMFHeader header = header_b.build();

    serializer.serializeHeader(header);
    serializer.serializeDataStart();
    serializer.serializeTrianglesStart();
    serializer.serializeTriangle(0L, 1L, 2L);
  }

  @Test
  public void testSerializeTriangles16()
    throws IOException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Path path = Paths.get("/data");
    final SMFFormatVersion version = SMFFormatVersion.of(1, 0);

    final SMFSerializerType serializer =
      new SMFFormatBinary().serializerCreate(version, path, out);

    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 16L));
    final SMFHeader header = header_b.build();

    serializer.serializeHeader(header);
    serializer.serializeDataStart();
    serializer.serializeTrianglesStart();
    serializer.serializeTriangle(0L, 1L, 2L);
  }

  @Test
  public void testSerializeTriangles32()
    throws IOException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Path path = Paths.get("/data");
    final SMFFormatVersion version = SMFFormatVersion.of(1, 0);

    final SMFSerializerType serializer =
      new SMFFormatBinary().serializerCreate(version, path, out);

    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 32L));
    final SMFHeader header = header_b.build();

    serializer.serializeHeader(header);
    serializer.serializeDataStart();
    serializer.serializeTrianglesStart();
    serializer.serializeTriangle(0L, 1L, 2L);
  }

  @Test
  public void testSerializeTriangles64()
    throws IOException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Path path = Paths.get("/data");
    final SMFFormatVersion version = SMFFormatVersion.of(1, 0);

    final SMFSerializerType serializer =
      new SMFFormatBinary().serializerCreate(version, path, out);

    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(2L, 64L));
    final SMFHeader header = header_b.build();

    serializer.serializeHeader(header);
    serializer.serializeDataStart();
    serializer.serializeTrianglesStart();
    serializer.serializeTriangle(0L, 1L, 2L);
  }

  @Test
  public void testSerializeTrianglesTooMany()
    throws IOException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Path path = Paths.get("/data");
    final SMFFormatVersion version = SMFFormatVersion.of(1, 0);

    final SMFSerializerType serializer =
      new SMFFormatBinary().serializerCreate(version, path, out);

    final SerializedHeader header_s = new SerializedHeader();
    final SMFHeader.Builder header_b = header_s.headerBuilder();
    header_b.setTriangles(SMFTriangles.of(1L, 32L));
    final SMFHeader header = header_b.build();

    serializer.serializeHeader(header);
    serializer.serializeDataStart();
    serializer.serializeTrianglesStart();
    serializer.serializeTriangle(0L, 1L, 2L);

    this.expected.expect(FSMTransitionException.class);
    serializer.serializeTriangle(0L, 1L, 2L);
  }
}
