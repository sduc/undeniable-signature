## Process this file with automake to produce Makefile.in.
#
# -----------------------------------------------------------------------------
# $Id: Makefile.am,v 1.30.2.2 2005/10/06 14:24:55 rsdio Exp $
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
# $Revision: 1.30.2.2 $
#

# Variables -------------------------------------------------------------------
# JCE_JAR = @JCE_JAR@
# SECURITY_JAR = @SECURITY_JAR@

EXTRA_DIST = $(main_sources) $(mauve_sources) $(pkits_mauve_sources) \
	$(der_sources) $(test_certificates) $(test_crls) \
	$(getopt_bundles) $(keytool_bundles) $(security_bundles) \
	gnu/crypto/Registry.java.in gnu/testlet/config.java.in \
	gnu/crypto/assembly/package.html gnu/crypto/cipher/package.html \
	gnu/crypto/hash/package.html gnu/crypto/key/package.html \
	gnu/crypto/keyring/package.html gnu/crypto/mac/package.html \
	gnu/crypto/mode/package.html gnu/crypto/pad/package.html \
	gnu/crypto/prng/package.html gnu/crypto/sasl/package.html \
	gnu/crypto/sasl/srp/package.html gnu/crypto/sig/package.html \
	overview.html choose-classes

JAVAC = @JAVAC@
JAVACFLAGS = @JAVACFLAGS@
JAVA = @JAVA@
JAVAFLAGS = @JAVAFLAGS@
JAR = @JAR@
CLASSPATH = @CLASSPATH@
MAUVEFLAGS = 
PS = @PATH_SEPARATOR@

main_jar = gnu-crypto.jar
der_jar = gnu-crypto-der.jar

BUILT_SOURCES = $(main_jar) $(der_jar)
data_DATA = $(main_jar) $(der_jar)

# if FOUND_JCE_JAR
# if FOUND_SECURITY_JAR
#   extra_classpath = $(CLASSPATH)$(PS)$(JCE_JAR)$(PS)$(SECURITY_JAR)
# else
#   extra_classpath = $(CLASSPATH)$(PS)$(JCE_JAR)
# endif # FOUND_SECURITY_JAR
# else
extra_classpath = $(CLASSPATH)
# endif # FOUND_JCE_JAR


# library source files --------------------------------------------------------
# JCE adapter and test classes; defined iff configured with-jce-jar
# if FOUND_JCE_JAR
jce_sources = \
	gnu/crypto/jce/GnuCrypto.java \
	gnu/crypto/jce/PBKDF2SecretKeyFactory.java \
	\
	gnu/crypto/jce/cipher/AESSpi.java \
	gnu/crypto/jce/cipher/ARCFourSpi.java \
	gnu/crypto/jce/cipher/AnubisSpi.java \
	gnu/crypto/jce/cipher/BlowfishSpi.java \
	gnu/crypto/jce/cipher/Cast5Spi.java \
	gnu/crypto/jce/cipher/CipherAdapter.java \
	gnu/crypto/jce/cipher/DESSpi.java \
	gnu/crypto/jce/cipher/KhazadSpi.java \
	gnu/crypto/jce/cipher/NullCipherSpi.java \
	gnu/crypto/jce/cipher/PBES2.java \
	gnu/crypto/jce/cipher/RijndaelSpi.java \
	gnu/crypto/jce/cipher/SerpentSpi.java \
	gnu/crypto/jce/cipher/SquareSpi.java \
	gnu/crypto/jce/cipher/TripleDESSpi.java \
	gnu/crypto/jce/cipher/TwofishSpi.java \
	\
	gnu/crypto/jce/key/AnubisKeyGeneratorImpl.java \
	gnu/crypto/jce/key/AnubisSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/BlowfishKeyGeneratorImpl.java \
	gnu/crypto/jce/key/BlowfishSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/Cast5KeyGeneratorImpl.java \
	gnu/crypto/jce/key/Cast5SecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/DESKeyGeneratorImpl.java \
	gnu/crypto/jce/key/DESSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/DESedeSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/KhazadKeyGeneratorImpl.java \
	gnu/crypto/jce/key/KhazadSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/RijndaelKeyGeneratorImpl.java \
	gnu/crypto/jce/key/RijndaelSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/SecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/SecretKeyGeneratorImpl.java \
	gnu/crypto/jce/key/SerpentKeyGeneratorImpl.java \
	gnu/crypto/jce/key/SerpentSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/SquareKeyGeneratorImpl.java \
	gnu/crypto/jce/key/SquareSecretKeyFactoryImpl.java \
	gnu/crypto/jce/key/TripleDESKeyGeneratorImpl.java \
	gnu/crypto/jce/key/TwofishKeyGeneratorImpl.java \
	gnu/crypto/jce/key/TwofishSecretKeyFactoryImpl.java \
	\
	gnu/crypto/jce/mac/HMacHavalSpi.java \
	gnu/crypto/jce/mac/HMacMD2Spi.java \
	gnu/crypto/jce/mac/HMacMD4Spi.java \
	gnu/crypto/jce/mac/HMacMD5Spi.java \
	gnu/crypto/jce/mac/HMacRipeMD128Spi.java \
	gnu/crypto/jce/mac/HMacRipeMD160Spi.java \
	gnu/crypto/jce/mac/HMacSHA160Spi.java \
	gnu/crypto/jce/mac/HMacSHA256Spi.java \
	gnu/crypto/jce/mac/HMacSHA384Spi.java \
	gnu/crypto/jce/mac/HMacSHA512Spi.java \
	gnu/crypto/jce/mac/HMacTigerSpi.java \
	gnu/crypto/jce/mac/HMacWhirlpoolSpi.java \
	gnu/crypto/jce/mac/MacAdapter.java \
	gnu/crypto/jce/mac/OMacAnubisImpl.java \
	gnu/crypto/jce/mac/OMacBlowfishImpl.java \
	gnu/crypto/jce/mac/OMacCast5Impl.java \
	gnu/crypto/jce/mac/OMacDESImpl.java \
	gnu/crypto/jce/mac/OMacImpl.java \
	gnu/crypto/jce/mac/OMacKhazadImpl.java \
	gnu/crypto/jce/mac/OMacRijndaelImpl.java \
	gnu/crypto/jce/mac/OMacSerpentImpl.java \
	gnu/crypto/jce/mac/OMacSquareImpl.java \
	gnu/crypto/jce/mac/OMacTripleDESImpl.java \
	gnu/crypto/jce/mac/OMacTwofishImpl.java \
	gnu/crypto/jce/mac/TMMH16Spi.java \
	gnu/crypto/jce/mac/UHash32Spi.java \
	gnu/crypto/jce/mac/UMac32Spi.java \
	\
	gnu/crypto/jce/params/BlockCipherParameters.java \
	gnu/crypto/jce/params/DEREncodingException.java \
	gnu/crypto/jce/params/DERReader.java \
	gnu/crypto/jce/params/DERWriter.java \
	\
	gnu/crypto/jce/spec/BlockCipherParameterSpec.java \
	gnu/crypto/jce/spec/TMMHParameterSpec.java \
	gnu/crypto/jce/spec/UMac32ParameterSpec.java

jce_mauve_sources = \
	gnu/testlet/gnu/crypto/jce/TestOfCipher.java \
	gnu/testlet/gnu/crypto/jce/TestOfMac.java
# else # no JCE framework available or needed
# jce_sources =
# jce_mauve_sources =
# endif # FOUND_JCE_JAR

