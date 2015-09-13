#include <string.h>          // strlen
#include <stdio.h>           // printf 
#include <stdlib.h>          // malloc 
#include <limits.h>
#include "gmp.h"
#include "discreteLogarithm.h"

int kl_t = 24;				//keylength for hash table
int kl_b = 24;				//keylength for hash table
int count;					//keep track of number of iterations executed
gmp_randstate_t s;			//random number generator


//return entry from hash table
int getLog(mpz_t res, unsigned int* table, mpz_t x, mpz_t a, mpz_t n, int kl){
// INPUT:
//	table: array containing discrete logarithms
//	a: generator of group G (subgroup of Zn*)
//	n: element of N
//	x: element of G for which logarithm is needed
//OUTPUT:
//	res : log such that x = a^log mod n
//  return 1 if succesful, -1 in case of failure

	//Variables
	unsigned int dirty, clean, k, log, collision;
	mpz_t t;

	//1: Validity test for table
	if (table == NULL){
		printf("\ngetLog: table pointer not valid!\n");
		return -1;
	}

	//2: while the collision flag is set, test if x = a^log
	dirty = 0xF00000;
	clean = 0x0FFFFF;
	mpz_init(t);
	mpz_fdiv_r_2exp(t,x,kl);
	k = mpz_get_ui(t);
	log = *(table+k);
	collision = 0;
	while (log & dirty){
		collision++;
		log = log & clean;
		mpz_powm_ui(t,a,log,n);
		if ( mpz_cmp(t,x) == 0){
			mpz_clear(t);
			mpz_set_ui(res,log);
			return 1;
		}
		mpz_fdiv_q_2exp(t,x,collision*kl);
		mpz_fdiv_r_2exp(t,t,kl);
		k =  mpz_get_ui(t);
		if (k == 0) {
			printf("getLog: getting element failed\n");
			return -1;
		}
		log = *(table+k);
	}

	//3: Test for special case: x=1 -> log=0
	if (log == 0){
		if( mpz_cmp_ui(x,1)!=0){
			mpz_clear(t);
			return -1;
		}
	}
	mpz_clear(t);
	mpz_set_ui(res,log);
	return 1;
}


//put entry into hash table
unsigned int* putLog(unsigned int* table, mpz_t key, unsigned int data,int kl){
//INPUT:
//	table: array containing discrete logarithms
//	key: element of G for which we want to put the logarithm into the table
//	data:logarithm to be put into table
//OUTPUT:
//	table with data added

	//Variables
	unsigned int dirty, collision, k;
	mpz_t t;

	//1: Validity test for table
	if (table == NULL){
		printf("\nputLog: table pointer not valid!\n");
		return NULL;
	}
	//2: Put data into table, mark collisions
	dirty = 0xF00000;
	collision = 0;
	mpz_init(t);
	mpz_fdiv_r_2exp(t,key,kl);
    k = mpz_get_ui(t);
	while (*(table+k)) {
		collision++;
		*(table+k) |= dirty;
		mpz_fdiv_q_2exp(t,key,collision*kl);
		mpz_fdiv_r_2exp(t,t,kl);
		k =  mpz_get_ui(t);
		if (k == 0) {
			printf("putLog: putting element failed\n"); 
			return table;
		}
	}
	*(table+k) = data;
	count = count + collision;
	mpz_clear(t);
	return table;
}


//construct the hash table containing all values for the discrete logarithm
unsigned int* logTable(mpz_t h, mpz_t d, mpz_t p){
// INPUT:
//		h, d, p parameters to calculate (j,aj) where aj = h^j mod p for j = 0..d-1
// OUTPUT.
//		complete table of discrete logarithm

	//Variables
	unsigned int* table;
	mpz_t j,aj;
	kl_t = mpz_sizeinbase(d,2)+4;
	if (kl_t > 24) kl_t = 24; //largest array possible contains 2^24 - 1 elements	
	kl_t=24; //remove this statement for dynamically sized hash table
	table = (unsigned int*)calloc(((1 << kl_t ) -1),sizeof(unsigned int));
	if (table == NULL) {
		printf("createTable: memory allocation failed\n");
	}
	mpz_init(j);mpz_init(aj);
	//1: construct table with entries (j,a_j)
	count = 0;
	mpz_set_ui(aj,1);
	putLog(table,aj,mpz_get_ui(j),kl_t);
	for(mpz_set_ui(j,1);mpz_cmp(j,d)<0;mpz_add_ui(j,j,1)){
		mpz_mul(aj,aj,h);
		mpz_mod(aj,aj,p);
		putLog(table,aj,mpz_get_ui(j),kl_t);
	}
	mpz_clear(j); mpz_clear(aj);
	return table;
}

