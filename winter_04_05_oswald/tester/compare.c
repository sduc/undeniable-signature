#include <stdio.h>
#define VC_EXTRALEAN
#include "gmp.h"
#include "frequence_cpu.h"			//time measuring
#include "help.h"				//additional functions
#include "quartic1.h"				//quartic: basic algorithm
#include "quartic2.h"				//quartic: basic algorithm optimized
#include "damgard.h"				//quartic: damgard's algorithm
#include "jacobi.h"				//jacobi: basic algorithm using modulo for reduction
#include "discreteLogarithm.h"			//discrete logarithm: precomputed table, baby step giant step, pollard's rho algorithm
#include "rsa.h"				//rsa: key generation and signature functions
 
int tests;			//number of tests to carry out
double freq;			//frequency of cpu
double before, after;		//for time measuring
gmp_randstate_t state;		//random number generator

//quartic: verify the homomorphic property
void testQuarticHom(FILE *pf, int bits){
    int i, j , primary, wrong;
    struct gaussInt xm1, ym1, solm1, xm2, ym2, solm2,xm3, ym3, solm3;
    mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
    mpz_init(xm3.a);mpz_init(xm3.b);mpz_init(ym3.a);mpz_init(ym3.b);mpz_init(solm3.a);mpz_init(solm3.b);
    mpz_init(xm1.a);mpz_init(xm1.b);mpz_init(ym1.a);mpz_init(ym1.b);mpz_init(solm1.a);mpz_init(solm1.b);
    i = 0; wrong = 0;
    for ( j = 1; i < tests; j++){
	mpz_urandomb (ym1.a, state, bits);
        mpz_urandomb (ym1.b, state, bits);
        primary = primaryExp(ym1.a,ym1.b);
        while ( primary  == -1) {
	    j++;
            mpz_urandomb (ym1.a, state, bits);
            mpz_urandomb (ym1.b, state, bits);
            primary = primaryExp(ym1.a,ym1.b);
        }
        mpz_urandomb (xm1.a, state, bits);
        mpz_urandomb (xm1.b, state, bits);
	i++;
        mpz_urandomb (xm2.a, state, bits);
        mpz_urandomb (xm2.b, state, bits);
		mpz_set(ym2.a,ym1.a);mpz_set(ym2.b,ym1.b);
		mpz_set(ym3.a,ym1.a);mpz_set(ym3.b,ym1.b);

		xm3 = MultComplex(xm1,xm2);

		solm1 = quarticd(xm1,ym1);
		solm2 = quarticd(xm2,ym2);
		solm3 = quarticd(xm3,ym3);

		xm3 = MultComplex(solm1,solm2);

		if ((mpz_cmp(xm3.a,solm3.a)!=0) || (mpz_cmp(xm3.b, solm3.b)!=0)){
			wrong++;
			printf("not hom!\n");
		}
	}
	fprintf(pf,"%i mistakes",wrong);
	mpz_clear(xm1.a);mpz_clear(xm1.b);mpz_clear(ym1.a);mpz_clear(ym1.b);
    mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
    mpz_clear(xm3.a);mpz_clear(xm3.b);mpz_clear(ym3.a);mpz_clear(ym3.b);
    mpz_clear(solm1.a);mpz_clear(solm1.b);
    mpz_clear(solm2.a);mpz_clear(solm2.b);
    mpz_clear(solm3.a);mpz_clear(solm3.b);
}

//quartic: count the number of times the main loop is executed
void testQuarticIter(FILE *pf, int bitsx, int bitsy){
	int i, j , primary, tm1, tm2;
	float ttm1, ttm2;
	struct gaussInt xm2, ym2, solm2;
	struct cornAlgo xm1, ym1, solm1;

	ttm1 = 0.0;
	ttm2 = 0.0;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm1.x_sol);mpz_init(xm1.y_sol); mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);

	i = 0;
	for ( j = 1; i < tests; j++){
		mpz_urandomb (ym1.x_sol, state, bitsy/2);
		mpz_urandomb (ym1.y_sol, state, bitsy/2);
		primary = primaryExp(ym1.x_sol,ym1.y_sol);
		while ( primary  == -1) {
			j++;
			mpz_urandomb (ym1.x_sol, state, bitsy/2);
			mpz_urandomb (ym1.x_sol, state, bitsy/2);
			primary = primaryExp(ym1.x_sol,ym1.y_sol);
		}
		mpz_urandomb (xm1.x_sol, state,bitsx);
		mpz_urandomb (xm1.y_sol, state,bitsx);
		mpz_set(xm2.a,xm1.x_sol);
		mpz_set(xm2.b,xm1.y_sol);
		mpz_set(ym2.a,ym1.x_sol);
		mpz_set(ym2.b,ym1.y_sol);

		//basic algo
		tm1 = quarticbIt(xm1,ym1);


		//damgard
		tm2 = quarticdIt(xm2,ym2);

		if ((tm1 > -1)&&(tm2 > 0)){
			ttm1 = ttm1 + tm1;
        	ttm2 = ttm2 + tm2;
			i++;
		}
	}
	ttm1 = (ttm1 / i);
	ttm2 = (ttm2 / i);
	fprintf(pf,"Basic algorithm: \t%f\nDamgard's algorithm: \t%f",ttm1,ttm2);
	mpz_clear(xm1.x_sol); mpz_clear(xm1.y_sol); mpz_clear(xm2.a); mpz_clear(xm2.b);
	mpz_clear(solm1.x_sol); mpz_clear(solm1.y_sol); mpz_clear(solm2.a); mpz_clear(solm2.b);
	mpz_clear(ym1.x_sol); mpz_clear(ym1.y_sol); mpz_clear(ym2.a); mpz_clear(ym2.b);
}

