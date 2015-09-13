#include "gmp.h"

//key generation
extern void setupRSA(gmp_randstate_t state, int bits);

//computes signature
extern void rsa(mpz_t rop, mpz_t m);

//deletes private key
extern void clear();