# if FOUND_SECURITY_JAR
security_sources = \
	gnu/crypto/auth/callback/AWTCallbackHandler.java \
	gnu/crypto/auth/callback/AbstractCallbackHandler.java \
	gnu/crypto/auth/callback/ConsoleCallbackHandler.java \
	gnu/crypto/auth/callback/DefaultCallbackHandler.java \
	gnu/crypto/auth/callback/Engine.java \
	gnu/crypto/auth/callback/GnuCallbacks.java \
	\
	gnu/crypto/jce/GnuSasl.java \
	\
	gnu/crypto/sasl/AuthInfo.java \
	gnu/crypto/sasl/AuthInfoProviderFactory.java \
	gnu/crypto/sasl/ClientFactory.java \
	gnu/crypto/sasl/ClientMechanism.java \
	gnu/crypto/sasl/ConfidentialityException.java \
	gnu/crypto/sasl/IAuthInfoProvider.java \
	gnu/crypto/sasl/IAuthInfoProviderFactory.java \
	gnu/crypto/sasl/IllegalMechanismStateException.java \
	gnu/crypto/sasl/InputBuffer.java \
	gnu/crypto/sasl/IntegrityException.java \
	gnu/crypto/sasl/NoSuchMechanismException.java \
	gnu/crypto/sasl/NoSuchUserException.java \
	gnu/crypto/sasl/OutputBuffer.java \
	gnu/crypto/sasl/SaslEncodingException.java \
	gnu/crypto/sasl/SaslInputStream.java \
	gnu/crypto/sasl/SaslOutputStream.java \
	gnu/crypto/sasl/SaslUtil.java \
	gnu/crypto/sasl/ServerFactory.java \
	gnu/crypto/sasl/ServerMechanism.java \
	gnu/crypto/sasl/UserAlreadyExistsException.java \
	\
	gnu/crypto/sasl/anonymous/AnonymousClient.java \
	gnu/crypto/sasl/anonymous/AnonymousServer.java \
	gnu/crypto/sasl/anonymous/AnonymousUtil.java \
	\
	gnu/crypto/sasl/crammd5/CramMD5AuthInfoProvider.java \
	gnu/crypto/sasl/crammd5/CramMD5Client.java \
	gnu/crypto/sasl/crammd5/CramMD5Registry.java \
	gnu/crypto/sasl/crammd5/CramMD5Server.java \
	gnu/crypto/sasl/crammd5/CramMD5Util.java \
	gnu/crypto/sasl/crammd5/PasswordFile.java \
	\
	gnu/crypto/sasl/plain/PasswordFile.java \
	gnu/crypto/sasl/plain/PlainAuthInfoProvider.java \
	gnu/crypto/sasl/plain/PlainClient.java \
	gnu/crypto/sasl/plain/PlainRegistry.java \
	gnu/crypto/sasl/plain/PlainServer.java \
	\
	gnu/crypto/sasl/srp/CALG.java \
	gnu/crypto/sasl/srp/ClientStore.java \
	gnu/crypto/sasl/srp/IALG.java \
	gnu/crypto/sasl/srp/KDF.java \
	gnu/crypto/sasl/srp/PasswordFile.java \
	gnu/crypto/sasl/srp/SecurityContext.java \
	gnu/crypto/sasl/srp/ServerStore.java \
	gnu/crypto/sasl/srp/SRP.java \
	gnu/crypto/sasl/srp/SRPAuthInfoProvider.java \
	gnu/crypto/sasl/srp/SRPClient.java \
	gnu/crypto/sasl/srp/SRPRegistry.java \
	gnu/crypto/sasl/srp/SRPServer.java \
	gnu/crypto/sasl/srp/StoreEntry.java \
	\
	gnu/crypto/tool/SaslConnection.java \
	gnu/crypto/tool/SimpleCallbackHandler.java

sasl_mauve_sources = \
	gnu/testlet/gnu/crypto/sasl/TestOfClientFactory.java \
	gnu/testlet/gnu/crypto/sasl/TestOfServerFactory.java \
	\
	gnu/testlet/gnu/crypto/sasl/srp/TestOfSRPAuthInfoProvider.java \
	gnu/testlet/gnu/crypto/sasl/srp/TestOfSRPPasswordFile.java \
	gnu/testlet/gnu/crypto/sasl/srp/TestOfSRPPrimitives.java

security_bundles = \
	gnu/crypto/auth/callback/MessagesBundle.properties

keytool_sources = \
	gnu/crypto/tool/keytool/Command.java \
	gnu/crypto/tool/keytool/Delete.java \
	gnu/crypto/tool/keytool/Export.java \
	gnu/crypto/tool/keytool/GenKey.java \
	gnu/crypto/tool/keytool/Import.java \
	gnu/crypto/tool/keytool/List.java \
	gnu/crypto/tool/keytool/Main.java \
	gnu/crypto/tool/keytool/Path.java \
	gnu/crypto/tool/keytool/Revoke.java \
	gnu/crypto/tool/keytool/Sign.java \
	gnu/crypto/tool/keytool/Trust.java

keytool_bundles = \
	gnu/crypto/tool/keytool/MessagesBundle.properties
# else # no javax.security found or needed
# sasl_sources =
# security_mauve_sources =
# security_bundles =
# keytool_sources =
# keytool_bundles =
# endif # FOUND_SECURITY_JAR

if WITH_INCLUDED_GETOPT
getopt_sources = \
	gnu/getopt/Getopt.java \
	gnu/getopt/LongOpt.java
getopt_bundles = \
	gnu/getopt/MessagesBundle.properties \
	gnu/getopt/MessagesBundle_cs.properties \
	gnu/getopt/MessagesBundle_de.properties \
	gnu/getopt/MessagesBundle_fr.properties \
	gnu/getopt/MessagesBundle_nl.properties \
	gnu/getopt/MessagesBundle_no.properties
else
getopt_sources =
getopt_bundles =
endif

generated_sources = gnu/crypto/Registry.java

