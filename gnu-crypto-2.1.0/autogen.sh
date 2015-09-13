#!/bin/sh
# ----------------------------------------------------------------------------
# $Id: autogen.sh,v 1.4 2005/10/06 04:24:10 rsdio Exp $
#
# Copyright (C) 2001, 2002, 2004 Free Software Foundation, Inc.
#
# This file is part of GNU Crypto.
#
# GNU Crypto is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2, or (at your option)
# any later version.
#
# GNU Crypto is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; see the file COPYING.  If not, write to the
#
#    Free Software Foundation Inc.,
#    51 Franklin Street, Fifth Floor,
#    Boston, MA 02110-1301
#    USA
#
# Linking this library statically or dynamically with other modules is
# making a combined work based on this library.  Thus, the terms and
# conditions of the GNU General Public License cover the whole
# combination.
#
# As a special exception, the copyright holders of this library give
# you permission to link this library with independent modules to
# produce an executable, regardless of the license terms of these
# independent modules, and to copy and distribute the resulting
# executable under terms of your choice, provided that you also meet,
# for each linked independent module, the terms and conditions of the
# license of that module.  An independent module is a module which is
# not derived from or based on this library.  If you modify this
# library, you may extend this exception to your version of the
# library, but you are not obligated to do so.  If you do not wish to
# do so, delete this exception statement from your version.
# ----------------------------------------------------------------------------
#
# A script to generate all needed GNU build toolchain scripts and files before
# packaging a distribution.
#
# $Revision: 1.4 $
#

[ -f configure.ac ] || {
   echo "$0: You must run this command in the top-level directory."
   exit 1
}

DIE=0

test -z "$AUTOCONF" && export AUTOCONF=autoconf
test -z "$AUTOMAKE" && export AUTOMAKE=automake
test -z "$ACLOCAL"  && export ACLOCAL=aclocal

($AUTOCONF --version) < /dev/null > /dev/null 2>&1 || {
   echo
   echo "$0: Need 'autoconf' to compile GNU Crypto."
   echo "Try http://www.gnu.org/software/autoconf/"
   DIE=1
   NO_AUTOCONF=yes
}

($AUTOMAKE --version) < /dev/null > /dev/null 2>&1 || {
   echo
   echo "$0: Need 'automake' to compile GNU Crypto."
   echo "Try http://www.gnu.org/software/automake/"
   DIE=1
   NO_AUTOMAKE=yes
}

# test for aclocal, only if automake was found
test -n "$NO_AUTOMAKE" || ($ACLOCAL --version) < /dev/null > /dev/null 2>&1 || {
   echo
   echo "$0: Need 'aclocal' to compile GNU Crypto."
   echo "Try http://www.gnu.org/software/automake/"
   DIE=1
}

if test "$DIE" -eq 1; then
   exit 1
fi

set -e
echo "$0: Generating GNU build toolchain scripts and files for GNU Crypto."

echo "$ACLOCAL -I ."
$ACLOCAL -I .

echo "$AUTOMAKE --add-missing --copy"
$AUTOMAKE --add-missing --copy

echo "$AUTOCONF"
$AUTOCONF

echo "$0: Done. Use \`configure' to configure GNU Crypto."