//quartic: count the number of times the main loop is executed with constraint beta = pi*sigma
void testQuarticPrimeIter(FILE *pf, int bitsx, int bitsy){
	int i, j , tm1, tm2;
	float ttm1, ttm2;
	struct gaussInt xm2, ym2, solm2,xm3, ym3, solm3, pi, sigma;
	struct cornAlgo xm1, ym1, solm1;
	mpz_t temp;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm3.a);mpz_init(xm3.b);mpz_init(ym3.a);mpz_init(ym3.b);mpz_init(solm3.a);mpz_init(solm3.b);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
	ttm1 = 0.0;
	ttm2 = 0.0;
	mpz_init(temp);
	mpz_init(pi.a); mpz_init(pi.b); mpz_init(sigma.a); mpz_init(sigma.b);
	i = 0;
	for ( j = 1; i < tests; j++){
		getRand2(temp, bitsy);
		genPrime(temp,temp);
		pi = cornacchia(temp);
		if (primaryExp(pi.a,pi.b) == -1) printf("pi not primary\n");

		getRand2(temp, bitsy);
		genPrime(temp,temp);
		sigma = cornacchia(temp);
		if (primaryExp(sigma.a,sigma.b) == -1) printf("sigma not primary\n");

		ym2 = MultComplex(pi,sigma);
		if (primaryExp(ym2.a,ym2.b) == -1) printf("ym2 not primary\n");

		mpz_urandomb (xm1.x_sol, state, bitsx);
        mpz_urandomb (xm1.y_sol, state, bitsx);
        mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym1.x_sol,ym2.a);mpz_set(ym1.y_sol,ym2.b);
        mpz_set(xm3.a,xm1.x_sol);mpz_set(xm3.b,xm1.y_sol);mpz_set(ym3.a,ym2.a);mpz_set(ym3.b,ym2.b);

 		//basic algo
		tm1 = quarticbIt(xm1,ym1);


		//damgard
		tm2 = quarticdIt(xm2,ym2);

		if ((tm1 > -1)&&(tm2 > 0)){
			ttm1 = ttm1 + tm1;
        	ttm2 = ttm2 + tm2;
			i++;
		}
	}
	ttm1 = (ttm1 / i);
	ttm2 = (ttm2 / i);
	fprintf(pf,"Basic algorithm: \t%f\nDamgard's algorithm: \t%f",ttm1,ttm2);
	mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(xm2.a);mpz_clear(xm2.b);
	mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);mpz_clear(solm2.a);mpz_clear(solm2.b);
	mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);mpz_clear(ym2.a);mpz_clear(ym2.b);
}

//quartic: count the number of times the main loop is executed with constraint beta = pi
void testQuarticPrimeIter2(FILE *pf, int bitsx, int bitsy){
	int i, j , tm1, tm2;
	float ttm1, ttm2;
	struct gaussInt xm2, ym2, solm2,xm3, ym3, solm3, pi, sigma;
	struct cornAlgo xm1, ym1, solm1;
	mpz_t temp;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm3.a);mpz_init(xm3.b);mpz_init(ym3.a);mpz_init(ym3.b);mpz_init(solm3.a);mpz_init(solm3.b);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
	ttm1 = 0.0;
	ttm2 = 0.0;
	mpz_init(temp);
	mpz_init(pi.a); mpz_init(pi.b); mpz_init(sigma.a); mpz_init(sigma.b);
	i = 0;
	for ( j = 1; i < tests; j++){
		getRand2(temp, bitsy);
		genPrime(temp,temp);
		ym2 = cornacchia(temp);
		if (primaryExp(ym2.a,ym2.b) == -1) printf("ym2 not primary\n");

		mpz_urandomb (xm1.x_sol, state, bitsx);
        mpz_urandomb (xm1.y_sol, state, bitsx);
        mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym1.x_sol,ym2.a);mpz_set(ym1.y_sol,ym2.b);
        mpz_set(xm3.a,xm1.x_sol);mpz_set(xm3.b,xm1.y_sol);mpz_set(ym3.a,ym2.a);mpz_set(ym3.b,ym2.b);

 		//basic algo
		tm1 = quarticbIt(xm1,ym1);

		//damgard
		tm2 = quarticdIt(xm2,ym2);

		if ((tm1 > -1)&&(tm2 > 0)){
			ttm1 = ttm1 + tm1;
        	ttm2 = ttm2 + tm2;
			i++;
		}
	}

	ttm1 = (ttm1 / i);
	ttm2 = (ttm2 / i);
	fprintf(pf,"Basic algorithm: \t%f\nDamgard's algorithm: \t%f",ttm1,ttm2);

	mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(xm2.a);mpz_clear(xm2.b);
	mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);mpz_clear(solm2.a);mpz_clear(solm2.b);
	mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);mpz_clear(ym2.a);mpz_clear(ym2.b);
}

//quartic: measure the time of the computation
void testQuartic(FILE *pf, int bitsx, int bitsy){
	int i, j , primary;
	double tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
    double td=0.0,tm=0.0, tmm=0.0;
    struct gaussInt xm2, ym2, solm2,xm3, ym3, solm3;
	struct cornAlgo xm1, ym1, solm1;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm3.a);mpz_init(xm3.b);mpz_init(ym3.a);mpz_init(ym3.b);mpz_init(solm3.a);mpz_init(solm3.b);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
        i = 0;
        for ( j = 1; i < tests; j++){
                mpz_urandomb (ym1.x_sol, state, bitsy);
                mpz_urandomb (ym1.y_sol, state, bitsy);
                primary = primaryExp(ym1.x_sol,ym1.y_sol);
                while ( primary  == -1) {
                        j++;
                        mpz_urandomb (ym1.x_sol, state, bitsy);
                        mpz_urandomb (ym1.x_sol, state, bitsy);
                        primary = primaryExp(ym1.x_sol,ym1.y_sol);
                }
                mpz_urandomb (xm1.x_sol, state,bitsx);
                mpz_urandomb (xm1.y_sol, state,bitsx);
                mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym2.a,ym1.x_sol);mpz_set(ym2.b,ym1.y_sol);
                mpz_set(xm3.a,xm1.x_sol);mpz_set(xm3.b,xm1.y_sol);mpz_set(ym3.a,ym1.x_sol);mpz_set(ym3.b,ym1.y_sol);

                //basic algo
              	before = RDTSC();
                        solm1 = quarticb1(xm1,ym1);
                after = RDTSC();
                tm1 =  after - before ;


                //basic algo optimized
                before = RDTSC();
                	solm2 = quarticb2(xm2,ym2);
                after = RDTSC();
                tm2 =     after - before ;



                //damgard
                before = RDTSC();
                        solm3 = quarticd(xm3,ym3);
				after = RDTSC();
                tm3 =     after - before ;


				if ((mpz_cmp_ui(solm3.a,0) != 0) || (mpz_cmp_ui(solm3.b,0) != 0)){
					i++;
					ttm1 = ttm1 + tm1;
					ttm2 = ttm2 + tm2;
					ttm3 = ttm3 + tm3;
				}
        }
        td =     (ttm1/    freq);
        tm =     (ttm2 /    freq);
        tmm =     (ttm3 /   freq);
        ttm1 =      (ttm1 / i);
        ttm2 =      (ttm2 / i);
        ttm3 =      (ttm3 / i);
        td = td/i;
        tm = tm/i;
        tmm = tmm/i;
        fprintf(pf,"\nBasic algorithm: \t\t%f\t%f\nBasic algorithm optimized: \t%f\t%f\nDamgard's algorithm: \t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
        mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
        mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
        mpz_clear(xm3.a);mpz_clear(xm3.b);mpz_clear(ym3.a);mpz_clear(ym3.b);
        mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
        mpz_clear(solm2.a);mpz_clear(solm2.b);
        mpz_clear(solm3.a);mpz_clear(solm3.b);

}