main_sources = \
	gnu/crypto/Properties.java \
	\
	gnu/crypto/assembly/Assembly.java \
	gnu/crypto/assembly/Cascade.java \
	gnu/crypto/assembly/CascadeStage.java \
	gnu/crypto/assembly/CascadeTransformer.java \
	gnu/crypto/assembly/DeflateTransformer.java \
	gnu/crypto/assembly/Direction.java \
	gnu/crypto/assembly/LoopbackTransformer.java \
	gnu/crypto/assembly/ModeStage.java \
	gnu/crypto/assembly/Operation.java \
	gnu/crypto/assembly/PaddingTransformer.java \
	gnu/crypto/assembly/Stage.java \
	gnu/crypto/assembly/Transformer.java \
	gnu/crypto/assembly/TransformerException.java \
	\
	gnu/crypto/auth/Password.java \
	\
	gnu/crypto/cipher/Anubis.java \
	gnu/crypto/cipher/BaseCipher.java \
	gnu/crypto/cipher/Blowfish.java \
	gnu/crypto/cipher/Cast5.java \
	gnu/crypto/cipher/CipherFactory.java \
	gnu/crypto/cipher/DES.java \
	gnu/crypto/cipher/IBlockCipher.java \
	gnu/crypto/cipher/IBlockCipherSpi.java \
	gnu/crypto/cipher/Khazad.java \
	gnu/crypto/cipher/NullCipher.java \
	gnu/crypto/cipher/Rijndael.java \
	gnu/crypto/cipher/Serpent.java \
	gnu/crypto/cipher/Square.java \
	gnu/crypto/cipher/TripleDES.java \
	gnu/crypto/cipher/Twofish.java \
	gnu/crypto/cipher/WeakKeyException.java \
	\
	gnu/crypto/exp/ust/UST.java \
	\
	gnu/crypto/hash/BaseHash.java \
	gnu/crypto/hash/HashFactory.java \
	gnu/crypto/hash/Haval.java \
	gnu/crypto/hash/IMessageDigest.java \
	gnu/crypto/hash/MD2.java \
	gnu/crypto/hash/MD4.java \
	gnu/crypto/hash/MD5.java \
	gnu/crypto/hash/RipeMD128.java \
	gnu/crypto/hash/RipeMD160.java \
	gnu/crypto/hash/Sha160.java \
	gnu/crypto/hash/Sha256.java \
	gnu/crypto/hash/Sha384.java \
	gnu/crypto/hash/Sha512.java \
	gnu/crypto/hash/Tiger.java \
	gnu/crypto/hash/Whirlpool.java \
	\
	gnu/crypto/jce/GnuSecurity.java \
	\
	gnu/crypto/jce/hash/HavalSpi.java \
	gnu/crypto/jce/hash/MD2Spi.java \
	gnu/crypto/jce/hash/MD4Spi.java \
	gnu/crypto/jce/hash/MD5Spi.java \
	gnu/crypto/jce/hash/MessageDigestAdapter.java \
	gnu/crypto/jce/hash/RipeMD128Spi.java \
	gnu/crypto/jce/hash/RipeMD160Spi.java \
	gnu/crypto/jce/hash/Sha160Spi.java \
	gnu/crypto/jce/hash/Sha256Spi.java \
	gnu/crypto/jce/hash/Sha384Spi.java \
	gnu/crypto/jce/hash/Sha512Spi.java \
	gnu/crypto/jce/hash/TigerSpi.java \
	gnu/crypto/jce/hash/WhirlpoolSpi.java \
	\
	gnu/crypto/jce/keyring/GnuKeyring.java \
	\
	gnu/crypto/jce/prng/ARCFourRandomSpi.java \
	gnu/crypto/jce/prng/CSPRNGSpi.java \
	gnu/crypto/jce/prng/HavalRandomSpi.java \
	gnu/crypto/jce/prng/ICMRandomSpi.java \
	gnu/crypto/jce/prng/MD2RandomSpi.java \
	gnu/crypto/jce/prng/MD4RandomSpi.java \
	gnu/crypto/jce/prng/MD5RandomSpi.java \
	gnu/crypto/jce/prng/RipeMD128RandomSpi.java \
	gnu/crypto/jce/prng/RipeMD160RandomSpi.java \
	gnu/crypto/jce/prng/SecureRandomAdapter.java \
	gnu/crypto/jce/prng/Sha160RandomSpi.java \
	gnu/crypto/jce/prng/Sha256RandomSpi.java \
	gnu/crypto/jce/prng/Sha384RandomSpi.java \
	gnu/crypto/jce/prng/Sha512RandomSpi.java \
	gnu/crypto/jce/prng/TigerRandomSpi.java \
	gnu/crypto/jce/prng/UMacRandomSpi.java \
	gnu/crypto/jce/prng/WhirlpoolRandomSpi.java \
	\
	gnu/crypto/jce/sig/DSSKeyPairGeneratorSpi.java \
	gnu/crypto/jce/sig/DSSRawSignatureSpi.java \
	gnu/crypto/jce/sig/KeyPairGeneratorAdapter.java \
	gnu/crypto/jce/sig/RSAKeyPairGeneratorSpi.java \
	gnu/crypto/jce/sig/RSAPSSRawSignatureSpi.java \
	gnu/crypto/jce/sig/SignatureAdapter.java \
	\
	gnu/crypto/key/BaseKeyAgreementParty.java \
	gnu/crypto/key/GnuSecretKey.java \
	gnu/crypto/key/IKeyAgreementParty.java \
	gnu/crypto/key/IKeyPairCodec.java \
	gnu/crypto/key/IKeyPairGenerator.java \
	gnu/crypto/key/IncomingMessage.java \
	gnu/crypto/key/KeyAgreementException.java \
	gnu/crypto/key/KeyAgreementFactory.java \
	gnu/crypto/key/KeyPairCodecFactory.java \
	gnu/crypto/key/KeyPairGeneratorFactory.java \
	gnu/crypto/key/OutgoingMessage.java \
	\
	gnu/crypto/key/dh/DHKeyPairRawCodec.java \
	gnu/crypto/key/dh/DiffieHellmanKeyAgreement.java \
	gnu/crypto/key/dh/DiffieHellmanReceiver.java \
	gnu/crypto/key/dh/DiffieHellmanSender.java \
	gnu/crypto/key/dh/ElGamalKeyAgreement.java \
	gnu/crypto/key/dh/ElGamalReceiver.java \
	gnu/crypto/key/dh/ElGamalSender.java \
	gnu/crypto/key/dh/GnuDHKey.java \
	gnu/crypto/key/dh/GnuDHKeyPairGenerator.java \
	gnu/crypto/key/dh/GnuDHPrivateKey.java \
	gnu/crypto/key/dh/GnuDHPublicKey.java \
	gnu/crypto/key/dh/RFC2631.java \
	\
	gnu/crypto/key/dss/DSSKey.java \
	gnu/crypto/key/dss/DSSKeyPairGenerator.java \
	gnu/crypto/key/dss/DSSKeyPairRawCodec.java \
	gnu/crypto/key/dss/DSSPrivateKey.java \
	gnu/crypto/key/dss/DSSPublicKey.java \
	gnu/crypto/key/dss/FIPS186.java \
	\
	gnu/crypto/key/rsa/GnuRSAKey.java \
	gnu/crypto/key/rsa/GnuRSAPrivateKey.java \
	gnu/crypto/key/rsa/GnuRSAPublicKey.java \
	gnu/crypto/key/rsa/RSAKeyPairGenerator.java \
	gnu/crypto/key/rsa/RSAKeyPairRawCodec.java \
	\
	gnu/crypto/key/srp6/SRP6Host.java \
	gnu/crypto/key/srp6/SRP6KeyAgreement.java \
	gnu/crypto/key/srp6/SRP6SaslClient.java \
	gnu/crypto/key/srp6/SRP6SaslServer.java \
	gnu/crypto/key/srp6/SRP6TLSClient.java \
	gnu/crypto/key/srp6/SRP6TLSServer.java \
	gnu/crypto/key/srp6/SRP6User.java \
	gnu/crypto/key/srp6/SRPAlgorithm.java \
	gnu/crypto/key/srp6/SRPKey.java \
	gnu/crypto/key/srp6/SRPKeyPairGenerator.java \
	gnu/crypto/key/srp6/SRPKeyPairRawCodec.java \
	gnu/crypto/key/srp6/SRPPrivateKey.java \
	gnu/crypto/key/srp6/SRPPublicKey.java \
	\
	gnu/crypto/keyring/AuthenticatedEntry.java \
	gnu/crypto/keyring/BaseKeyring.java \
	gnu/crypto/keyring/BinaryDataEntry.java \
	gnu/crypto/keyring/CertPathEntry.java \
	gnu/crypto/keyring/CertificateEntry.java \
	gnu/crypto/keyring/CompressedEntry.java \
	gnu/crypto/keyring/EncryptedEntry.java \
	gnu/crypto/keyring/Entry.java \
	gnu/crypto/keyring/EnvelopeEntry.java \
	gnu/crypto/keyring/GnuPrivateKeyring.java \
	gnu/crypto/keyring/GnuPublicKeyring.java \
	gnu/crypto/keyring/IKeyring.java \
	gnu/crypto/keyring/IPrivateKeyring.java \
	gnu/crypto/keyring/IPublicKeyring.java \
	gnu/crypto/keyring/MalformedKeyringException.java \
	gnu/crypto/keyring/MaskableEnvelopeEntry.java \
	gnu/crypto/keyring/MeteredInputStream.java \
	gnu/crypto/keyring/PasswordAuthenticatedEntry.java \
	gnu/crypto/keyring/PasswordEncryptedEntry.java \
	gnu/crypto/keyring/PasswordProtectedEntry.java \
	gnu/crypto/keyring/PrimitiveEntry.java \
	gnu/crypto/keyring/PrivateKeyEntry.java \
	gnu/crypto/keyring/Properties.java \
	gnu/crypto/keyring/PublicKeyEntry.java \
	\
	gnu/crypto/mac/BaseMac.java \
	gnu/crypto/mac/HMac.java \
	gnu/crypto/mac/HMacFactory.java \
	gnu/crypto/mac/IMac.java \
	gnu/crypto/mac/MacFactory.java \
	gnu/crypto/mac/MacInputStream.java \
	gnu/crypto/mac/MacOutputStream.java \
	gnu/crypto/mac/OMAC.java \
	gnu/crypto/mac/TMMH16.java \
	gnu/crypto/mac/UHash32.java \
	gnu/crypto/mac/UMac32.java \
	\
	gnu/crypto/mode/BaseMode.java \
	gnu/crypto/mode/CBC.java \
	gnu/crypto/mode/CFB.java \
	gnu/crypto/mode/CTR.java \
	gnu/crypto/mode/EAX.java \
	gnu/crypto/mode/ECB.java \
	gnu/crypto/mode/ICM.java \
	gnu/crypto/mode/IAuthenticatedMode.java \
	gnu/crypto/mode/IMode.java \
	gnu/crypto/mode/ModeFactory.java \
	gnu/crypto/mode/OFB.java \
	\
	gnu/crypto/pad/BasePad.java \
	gnu/crypto/pad/IPad.java \
	gnu/crypto/pad/PadFactory.java \
	gnu/crypto/pad/PKCS1_V1_5.java \
	gnu/crypto/pad/PKCS7.java \
	gnu/crypto/pad/SSL3.java \
	gnu/crypto/pad/TBC.java \
	gnu/crypto/pad/TLS1.java \
	gnu/crypto/pad/WrongPaddingException.java \
	\
	gnu/crypto/pki/GnuPKIExtension.java \
	gnu/crypto/pki/PolicyNodeImpl.java \
	gnu/crypto/pki/Util.java \
	gnu/crypto/pki/X500Name.java \
	gnu/crypto/pki/X509CRLEntryImpl.java \
	gnu/crypto/pki/X509CRLImpl.java \
	gnu/crypto/pki/X509CRLSelectorImpl.java \
	gnu/crypto/pki/X509CertPath.java \
	gnu/crypto/pki/X509CertSelectorImpl.java \
	gnu/crypto/pki/X509CertificateBuilder.java \
	gnu/crypto/pki/X509CertificateImpl.java \
	gnu/crypto/pki/ext/AuthorityKeyIdentifier.java \
	gnu/crypto/pki/ext/BasicConstraints.java \
	gnu/crypto/pki/ext/CRLNumber.java \
	gnu/crypto/pki/ext/CertificatePolicies.java \
	gnu/crypto/pki/ext/ExtendedKeyUsage.java \
	gnu/crypto/pki/ext/Extension.java \
	gnu/crypto/pki/ext/GeneralNames.java \
	gnu/crypto/pki/ext/IssuerAlternativeNames.java \
	gnu/crypto/pki/ext/KeyUsage.java \
	gnu/crypto/pki/ext/PolicyConstraint.java \
	gnu/crypto/pki/ext/PolicyMappings.java \
	gnu/crypto/pki/ext/PrivateKeyUsagePeriod.java \
	gnu/crypto/pki/ext/ReasonCode.java \
	gnu/crypto/pki/ext/SubjectAlternativeNames.java \
	gnu/crypto/pki/ext/SubjectKeyIdentifier.java \
	gnu/crypto/pki/io/Base64InputStream.java \
	gnu/crypto/pki/provider/CollectionCertStoreImpl.java \
	gnu/crypto/pki/provider/DSAParameters.java \
	gnu/crypto/pki/provider/DSASignature.java \
	gnu/crypto/pki/provider/EncodedKeyFactory.java \
	gnu/crypto/pki/provider/GnuDHPublicKey.java \
	gnu/crypto/pki/provider/GnuDSAPrivateKey.java \
	gnu/crypto/pki/provider/GnuDSAPublicKey.java \
	gnu/crypto/pki/provider/GnuPki.java \
	gnu/crypto/pki/provider/GnuRSAPrivateKey.java \
	gnu/crypto/pki/provider/GnuRSAPublicKey.java \
	gnu/crypto/pki/provider/MD2withRSA.java \
	gnu/crypto/pki/provider/MD4withRSA.java \
	gnu/crypto/pki/provider/MD5withRSA.java \
	gnu/crypto/pki/provider/PKIXCertPathValidator.java \
	gnu/crypto/pki/provider/RSA.java \
	gnu/crypto/pki/provider/RSAKeyFactory.java \
	gnu/crypto/pki/provider/SHA1withRSA.java \
	gnu/crypto/pki/provider/X509CertificateFactory.java \
	\
	gnu/crypto/prng/ARCFour.java \
	gnu/crypto/prng/BasePRNG.java \
	gnu/crypto/prng/CSPRNG.java \
	gnu/crypto/prng/EntropySource.java \
	gnu/crypto/prng/Fortuna.java \
	gnu/crypto/prng/ICMGenerator.java \
	gnu/crypto/prng/IPBE.java \
	gnu/crypto/prng/IRandom.java \
	gnu/crypto/prng/LimitReachedException.java \
	gnu/crypto/prng/MDGenerator.java \
	gnu/crypto/prng/PBKDF2.java \
	gnu/crypto/prng/PRNGFactory.java \
	gnu/crypto/prng/RandomEvent.java \
	gnu/crypto/prng/RandomEventListener.java \
	gnu/crypto/prng/UMacGenerator.java \
	\
	gnu/crypto/sig/BaseSignature.java \
	gnu/crypto/sig/ISignature.java \
	gnu/crypto/sig/ISignatureCodec.java \
	gnu/crypto/sig/SignatureFactory.java \
	\
	gnu/crypto/sig/dss/DSSSignature.java \
	gnu/crypto/sig/dss/DSSSignatureRawCodec.java \
	\
	gnu/crypto/sig/rsa/EME_PKCS1_V1_5.java \
	gnu/crypto/sig/rsa/EMSA_PKCS1_V1_5.java \
	gnu/crypto/sig/rsa/EMSA_PSS.java \
	gnu/crypto/sig/rsa/RSA.java \
	gnu/crypto/sig/rsa/RSAPKCS1V1_5Signature.java \
	gnu/crypto/sig/rsa/RSAPSSSignature.java \
	gnu/crypto/sig/rsa/RSAPSSSignatureRawCodec.java \
	\
	gnu/crypto/util/Base64.java \
	gnu/crypto/util/ExpirableObject.java \
	gnu/crypto/util/Prime.java \
	gnu/crypto/util/PRNG.java \
	gnu/crypto/util/Sequence.java \
	gnu/crypto/util/SimpleList.java \
	gnu/crypto/util/Util.java \
	\
	$(jce_sources) \
	$(security_sources) \
	$(getopt_sources) \
	$(keytool_sources)

