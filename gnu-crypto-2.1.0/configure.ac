## Process this file with autoconf to produce a configure script.
#
# ----------------------------------------------------------------------------
# $Id: configure.ac,v 1.17 2005/10/06 04:24:10 rsdio Exp $
#
# Copyright (C) 2001, 2002, 2003, 2004  Free Software Foundation, Inc.
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
# $Revision: 1.17 $
#

AC_INIT(gnu-crypto, 2.1.0, gnu-crypto-discuss@gnu.org)
AC_CONFIG_SRCDIR(source/gnu/crypto/Properties.java)
AM_INIT_AUTOMAKE(dist-bzip2 dist-zip 1.9.0 gnu std-options tar-ustar)

PACKAGE="gnu-crypto"
VERSION="2.1.0"
LIBVERSION="2:1"
AC_SUBST(PACKAGE)
AC_SUBST(VERSION)
AC_SUBST(LIBVERSION)

# Checks for programs
AC_PREREQ(2.52)

#AM_PROG_LIBTOOL
#AM_PROG_GCJ
#AC_PROG_INSTALL
#AC_PROG_LN_S

#AC_SUBST(LIBTOOL_DEPS)

AC_PREFIX_DEFAULT(/usr/local/gnu-crypto)

# Initialise maintainer mode:
#     This macro adds a `--enable-maintainer-mode' option to
#     `configure'.  If this is used, `automake' will cause
#     `maintainer-only' rules to be turned off by default in the
#     generated `Makefile.in's. This macro defines the `MAINTAINER_MODE'
#     conditional, which you can use in your own `Makefile.am'.
AM_MAINTAINER_MODE

SRCDIR=`(cd $srcdir; pwd)`
AC_SUBST(SRCDIR)

GENERATED="// THIS IS A GENERATED FILE, DO NOT EDIT -*- buffer-read-only: t -*-]"
AC_SUBST(GENERATED)

# -----------------------------------------------------------------------------

AC_CHECK_CLASSPATH
AC_PROG_JAVAC
AC_PROG_JAVA
AC_PROG_JAR

if test "x$CLASSPATH" = "x"; then
   CLASSPATH=.
fi

# GNU_CRYPTO_WITH_JCE
# GNU_CRYPTO_WITH_SASL
# GNU_CRYPTO_WITH_JGSS

ACX_CHECK_PATHNAME_STYLE_DOS
GNU_CRYPTO_WITH_TMPDIR

AM_CONDITIONAL(FOUND_SECURITY_JAR, true)
AM_CONDITIONAL(WITH_INCLUDED_GETOPT, true)

# -----------------------------------------------------------------------------

AC_CONFIG_FILES([Makefile source/Makefile source/gnu/crypto/Registry.java source/gnu/testlet/config.java source/mauve-gnu-crypto docs/Makefile docs/diagrams/Makefile])

AC_OUTPUT
