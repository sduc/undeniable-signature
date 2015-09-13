#include "help.h"

gmp_randstate_t state;

//prints struct gaussInt
void printgaussInt(struct gaussInt x){
        int a,b;
        a = mpz_get_si(x.a);
        b = mpz_get_si(x.b);
        printf(" %i +  %i i    ",a,b);
}

//prints mpz_t
void printmpz(mpz_t x){
        int a = mpz_get_si(x);
        printf("%i ",a);
}


//-------------programmed by Malik Hammoutene---------------


//initializes the random generator
void initRandom(){
	gmp_randinit_default(state);
}

//generates a random number < 2^32
int getrand(){
	mpz_t t; mpz_init(t);
	mpz_urandomb(t,state,32);
	return mpz_get_ui(t);
}

//generates a random number > 2^(d-1)
void getRand2(mpz_t x, int d){
	mpz_urandomb(x,state,d-1);
	mpz_setbit(x,d-1);
}

//  génération d'un grand premier tel que p = 1+4t
extern void genPrime(mpz_t prime_n,mpz_t seedNb){
 	mpz_t t,t2;
	mpz_init(t);
	mpz_init(t2);
	mpz_urandomb(t2,state,32);
	mpz_mul(t,seedNb,t2);
	mpz_mul_ui(t,t,4);
	mpz_add_ui(t,t,1);

	if (mpz_probab_prime_p(t,20)==0){ 
		while (mpz_probab_prime_p(t,20)==0){
			mpz_urandomb(t2,state,32);
			mpz_mul(t,seedNb,t2);
			mpz_mul_ui(t,t,4);
			mpz_add_ui(t,t,1);
		}
	}

	mpz_set(t2,t);
	mpz_set(t,t2);
	mpz_set(prime_n,t);
	mpz_clear(t);
	mpz_clear(t2);
}


//SHANKS-TONELLI Algorithm
extern void shanks(mpz_t a2, mpz_t prime_number, mpz_t x){	
	int random_n;
	mpz_t a,a_prime,b,y,z,q,t,temp,temp2;
	mpz_t rand_n;
	int e = 0;
	int compteur=1;
	int r,e2,m,mp,r2;
	
	mpz_init(a);
	mpz_sub_ui(a,prime_number,1);

	mpz_init(q);
	mpz_init(a_prime);
	
	mpz_mod_ui(q,a,2);
	
	while(mpz_cmp_ui(q,0) == 0)
		{
		mpz_div_ui(a_prime, a, 2);
		e = e+1;
		mpz_set(a,a_prime);
		mpz_mod_ui(q,a,2);
	
		}

	e2 = power(2,e);
	mpz_div_ui(q, prime_number, e2);
	
// 1 Find generator
	random_n = getrand();
	mpz_init(rand_n);
	mpz_set_ui(rand_n,random_n);

	if (mpz_legendre(rand_n, prime_number) != -1){
		
		while (mpz_legendre(rand_n, prime_number) != -1){
			random_n = getrand();
			mpz_set_ui(rand_n,random_n);
			}
		}
	mpz_init(z);
	mpz_powm(z,rand_n,q,prime_number);

// 2 Initialize
	mpz_init(y);
	mpz_set(y,z);
	r = e;
	mpz_init(temp);
	mpz_sub_ui(temp, q, 1);
	mpz_div_ui(temp, temp, 2);
	mpz_powm(x,a2,temp,prime_number);
	mpz_pow_ui(temp,x,2);
	mpz_mul(temp, temp, a2);
	mpz_init(b);
	mpz_mod(b,temp,prime_number);
	mpz_mul(temp, a2, x);
	mpz_mod(x,temp,prime_number);

// 3 Find exponent
	mpz_mod(temp, b, prime_number);
	mpz_init(t);

	if (mpz_cmp_ui(temp,1) != 0){
		
		mpz_init(temp2);
		while(mpz_cmp_ui(temp,1) != 0){
			
			m=1;
			mp=power(2,m);
			mpz_powm_ui(temp2,b,mp,prime_number);
			
			while(mpz_cmp_ui(temp2,1) !=0)
				{
				m++;
				mp=power(2,m);
				mpz_powm_ui(temp2,b,mp,prime_number);
				}
			
			if (m==r){
				printf("a is not a quadratic residue mod p...\n");
				//exit(0);
			}	
			
// 4 Reduce exponent
			r2 = r-m-1;
			r2 = power(2,r2);
	
			mpz_powm_ui(t,y,r2,prime_number);
			mpz_powm_ui(y,t,2,prime_number);
			r=m;
			mpz_mul(x,x,t);
			mpz_mod(x,x,prime_number);
			mpz_mul(b,b,y);
			mpz_mod(b,b,prime_number);
			mpz_mod(temp, b, prime_number);

		}
	}
	
	mpz_clear(a);
	mpz_clear(a_prime);
	mpz_clear(b);
	mpz_clear(rand_n);
	mpz_clear(y);
	mpz_clear(z);
	mpz_clear(q);
	mpz_clear(t);
	mpz_clear(temp);
	mpz_clear(temp2);
}	