generated_mauve_sources = gnu/testlet/config.java

mauve_sources = \
	gnu/testlet/ResourceNotFoundException.java \
	gnu/testlet/SimpleTestHarness.java \
	gnu/testlet/TestHarness.java \
	gnu/testlet/Testlet.java \
	\
	gnu/testlet/gnu/crypto/assembly/TestOfAssembly.java \
	gnu/testlet/gnu/crypto/assembly/TestOfCascade.java \
	\
	gnu/testlet/gnu/crypto/cipher/BaseCipherTestCase.java \
	gnu/testlet/gnu/crypto/cipher/TestOfAnubis.java \
	gnu/testlet/gnu/crypto/cipher/TestOfBlowfish.java \
	gnu/testlet/gnu/crypto/cipher/TestOfCast5.java \
	gnu/testlet/gnu/crypto/cipher/TestOfCipherFactory.java \
	gnu/testlet/gnu/crypto/cipher/TestOfDES.java \
	gnu/testlet/gnu/crypto/cipher/TestOfKhazad.java \
	gnu/testlet/gnu/crypto/cipher/TestOfNistVectors.java \
	gnu/testlet/gnu/crypto/cipher/TestOfNullCipher.java \
	gnu/testlet/gnu/crypto/cipher/TestOfRijndael.java \
	gnu/testlet/gnu/crypto/cipher/TestOfSerpent.java \
	gnu/testlet/gnu/crypto/cipher/TestOfSquare.java \
	gnu/testlet/gnu/crypto/cipher/TestOfTripleDES.java \
	gnu/testlet/gnu/crypto/cipher/TestOfTwofish.java \
	\
	gnu/testlet/gnu/crypto/exp/TestOfUST.java \
	\
	gnu/testlet/gnu/crypto/hash/TestOfHashFactory.java \
	gnu/testlet/gnu/crypto/hash/TestOfHaval.java \
	gnu/testlet/gnu/crypto/hash/TestOfMD2.java \
	gnu/testlet/gnu/crypto/hash/TestOfMD4.java \
	gnu/testlet/gnu/crypto/hash/TestOfMD5.java \
	gnu/testlet/gnu/crypto/hash/TestOfRipeMD128.java \
	gnu/testlet/gnu/crypto/hash/TestOfRipeMD160.java \
	gnu/testlet/gnu/crypto/hash/TestOfTiger.java \
	gnu/testlet/gnu/crypto/hash/TestOfSha160.java \
	gnu/testlet/gnu/crypto/hash/TestOfSha256.java \
	gnu/testlet/gnu/crypto/hash/TestOfSha384.java \
	gnu/testlet/gnu/crypto/hash/TestOfSha512.java \
	gnu/testlet/gnu/crypto/hash/TestOfWhirlpool.java \
	\
	gnu/testlet/gnu/crypto/jce/TestOfKeyPairGenerator.java \
	gnu/testlet/gnu/crypto/jce/TestOfMessageDigest.java \
	gnu/testlet/gnu/crypto/jce/TestOfProvider.java \
	gnu/testlet/gnu/crypto/jce/TestOfSignature.java \
	\
	gnu/testlet/gnu/crypto/key/TestOfKeyPairGeneratorFactory.java \
	\
	gnu/testlet/gnu/crypto/key/dh/TestOfDHCodec.java \
	gnu/testlet/gnu/crypto/key/dh/TestOfDHKeyAgreements.java \
	gnu/testlet/gnu/crypto/key/dh/TestOfDHKeyGeneration.java \
	\
	gnu/testlet/gnu/crypto/key/dss/TestOfDSSCodec.java \
	gnu/testlet/gnu/crypto/key/dss/TestOfDSSKeyGeneration.java \
	\
	gnu/testlet/gnu/crypto/key/rsa/TestOfRSACodec.java \
	gnu/testlet/gnu/crypto/key/rsa/TestOfRSAKeyGeneration.java \
	\
	gnu/testlet/gnu/crypto/key/srp6/TestOfSRP6KeyAgreements.java \
	gnu/testlet/gnu/crypto/key/srp6/TestOfSRPCodec.java \
	gnu/testlet/gnu/crypto/key/srp6/TestOfSRPKeyGeneration.java \
	\
	gnu/testlet/gnu/crypto/keyring/TestOfPrivateKeyring.java \
	gnu/testlet/gnu/crypto/keyring/TestOfPublicKeyring.java \
	\
	gnu/testlet/gnu/crypto/mac/TestOfHMac.java \
	gnu/testlet/gnu/crypto/mac/TestOfHMacFactory.java \
	gnu/testlet/gnu/crypto/mac/TestOfHMacMD5.java \
	gnu/testlet/gnu/crypto/mac/TestOfHMacSha1.java \
	gnu/testlet/gnu/crypto/mac/TestOfMacFactory.java \
	gnu/testlet/gnu/crypto/mac/TestOfOMAC.java \
	gnu/testlet/gnu/crypto/mac/TestOfTMMH16.java \
	\
	gnu/testlet/gnu/crypto/mode/TestOfCBC.java \
	gnu/testlet/gnu/crypto/mode/TestOfCFB.java \
	gnu/testlet/gnu/crypto/mode/TestOfECB.java \
	gnu/testlet/gnu/crypto/mode/TestOfEAX.java \
	gnu/testlet/gnu/crypto/mode/TestOfModeFactory.java \
	gnu/testlet/gnu/crypto/mode/TestOfOFB.java \
	\
	gnu/testlet/gnu/crypto/pad/TestOfPadFactory.java \
	gnu/testlet/gnu/crypto/pad/TestOfPKCS7.java \
	gnu/testlet/gnu/crypto/pad/TestOfTBC.java \
	\
	gnu/testlet/gnu/crypto/prng/TestOfARCFour.java \
	gnu/testlet/gnu/crypto/prng/TestOfICMGenerator.java \
	gnu/testlet/gnu/crypto/prng/TestOfPBKDF2.java \
	gnu/testlet/gnu/crypto/prng/TestOfPRNGFactory.java \
	\
	gnu/testlet/gnu/crypto/sig/TestOfSignatureFactory.java \
	\
	gnu/testlet/gnu/crypto/sig/dss/TestOfDSSSignature.java \
	gnu/testlet/gnu/crypto/sig/dss/TestOfDSSSignatureCodec.java \
	\
	gnu/testlet/gnu/crypto/sig/rsa/TestOfRSAPKCS1V1_5Signature.java \
	gnu/testlet/gnu/crypto/sig/rsa/TestOfRSAPSSSignature.java \
	gnu/testlet/gnu/crypto/sig/rsa/TestOfRSASignatureCodec.java \
	\
	$(jce_mauve_sources) \
	$(security_mauve_sources)