//construct the hash table for the baby step giant step algorithm
unsigned int* bSGSTable(mpz_t a, mpz_t d, mpz_t n){
// INPUT:
//	a: generator of a cyclic group G (subgroup of Zn*)
//	d: |G|
//	n: element of N, d is divisor of phi(n)
// OUTPUT.
//	table for BSGS Algorithm

	//Variables
	unsigned int* table;
	mpz_t m,j,aj;
	mpz_init(j);mpz_init(aj);mpz_init(m);

	//1: set m <- ceil(sqrt(d))	
	if(!mpz_root(m,d,2)){
		mpz_add_ui(m,m,1);
	}

	//2: construct table with entries (j,a_j)
	count = 0;
	kl_b = mpz_sizeinbase(m,2)+4;
	if (kl_b > 24) kl_b = 24; //largest array possible contains 2^24 - 1 elements
	kl_b = 24; //remove this statement for dynamically sized hash table
	table = (unsigned int*)calloc(((1 << kl_b ) -1),sizeof(unsigned int));
	if (table == NULL) {
		printf("createTable: memory allocation failed\n");
	}
	mpz_set_ui(aj,1);
	putLog(table,aj,mpz_get_ui(j),kl_b);
	for(mpz_set_ui(j,1);mpz_cmp(j,m)<0;mpz_add_ui(j,j,1)){
		mpz_mul(aj,aj,a);
		mpz_mod(aj,aj,n);
		putLog(table,aj,mpz_get_ui(j),kl_b);
	}
	mpz_clear(j); mpz_clear(aj);
	return table;
}

//compute the discrete logarithm using the baby step giant step algorithm
int babyStepGiantStep(mpz_t x, mpz_t a,  mpz_t d, mpz_t b, mpz_t p, unsigned int* table){
//INPUT:
//	p: prime
//	a: generator of a cyclic group G (subgroup of Zp*)
//	d: |G|, prime divisor of p-1
//	b: element of G
//OUTPUT:
//	x = log_a(b)
//  return 1 if successful, -1 in case of failure

	//Variables
	mpz_t m,j,aj,c,i,a_m;
	mpz_init(m);mpz_init(j);mpz_init(aj);mpz_init(c);mpz_init(i);mpz_init(a_m);

	//1: test validity of table
	if (table == NULL){
		printf("babyStepGiantStep2: table not valid");
		return -1;
	}

	//2: set m <- ceil(sqrt(d))
	if(!mpz_root(m,d,2)){
		mpz_add_ui(m,m,1);
	}

	//3: compute a^-m and set c <- b
	mpz_invert(i,a,p);
	mpz_powm(a_m,i,m,p);
	mpz_set(c,b);

	//4: for i = 0..m-1 do
	//   	if c in table (c = a^j) return x = i*m + j
	//   	else set c <- c*a^-m
	for(mpz_set_ui(i,0);mpz_cmp(i,m)<0;mpz_add_ui(i,i,1)){
		if (getLog(j,table,c,a,p,kl_b)==1){
			mpz_mul(x,m,i);
			mpz_add(x,x,j);
			return 1;
		}
		else {
			mpz_mul(c,c,a_m);
			mpz_mod(c,c,p);
		}

	}
	return -1;
}

