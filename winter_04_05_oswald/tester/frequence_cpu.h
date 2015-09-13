/******************************************************************************
 * Code permettant de :
 * - Lire la fréquence du processeur
 * - Accéder à l'instruction RDTSC
 * - Chronométrer très précisément une durée.
 *
 * Ce code est prévu pour fonctionner sous Linux ou sous Windows.
 *
 * ATTENTION: Ce programme ne fonctionne que sur des processeurs compatibles
 * Intel Pentium ou supérieur (à cause de l'instruction RDTSC).
 *
 * CE CODE EST SOUS LICENCE GPL : http://www.fsf.org/licenses/gpl.html
 *
 * Historique :
 * - 8 septembre 2003 : Correction pour Visual C++, ce naze ne sait pas 
 *   convertir des uint64 en double, j'ai fait un ptit hack tout naze ...
 * - 5 septembre 2003 : Portage pour Visual C++
 * - 29 mars 2003     : Création, code pour Linux sous GCC, 
 *                      et Borland C++ Builder sous Windows
 *
 * Par Haypo (victor.stinner@haypocalc.com) - http://www.haypocalc.com/
 *****************************************************************************/

// Visual C++ : Définit _Windows
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

// Définit les types uint32 et uint64 sous Windows
typedef unsigned __int32 uint32;
typedef unsigned __int64 uint64;

// Définit le convertion uint64 vers double
double uint64_to_double (const uint64 x);
#endif

//---------------------------------------------------------------------------

// Instruction RDTSC du processeur Pentium
double RDTSC(void);
//---------------------------------------------------------------------------

// Affichage d'une fréquence en utilisant le suffixe adapté (GHz, MHz, KHz, Hz)
void AfficheFrequence (double frequence);
//---------------------------------------------------------------------------

// Lit la fréquence du processeur
// Renvoie la fréquence en Hz dans 'frequence' si le code de retour est
// différent de 1. Renvoie 0 en cas d'erreur.
int LitFrequenceCpu (double* frequence);
//---------------------------------------------------------------------------

// Fonction de calcul : fait une pause d'une seconde :-)
void Calcul(void);
//---------------------------------------------------------------------------