pkits_generated_mauve_sources = \
	gnu/testlet/config.java

pkits_mauve_sources = \
	gnu/testlet/ResourceNotFoundException.java \
	gnu/testlet/SimpleTestHarness.java \
	gnu/testlet/TestHarness.java \
	gnu/testlet/Testlet.java \
	\
	gnu/testlet/gnu/crypto/pki/pkits/BaseInvalidTest.java \
	gnu/testlet/gnu/crypto/pki/pkits/BaseValidTest.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidBadCRLIssuerNameTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidBadCRLSignatureTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidBasicSelfIssuedCRLSigningKeyTest7.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidBasicSelfIssuedCRLSigningKeyTest8.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidBasicSelfIssuedNewWithOldTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidBasicSelfIssuedOldWithNewTest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidCASignatureTest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidCAnotAfterDateTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidCAnotBeforeDateTest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidDSASignatureTest6.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidEESignatureTest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidEEnotAfterDateTest6.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidEEnotBeforeDateTest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidLongSerialNumberTest18.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidMissingbasicConstraintsTest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidNameChainingEETest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidNameChainingOrderTest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidNegativeSerialNumberTest15.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidOldCRLnextUpdateTest11.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidRevokedCATest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidRevokedEETest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidSelfIssuedpathLenConstraintTest16.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidUnknownCRLEntryExtensionTest8.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidUnknownCRLExtensionTest10.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidUnknownCRLExtensionTest9.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidWrongCRLTest6.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidcAFalseTest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidcAFalseTest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidkeyUsageCriticalcRLSignFalseTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidkeyUsageCriticalkeyCertSignFalseTest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidkeyUsageNotCriticalcRLSignFalseTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidkeyUsageNotCriticalkeyCertSignFalseTest2.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidpathLenConstraintTest10.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidpathLenConstraintTest11.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidpathLenConstraintTest12.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidpathLenConstraintTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidpathLenConstraintTest6.java \
	gnu/testlet/gnu/crypto/pki/pkits/InvalidpathLenConstraintTest9.java \
	gnu/testlet/gnu/crypto/pki/pkits/Invalidpre2000CRLnextUpdateTest12.java \
	gnu/testlet/gnu/crypto/pki/pkits/Invalidpre2000UTCEEnotAfterDateTest7.java \
	gnu/testlet/gnu/crypto/pki/pkits/MissingCRLTest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/PKITS.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidBasicSelfIssuedCRLSigningKeyTest6.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidBasicSelfIssuedNewWithOldTest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidBasicSelfIssuedNewWithOldTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidBasicSelfIssuedOldWithNewTest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidDSAParameterInheritenceTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidDSASignaturesTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidGeneralizedTimeCRLnextUpdateTest13.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidGeneralizedTimenotAfterDateTest8.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidGeneralizedTimenotBeforeDateTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidLongSerialNumberTest16.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidLongSerialNumberTest17.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidNameChainingCapitalizationTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidNameChainingUIDsTest6.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidNameChainingWhitespaceTest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidNameChainingWhitespaceTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidNegativeSerialNumberTest14.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidRFC3280MandatoryAttributeTypesTest7.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidRFC3280OptionalAttributeTypesTest8.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidRolloverfromPrintableStringtoUTF8StringTest10.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidSelfIssuedpathLenConstraintTest15.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidSelfIssuedpathLenConstraintTest17.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidSignaturesTest1.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidTwoCRLsTest7.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidUTF8StringCaseInsensitiveMatchTest11.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidUTF8StringEncodedNamesTest9.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidbasicConstraintsNotCriticalTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidkeyUsageNotCriticalTest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidpathLenConstraintTest13.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidpathLenConstraintTest14.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidpathLenConstraintTest7.java \
	gnu/testlet/gnu/crypto/pki/pkits/ValidpathLenConstraintTest8.java \
	gnu/testlet/gnu/crypto/pki/pkits/Validpre2000UTCnotBeforeDateTest3.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePolicyTest1_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePolicyTest1_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePolicyTest1_3.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePolicyTest1_4.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePolicyTest2_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePolicyTest2_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest3_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest3_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest3_3.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest4.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest5.java \
	gnu/testlet/gnu/crypto/pki/pkits/OverlappingPoliciesTest6_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/OverlappingPoliciesTest6_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/OverlappingPoliciesTest6_3.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest7.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest8.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest9.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePoliciesTest10_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePoliciesTest10_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePoliciesTest10_3.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesAnyPolicyTest11_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesAnyPolicyTest11_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/DifferentPoliciesTest12.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePoliciesTest13_1.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePoliciesTest13_2.java \
	gnu/testlet/gnu/crypto/pki/pkits/AllCertificatesSamePoliciesTest13_3.java