//compute the discrete logarithm using Pollard's rho algorithm with the first partition described in the report
int pollardRho(mpz_t x, mpz_t a, mpz_t d, mpz_t n, mpz_t b){
//INPUT:
//	a: generator of a cyclic group G (subgroup of Zn*)
//	d: |G| prime
//	n: element of N, d is divisor of phi(n)
//	b: element of G
//OUTPUT:
//	x = log_a(b) using partition Si={x | ceil(i/3) < x < ceil(2i/3)}
//	return 1 if successful, -1 in case of failure

	//Variables
	int i;
	mpz_t r, s1, s2;
	mpz_t ai, bi, xi, a2i, b2i, x2i, ai_1, bi_1, xi_1, a2i_2, b2i_2, x2i_2;
	mpz_init(r); mpz_init(s1); mpz_init(s2);
	mpz_init(ai); mpz_init(bi); mpz_init(xi); mpz_init(a2i); mpz_init(b2i); mpz_init(x2i);
	mpz_init(ai_1); mpz_init(bi_1); mpz_init_set_ui(xi_1,1); mpz_init(a2i_2); mpz_init(b2i_2); mpz_init_set_ui(x2i_2,1);
	//1: partition G
	mpz_cdiv_q_ui(s1,n,3);
	mpz_mul_2exp(s2,s1,1);
	//2: main loop
	while(1){
	//2.1: compute xi,ai,bi
		if (mpz_cmp(xi_1,s1)<0){
			mpz_mul(xi,xi_1,b);
			mpz_mod(xi,xi,n); // xi = b*xi_1 mod n
			mpz_set(ai,ai_1); // ai = ai_1
			mpz_add_ui(bi_1, bi_1,1);
			mpz_mod(bi,bi_1,d); // bi = bi_1 + 1 mod d
		} else {
			if(mpz_cmp(xi_1,s2)<0){
				mpz_powm_ui(xi,xi_1,2,n); // xi = xi_1^2 mod n
				mpz_mul_2exp(ai_1,ai_1,1);
				mpz_mod(ai,ai_1,d); // ai = 2*ai_1 mod d
				mpz_mul_2exp(bi_1,bi_1,1);
				mpz_mod(bi,bi_1,d); //bi = 2*bi_1 mod d
			} else {
				mpz_mul(xi,xi_1,a);
				mpz_mod(xi,xi,n); // xi = xi_1*a mod n
				mpz_add_ui(ai_1, ai_1,1);
				mpz_mod(ai,ai_1,d); // ai = ai_1 + 1 mod n
				mpz_set(bi, bi_1);// bi = bi_1
			}
		}
		// 2:1 compute x2i, a2i, b2i
		for(i=0;i<2;i++){
			if (mpz_cmp(x2i_2,s1)<0){
				mpz_mul(x2i,x2i_2,b);
				mpz_mod(x2i,x2i,n); // x2i = b*x2i_2 mod n
				mpz_set(a2i,a2i_2); // a2i = a2i_2
				mpz_add_ui(b2i_2, b2i_2,1);
				mpz_mod(b2i,b2i_2,d); // b2i = b2i_2 + 1 mod d
			} else {
				if(mpz_cmp(x2i_2,s2)<0){
					mpz_powm_ui(x2i,x2i_2,2,n); // xi = x2i_2^2 mod n
					mpz_mul_2exp(a2i_2,a2i_2,1);
					mpz_mod(a2i,a2i_2,d); // a2i = 2*a2i_2 mod d
					mpz_mul_2exp(b2i_2,b2i_2,1);
					mpz_mod(b2i,b2i_2,d); //b2i = 2*b2i_2 mod d
				} else {
					mpz_mul(x2i,x2i_2,a);
					mpz_mod(x2i,x2i,n); // xi = x2i_2*a mod n
					mpz_add_ui(a2i_2, a2i_2,1);
					mpz_mod(a2i,a2i_2,d); // a2i = a2i_2 + 1 mod d
					mpz_set(b2i, b2i_2);// b2i = b2i_2
				}
			}
			if (i == 0) mpz_set(x2i_2, x2i);mpz_set(a2i_2, a2i);mpz_set(b2i_2, b2i);
		}
		//2.2: if xi = x2i
		//	      set r <- bi - b2i mod d
		//	      if r != 0 return x = r^-1(a2i - ai) mod d
		//	      else restart with random a0, b0, x0 = a^a0*b^b0 mod n
		if (mpz_cmp(xi,x2i) == 0){
			mpz_sub(r,bi,b2i);
			mpz_mod(r,r,d);
			if (mpz_cmp_ui(r,0)!=0){
				mpz_invert(r,r,d);
				mpz_sub(a2i,a2i,ai);
				mpz_mul(x,r,a2i);
				mpz_mod(x,x,d);
				return 1;
			}else{
				mpz_urandomm(ai, s, d);
				mpz_urandomm(bi, s, d);
				mpz_powm(x2i,a,ai,n);
				mpz_powm(xi,b,bi,n);
				mpz_mul(x2i,xi,x2i);
				mpz_mod(xi,x2i,n);
				mpz_set(a2i,ai);
				mpz_set(b2i,bi);
				mpz_set(x2i,xi);
				printf("restart\n\n");
			}

		}
		//2.3: set variables for next round
		mpz_set(xi_1,xi); mpz_set(x2i_2, x2i);
		mpz_set(ai_1,ai); mpz_set(a2i_2, a2i);
		mpz_set(bi_1,bi); mpz_set(b2i_2, b2i);
	}
	return -1;
}


