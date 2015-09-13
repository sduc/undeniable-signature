#include "rsa.h"

//key variables
mpz_t n, e, d, p, q, phi;

//returns public key
void setupRSA(gmp_randstate_t state, int bits){
	mpz_t t; 
	mpz_init(t); mpz_init(n); mpz_init(e); mpz_init(d);
	mpz_init(p); mpz_init(q); mpz_init(phi);
	mpz_urandomb (p, state, bits);
	mpz_nextprime(p,p);
	mpz_urandomb (q, state, bits);
	mpz_nextprime(q,q);
	mpz_sub_ui(n,p,1);
	mpz_sub_ui(t,q,1);
	mpz_mul(phi,n,t);
	mpz_urandomm (e, state, n);
	mpz_gcd(t,e,n);
	while( mpz_cmp_ui(t,1) != 0){
		mpz_urandomm (e, state,n);
		mpz_gcd(t,e,n);
	}
	mpz_invert(d,e,n);
	mpz_mul(n,p,q);
}

//computes signature
void rsa(mpz_t rop, mpz_t m){
	mpz_powm(rop,m,d,n);
}

//clears key variables
void clear(){
	mpz_clear(d); mpz_clear(p); mpz_clear(q); mpz_clear(phi);
}