test_certificates = \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BadnotBeforeDateCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UTF8StringCaseInsensitiveMatchCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/deltaCRLCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/deltaCRLCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidGeneralizedTimenotBeforeDateTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/NoissuingDistributionPointCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/keyUsageNotCriticalCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest12EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitPolicyMappingTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/AllCertificatesanyPolicyTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidURInameConstraintsTest35EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/GoodsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP123CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/basicConstraintsCriticalcAFalseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNandRFC822nameConstraintsTest29EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/GeneralizedTimeCRLnextUpdateCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidpathLenConstraintTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlySomeReasonsCA4Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidSelfIssuedpathLenConstraintTest17EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/basicConstraintsNotCriticalcAFalseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint0CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidBadCRLIssuerNameTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedinhibitPolicyMappingTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitPolicyMappingTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitAnyPolicyTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidNoissuingDistributionPointTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidNameUIDsTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/OldCRLnextUpdateCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlyContainsCACertsCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidMissingCRLTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest18EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddistributionPointTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDSASignatureTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy0CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidonlyContainsCACertsTest13EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitPolicyMappingTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/WrongCRLCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedinhibitAnyPolicyTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UserNoticeQualifierTest18EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidRevokedCATest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP12CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddeltaCRLTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/RFC3280OptionalAttributeTypesCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP1234subCAP123Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidinhibitPolicyMappingTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/P1Mapping1to234subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlyContainsAttributeCertsTest14EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcAFalseTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidSelfIssuedinhibitPolicyMappingTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidSelfIssuedpathLenConstraintTest15EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitAnyPolicyTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidIDPwithindirectCRLTest25EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidTwoCRLsTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy4CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedinhibitPolicyMappingTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidSelfIssuedinhibitAnyPolicyTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping5subsubsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsRFC822CA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UserNoticeQualifierTest19EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DSAParametersInheritedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNSnameConstraintsTest38EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidMappingFromanyPolicyTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BasicSelfIssuedCRLSigningKeyCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P12subCAIPM5Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlySomeReasonsTest15EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddistributionPointTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidRFC822nameConstraintsTest22EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P1subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidrequireExplicitPolicyTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/Invalidpre2000UTCEEnotAfterDateTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BasicSelfIssuedNewKeyOldWithNewCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedrequireExplicitPolicyTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy4subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidBasicSelfIssuedCRLSigningKeyTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1subCAIAP5Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidNameChainingOrderTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA4Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy2subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BadnotAfterDateCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy4subsubsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy10CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcRLIssuerTest31EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedinhibitPolicyMappingTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UserNoticeQualifierTest17EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidNameChainingWhitespaceTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidinhibitPolicyMappingTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/AllCertificatesSamePoliciesTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/keyUsageCriticalkeyCertSignFalseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidrequireExplicitPolicyTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping5CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint0subCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddeltaCRLTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddistributionPointTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy7CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidbasicConstraintsNotCriticalTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidpathLenConstraintTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidRFC3280OptionalAttributeTypesTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddeltaCRLTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN1SelfIssuedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy0subsubsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BadCRLSignatureCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/AllCertificatesSamePoliciesTest13EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlySomeReasonsTest20EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidCertificatePathTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN3subCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/distributionPoint2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidNameChainingTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN1subCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1subCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNSnameConstraintsTest33EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcRLIssuerTest34EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidCAnotAfterDateTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidrequireExplicitPolicyTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/basicConstraintsNotCriticalCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidRFC822nameConstraintsTest23EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidNameChainingCapitalizationTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidPolicyMappingTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest15EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy5subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlySomeReasonsTest21EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcRLIssuerTest32EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/AnyPolicyTest14EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidRFC822nameConstraintsTest21EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddeltaCRLTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidpathLenConstraintTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidPolicyMappingTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UIDCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsRFC822CA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDNS1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitAnyPolicyTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcRLIssuerTest27EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BadSignedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidEEnotAfterDateTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P12subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/MappingFromanyPolicyCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidLongSerialNumberTest17EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidpathLenConstraintTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP123subCAP12Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping0CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP123subsubCAP12P2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidSelfIssuedrequireExplicitPolicyTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest20EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddistributionPointTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidUTF8StringCaseInsensitiveMatchTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNSnameConstraintsTest30EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy5subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/keyUsageNotCriticalcRLSignFalseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest12EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN3subCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BasicSelfIssuedCRLSigningKeyCRLCert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN1subCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidCASignatureTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidPolicyMappingTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest12EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlyContainsCACertsTest12EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/Mapping1to2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidNegativeSerialNumberTest14EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BasicSelfIssuedOldKeyCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlySomeReasonsCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidSelfIssuedinhibitAnyPolicyTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP1234subsubCAP123P12Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidcRLIssuerTest30EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy4subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidRFC822nameConstraintsTest25EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddeltaCRLTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDNS2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest19EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidBasicSelfIssuedOldWithNewTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlySomeReasonsCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidUnknownCRLExtensionTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitAnyPolicyTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/TrustAnchorRootCertificate.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P1subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidIDPwithindirectCRLTest24EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1SelfIssuedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidURInameConstraintsTest36EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping5subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidOldCRLnextUpdateTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidcRLIssuerTest28EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP123subsubCAP12P1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/P1anyPolicyMapping1to2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA3Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidLongSerialNumberTest18EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidRolloverfromPrintableStringtoUTF8StringTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidGeneralizedTimenotAfterDateTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P12subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pre2000CRLnextUpdateCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA5Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlyContainsUserCertsTest11EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN5CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidUnknownCRLEntryExtensionTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidinhibitAnyPolicyTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/GoodCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/distributionPoint1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UnknownCRLExtensionCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subsubsubCA11XCert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidLongSerialNumberTest16EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidkeyUsageNotCriticalTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicyTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BadCRLIssuerNameCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subCA0Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP3CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidBasicSelfIssuedNewWithOldTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/keyUsageCriticalcRLSignFalseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UserNoticeQualifierTest15EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlyContainsAttributeCertsCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest13EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsURI2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidRFC3280MandatoryAttributeTypesTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/OverlappingPoliciesTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy7subsubsubCARE2RE4Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/NoPoliciesCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlySomeReasonsTest17EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidCAnotBeforeDateTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy5subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlyContainsUserCertsCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddeltaCRLTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidpathLenConstraintTest12EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PanyPolicyMapping1to2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint0SelfIssuedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy5CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidURInameConstraintsTest34EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidUTF8StringEncodedNamesTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDSASignaturesTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy0subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest13EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidcRLIssuerTest33EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidRFC822nameConstraintsTest26EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidrequireExplicitPolicyTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidpathLenConstraintTest14EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidIDPwithindirectCRLTest22EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidNegativeSerialNumberTest15EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNandRFC822nameConstraintsTest27EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint1SelfIssuedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy7subsubCARE2RE4Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy10subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN4CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/LongSerialNumberCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subsubCA11Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidonlySomeReasonsTest19EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1subsubCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA4cRLIssuerCert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidinhibitPolicyMappingTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/onlySomeReasonsCA3Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN3CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP12subCAP1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedrequireExplicitPolicyTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P12CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DSACACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedinhibitAnyPolicyTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/anyPolicyCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UserNoticeQualifierTest16EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BasicSelfIssuedOldKeyNewWithOldCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidpathLenConstraintTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidMappingToanyPolicyTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest16EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subCA4Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/keyUsageNotCriticalkeyCertSignFalseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidNameChainingWhitespaceTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/RevokedsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidRFC822nameConstraintsTest24EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy0subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP123subsubsubCAP12P2P1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidBasicSelfIssuedCRLSigningKeyTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/Validpre2000UTCnotBeforeDateTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/RFC3280MandatoryAttributeTypesCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidkeyUsageCriticalcRLSignFalseTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidonlySomeReasonsTest16EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/MappingToanyPolicyCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA6Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddistributionPointTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subsubCA41Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/NegativeSerialNumberCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidBadCRLSignatureTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidrequireExplicitPolicyTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/BasicSelfIssuedNewKeyCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA3cRLIssuerCert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping0subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcAFalseTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/NoCRLCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/GoodsubCAPanyPolicyMapping1to2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidEESignatureTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint1subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidpathLenConstraintTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddistributionPointTest7EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/deltaCRLCA3Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UnknownCRLEntryExtensionCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidIDPwithindirectCRLTest26EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/Invalidpre2000CRLnextUpdateTest12EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/P12Mapping1to3subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNSnameConstraintsTest31EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidBasicSelfIssuedOldWithNewTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1SelfIssuedsubCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy2SelfIssuedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidcRLIssuerTest35EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddeltaCRLIndicatorNoBaseTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidUnknownCRLExtensionTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P12subsubCAIPM5Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidURInameConstraintsTest37EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddeltaCRLTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/P12Mapping1to3CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping5subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidGeneralizedTimeCRLnextUpdateTest13EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsURI1CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidBasicSelfIssuedCRLSigningKeyTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedpathLenConstraintTest16EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddistributionPointTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy5CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP2subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP1234CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/indirectCRLCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P1SelfIssuedsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN2CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidEEnotBeforeDateTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy1subCA1Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddistributionPointTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/MissingbasicConstraintsCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy2SelfIssuedsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy10subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidRevokedEETest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidpathLenConstraintTest13EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint1SelfIssuedsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidUnknownCriticalCertificateExtensionTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/DifferentPoliciesTest4EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsDN1subCA3Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy5subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddistributionPointTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidkeyUsageNotCriticalkeyCertSignFalseTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/P12Mapping1to3subsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNSnameConstraintsTest32EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitAnyPolicy0CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint0subCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidIDPwithindirectCRLTest23EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidUnknownNotCriticalCertificateExtensionTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDSAParameterInheritanceTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidkeyUsageNotCriticalcRLSignFalseTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP2subCA2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/TwoCRLsCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest17EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy5subsubsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/NameOrderingCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNandRFC822nameConstraintsTest28EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidMissingbasicConstraintsTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidDNnameConstraintsTest14EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest14EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/deltaCRLIndicatorNoBaseCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy10subsubsubCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidPolicyMappingTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/P1Mapping1to234CACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidSelfIssuedinhibitPolicyMappingTest9EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/UTF8StringEncodedNamesCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/PoliciesP12subsubCAP1P2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/nameConstraintsRFC822CA3Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidWrongCRLTest6EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/RolloverfromPrintableStringtoUTF8StringCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidBasicSelfIssuedNewWithOldTest3EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/CPSPointerQualifierTest20EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subsubCA00Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidBasicSelfIssuedNewWithOldTest5EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidpathLenConstraintTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/inhibitPolicyMapping1P1SelfIssuedCACert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidDNnameConstraintsTest10EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/AllCertificatesNoPoliciesTest2EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValiddeltaCRLTest8EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/requireExplicitPolicy7subCARE2Cert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvalidkeyUsageCriticalkeyCertSignFalseTest1EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidcRLIssuerTest29EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/ValidonlySomeReasonsTest18EE.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/pathLenConstraint6subsubsubCA41XCert.crt \
	gnu/testlet/gnu/crypto/pki/pkits/data/certs/InvaliddeltaCRLTest3EE.crt