//compute the discrete logarithm using Pollard's rho algorithm with the first partition described in the report
int pollardRho2(mpz_t x, mpz_t a, mpz_t d, mpz_t n, mpz_t b){
//INPUT:
//	a: generator of a cyclic group G (subgroup of Zn*)
//	d: |G| prime
//	n: element of N, d is divisor of phi(n)
//	b: element of G
//OUTPUT:
//	x = log_a(b) using partition Si={x|x-1 mod 3 = i} 
//	return 1 if successful, -1 in case of failure

	//Variables
	int i,mod;
	mpz_t r, s1, s2;
	mpz_t ai, bi, xi, a2i, b2i, x2i, ai_1, bi_1, xi_1, a2i_2, b2i_2, x2i_2;
	mpz_init(r); mpz_init(s1); mpz_init(s2);
	mpz_init(ai); mpz_init(bi); mpz_init(xi); mpz_init(a2i); mpz_init(b2i); mpz_init(x2i);
	mpz_init(ai_1); mpz_init(bi_1); mpz_init_set_ui(xi_1,1); mpz_init(a2i_2); mpz_init(b2i_2); mpz_init_set_ui(x2i_2,1);

	//1: main loop
	while(1){
		//1.1: compute xi,ai,bi
		mod = mpz_mod_ui(s1,xi_1,3)+1;
		switch(mod){
		case 3:{}
		case 0:{
			mpz_mul(xi,xi_1,b);
			mpz_mod(xi,xi,n); // xi = b*xi_1 mod n
			mpz_set(ai,ai_1); // ai = ai_1
			mpz_add_ui(bi_1, bi_1,1);
			mpz_mod(bi,bi_1,d); // bi = bi_1 + 1 mod d
			break;
		} case 1:{
			mpz_powm_ui(xi,xi_1,2,n); // xi = xi_1^2 mod n
			mpz_mul_2exp(ai_1,ai_1,1);
			mpz_mod(ai,ai_1,d); // ai = 2*ai_1 mod d
			mpz_mul_2exp(bi_1,bi_1,1);
			mpz_mod(bi,bi_1,d); //bi = 2*bi_1 mod d
			break;
		} case 2:{
			mpz_mul(xi,xi_1,a);
			mpz_mod(xi,xi,n); // xi = xi_1*a mod n
			mpz_add_ui(ai_1, ai_1,1);
			mpz_mod(ai,ai_1,d); // ai = ai_1 + 1 mod n
			mpz_set(bi, bi_1);// bi = bi_1
		}}
		// 1:1 compute x2i, a2i, b2i
		for(i=0;i<2;i++){
			mod = mpz_mod_ui(s1,x2i_2,3)+1;
			switch(mod){
				case 3:{}
				case 0:{	
					mpz_mul(x2i,x2i_2,b);
					mpz_mod(x2i,x2i,n); // x2i = b*x2i_2 mod n
					mpz_set(a2i,a2i_2); // a2i = a2i_2
					mpz_add_ui(b2i_2, b2i_2,1);
					mpz_mod(b2i,b2i_2,d); // b2i = b2i_2 + 1 mod d
					break;
				} 
				case 1: {
					mpz_powm_ui(x2i,x2i_2,2,n); // xi = x2i_2^2 mod n
					mpz_mul_2exp(a2i_2,a2i_2,1);
					mpz_mod(a2i,a2i_2,d); // a2i = 2*a2i_2 mod d
					mpz_mul_2exp(b2i_2,b2i_2,1);
					mpz_mod(b2i,b2i_2,d); //b2i = 2*b2i_2 mod d
					break;
				}case 2:{
					mpz_mul(x2i,x2i_2,a);
					mpz_mod(x2i,x2i,n); // xi = x2i_2*a mod n
					mpz_add_ui(a2i_2, a2i_2,1);
					mpz_mod(a2i,a2i_2,d); // a2i = a2i_2 + 1 mod d
					mpz_set(b2i, b2i_2);// b2i = b2i_2
				}
			}
			if (i == 0) mpz_set(x2i_2, x2i);mpz_set(a2i_2, a2i);mpz_set(b2i_2, b2i);
		}
        //1.2: if xi = x2i
		//	      set r <- bi - b2i mod d
		//	      if r != 0 return x = r^-1(a2i - ai) mod d
		//	      else restart with random a0, b0, x0 = a^a0*b^b0 mod n
		if (mpz_cmp(xi,x2i) == 0){
			mpz_sub(r,bi,b2i);
			mpz_mod(r,r,d);
			if (mpz_cmp_ui(r,0)!=0){
				mpz_invert(r,r,d);
				mpz_sub(a2i,a2i,ai);
				mpz_mul(x,r,a2i);
				mpz_mod(x,x,d);
				return 1;
			}else{
				mpz_urandomm(ai, s, d);
				mpz_urandomm(bi, s, d);
				mpz_powm(x2i,a,ai,n);
				mpz_powm(xi,b,bi,n);
				mpz_mul(x2i,xi,x2i);
				mpz_mod(xi,x2i,n);
				mpz_set(a2i,ai);
				mpz_set(b2i,bi);
				mpz_set(x2i,xi);
				printf("restart\n\n");
			}

		}
		//1.3: set variables for next round
		mpz_set(xi_1,xi); mpz_set(x2i_2, x2i);
		mpz_set(ai_1,ai); mpz_set(a2i_2, a2i);
		mpz_set(bi_1,bi); mpz_set(b2i_2, b2i);
	}
	return -1;
}


