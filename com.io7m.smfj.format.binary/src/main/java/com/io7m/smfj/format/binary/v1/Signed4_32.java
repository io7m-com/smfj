/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.smfj.format.binary.v1;

import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.format.binary.SMFBDataStreamWriterType;
import java.io.IOException;

final class Signed4_32 extends BaseValues
{
  Signed4_32(
    final SMFBDataStreamWriterType in_writer,
    final SMFAttribute in_attribute)
  {
    super(in_writer, in_attribute);
  }

  @Override
  public void serializeValueIntegerSigned4(
    final long x,
    final long y,
    final long z,
    final long w)
    throws IOException
  {
    super.writer().putS32(x);
    super.writer().putS32(y);
    super.writer().putS32(z);
    super.writer().putS32(w);
  }
}