test_crls = \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BadCRLIssuerNameCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BadCRLSignatureCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BadSignedCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BadnotAfterDateCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BadnotBeforeDateCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BasicSelfIssuedCRLSigningKeyCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BasicSelfIssuedNewKeyCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/BasicSelfIssuedOldKeyCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/DSACACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/DSAParametersInheritedCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/GeneralizedTimeCRLnextUpdateCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/GoodCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/GoodsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/GoodsubCAPanyPolicyMapping1to2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/LongSerialNumberCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/Mapping1to2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/MappingFromanyPolicyCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/MappingToanyPolicyCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/MissingbasicConstraintsCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/NameOrderCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/NegativeSerialNumberCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/NoPoliciesCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/NoissuingDistributionPointCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/OldCRLnextUpdateCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/P12Mapping1to3CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/P12Mapping1to3subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/P12Mapping1to3subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/P1Mapping1to234CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/P1Mapping1to234subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/P1anyPolicyMapping1to2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PanyPolicyMapping1to2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP1234CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP1234subCAP123CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP1234subsubCAP123P12CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP123CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP123subCAP12CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP123subsubCAP12P1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP123subsubCAP2P2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP123subsubsubCAP12P2P1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP12CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP12subCAP1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP12subsubCAP1P2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP2subCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP2subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/PoliciesP3CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/RFC3280MandatoryAttributeTypesCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/RFC3280OptionalAttributeTypesCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/RevokedsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/RolloverfromPrintableStringtoUTF8StringCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/TrustAnchorRootCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/TwoCRLsCABadCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/TwoCRLsCAGoodCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/UIDCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/UTF8StringCaseInsensitiveMatchCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/UTF8StringEncodedNamesCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/UnknownCRLEntryExtensionCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/UnknownCRLExtensionCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/WrongCRLCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/anyPolicyCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/basicConstraintsCriticalcAFalseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/basicConstraintsNotCriticalCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/basicConstraintsNotCriticalcAFalseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLCA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLCA1deltaCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLCA2deltaCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLCA3CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLCA3deltaCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/deltaCRLIndicatorNoBaseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/distributionPoint1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/distributionPoint2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/indirectCRLCA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/indirectCRLCA3CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/indirectCRLCA3cRLIssuerCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/indirectCRLCA4cRLIssuerCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/indirectCRLCA5CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy0CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy1subCA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy1subCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy1subCAIAP5CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy1subsubCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy5CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy5subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitAnyPolicy5subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping0CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping0subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P12CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P12subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P12subCAIPM5CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P12subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P12subsubCAIPM5CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P1subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping1P1subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping5CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping5subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping5subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/inhibitPolicyMapping5subsubsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/keyUsageCriticalcRLSignFalseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/keyUsageCriticalkeyCertSignFalseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/keyUsageNotCriticalCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/keyUsageNotCriticalcRLSignFalseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/keyUsageNotCriticalkeyCertSignFalseCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN1subCA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN1subCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN1subCA3CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN3CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN3subCA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN3subCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN4CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDN5CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDNS1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsDNS2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsRFC822CA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsRFC822CA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsRFC822CA3CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsURI1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/nameConstraintsURI2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlyContainsAttributeCertsCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlyContainsCACertsCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlyContainsUserCertsCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA1compromiseCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA1otherreasonsCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA2CRL1.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA2CRL2.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA3compromiseCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA3otherreasonsCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA4compromiseCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/onlySomeReasonsCA4otherreasonsCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint0CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint0subCA2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint0subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint1CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint1subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subCA0CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subCA1CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subCA4CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subsubCA00CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subsubCA11CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subsubCA41CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subsubsubCA11XCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pathLenConstraint6subsubsubCA41XCRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/pre2000CRLnextUpdateCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy0CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy0subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy0subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy0subsubsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy10CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy10subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy10subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy10subsubsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy2CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy2subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy4CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy4subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy4subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy4subsubsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy5CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy5subCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy5subsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy5subsubsubCACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy7CACRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy7subCARE2CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy7subsubCARE2RE4CRL.crl \
	gnu/testlet/gnu/crypto/pki/pkits/data/crls/requireExplicitPolicy7subsubsubCARE2RE4CRL.crl

der_sources = \
	gnu/crypto/der/BitString.java \
	gnu/crypto/der/DER.java \
	gnu/crypto/der/DEREncodingException.java \
	gnu/crypto/der/DERReader.java \
	gnu/crypto/der/DERValue.java \
	gnu/crypto/der/DERWriter.java \
	gnu/crypto/der/OID.java

# .class files ----------------------------------------------------------------
main_classes = $(patsubst %.java,classes/%.class,$(main_sources))
mauve_classes = $(patsubst %.java,mauve-classes/%.class,$(mauve_sources))
pkits_mauve_classes = $(patsubst %.java,pkits-classes/%.class,$(pkits_mauve_sources))
der_classes = $(patsubst %.java,der-classes/%.class,$(der_sources))

gnu/testlet/config.java: gnu/testlet/config.java.in
	cd $(top_builddir) && $(SHELL) ./config.status $(subdir)/$@

# -----------------------------------------------------------------------------

check-local: mauve-classes $(mauve_classes) mauve-$(PACKAGE) test_classes
	-$(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath)$(PS)$(main_jar)$(PS)$(der_jar)$(PS)mauve-classes" gnu.testlet.SimpleTestHarness -file test_classes $(MAUVEFLAGS)

check-pkits: pkits-classes pkits-classes/gnu/testlet/config.class $(pkits_mauve_classes) pkits_tests
	test -d pkits-classes/gnu/testlet/gnu/crypto/pki/pkits/data/certs || mkdir -p pkits-classes/gnu/testlet/gnu/crypto/pki/pkits/data/certs
	test -d pkits-classes/gnu/testlet/gnu/crypto/pki/pkits/data/crls  || mkdir -p pkits-classes/gnu/testlet/gnu/crypto/pki/pkits/data/crls
	@cp $(patsubst %.crt,$(srcdir)/%.crt,$(test_certificates)) pkits-classes/gnu/testlet/gnu/crypto/pki/pkits/data/certs
	@cp $(patsubst %.crl,$(srcdir)/%.crl,$(test_crls)) pkits-classes/gnu/testlet/gnu/crypto/pki/pkits/data/crls
	-$(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath)$(PS)$(main_jar)$(PS)$(der_jar)$(PS)pkits-classes$(PS)$(srcdir)" gnu.testlet.SimpleTestHarness -file pkits_tests $(MAUVEFLAGS)

# if FOUND_JCE_JAR
# if FOUND_SECURITY_JAR
# #	$(JAVA) -Xbootclasspath/p:$(JCE_JAR) -cp "$(SECURITY_JAR)$(PATH_SEPARATOR)$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file test_classes
# 	$(JAVA) $(JAVAFLAGS) -cp "$(SECURITY_JAR)$(PATH_SEPARATOR)$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file test_classes
# else
# #	$(JAVA) -Xbootclasspath/p:$(JCE_JAR) -cp "$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file test_classes
# 	$(JAVA) $(JAVAFLAGS) -cp "$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file test_classes
# endif # FOUND_SECURITY_JAR
# else
# if FOUND_SECURITY_JAR
# #	$(JAVA) -cp "$(SECURITY_JAR)$(PATH_SEPARATOR)$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file
# 	$(JAVA) $(JAVAFLAGS) -cp "$(SECURITY_JAR)$(PATH_SEPARATOR)$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file
# else
# #	$(JAVA) -cp "$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file test_classes
# 	$(JAVA) $(JAVAFLAGS) -cp "$(main_jar)$(PATH_SEPARATOR)$(srcdir)" gnu.testlet.SimpleTestHarness -file test_classes
# endif # FOUND_SECURITY_JAR
# endif