//generate the public and private key for the signature scheme
//using the homomorphism based on the discrete logarithm
void setupLogarithm(mpz_t d, mpz_t r, mpz_t p, mpz_t q, mpz_t h, mpz_t n,gmp_randstate_t state,int bits){
//INPUT:  state: variable for random number generator
//		  bits: size of p,q
//OUTPUT: parameters for homomorphism based on discrete logarithm:
//		  d: prime, 20 bits
//        p: prime number, 512 bits, p=r*d+1
//		  q: prime number, 512 bits, gcd(q-1,d)=1
//		  n: 1024 bits, n=p*q
//		  g: element of Zn*, g^r mod p != 1

	mpz_t temp,g,key;
	mpz_init(temp); mpz_init(g); mpz_init(key); gmp_randinit_default(s);

	//d: prime number, 20 bits
	mpz_urandomb(d, state, 20);
	mpz_nextprime(d,d);

	//p: prime number, 512 bits, p=r*d+1
	mpz_urandomb(r,state,bits - 20);
	mpz_clrbit(r,0);
	mpz_mul(p,r,d);
	mpz_add_ui(p,p,1);
	while (mpz_probab_prime_p(p,5) == 0){
		mpz_urandomb(r,state,bits - 20);
		mpz_clrbit(r,0);
		mpz_mul(p,r,d);
		mpz_add_ui(p,p,1);
	}

	//q: prime number, 512 bits, gcd(q-1,d)=1
	mpz_urandomb(q,state,bits);
	mpz_nextprime(q,q);
	mpz_sub_ui(temp,q,1);
	mpz_gcd(temp,temp,d);
	while ( mpz_cmp_ui(temp,1) != 0) {
		mpz_urandomb(q,state,bits);
		mpz_nextprime(q,q);
		mpz_sub_ui(temp,q,1);
		mpz_gcd(temp,temp,d);
	}

	//n: 1024 bits, n=p*q
	mpz_mul(n,p,q);

	//g: element of Zn*, g^r mod p != 1
	mpz_urandomm(g, state, n);
	mpz_powm(h, g, r, p);
	while ( mpz_cmp_ui(h,1) == 0) {
		mpz_urandomm(g, state, n);
		mpz_powm(h, g, r, p);
	}
}