//quartic: measure the time of the computation with constraint beta = pi*sigma
void testQuarticPrime(FILE *pf, int bitsx, int bitsy){
	int i, j ;
	double tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
    double td=0.0,tm=0.0, tmm=0.0;
	struct gaussInt xm2, ym2, solm2,xm3, ym3, solm3, pi, sigma;
	struct cornAlgo xm1, ym1, solm1;
	 mpz_t temp;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm3.a);mpz_init(xm3.b);mpz_init(ym3.a);mpz_init(ym3.b);mpz_init(solm3.a);mpz_init(solm3.b);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
	ttm1 = 0.0;
	ttm2 = 0.0;
	mpz_init(temp);	mpz_init(pi.a); mpz_init(pi.b); mpz_init(sigma.a); mpz_init(sigma.b);
	i = 0;
	for ( j = 1; i < tests; j++){
		getRand2(temp, bitsy);
		genPrime(temp,temp);
		pi = cornacchia(temp);
		if (primaryExp(pi.a,pi.b) == -1) printf("pi not primary\n");

		getRand2(temp, bitsy);
		genPrime(temp,temp);
		sigma = cornacchia(temp);
		if (primaryExp(sigma.a,sigma.b) == -1) printf("sigma not primary\n");

		ym2 = MultComplex(pi,sigma);
		if (primaryExp(ym2.a,ym2.b) == -1) printf("ym2 not primary\n");

		mpz_urandomb (xm1.x_sol, state, bitsx);
        mpz_urandomb (xm1.y_sol, state, bitsx);
        mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym1.x_sol,ym2.a);mpz_set(ym1.y_sol,ym2.b);
        mpz_set(xm3.a,xm1.x_sol);mpz_set(xm3.b,xm1.y_sol);mpz_set(ym3.a,ym2.a);mpz_set(ym3.b,ym2.b);

		//basic algo
        before = RDTSC();
              solm1 = quarticb1(xm1,ym1);
        after = RDTSC();
        tm1 =  after - before ;

        //basic algo optimized
        before = RDTSC();
			solm2 = quarticb2(xm2,ym2);
		after = RDTSC();
        tm2 =     after - before ;

        //damgard
        before = RDTSC();
	        solm3 = quarticd(xm3,ym3);
		after = RDTSC();
        tm3 =     after - before ;

		if ((mpz_cmp_ui(solm3.a,0) != 0) || (mpz_cmp_ui(solm3.b,0) != 0)){
			i++;
			ttm1 = ttm1 + tm1;
			ttm2 = ttm2 + tm2;
			ttm3 = ttm3 + tm3;
			}
        }
        td = ttm1/freq;
        tm = ttm2/freq;
        tmm = ttm3/freq;
        ttm1 = ttm1/i;
        ttm2 = ttm2/i;
        ttm3 = ttm3/i;
        td = td/i;
        tm = tm/i;
        tmm = tmm/i;
		fprintf(pf,"\nBasic algorithm: \t\t%f\t%f\nBasic algorithm optimized: \t%f\t%f\nDamgard's algorithm: \t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
        mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
        mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
        mpz_clear(xm3.a);mpz_clear(xm3.b);mpz_clear(ym3.a);mpz_clear(ym3.b);
        mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
        mpz_clear(solm2.a);mpz_clear(solm2.b);
        mpz_clear(solm3.a);mpz_clear(solm3.b);
}
//quartic: measure the time of the computation with constraint beta = pi
void testQuarticPrime2(FILE *pf, int bitsx, int bitsy){
	int i, j ;
	double tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
    double td=0.0,tm=0.0, tmm=0.0;
	struct gaussInt xm2, ym2, solm2,xm3, ym3, solm3;
	struct cornAlgo xm1, ym1, solm1;
	 mpz_t temp;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm3.a);mpz_init(xm3.b);mpz_init(ym3.a);mpz_init(ym3.b);mpz_init(solm3.a);mpz_init(solm3.b);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
	ttm1 = 0.0;
	ttm2 = 0.0;
	mpz_init(temp);
	i = 0;
	for ( j = 1; i < tests; j++){
		getRand2(temp, bitsy);
		genPrime(temp,temp);
		ym2 = cornacchia(temp);
		if (primaryExp(ym2.a,ym2.b) == -1) printf("ym2 not primary\n");

		mpz_urandomb (xm1.x_sol, state, bitsx);
        mpz_urandomb (xm1.y_sol, state, bitsx);
        mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym1.x_sol,ym2.a);mpz_set(ym1.y_sol,ym2.b);
        mpz_set(xm3.a,xm1.x_sol);mpz_set(xm3.b,xm1.y_sol);mpz_set(ym3.a,ym2.a);mpz_set(ym3.b,ym2.b);

		//basic algo
        before = RDTSC();
              solm1 = quarticb1(xm1,ym1);
        after = RDTSC();
        tm1 =  after - before ;

        //basic algo optimized
        before = RDTSC();
			solm2 = quarticb2(xm2,ym2);
		after = RDTSC();
        tm2 =     after - before ;

        //damgard
        before = RDTSC();
	        solm3 = quarticd(xm3,ym3);
		after = RDTSC();
        tm3 =     after - before ;

		if ((mpz_cmp_ui(solm3.a,0) != 0) || (mpz_cmp_ui(solm3.b,0) != 0)){
			i++;
			ttm1 = ttm1 + tm1;
			ttm2 = ttm2 + tm2;
			ttm3 = ttm3 + tm3;
			}
        }
        td = ttm1/freq;
        tm = ttm2/freq;
        tmm = ttm3/freq;
        ttm1 = ttm1/i;
        ttm2 = ttm2/i;
        ttm3 = ttm3/i;
        td = td/i;
        tm = tm/i;
        tmm = tmm/i;
		fprintf(pf,"\nBasic algorithm: \t\t%f\t%f\nBasic algorithm optimized: \t%f\t%f\nDamgard's algorithm: \t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
        mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
        mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
        mpz_clear(xm3.a);mpz_clear(xm3.b);mpz_clear(ym3.a);mpz_clear(ym3.b);
        mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
        mpz_clear(solm2.a);mpz_clear(solm2.b);
        mpz_clear(solm3.a);mpz_clear(solm3.b);
}

