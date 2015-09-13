#pragma once
#include "gmp.h"
#define norm(n,x) mpz_mul(n,x.a,x.a);mpz_addmul(n,x.b,x.b) //compute norm


// structure for Gaussian integers
struct gaussInt{
	mpz_t a;
	mpz_t b;
};

//transform (a + bi) into its primary associate
extern int primaryExp(mpz_t a, mpz_t b);


//compute (x * y) in Z[i]
extern struct gaussInt MultComplex(struct gaussInt x, struct gaussInt y);


//compute (x mod y) in Z[i]
extern struct gaussInt ModuloGauss(struct gaussInt x, struct gaussInt y);

//compute x/(1+i)^r
extern struct gaussInt divUnPlusI(struct gaussInt x, int r);

//compute the quartic residue symbol using the basic algorithm with the optimized subfunctions
extern struct gaussInt quarticb2(struct gaussInt x, struct gaussInt y);

//compute the quartic residue symbol using the basic algorithm with the optimized subfunctions rewritten
extern struct gaussInt quarticb3(struct gaussInt x, struct gaussInt y);




