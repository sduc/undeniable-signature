package gnu.crypto.assembly;

// ----------------------------------------------------------------------------
// $Id: CascadeStage.java,v 1.3 2005/10/06 04:24:13 rsdio Exp $
//
// Copyright (C) 2003, Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to the
//
//    Free Software Foundation Inc.,
//    51 Franklin Street, Fifth Floor,
//    Boston, MA 02110-1301
//    USA
//
// Linking this library statically or dynamically with other modules is
// making a combined work based on this library.  Thus, the terms and
// conditions of the GNU General Public License cover the whole
// combination.
//
// As a special exception, the copyright holders of this library give
// you permission to link this library with independent modules to
// produce an executable, regardless of the license terms of these
// independent modules, and to copy and distribute the resulting
// executable under terms of your choice, provided that you also meet,
// for each linked independent module, the terms and conditions of the
// license of that module.  An independent module is a module which is
// not derived from or based on this library.  If you modify this
// library, you may extend this exception to your version of the
// library, but you are not obligated to do so.  If you do not wish to
// do so, delete this exception statement from your version.
// ----------------------------------------------------------------------------

import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * <p>A Cascade <i>Stage</i> in a Cascade Cipher.</p>
 *
 * @version $Revision: 1.3 $
 */
class CascadeStage extends Stage {

   // Constants and variables
   // -------------------------------------------------------------------------

   private Cascade delegate;

   // Constructor(s)
   // -------------------------------------------------------------------------

   CascadeStage(Cascade cascade, Direction forwardDirection) {
      super(forwardDirection);

      this.delegate = cascade;
   }

   // Class methods
   // -------------------------------------------------------------------------

   // Instance methods
   // -------------------------------------------------------------------------

   public Set blockSizes() {
      return Collections.unmodifiableSet(delegate.blockSizes());
   }

   void initDelegate(Map attributes) throws InvalidKeyException {
      Direction flow = (Direction) attributes.get(DIRECTION);
      attributes.put(DIRECTION,
            flow.equals(forward) ? forward : Direction.reverse(forward));
//      delegate.init(flow.equals(forward) ? forward : backward);
//      delegate.init(flow.equals(forward) ? forward : Direction.reverse(forward));
      delegate.init(attributes);
   }

   public int currentBlockSize() throws IllegalStateException {
      return delegate.currentBlockSize();
   }

   void resetDelegate() {
      delegate.reset();
   }

   void updateDelegate(byte[] in, int inOffset, byte[] out, int outOffset) {
      delegate.update(in, inOffset, out, outOffset);
   }

   public boolean selfTest() {
      return delegate.selfTest();
   }
}