//compute the homomorphism using a precomputed table
int homTable(mpz_t y, mpz_t h, mpz_t r, mpz_t p, unsigned int* table, mpz_t x){
//INPUT
//	r,x,p: parameters for function
//	table: stores logarithms
//OUTPUT
//	y = log_h(x^r mod p)
//	return 1 if successful, -1 in case of failure
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	if (getLog(y,table, t, h, p, kl_t) == 1){
		mpz_clear(t);
		return 1;
	} else {
		printf("homTable: Log not found");
		mpz_clear(t);
		return -1;
	}
}

//compute the homomorphism using the baby step giant step algorithm
int homBSGS(mpz_t y, mpz_t h, mpz_t d, mpz_t r, mpz_t p, unsigned int* table, mpz_t x){
//INPUT
//	r,d,x,p: parameters for homomorphism
//	table: stores logarithms
//OUTPUT
//	y = log_h(x^r mod p)
//	return 1 if successful, -1 in case of failure
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	if (babyStepGiantStep(y,h,d,t,p,table)==1){
		mpz_clear(t);
		return 1;
	} else {
		printf("homBSGS: Log not found");
		mpz_clear(t);
		return -1;
	}
}




//compute the homomorphism using Pollard's rho algorithm with the first partition described in the report
int homPollard(mpz_t y, mpz_t h, mpz_t d, mpz_t r, mpz_t p, mpz_t x){
//INPUT
//	r,d,x,p: parameters for homomorphism
//	table: stores logarithms
//OUTPUT
//	y = log_h(x^r mod p)
//	return 1 if successful, -1 in case of failure
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	if (pollardRho(y, h, d, p, t)==1){
		mpz_clear(t);
		return 1;
	} else {
		printf("homPollard: Log not found");
		mpz_clear(t);
		return -1;
	}
}

//compute the homomorphism using Pollard's rho algorithm with the first partition described in the report
int homPollard2(mpz_t y, mpz_t h, mpz_t d, mpz_t r, mpz_t p, mpz_t x){
//INPUT
//	r,d,x,p: parameters for homomorphism
//	table: stores logarithms
//OUTPUT
//	y = log_h(x^r mod p)
//	return 1 if successful, -1 in case of failure
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	if (pollardRho2(y, h, d, p, t)==1){
		mpz_clear(t);
		return 1;
	} else {
		printf("homPollard: Log not found");
		mpz_clear(t);
		return -1;
	}
}

//count number of table look ups
int getLogLU(unsigned int* table, mpz_t x, mpz_t a, mpz_t n, int kl){
// INPUT:
//	table: array containing discrete logarithm
//	a: generator of group G (subgroup of Zn*)
//	n: element of N
//	x: element of G for which logarithm is needed
//OUTPUT:
//	return number of table lookups

	//Variables
	unsigned int dirty, clean, k, log, collision;
	mpz_t t;

	//1: Validity test for table
	if (table == NULL){
		printf("\ngetLog: table pointer not valid!\n");
		return -1;
	}

	//2: while the collision flag is set, test if x = a^log
	dirty = 0xF00000;
	clean = 0x0FFFFF;
	mpz_init(t);
	mpz_fdiv_r_2exp(t,x,kl);
   	k = mpz_get_ui(t);
	log = *(table+k);
	collision = 0;
	while (log & dirty){
		collision++;
		log = log & clean;
		mpz_powm_ui(t,a,log,n);
		if ( mpz_cmp(t,x) == 0){
			mpz_clear(t);
			return collision+1;
		}
		mpz_fdiv_q_2exp(t,x,collision*kl);
		mpz_fdiv_r_2exp(t,t,kl);
		k =  mpz_get_ui(t);
		if (k == 0) {
			printf("shit1\n");
		}
		log = *(table+k);
	}

	//3: Test for special case: x=1 -> log=0
	if (log == 0){
		if( mpz_cmp_ui(x,1)!=0){
			mpz_clear(t);
			return -1;
		}
	}
	mpz_clear(t);
	return collision+1;
}

