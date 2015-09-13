#include <string.h>          /* strlen */
#include <stdio.h>           /* printf */
#include <stdlib.h>          /* malloc */
#include "gmp.h"

//generate the public and private key for the signature scheme
//using the homomorphism based on the discrete logarithm
extern void setupLogarithm(mpz_t d, mpz_t r, mpz_t p, mpz_t q, mpz_t h, mpz_t n,gmp_randstate_t state,int bits);

//construct the hash table containing all values for the discrete logarithm
extern unsigned int* logTable(mpz_t h, mpz_t d, mpz_t p);

//construct the hash table for the baby step giant step algorithm
extern unsigned int* bSGSTable(mpz_t a, mpz_t d, mpz_t n);

//compute the homomorphism using a precomputed table
extern int homTable(mpz_t y, mpz_t h, mpz_t r, mpz_t p, unsigned int* table, mpz_t x);

//compute the homomorphism using the baby step giant step algorithm
extern int homBSGS(mpz_t y, mpz_t h, mpz_t d, mpz_t r, mpz_t p, unsigned int* table, mpz_t x);

//compute the homomorphism using Pollard's rho algorithm with the first partition described in the report
extern int homPollard(mpz_t y, mpz_t h, mpz_t d, mpz_t r, mpz_t p, mpz_t x);

//compute the homomorphism using Pollard's rho algorithm with the second partition described in the report
extern int homPollard2(mpz_t y, mpz_t h, mpz_t d, mpz_t r, mpz_t p, mpz_t x);

//count the number of table look ups of the algorithm using the precomputed table
extern int homTableLU(mpz_t h, mpz_t r, mpz_t p, unsigned int* table, mpz_t x);

//count the number of iterations of the baby step giant step algorithm
extern int homBSGSIter(mpz_t h, mpz_t d, mpz_t r, mpz_t p, unsigned int* table, mpz_t x);

//count the number of iterations of Pollard's rho algorithm
extern int homPollardIter(mpz_t h, mpz_t d, mpz_t r, mpz_t p, mpz_t x);

