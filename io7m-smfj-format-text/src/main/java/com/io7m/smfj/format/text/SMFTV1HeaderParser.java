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

package com.io7m.smfj.format.text;

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jnull.NullCheck;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFComponentType;
import com.io7m.smfj.core.SMFFormatVersion;
import com.io7m.smfj.parser.api.SMFParserEventsType;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

final class SMFTV1HeaderParser extends SMFTAbstractParser
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SMFTV1HeaderParser.class);
  }

  private final SMFTAbstractParser parent;
  private final SMFFormatVersion version;
  protected Map<SMFAttributeName, SMFAttribute> attributes;
  protected Map<SMFAttributeName, Integer> attribute_lines;
  protected List<SMFAttribute> attributes_list;
  protected long vertex_count;
  protected long triangle_count;
  protected long triangle_size;
  private boolean ok_vertices;
  private boolean ok_triangles;

  SMFTV1HeaderParser(
    final SMFTAbstractParser in_parent,
    final SMFParserEventsType in_events,
    final SMFTLineReader in_reader,
    final SMFFormatVersion in_version)
  {
    super(in_events, in_reader, in_parent.state);
    this.parent = NullCheck.notNull(in_parent, "Parent");
    this.version = NullCheck.notNull(in_version, "Version");
    this.attribute_lines = HashMap.empty();
    this.attributes_list = List.empty();
    this.attributes = HashMap.empty();
    this.ok_vertices = false;
    this.ok_triangles = false;
  }

  @Override
  public void parse()
  {
    this.log().debug("parsing header");

    try {
      super.events.onHeaderStart();

      this.parseHeaderCommands();

      if (super.state.get() != ParserState.STATE_FINISHED) {
        this.parseHeaderCheckUniqueAttributeNames();
      }

      if (super.state.get() != ParserState.STATE_FINISHED) {
        super.events.onHeaderAttributeCountReceived(
          (long) this.attributes_list.size());

        for (final SMFAttribute attribute : this.attributes_list) {
          super.events.onHeaderAttributeReceived(attribute);
        }

        if (this.ok_vertices) {
          super.events.onHeaderVerticesCountReceived(this.vertex_count);
        }

        if (this.ok_triangles) {
          super.events.onHeaderTrianglesCountReceived(this.triangle_count);
          super.events.onHeaderTrianglesIndexSizeReceived(this.triangle_size);
        }
      }

    } catch (final Exception e) {
      this.fail(e.getMessage());
    } finally {
      super.events.onHeaderFinish();
    }
  }

  private void parseHeaderCommands()
    throws Exception
  {
    while (true) {
      final Optional<List<String>> line_opt = super.reader.line();
      if (!line_opt.isPresent()) {
        this.fail("Unexpected EOF");
        return;
      }

      this.log().debug("line: {}", line_opt.get());
      final List<String> line = line_opt.get();
      if (line.isEmpty()) {
        continue;
      }

      switch (line.get(0)) {
        case "data": {
          if (line.size() == 1) {
            return;
          }

          super.failExpectedGot(
            "Incorrect number of arguments",
            "data",
            line.toJavaStream().collect(Collectors.joining(" ")));
          return;
        }

        case "vertices": {
          this.parseHeaderCommandVertices(line);
          break;
        }

        case "triangles": {
          this.parseHeaderCommandTriangles(line);
          break;
        }

        case "attribute": {
          this.parseHeaderCommandAttribute(line);
          break;
        }

        default: {
          super.failExpectedGot(
            "Unrecognized command.",
            "attribute | triangles | vertices | data",
            line.toJavaStream().collect(Collectors.joining(" ")));
          return;
        }
      }
    }
  }

  private void parseHeaderCommandAttribute(
    final Seq<String> line)
  {
    if (line.size() == 5) {
      try {
        final SMFAttributeName name =
          SMFAttributeName.of(line.get(1));
        final SMFComponentType type =
          SMFComponentType.of(line.get(2));
        final int count =
          Integer.parseUnsignedInt(line.get(3));
        final int size =
          Integer.parseUnsignedInt(line.get(4));
        final SMFAttribute attr =
          SMFAttribute.of(name, type, count, size);

        this.attribute_lines = this.attribute_lines.put(
          name, Integer.valueOf(super.reader.position().line()));
        this.attributes_list = this.attributes_list.append(attr);
      } catch (final IllegalArgumentException e) {
        super.failExpectedGot(
          e.getMessage(),
          "attribute <attribute-name> <component-type> <component-count> <component-size>",
          line.toJavaStream().collect(Collectors.joining(" ")));
      }
    } else {
      super.failExpectedGot(
        "Incorrect number of arguments",
        "attribute <attribute-name> <component-type> <component-count> <component-size>",
        line.toJavaStream().collect(Collectors.joining(" ")));
    }
  }

  private void parseHeaderCommandTriangles(
    final Seq<String> line)
  {
    if (line.size() == 3) {
      try {
        this.triangle_count = Long.parseUnsignedLong(line.get(1));
        this.triangle_size = Long.parseUnsignedLong(line.get(2));
        this.ok_triangles = true;
      } catch (final NumberFormatException e) {
        super.failExpectedGot(
          "Cannot parse number: " + e.getMessage(),
          "triangles <triangle-count> <triangle-index-size>",
          line.toJavaStream().collect(Collectors.joining(" ")));
      }
    } else {
      super.failExpectedGot(
        "Incorrect number of arguments",
        "triangles <triangle-count> <triangle-index-size>",
        line.toJavaStream().collect(Collectors.joining(" ")));
    }
  }

  private void parseHeaderCommandVertices(
    final Seq<String> line)
  {
    if (line.size() == 2) {
      try {
        this.vertex_count = Long.parseUnsignedLong(line.get(1));
        this.ok_vertices = true;
      } catch (final NumberFormatException e) {
        super.failExpectedGot(
          "Cannot parse number: " + e.getMessage(),
          "vertices <vertex-count>",
          line.toJavaStream().collect(Collectors.joining(" ")));
      }
    } else {
      super.failExpectedGot(
        "Incorrect number of arguments",
        "vertices <vertex-count>",
        line.toJavaStream().collect(Collectors.joining(" ")));
    }
  }

  private void parseHeaderCheckUniqueAttributeNames()
  {
    final Collection<SMFAttributeName> names =
      new HashSet<>(this.attributes_list.size());

    for (final SMFAttribute attribute : this.attributes_list) {
      final SMFAttributeName name = attribute.name();
      if (names.contains(name)) {
        Invariants.checkInvariant(
          name,
          this.attribute_lines.containsKey(name),
          a_name -> "Attribute lines must contain " + a_name);

        this.failWithLineNumber(
          this.attribute_lines.get(name).get().intValue(),
          "Duplicate attribute name: " + name.value());
      } else {
        this.attributes = this.attributes.put(name, attribute);
      }
      names.add(name);
    }
  }

  @Override
  protected Logger log()
  {
    return LOG;
  }
}