//quartic: measure the time of subfunction multiplication in Z[i]
void testMul(FILE *pf, int bits){
    int i, j;
	double tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
    double td=0.0,tm=0.0, tmm=0.0;
	mpz_t xm3, ym3, solm3;
    struct gaussInt xm2, ym2, solm2;
	struct cornAlgo xm1, ym1, solm1;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
    mpz_init(xm3);mpz_init(ym3);mpz_init(solm3);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
    i = 0;
    for ( j = 1; i < tests; j++){
	mpz_urandomb (ym1.x_sol, state, bits);
        mpz_urandomb (ym1.y_sol, state, bits);
        mpz_urandomb (xm1.x_sol, state, bits);
        mpz_urandomb (xm1.y_sol, state, bits);
        mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);
	mpz_set(ym2.a,ym1.x_sol);mpz_set(ym2.b,ym1.y_sol);
        mpz_set(xm3,xm1.x_sol);mpz_set(ym3,ym1.x_sol);
        i++;

		//malik
        before = RDTSC();
		solm1 = multComplex(xm1,ym1);
        after = RDTSC();

        tm1 = after - before ;
        ttm1 = ttm1 + tm1;

        //optimized
        before = RDTSC();
		solm2 = MultComplex(xm2,ym2);
        after = RDTSC();

        tm2 = after - before ;
        ttm2 = ttm2 + tm2;


        //gmp
        before = RDTSC();
		mpz_mul(solm3,xm3,ym3);
        after = RDTSC();

        tm3 = after - before ;
        ttm3 = ttm3 + tm3;
	}
    td = ttm1/freq;
    tm = ttm2/freq;
    tmm = ttm3/freq;
    ttm1 = ttm1/i;
    ttm2 = ttm2/i;
    ttm3 = ttm3/i;
    td = td/i;
    tm = tm/i;
    tmm = tmm/i;
    fprintf(pf,"\nNot optimized: \t%f\t%f\nOptimized: \t%f\t%f\ngmp: \t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
    mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
    mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
    mpz_clear(xm3);mpz_clear(ym3);mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
    mpz_clear(solm2.a);mpz_clear(solm2.b);mpz_clear(solm3);
}
//quartic: measure the time of subfunction modulo in Z[i]
void testMod(FILE *pf, int bits){
    int i, j;
	double tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
    double td=0.0,tm=0.0, tmm=0.0;
	mpz_t xm3, ym3, solm3;
    struct gaussInt xm2, ym2, solm2;
	struct cornAlgo xm1, ym1, solm1;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
    mpz_init(xm3);mpz_init(ym3);mpz_init(solm3);
    mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
    i = 0;
    for ( j = 1; i < tests; j++){
		mpz_urandomb (ym1.x_sol, state, bits);
        mpz_urandomb (ym1.y_sol, state, bits);
        mpz_urandomb (xm1.x_sol, state, bits);
        mpz_urandomb (xm1.y_sol, state, bits);
        mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym2.a,ym1.x_sol);mpz_set(ym2.b,ym1.y_sol);
        mpz_set(xm3,xm1.x_sol);mpz_set(ym3,ym1.x_sol);
        i++;

		//malik
        before = RDTSC();
			solm1 = moduloGauss(xm1,ym1);   
        after = RDTSC();
                
        tm1 = after - before ;
        ttm1 = ttm1 + tm1;
                
        //optimized
        before = RDTSC();
			solm2 = ModuloGauss(xm2,ym2);
        after = RDTSC();
                        
        tm2 = after - before ;
        ttm2 = ttm2 + tm2;


        //gmp
        before = RDTSC();
			mpz_mod(solm3,xm3,ym3);
        after = RDTSC();
                        
        tm3 = after - before ;
        ttm3 = ttm3 + tm3;
	}
    td = ttm1/freq;
    tm = ttm2/freq;
    tmm = ttm3/freq;
    ttm1 = ttm1/i; 
    ttm2 = ttm2/i;
    ttm3 = ttm3/i;
    td = td/i;
    tm = tm/i;
    tmm = tmm/i;
    fprintf(pf,"\nNot optimized: \t%f\t%f\nOptimized: \t%f\t%f\ngmp: \t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
    mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
    mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
    mpz_clear(xm3);mpz_clear(ym3);mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
    mpz_clear(solm2.a);mpz_clear(solm2.b);mpz_clear(solm3);
}
//quartic: measure the time of subfunction division by (1+i)^r in Z[i]
void testDiv(FILE *pf, int bits){
    int i, j, r ;
	double tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
  	double td=0.0,tm=0.0, tmm=0.0;
    mpz_t xm3, ym3, solm3, n;
    struct gaussInt xm2, ym2, solm2;
	struct cornAlgo xm1, ym1, solm1;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
    mpz_init(xm3);mpz_init(ym3);mpz_init(solm3); mpz_init(n);
  	mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
    i = 0;
    for ( j = 1; i < tests; j++){
		mpz_urandomb (ym1.x_sol, state, bits);
		mpz_urandomb (ym1.y_sol, state, bits);
		i++;
		mpz_urandomb (xm1.x_sol, state, bits);
		mpz_urandomb (xm1.y_sol, state, bits);
		mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);mpz_set(ym2.a,ym1.x_sol);mpz_set(ym2.b,ym1.y_sol);
		mpz_set(xm3,xm1.x_sol);mpz_set(ym3,ym1.x_sol);
		norm(n,ym2);
		r = mpz_scan1(n,0);

        //malik1

		before = RDTSC();
			solm1 = powerUnPlusI(r);
			solm1 = divExactComplex(ym1,solm1);
		after = RDTSC();
		tm1 = after - before ;
		ttm1 = ttm1 + tm1;


		//optimized
		before = RDTSC();
			solm2 = divUnPlusI(ym2,r);
		after = RDTSC();
		tm2 = after - before ;
		ttm2 = ttm2 + tm2;

		//gmp
		before = RDTSC();
			mpz_tdiv_q_2exp(solm3,n,r);
		after = RDTSC();
		tm3 = after - before ;
		ttm3 = ttm3 + tm3;
	}
	td = ttm1/freq;
    tm = ttm2/freq;
    tmm = ttm3/freq;
    ttm1 = ttm1/i;
    ttm2 = ttm2/i;
    ttm3 = ttm3/i;
    td = td/i;
    tm = tm/i;
    tmm = tmm/i;
    fprintf(pf,"\nNot optimized: \t%f\t%f\nOptimized: \t%f\t%f\ngmp: \t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
    mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
	mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
	mpz_clear(xm3);mpz_clear(ym3);
	mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
	mpz_clear(solm2.a);mpz_clear(solm2.b);
	mpz_clear(solm3);
}
//quartic: measure the time of subfunction transformation into primary
void testTra(FILE *pf, int bits){
	int i, j;
	double tm1, tm2, ttm1=0.0, ttm2=0.0;
	double td=0.0,tm=0.0;
	mpz_t xm3, ym3, solm3;
	struct gaussInt xm2, ym2, solm2;
	struct cornAlgo xm1, ym1, solm1;
	mpz_init(xm2.a);mpz_init(xm2.b);mpz_init(ym2.a);mpz_init(ym2.b);mpz_init(solm2.a);mpz_init(solm2.b);
	mpz_init(xm3);mpz_init(ym3);mpz_init(solm3);
	mpz_init(xm1.x_sol);mpz_init(xm1.y_sol);
	mpz_init(ym1.x_sol);mpz_init(ym1.y_sol);mpz_init(solm1.x_sol);mpz_init(solm1.y_sol);
	i = 0;
	for ( j = 1; i < tests; j++){
		mpz_urandomb (ym1.x_sol, state, bits);
		mpz_urandomb (ym1.y_sol, state, bits);
		i++;
		mpz_urandomb (xm1.x_sol, state, bits);
		mpz_urandomb (xm1.y_sol, state, bits);
		mpz_set(xm2.a,xm1.x_sol);mpz_set(xm2.b,xm1.y_sol);
		mpz_set(ym2.a,ym1.x_sol);mpz_set(ym2.b,ym1.y_sol);

		//malik
		before = RDTSC();
			solm1 = transformPrimary(xm1.x_sol, xm1.y_sol);
		after = RDTSC();
		tm1 = after - before;
		ttm1 = ttm1 + tm1;


		//optimized
		before = RDTSC();
			primaryExp(xm2.a,xm2.b);
		after = RDTSC();
		tm2 = after - before;
		ttm2 = ttm2 + tm2;
	}
	td = ttm1/freq;
	tm = ttm2/freq;
	ttm1 = ttm1/i;
	ttm2 = ttm2/i;
	td = td/i;
	tm = tm/i;
	fprintf(pf,"\nNot optimized: \t%f\t%f\nOptimized: \t%f\t%f",td,ttm1,tm,ttm2);
	mpz_clear(xm1.x_sol);mpz_clear(xm1.y_sol);mpz_clear(ym1.x_sol);mpz_clear(ym1.y_sol);
	mpz_clear(xm2.a);mpz_clear(xm2.b);mpz_clear(ym2.a);mpz_clear(ym2.b);
	mpz_clear(solm1.x_sol);mpz_clear(solm1.y_sol);
	mpz_clear(solm2.a);mpz_clear(solm2.b);
}

