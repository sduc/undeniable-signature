#include "gmp.h"
#include "quartic2.h"

//prints struct gaussInt
void printgaussInt(struct gaussInt x);

//prints mpz_t
void printmpz(mpz_t x);


//-------------programmed by Malik Hammoutene---------------

//initializes the random generator
extern void initRandom();

//generates a random number > 2^(d-1)
extern void getRand2(mpz_t x, int d);

//generates a random number < 2^32
extern int getrand();

//  génération d'un grand premier tel que p = 1+4t
extern void genPrime(mpz_t prime_n,mpz_t seedNb);

//SHANKS-TONELLI Algorithm
extern void shanks(mpz_t a2, mpz_t prime_number, mpz_t x);

//CORNACCHIA ALGORITHM
extern struct gaussInt cornacchia(mpz_t prime);


//-------------programmed by Malik Hammoutene---------------