test_classes: FORCE
	HERE=`pwd` ; \
	(cd $(srcdir) ; \
	 $(SHELL) choose-classes $$HERE $(PACKAGE))

pkits_tests: FORCE
	-mkdir .tmp
	HERE=`pwd` ; \
	(cd $(srcdir) ; \
	 $(SHELL) choose-classes -verbose $$HERE/.tmp PKITS)
	mv .tmp/test_classes pkits_tests
	rm -rf .tmp

FORCE:

# Extra targets ---------------------------------------------------------------
SUFFIXES = .java .lo .o .obj .class .jar

$(main_classes): classes/%.class: $(srcdir)/%.java
	CLASSPATH=$(extra_classpath)$(PS)$(der_jar) $(JAVAC) $(JAVACFLAGS) -sourcepath $(srcdir):. -d classes $<

classes/gnu/crypto/Registry.class: gnu/crypto/Registry.java
	CLASSPATH=$(extra_classpath)$(PS)$(der_jar) $(JAVAC) $(JAVACFLAGS) -sourcepath $(srcdir):. -d classes $<

$(main_jar): classes classes/gnu/crypto/Registry.class $(main_classes) copy-bundles
	$(JAR) cf $@ -C classes gnu

$(der_classes): der-classes/%.class: $(srcdir)/%.java
	CLASSPATH=$(extra_classpath) $(JAVAC) $(JAVACFLAGS) -sourcepath $(srcdir):. -d der-classes $<

$(der_jar): der-classes $(der_classes)
	$(JAR) cf $@ -C der-classes gnu

$(mauve_classes): mauve-classes/%.class: $(srcdir)/%.java
	CLASSPATH=$(extra_classpath) $(JAVAC) $(JAVACFLAGS) -sourcepath $(srcdir):. -d mauve-classes $<

pkits-classes/gnu/testlet/config.class: gnu/testlet/config.java
	CLASSPATH=$(extra_classpath) $(JAVAC) $(JAVACFLAGS) -sourcepath $(srcdir):. -d pkits-classes $<

$(pkits_mauve_classes): pkits-classes/%.class: $(srcdir)/%.java
	CLASSPATH=$(extra_classpath):pkits-classes $(JAVAC) $(JAVACFLAGS) -sourcepath $(srcdir):. -d pkits-classes $<

classes:
	mkdir classes

der-classes:
	mkdir der-classes

mauve-classes:
	mkdir mauve-classes

pkits-classes:
	mkdir pkits-classes

copy-bundles:
if WITH_INCLUDED_GETOPT
	test -d classes/gnu/getopt || mkdir -p classes/gnu/getopt
	test -f classes/gnu/getopt/MessagesBundle.properties || cp $(srcdir)/gnu/getopt/*.properties classes/gnu/getopt
endif
if FOUND_SECURITY_JAR
	test -d classes/gnu/crypto/auth/callback || mkdir -p classes/gnu/crypto/auth/callback
	test -f classes/gnu/crypto/auth/callback/MessagesBundle.properties || cp $(srcdir)/gnu/crypto/auth/callback/*.properties classes/gnu/crypto/auth/callback
	test -d classes/gnu/crypto/tool/keytool || mkdir -p classes/gnu/crypto/tool/keytool
	test -f classes/gnu/crypto/tool/keytool/MessagesBundle.properties || cp $(srcdir)/gnu/crypto/tool/keytool/*.properties classes/gnu/crypto/tool/keytool
endif

NESSIE_TV = $(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.NessieCipherTVEmitter
NESSIE_HASH = $(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.NessieHashTVEmitter
NIST_KAT = $(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.NistKat
NIST_MCT = $(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.NistMCT

test-vectors: $(main_jar)
	@ test -e ../tv || mkdir ../tv
	@ test -e ../tv/nessie || mkdir ../tv/nessie
	@ test -e ../tv/nist || mkdir ../tv/nist
	@ test -e ../tv/nist/anubis || mkdir ../tv/nist/anubis
	@ test -e ../tv/nist/rijndael || mkdir ../tv/nist/rijndael
	@ test -e ../tv/nist/serpent || mkdir ../tv/nist/serpent
	@ test -e ../tv/nist/twofish || mkdir ../tv/nist/twofish
	@ test -e ../tv/nist/khazad || mkdir ../tv/nist/khazad
	@ test -e ../tv/nist/square || mkdir ../tv/nist/square
	$(NESSIE_TV) anubis 128    > ../tv/nessie/anubis-128-tv.txt
	$(NESSIE_TV) anubis 160    > ../tv/nessie/anubis-160-tv.txt
	$(NESSIE_TV) anubis 192    > ../tv/nessie/anubis-192-tv.txt
	$(NESSIE_TV) anubis 224    > ../tv/nessie/anubis-224-tv.txt
	$(NESSIE_TV) anubis 256    > ../tv/nessie/anubis-256-tv.txt
	$(NESSIE_TV) anubis 288    > ../tv/nessie/anubis-288-tv.txt
	$(NESSIE_TV) anubis 320    > ../tv/nessie/anubis-320-tv.txt
	$(NESSIE_TV) khazad        > ../tv/nessie/khazad-tv.txt
	$(NESSIE_HASH) whirlpool   > ../tv/nessie/whirlpool-tv.txt
	$(NIST_KAT) -k anubis      > ../tv/nist/anubis/ecb_vk.txt
	$(NIST_KAT) -t anubis      > ../tv/nist/anubis/ecb_vt.txt
	$(NIST_KAT) -k rijndael    > ../tv/nist/rijndael/ecb_vk.txt
	$(NIST_KAT) -t rijndael    > ../tv/nist/rijndael/ecb_vt.txt
	$(NIST_KAT) -k serpent     > ../tv/nist/serpent/ecb_vk.txt
	$(NIST_KAT) -t serpent     > ../tv/nist/serpent/ecb_vt.txt
	$(NIST_KAT) -k twofish     > ../tv/nist/twofish/ecb_vk.txt
	$(NIST_KAT) -t twofish     > ../tv/nist/twofish/ecb_vt.txt
	$(NIST_KAT) -k khazad 128  > ../tv/nist/khazad/ecb_vk.txt
	$(NIST_KAT) -t khazad 128  > ../tv/nist/khazad/ecb_vt.txt
	$(NIST_KAT) -k square 128  > ../tv/nist/square/ecb_vk.txt
	$(NIST_KAT) -t square 128  > ../tv/nist/square/ecb_vt.txt
	$(NIST_MCT) -E -e anubis   > ../tv/nist/anubis/ecb_e_m.txt
	$(NIST_MCT) -E -d anubis   > ../tv/nist/anubis/ecb_d_m.txt
	$(NIST_MCT) -C -e anubis   > ../tv/nist/anubis/cbc_e_m.txt
	$(NIST_MCT) -C -d anubis   > ../tv/nist/anubis/cbc_d_m.txt
	$(NIST_MCT) -E -e rijndael > ../tv/nist/rijndael/ecb_e_m.txt
	$(NIST_MCT) -E -d rijndael > ../tv/nist/rijndael/ecb_d_m.txt
	$(NIST_MCT) -C -e rijndael > ../tv/nist/rijndael/cbc_e_m.txt
	$(NIST_MCT) -C -d rijndael > ../tv/nist/rijndael/cbc_d_m.txt
	$(NIST_MCT) -E -e serpent  > ../tv/nist/serpent/ecb_e_m.txt
	$(NIST_MCT) -E -d serpent  > ../tv/nist/serpent/ecb_d_m.txt
	$(NIST_MCT) -C -e serpent  > ../tv/nist/serpent/cbc_e_m.txt
	$(NIST_MCT) -C -d serpent  > ../tv/nist/serpent/cbc_d_m.txt
	$(NIST_MCT) -E -e twofish  > ../tv/nist/twofish/ecb_e_m.txt
	$(NIST_MCT) -E -d twofish  > ../tv/nist/twofish/ecb_d_m.txt
	$(NIST_MCT) -C -e twofish  > ../tv/nist/twofish/cbc_e_m.txt
	$(NIST_MCT) -C -d twofish  > ../tv/nist/twofish/cbc_d_m.txt

speed: $(main_jar)
	$(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.CipherSpeed
	$(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.HashSpeed

ent: $(main_jar)
	$(JAVA) $(JAVAFLAGS) -cp "$(extra_classpath):$(main_jar)" gnu.crypto.tool.Ent

MOSTLYCLEANFILES = $(main_jar) $(der_jar) test_classes

clean-local:
	rm -rf classes der-classes mauve-classes pkits-classes

# -----------------------------------------------------------------------------