//jacobi: measure the time of the computation
void testJac(FILE *pf, int bits){
	int i, j, r1, r2;
	double tm1, tm2, ttm1=0.0, ttm2=0.0;
	double td=0.0,tm=0.0;
	mpz_t xm1, ym1,xm2, ym2;
	mpz_init(xm1);mpz_init(ym1); mpz_init(xm2);mpz_init(ym2);
	i = 0;
	for ( j = 1; i  < tests; j++){
		mpz_urandomb (ym1, state, 2*bits);
		mpz_urandomb (xm1, state, bits);
		mpz_setbit(ym1,0);                
		mpz_set(xm2,xm1);mpz_set(ym2,ym1);
                
		//jacobi1
                
		before = RDTSC();                        
			r1 = jacobi(xm1,ym1);                
		after = RDTSC();                
		tm1 = after - before;
                           
                
		//GMP                
		before = RDTSC();                        		
			r2 = mpz_jacobi(xm2,ym2);    
		after = RDTSC();                
		tm2 = after - before;          
				
		if (r1 != 0){					
			i++;					
			ttm1 = ttm1 + tm1;					
			ttm2 = ttm2 + tm2;				
		}        
	}
	td = ttm1/freq;        
	tm = ttm2/freq;        
	ttm1 = ttm1/i;        
	ttm2 = ttm2/i;        
	td = td/i;        
	tm = tm/i;        
	fprintf(pf,"Basic algorithm: \t%f\t%f\nBinary algorithm(gmp): \t%f\t%f",td,ttm1,tm,ttm2);
	mpz_clear(xm1); mpz_clear(xm2); mpz_clear(ym1); mpz_clear(ym2);         
}

//jacobi: count the number of times the main loop is executed
void testJacIt(FILE *pf, int bits){
	int i, j, it;
	double ttm1=0.0;
	mpz_t xm1, ym1;
	mpz_init(xm1);mpz_init(ym1);
        
	i = 0;
	for ( j = 1; i < tests; j++){
		mpz_urandomb (ym1, state, bits); 
		mpz_urandomb (xm1, state,2*bits);
	    mpz_setbit(ym1,0);
        	
		it = jacobiIter(xm1,ym1);
		if (it > 0 ){
            ttm1 += it;
			i++;
		}
	}
	ttm1 = ttm1 / i;
	fprintf(pf,"Basic algorithm: \t%f\n",ttm1);
	mpz_clear(xm1);  mpz_clear(ym1);
}


