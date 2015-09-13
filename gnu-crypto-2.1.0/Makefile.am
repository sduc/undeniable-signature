## Process this file with automake to produce Makefile.in.
#
# -----------------------------------------------------------------------------
# $Id: Makefile.am,v 1.20 2005/10/06 04:24:10 rsdio Exp $
#
# Copyright (C) 2001, 2002, 2003, 2004 Free Software Foundation, Inc.
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
# -----------------------------------------------------------------------------
#
# $Revision: 1.20 $
#

# DISTCHECK_CONFIGURE_FLAGS = --without-jce
# EXTRA_DIST = \
# 	$(srcdir)/gcj/jce/Makefile.am $(srcdir)/gcj/jce/Makefile.in $(srcdir)/gcj/Makefile.in \
# 	$(srcdir)/gcj/aclocal.m4 $(srcdir)/gcj/autogen.sh $(srcdir)/gcj/config.guess $(srcdir)/gcj/config.sub \
# 	$(srcdir)/gcj/configure $(srcdir)/gcj/configure.ac $(srcdir)/gcj/depcomp $(srcdir)/gcj/init.sh $(srcdir)/gcj/install-sh \
# 	$(srcdir)/gcj/libtool $(srcdir)/gcj/libtool.m4 $(srcdir)/gcj/ltcf-c.sh $(srcdir)/gcj/ltcf-cxx.sh \
# 	$(srcdir)/gcj/ltcf-$(srcdir)/gcj.sh $(srcdir)/gcj/ltconfig $(srcdir)/gcj/ltmain.sh $(srcdir)/gcj/missing \
# 	$(srcdir)/gcj/mkinstalldirs $(srcdir)/gcj/security/Makefile.am $(srcdir)/gcj/security/Makefile.in \
# 	$(srcdir)/gcj/source/gnu/crypto/cipher/Blowfish.java \
# 	$(srcdir)/gcj/source/gnu/crypto/cipher/Khazad.java \
# 	$(srcdir)/gcj/source/gnu/crypto/cipher/Rijndael.java \
# 	$(srcdir)/gcj/source/gnu/crypto/cipher/Serpent.java.in \
# 	$(srcdir)/gcj/source/gnu/crypto/cipher/Square.java \
# 	$(srcdir)/gcj/source/gnu/crypto/hash/RipeMD128.java \
# 	$(srcdir)/gcj/source/gnu/crypto/hash/RipeMD160.java \
# 	$(srcdir)/gcj/source/gnu/crypto/hash/Sha160.java \
# 	$(srcdir)/gcj/source/gnu/crypto/hash/Whirlpool.java $(srcdir)/gcj/source/Makefile.am \
# 	$(srcdir)/gcj/source/Makefile.in
EXTRA_DIST = \
	$(srcdir)/autogen.sh \
	$(srcdir)/etc/gnu-crypto-2.1.0-1-rh.spec $(srcdir)/etc/gnu-crypto.mf.in \
	$(srcdir)/.ant.properties $(srcdir)/gnu-crypto.m4 $(srcdir)/meta/serpent.m4

# if WITH_JCE
#   JCE_SUBDIR = jce
# else
#   JCE_SUBDIR =
# endif

# if WITH_SASL
#   SASL_SUBDIR = security
# else
#   SASL_SUBDIR =
# endif

# if WITH_JGSS
#   JGSS_SUBDIR = jgss
# else
#   JGSS_SUBDIR =
# endif

SUBDIRS = source docs

# test vector generation targets ----------------------------------------------

test-vectors: 
	$(MAKE) -C source test-vectors

# tools -----------------------------------------------------------------------
speed:
	$(MAKE) -C source speed

ent:
	$(MAKE) -C source ent

check-pkits:
	$(MAKE) -C source check-pkits

# -----------------------------------------------------------------------------

bin-dist: gnu-crypto-$(VERSION)-bin.tar.gz gnu-crypto-$(VERSION)-bin.tar.bz2 gnu-crypto-$(VERSION)-bin.zip

gnu-crypto-$(VERSION)-bin.tar.gz gnu-crypto-$(VERSION)-bin.tar.bz2 gnu-crypto-$(VERSION)-bin.zip: BUILD
	$(MAKE) -C source
	$(MAKE) -C docs pdf
	makeinfo $(srcdir)/docs/gnu-crypto.texi
	-rm -rf gnu-crypto-$(VERSION)-bin
	mkdir gnu-crypto-$(VERSION)-bin
	cp $(srcdir)/README.bin gnu-crypto-$(VERSION)-bin/README
	cp $(srcdir)/COPYING BUILD gnu-crypto-$(VERSION)-bin
	mkdir gnu-crypto-$(VERSION)-bin/lib
	cp source/*.jar gnu-crypto-$(VERSION)-bin/lib
	mkdir gnu-crypto-$(VERSION)-bin/doc
	cp docs/gnu-crypto.pdf gnu-crypto-$(VERSION)-bin/doc
	mkdir gnu-crypto-$(VERSION)-bin/info
	cp gnu-crypto.info gnu-crypto-$(VERSION)-bin/info
	tar -chf - gnu-crypto-$(VERSION)-bin | gzip --best -c > gnu-crypto-$(VERSION)-bin.tar.gz
	tar -chf - gnu-crypto-$(VERSION)-bin | bzip2 -9 -c > gnu-crypto-$(VERSION)-bin.tar.bz2
	zip -r gnu-crypto-$(VERSION)-bin.zip gnu-crypto-$(VERSION)-bin
	rm -rf gnu-crypto-$(VERSION)-bin

BUILD: .FORCE
	echo Built on $(shell date) > BUILD
	echo Compiler: >> BUILD
	$(JAVAC) -version | sed 's/^/> /' >> BUILD
	echo -n "Compilation flags: " >> BUILD
	echo $(JAVACFLAGS) >> BUILD
	echo -n "Build machine: " >> BUILD
	uname -a >> BUILD

.FORCE:

dist-prepare: dist bin-dist
	@for f in gnu-crypto-$(VERSION).tar.gz gnu-crypto-$(VERSION).tar.bz2 gnu-crypto-$(VERSION).zip gnu-crypto-$(VERSION)-bin.tar.gz gnu-crypto-$(VERSION)-bin.tar.bz2 gnu-crypto-$(VERSION)-bin.zip; do echo $$f...; gpg -b $$f; echo gnu-crypto > $$f.directive; gpg --clearsign $$f.directive; mv $$f.directive.asc $$f.directive; done