//count number of main loop iterations of BSGS algorithm
int babyStepGiantStepIter(mpz_t a,  mpz_t d, mpz_t b, mpz_t p, unsigned int* table){
//INPUT:
//	p: prime
//	a: generator of a cyclic group G (subgroup of Zp*)
//	d: |G|, prime divisor of p-1
//	b: element of G
//OUTPUT:
//	return number of lookups, -1 in case of failure

	//Variables
	int lu;
	mpz_t m,j,aj,c,i,a_m;
	mpz_init(m);mpz_init(j);mpz_init(aj);mpz_init(c);mpz_init(i);mpz_init(a_m);
	lu = 0;

	//1: test validity of table
	if (table == NULL){
		printf("babyStepGiantStep2: table not valid");
		return -1;
	}

	//2: set m <- ceil(sqrt(d))
	if(!mpz_root(m,d,2)){
		mpz_add_ui(m,m,1);
	}

	//3: compute a^-m and set c <- b
	mpz_invert(i,a,p);		
	mpz_powm(a_m,i,m,p);
	mpz_set(c,b);

	//4: for i = 0..m-1 do
	//   	if c in table (c = a^j) return x = i*m + j
	//   	else set c <- c*a^-m
	for(mpz_set_ui(i,0);mpz_cmp(i,m)<0;mpz_add_ui(i,i,1)){
		if (getLog(j,table,c,a,p,kl_b)==1){
			return mpz_get_ui(i)+1;
		}
		else {
			mpz_mul(c,c,a_m);
			mpz_mod(c,c,p);
		}

	}
	return -1;
}