//discrete logarithm: verify the homomorphic property
void testLogHom(FILE *pf, int bits){
    int i, j, wrong;
	unsigned int *log_t;
	mpz_t d,r,p,q,n,h,t;
	mpz_t xh,xb,xp,yh,yb,yp;

	mpz_init(d);
	mpz_init(r);
	mpz_init(p);
	mpz_init(q);
	mpz_init(n);
	mpz_init(h);
	mpz_init(t);

	mpz_init(xh);mpz_init(xb);mpz_init(xp);
	mpz_init(yh);mpz_init(yb);mpz_init(yp);

	setupLogarithm(d,r,p,q,h,n,state,bits);
	log_t = logTable(h,d,p);

    i = 0; wrong = 0;
    for ( j = 1; i < tests; j++){
		mpz_urandomm(xh, state, n);
		mpz_urandomm(xb, state, n);
		mpz_mul(xp,xb,xh);
		mpz_mod(xp,xp,n);
		i++;

		homTable(yh,h, r, p, log_t, xh);
		homTable(yb,h, r, p, log_t, xb);
		homTable(yp,h, r, p, log_t, xp);

		mpz_add(xp,yh,yb);
		mpz_mod(xp,xp,d);
		if (mpz_cmp(xp,yp)!=0){
			wrong++;
			printf("not hom!\n");
		}



	}
	fprintf(pf,"%i mistakes",wrong);
	free(log_t);
	mpz_clear(h);mpz_clear(r);mpz_clear(p);mpz_clear(n);mpz_clear(q);mpz_clear(t);
	mpz_clear(xh);mpz_clear(xb);mpz_clear(xp);
}


//discrete logarithm: count the number of times the main loop is executed
void testLogIter(FILE *pf, int bits){
    int i, j,it;
	double lu1, lu2,it2;
	unsigned int *log_t, *bsgs_t;
	mpz_t d,r,p,q,n,h,t;
	mpz_t xb,xp,xh;

	mpz_init(d);
	mpz_init(r);
	mpz_init(p);
	mpz_init(q);
	mpz_init(n);
	mpz_init(h);
	mpz_init(t);

	mpz_init(xb);mpz_init(xp);mpz_init(xh);

	setupLogarithm(d,r,p,q,h,n,state,bits);
	log_t = logTable(h,d,p);
	bsgs_t = bSGSTable(h,d,p);
      
	i = 0; lu1 = 0.0; lu2 = 0.0; it2 = 0.0,it = 0;
    for ( j = 1; i < tests; j++){
		mpz_urandomm(xb, state, n);
		mpz_set(xp,xb);
		mpz_set(xh,xb);
		i++;

		it  = homTableLU(h,r,p,log_t,xb);
		lu1 = lu1 + it;
		it = homBSGSIter(h, d, r, p, bsgs_t, xb);
		lu2 = lu2 + it;
		it = homPollardIter(h, d, r, p, xp);
		it2 = it2 + it;

	}
	lu1 = lu1/i;
	lu2 = lu2/i;
	it2 = it2/i;
	fprintf(pf,"d: %u\n",(unsigned int)mpz_get_ui(d));
	fprintf(pf,"LogTable Table Look Ups: \t%f\nBSGS Table Look Ups:\t\t%f\nPollardRho Iterations:\t\t%f",lu1,lu2,it2);
	free(log_t);
	free(bsgs_t);
	mpz_clear(h);mpz_clear(r);mpz_clear(p);mpz_clear(n);mpz_clear(q);mpz_clear(t);
	mpz_clear(xh);mpz_clear(xb);mpz_clear(xp);
}

//discrete logarithm: measure the time of the computation
void testLog(FILE *pf, int bits){
	int i;
	double st, ltt, btt, tm1, tm2, tm3, ttm1=0.0, ttm2=0.0, ttm3=0.0;
    double td=0.0,tm=0.0, tmm=0.0;
	unsigned int *log_t, *bsgs_t;
	mpz_t d,r,p,q,n,h,t;
	mpz_t xh,xb,xp,yh,yb,yp;

	mpz_init(d);
	mpz_init(r);
	mpz_init(p);
	mpz_init(q);
	mpz_init(n);
	mpz_init(h);
	mpz_init(t);

	mpz_init(xh);mpz_init(xb);mpz_init(xp);
	mpz_init(yh);mpz_init(yb);mpz_init(yp);

	before = RDTSC();
		setupLogarithm(d,r,p,q,h,n,state,bits);
	after = RDTSC();
	st = after - before ;
	fprintf(pf,"Setup time:\t\t%f\t%f\n",st/freq, st);
	
	before = RDTSC();
		log_t = logTable(h,d,p);
	after = RDTSC();
	ltt = after - before ;
	fprintf(pf,"Create LogTable time:\t%f\t%f\n",ltt/freq,ltt);


	before = RDTSC();
		bsgs_t = bSGSTable(h,d,p);
    after = RDTSC();
    btt = after - before ;
	fprintf(pf,"Create BSGSTable time:\t%f\t%f\n",btt/freq,btt);

	for (i=0; i < tests; i++){
		mpz_urandomm(xh, state, n);
		mpz_set(xb,xh);
		mpz_set(xp,xh);

		//LogTable
        before = RDTSC();
			homTable(yh,h, r, p, log_t, xh);
        after = RDTSC();

		tm1 =  after - before ;
        ttm1 = ttm1 + tm1;

                //BSGS
        before = RDTSC();
			homBSGS(yb, h, d, r, p, bsgs_t, xb);
        after = RDTSC();
                        
        tm2 = after - before ;
        ttm2 = ttm2 + tm2;


                //Pollard
         before = RDTSC();
			homPollard(yp, h, d, r, p, xp);
         after = RDTSC();

         tm3 = after - before ;
         ttm3 = ttm3 + tm3;
		

	}
    td = ttm1/freq;        
	tm = ttm2/freq;        
	tmm = ttm3/freq;        
	ttm1 = ttm1/i;        
	ttm2 = ttm2/i;        
	ttm3 = ttm3/i;        
	td = td/i;        
	tm = tm/i;        
	tmm = tmm/i;        
	fprintf(pf,"\nLogTable:\t\t%f\t%f\nBSGS:\t\t\t%f\t%f\nPollard:\t\t%f\t%f",td,ttm1,tm,ttm2,tmm,ttm3);
	free(log_t);
	free(bsgs_t);
	mpz_clear(h);mpz_clear(r);mpz_clear(p);mpz_clear(n);mpz_clear(q);mpz_clear(t);
	mpz_clear(xh);mpz_clear(xb);mpz_clear(xp);mpz_clear(yh);mpz_clear(yb);mpz_clear(yp);

}
//discrete logarithm: measure the time for two different implementation of
//pollard's rho algorithm (different sets S1, S2, S3)
void testPoll(FILE *pf, int bits){
	double tm2, tm3, ttm2=0.0, ttm3=0.0;
    double tm=0.0, tmm=0.0;
	mpz_t d,r,p,q,n,h,t;
	mpz_t xh,xb,xp,yh,yb,yp;
	int i,wrong;
	wrong = 0;

	mpz_init(d);
	mpz_init(r);
	mpz_init(p);
	mpz_init(q);
	mpz_init(n);
	mpz_init(h);
	mpz_init(t);

	mpz_init(xh);mpz_init(xb);mpz_init(xp);
	mpz_init(yh);mpz_init(yb);mpz_init(yp);

	setupLogarithm(d,r,p,q,h,n,state,bits);
	wrong = 0;
	for (i=0; i < tests; i++){
		mpz_urandomm(xb, state, n);
		mpz_set(xp,xb);
                
		//Pollard1
                
		before = RDTSC();
			homPollard(yb, h, d, r, p, xp);          
		after = RDTSC();   
		tm2 = after - before;                
		ttm2 = ttm2 + tm2;
                
		//Pollard3                
		before = RDTSC();
			homPollard2(yp, h, d, r, p, xp);                
		after = RDTSC();                
		tm3 = after - before;                
		ttm3 = ttm3 + tm3;
		wrong += mpz_cmp(yp,yb);
	}
	tm = ttm2/freq;        
	tmm = ttm3/freq;        
	ttm2 = ttm2/i;       
	ttm3 = ttm3/i;        
	tm = tm/i;        
	tmm = tmm/i;        
	fprintf(pf,"\nPollard:\t\t%f\nPollard3:\t\t%f",tm,tmm);
	mpz_clear(h);mpz_clear(r);mpz_clear(p);mpz_clear(n);mpz_clear(q);mpz_clear(t);
	mpz_clear(xb);mpz_clear(xp);mpz_clear(yb);mpz_clear(yp);
}