//CORNACCHIA ALGORITHM
extern struct gaussInt cornacchia(mpz_t prime){
	
	struct gaussInt resultats;
	int d = 1;
	int k2;
	mpz_t temp,temp3,a2,a,x01,b,l,c,r;
	mpz_t prime_number;
	mpz_t x;
	mpz_init(prime_number);
	mpz_set(prime_number,prime);

	// 1 Test if residue
	mpz_init(temp);
	mpz_set_ui(temp,d);
	mpz_neg(temp,temp);

	k2= mpz_legendre(temp,prime_number);
	mpz_clear(temp);

	if (k2 == -1) {
		printf("the equation has no solution\n");
		//exit (0);
	}


	// 2 Compute square root
	mpz_init(a2);
	mpz_set_ui(a2,d);
	mpz_neg(a2,a2);
	mpz_init(x);
	mpz_init(x01);

	shanks(a2,prime_number,x);
	mpz_set(x01,x);

	if (mpz_cmp(x01,prime_number)>0){ 
		mpz_neg(x01,x01);
		}

	mpz_init(temp3);
	mpz_div_ui(temp3, prime_number,2);
	mpz_add_ui(temp3, temp3, 1);

	if (mpz_cmp(x01,temp3)>=0){
		while (!(mpz_cmp(x01,temp3)>=0)){
			mpz_add(x01, x01, prime_number);
		}
	}

	mpz_init(a);
	mpz_set(a,prime_number);
	mpz_init(b);
	mpz_set(b,x01);
	mpz_init(l);
	mpz_sqrt(l,prime_number);

	// 3 Euclidean algorithm
	mpz_init(r);

	if (mpz_cmp(b,l)>0){
		while ((mpz_cmp(b,l)>0)){
			mpz_mod(r,a,b);
			mpz_set(a,b);
			mpz_set(b,r);
			}
		
	}

	// 4 Test solution: ici, comme d = 1, les tests ne sont pas utiles
	mpz_pow_ui(temp3,b,2);
	mpz_neg(temp3,temp3);
	mpz_add(temp3,temp3,prime_number);
	mpz_init(c); 
	mpz_set(c, temp3);
	mpz_sqrt(c,c);
	
	mpz_init(resultats.a);
	mpz_set(resultats.a,b);
	mpz_abs(resultats.a,resultats.a);

	mpz_init(resultats.b);
	mpz_set(resultats.b,c);
	mpz_abs(resultats.b,resultats.b);

	mpz_clear(temp3);
	mpz_clear(a);	
	mpz_clear(x01);
	mpz_clear(l);	
	mpz_clear(a2);	
	mpz_clear(b);	
	mpz_clear(c);	
	mpz_clear(r);	
	mpz_clear(prime_number);

	return resultats;

}

//-------------programmed by Malik Hammoutene---------------