//count number of main loop iterations of Pollard's rho algorithm
int pollardRhoIter(mpz_t a, mpz_t d, mpz_t n, mpz_t b){
//INPUT:
//	a: generator of a cyclic group G (subgroup of Zn*)
//  d: |G| prime
//	n: element of N, d is divisor of phi(n)
//	b: element of G
//OUTPUT:
//	number of iterations -1 in case of failure

	//Variables
	int i, it;
	mpz_t r, s1, s2;
	mpz_t ai, bi, xi, a2i, b2i, x2i, ai_1, bi_1, xi_1, a2i_2, b2i_2, x2i_2;
	mpz_init(r); mpz_init(s1); mpz_init(s2);
	mpz_init(ai); mpz_init(bi); mpz_init(xi); mpz_init(a2i); mpz_init(b2i); mpz_init(x2i);
	mpz_init(ai_1); mpz_init(bi_1); mpz_init_set_ui(xi_1,1); mpz_init(a2i_2); mpz_init(b2i_2); mpz_init_set_ui(x2i_2,1);

	it = 0;

	//1: partition G
	mpz_cdiv_q_ui(s1,n,3);
	mpz_mul_2exp(s2,s1,1);
	//2: main loop
	while(1){
	it++;
	//2.1: compute xi,ai,bi
		if (mpz_cmp(xi_1,s1)<0){
			mpz_mul(xi,xi_1,b);
			mpz_mod(xi,xi,n); // xi = b*xi_1 mod n
			mpz_set(ai,ai_1); // ai = ai_1
			mpz_add_ui(bi_1, bi_1,1);
			mpz_mod(bi,bi_1,d); // bi = bi_1 + 1 mod d
		} else {
			if(mpz_cmp(xi_1,s2)<0){
				mpz_powm_ui(xi,xi_1,2,n); // xi = xi_1^2 mod n
				mpz_mul_2exp(ai_1,ai_1,1);
				mpz_mod(ai,ai_1,d); // ai = 2*ai_1 mod d
				mpz_mul_2exp(bi_1,bi_1,1);
				mpz_mod(bi,bi_1,d); //bi = 2*bi_1 mod d
			} else {
				mpz_mul(xi,xi_1,a);
				mpz_mod(xi,xi,n); // xi = xi_1*a mod n
				mpz_add_ui(ai_1, ai_1,1);
				mpz_mod(ai,ai_1,d); // ai = ai_1 + 1 mod n
				mpz_set(bi, bi_1);// bi = bi_1
			}
		}
		// 2:1 compute x2i, a2i, b2i
		//gmp_printf("PollardRho2:222 x2i_2=%Zu, a2i_2=%Zu, b2i_2=%Zu", x2i_2, a2i_2, b2i_2);
		
		for(i=0;i<2;i++){
			if (mpz_cmp(x2i_2,s1)<0){
				mpz_mul(x2i,x2i_2,b);
				mpz_mod(x2i,x2i,n); // x2i = b*x2i_2 mod n
				mpz_set(a2i,a2i_2); // a2i = a2i_2
				mpz_add_ui(b2i_2, b2i_2,1);
				mpz_mod(b2i,b2i_2,d); // b2i = b2i_2 + 1 mod d
			} else {
				if(mpz_cmp(x2i_2,s2)<0){
					mpz_powm_ui(x2i,x2i_2,2,n); // xi = x2i_2^2 mod n
					mpz_mul_2exp(a2i_2,a2i_2,1);
					mpz_mod(a2i,a2i_2,d); // a2i = 2*a2i_2 mod d
					mpz_mul_2exp(b2i_2,b2i_2,1);
					mpz_mod(b2i,b2i_2,d); //b2i = 2*b2i_2 mod d
				} else {
					mpz_mul(x2i,x2i_2,a);
					mpz_mod(x2i,x2i,n); // xi = x2i_2*a mod n
					mpz_add_ui(a2i_2, a2i_2,1);
					mpz_mod(a2i,a2i_2,d); // a2i = a2i_2 + 1 mod d
					mpz_set(b2i, b2i_2);// b2i = b2i_2
				}
			}
			if (i == 0) mpz_set(x2i_2, x2i);mpz_set(a2i_2, a2i);mpz_set(b2i_2, b2i);
		}
		//2.2: if xi = x2i
		//	      set r <- bi - b2i mod d
		//	      if r != 0 return x = r^-1(a2i - ai) mod d
		//	      else restart with random a0, b0, x0 = a^a0*b^b0 mod n
		if (mpz_cmp(xi,x2i) == 0){
			mpz_sub(r,bi,b2i);
			mpz_mod(r,r,d);
			if (mpz_cmp_ui(r,0)!=0){
				return it;
			}else{               			
				mpz_urandomm(ai, s, d);
			    mpz_urandomm(bi, s, d);
				mpz_powm(x2i,a,ai,n);
				mpz_powm(xi,b,bi,n);
				mpz_mul(x2i,xi,x2i);
				mpz_mod(xi,x2i,n);
				mpz_set(a2i,ai);
			    mpz_set(b2i,bi);
				mpz_set(x2i,xi);
				printf("restart\n\n");
			}

		}
		//2.3: set variables for next round
		mpz_set(xi_1,xi); mpz_set(x2i_2, x2i);
		mpz_set(ai_1,ai); mpz_set(a2i_2, a2i);
		mpz_set(bi_1,bi); mpz_set(b2i_2, b2i);
	}
	return -1;
}


//count the number of table look ups of the algorithm using the precomputed table
int homTableLU(mpz_t h, mpz_t r, mpz_t p, unsigned int* table, mpz_t x){
//INPUT
//	r,x,p: parameters for function
//	table: stores logarithms
//OUTPUT
//	return number of Table Lookups
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	return getLogLU(table, t, h, p, kl_t);
}


//count the number of iterations of the baby step giant step algorithm
int homBSGSIter(mpz_t h, mpz_t d, mpz_t r, mpz_t p, unsigned int* table, mpz_t x){
//INPUT
//	r,d,x,p: parameters for homomorphism
//	table: stores logarithms
//OUTPUT
//	return number of Table Lookups
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	return babyStepGiantStepIter(h,d,t,p,table);
}


//count the number of iterations of Pollard's rho algorithm
int homPollardIter(mpz_t h, mpz_t d, mpz_t r, mpz_t p, mpz_t x){
//INPUT
//	r,d,x,p: parameters for homomorphism
//	table: stores logarithms
//OUTPUT
//	return number of iterations
	mpz_t t;
	mpz_init(t);
	mpz_powm(t,x,r,p);
	return pollardRhoIter(h, d, p, t);
}