//RSA: measure time of computation
void testRSA(FILE *pf, int bits){
	int i;
	double tm1, ttm1=0.0;
	double td=0.0, st = 0.0;
	mpz_t m, sig;
	mpz_init(m); mpz_init(sig);
	      
	before = RDTSC();
		setupRSA(state, bits);
	after = RDTSC();
	st = after - before ;
	fprintf(pf,"Key Generation:\t\t\t%f\t%f\n",st/freq, st);

	for ( i = 0; i < tests; i++){
		//rsa
		before = RDTSC();
			rsa(sig,m);
		after = RDTSC();
		tm1 = after - before;
		ttm1 = ttm1 + tm1;
	}
	td = ttm1/freq;
	ttm1 = ttm1/i;
	td = td/i;
	fprintf(pf,"RSA signature generation:\t%f\t%f",td,ttm1);
	mpz_clear(m); mpz_clear(sig);
	clear();
}

int main(int argc, char* argv[]){
	FILE *pf;
	int bits,mode,i;
	char filename[30];
	initRandom();
	gmp_randinit_default (state);
	printf("    TESTER\n  __________\n");
	if (LitFrequenceCpu(&freq)) {
		printf("\n");
		printf("Enter filename to write test results to:  \n");
		scanf("%s",filename);
		if (( pf = fopen(filename,"w")) == NULL){
			printf("Error opening the file");
		}
		else {
			fprintf(pf,"TEST RESULTS (time in seconds and cpu cycles)\n\n");
			i = 0;
			while(i<10){
				i++;
				printf("\nEnter length of random numbers in bits(128 - 2048) and number of tests(1 - 1000)\n");
				scanf("%u %i",&bits,&tests);
				if ((bits < 128) || (bits > 2048) || (tests < 1) || (tests > 1000)){
					printf("Invalid entry, set #bits to 512 and #tests to 100\n");
					bits = 512; tests = 100;
				}
				printf("\nEnter Testmode: \n");
				printf("Quartic: \n");
				printf("  11  = Quartic: Homomorphism Property\n");
				printf("  12  = Quartic: Iterations\n");
				printf("  13  = Quartic: Time\n");
				printf("  14  = Quartic: Subfunctions Time\n");
				printf("Jacobi: \n");
				printf("  22  = Jacobi: Iterations\n");
				printf("  23  = Jacobi: Time\n");
				printf("Discrete Homomorphism: \n");
				printf("  31  = DiscreteLogarithm: Homomorphism Property\n");
				printf("  32  = DiscreteLogarithm: Iterations\n");
				printf("  33  = DiscreteLogarithm: Time\n");
				printf("RSA:\n");
				printf("  43  = RSA: Time\n\n");
				printf("Help:\n");				
				printf("  999 = Help: more information regarding the testmodes\n\n");
				scanf("%u",&mode);
				switch(mode){
					case 11:{
						fprintf(pf,"\nHOMOMORPHISM PROPERTY OF QUARTIC IMPLEMENTATIONS, %i bits, %i tests\n\n",bits,tests);
						testQuarticHom(pf,bits);
						break;
					}
					case 12:{
						printf("\nQuartic Iterations: Enter Testmode: \n");
						printf("  1 = alpha, beta random numbers of same size\n");
						printf("  2 = alpha, beta random numbers, alpha twice as big as beta\n");
						printf("  3 = alpha random number, beta=sigma*pi\n");
						printf("  4 = alpha random number, beta=pi\n");
						scanf("%u",&mode);
						switch(mode){
							case 1:{
								fprintf(pf,"\nNUMBER OF ITERATIONS OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta %i bits, %i tests\n\n",bits,bits,tests);
								testQuarticIter(pf,bits,bits);//ok
								break;
								   }
							case 2:{
								fprintf(pf,"\nNUMBER OF ITERATIONS OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta %i bits, %i tests\n\n",2*bits,bits,tests);
								testQuarticIter(pf,2*bits,bits);//ok
								break;
								   }
						   case 3:{
								fprintf(pf,"\nNUMBER OF ITERATIONS OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta=pi*sigma %i bits, %i tests\n\n",2*bits,bits,tests);
								testQuarticPrimeIter(pf,2*bits,bits);//ok
								break;
								   }
							case 4:{
								fprintf(pf,"\nNUMBER OF ITERATIONS OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta=pi %i bits, %i tests\n\n",2*bits,bits/2,tests);
								testQuarticPrimeIter2(pf,2*bits,bits);//ok
								break;
								   }
						}
						break;
						}
					case 13:{
						printf("\nQuartic Time: Enter Testmode: \n");
						printf("  1 = alpha, beta random numbers of same size\n");
						printf("  2 = alpha, beta random numbers, alpha twice as big as beta\n");
						printf("  3 = alpha random number, beta=sigma*pi, alpha 2*'bits' bits, beta 'bits' bits long\n");
						printf("  4 = alpha random number, beta=pi, alpha 2*'bits' bits, beta 'bits' bits long\n");
						scanf("%u",&mode);
						switch(mode){
							case 1:{
								fprintf(pf,"\nRUNNING TIME OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta %i bits, %i tests\n",bits,bits,tests);
								testQuartic(pf,bits,bits);//ok
								break;
								   }
							case 2:{
								fprintf(pf,"\nRUNNING TIME OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta %i bits, %i tests\n",2*bits,bits,tests);
								testQuartic(pf,2*bits,bits);//ok
								break;
								   }
						   case 3:{
								fprintf(pf,"\nRUNNING TIME OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta=pi*sigma %i bits, %i tests\n",2*bits,bits,tests);
								testQuarticPrime(pf,2*bits,bits);//ok
								break;
								   }
							case 4:{
								fprintf(pf,"\nRUNNING TIME OF QUARTIC IMPLEMENTATIONS, alpha %i bits, beta=pi %i bits, %i tests\n",2*bits,bits/2,tests);
								testQuarticPrime2(pf,2*bits,bits);//ok
								break;
								   }
						}
							break;
				}
				case 14:{
					fprintf(pf,"SUBFUNCTIONS RUNNING TIME\n");
					fprintf(pf,"\nMULTIPLICATION RUNNING TIME, %i bits, %i tests\n", bits, tests);
					testMul(pf,bits);
					fprintf(pf,"\n\n\nMODULO RUNNING TIME, %i bits, %i tests\n", bits, tests);
					testMod(pf,bits);
					fprintf(pf,"\n\n\nDIVISION BY (1+i) RUNNING TIME, %i bits, %i tests\n", bits, tests);
					testDiv(pf,bits);
					fprintf(pf,"\n\n\nTRANSFORMATION INTO PRIMARY NUMBER RUNNING TIME, %i bits",bits);
					testTra(pf, bits);
					break;
				}
				case 22:{
					fprintf(pf,"\nNUMBER OF ITERATIONS OF JACOBI IMPLEMENTATION, alpha %i bits, beta %i bits, %i tests\n\n", 2*bits, bits, tests);
					testJacIt(pf,bits);
					break;
				}
				case 23:{
					fprintf(pf,"\nRUNNING TIME JACOBI SYMBOL, %i bits, %i tests\n\n", bits, tests);
					testJac(pf,bits);
					break;
				}
				case 31:{
					fprintf(pf,"\nTEST HOMOMORPHISM PROPERTY OF LOGARITHM IMPLEMENTATIONS, %i bits, %i tests\n\n", bits, tests);
					testLogHom(pf,bits);
					break;
				}
				case 32:{
					fprintf(pf,"\nNUMBER OF ITERATIONS OF LOGARITHM IMPLEMENTATIONS, %i bits, %i tests\n\n", bits, tests);
					testLogIter(pf,bits);
					break;
				}
				case 33:{
					fprintf(pf,"\nRUNNING TIME DISCRETE LOGARITHM, %i bits, %i tests\n\n", bits, tests);
					testLog(pf, bits);
					break;
				}
				case 34: {
					fprintf(pf,"\nRUNNING TIME POLLARD RHO DISCRETE LOGARITHM, %i bits, %i tests\n\n", bits, tests);
					testPoll(pf, bits);
					break;
				}
				case 43:{
					fprintf(pf,"\nRUNNING TIME RSA, %i bits, %i tests\n\n", bits, tests);
					testRSA(pf,bits);
					break;
				}
				case 999:{
				printf("Quartic: \n");
				printf("  11  = Homomorphism Property\n");
				printf("        Verify that (alpha1/beta)(alpha2/beta)=(alpha1 alpha2/beta).\n\n");
				printf("        Print number of mistakes.\n");
				printf("  12  = Iterations\n");
				printf("        Count number of times the main loop of the basic and Damgaard's\n");
				printf("        quartic residue algorithm is executed. Print average value.\n");
				printf("  13  = Time\n");
				printf("        Measure running time of the basic and Damgaard's quartic residue\n");
				printf("        implementation. Print average value.\n");
				printf("  14  = Subfunctions Time\n");
				printf("        Measure running time of the optimized and not optimized functions\n");
				printf("        Multiplication, Modulo, Division by (1+i)^r and Primarization in Z[i]\n");
				printf("        As a reference measure the running time of the equivalent gmp \n");
				printf("        functions. Print average value.\n");
				printf("Jacobi: \n");
				printf("  22  = Iterations\n");
				printf("        Count number of times the main loop of the ordinary Jacobi symbol\n");
				printf("        algorithm is executed. Print average value.\n");
				printf("  23  = Time\n");
				printf("        Measure running time of the implementation of the ordinary algorithm\n");
				printf("        for the Jacobi symbol and the running time of the gmp\n");
				printf("        function mpz_jacobi. Print average value.\n");
				printf("Discrete Homomorphism: \n");
				printf("  31  = Homomorphism Property\n");
				printf("        Verify that hom(x1,y)hom(x2,y)=hom(x1 x2,z).\n");
				printf("        Print number of mistakes.\n");
				printf("  32  = Iterations\n");
				printf("        Count the number of table look ups that necessary for the\n");
				printf("        precomputed table. Count number of times the main loop of the BSGS \n");
				printf("        and Pollard's rho algorithm is executed. Print average value.\n");
				printf("  33  = Time\n");
   				printf("        Measure key setup, preprocessing and running time of the precomputed\n");
   				printf("        table, BSGS and Pollard's Rho implementation. Print average value.\n");
				printf("RSA:\n");
				printf("  43  = RSA: Time\n");
   				printf("        Measure key setup and running time of the RSA implementation.\n");
   				printf("        Print average value.\n");
					break;
				}
				default: printf("Invalid mode selected\n");
				}
				printf("Continue? (Yes = 1, No = 2)");
				scanf("%u",&bits);
				if ( bits == 2) {
					printf("End of program");
					fclose(pf);
					return 0;

				}
				printf("\n\n");
				fprintf(pf,"\n\n\n");
			}
		fclose(pf);
		}
	}
	return 0;
}



