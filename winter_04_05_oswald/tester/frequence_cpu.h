/******************************************************************************
 * Code permettant de :
 * - Lire la fr�quence du processeur
 * - Acc�der � l'instruction RDTSC
 * - Chronom�trer tr�s pr�cis�ment une dur�e.
 *
 * Ce code est pr�vu pour fonctionner sous Linux ou sous Windows.
 *
 * ATTENTION: Ce programme ne fonctionne que sur des processeurs compatibles
 * Intel Pentium ou sup�rieur (� cause de l'instruction RDTSC).
 *
 * CE CODE EST SOUS LICENCE GPL : http://www.fsf.org/licenses/gpl.html
 *
 * Historique :
 * - 8 septembre 2003 : Correction pour Visual C++, ce naze ne sait pas 
 *   convertir des uint64 en double, j'ai fait un ptit hack tout naze ...
 * - 5 septembre 2003 : Portage pour Visual C++
 * - 29 mars 2003     : Cr�ation, code pour Linux sous GCC, 
 *                      et Borland C++ Builder sous Windows
 *
 * Par Haypo (victor.stinner@haypocalc.com) - http://www.haypocalc.com/
 *****************************************************************************/

// Visual C++ : D�finit _Windows
#if defined(_MSC_VER)
#  define _Windows
#endif

#include <stdio.h>
#ifdef linux
#  include <unistd.h>
#  include <string.h>
#  include <stdlib.h>
#  define NOMFICH_CPUINFO "/proc/cpuinfo"
#elif defined(_Windows)
#  include <windows.h>
#endif

//---------------------------------------------------------------------------

#if defined(_Windows)

// D�finit les types uint32 et uint64 sous Windows
typedef unsigned __int32 uint32;
typedef unsigned __int64 uint64;

// D�finit le convertion uint64 vers double
double uint64_to_double (const uint64 x);
#endif

//---------------------------------------------------------------------------

// Instruction RDTSC du processeur Pentium
double RDTSC(void);
//---------------------------------------------------------------------------

// Affichage d'une fr�quence en utilisant le suffixe adapt� (GHz, MHz, KHz, Hz)
void AfficheFrequence (double frequence);
//---------------------------------------------------------------------------

// Lit la fr�quence du processeur
// Renvoie la fr�quence en Hz dans 'frequence' si le code de retour est
// diff�rent de 1. Renvoie 0 en cas d'erreur.
int LitFrequenceCpu (double* frequence);
//---------------------------------------------------------------------------

// Fonction de calcul : fait une pause d'une seconde :-)
void Calcul(void);
//---------------------------------------------------------------------------

