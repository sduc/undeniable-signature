#include "gmp.h"
#include "jacobi.h"
#include <stdio.h>           /* printf */


//calculate the Jacobi symbol using the ordinary algorithm 
//the numbers refer to the description of the ordinary algorithm
//in Meyer's and Sorendson paper concerning the Right-/Left-Shift algorithm
int jacobi(mpz_t u, mpz_t v){
 	int t,p2;       
	mpz_t temp,temp2;mpz_init(temp);mpz_init(temp2);
        if (mpz_even_p(v)){
                printf("v must be odd!");
                return 0;
        }
        //1:
        t=1;
        while (mpz_cmp(u,v)!=0){
        //2:
                if (mpz_sgn(u) == -1){
                        mpz_neg(u,u);
                        if (mpz_congruent_ui_p(v,3,4)){
                          t=-1*t;
                        }
                }
                //3:
                p2 = mpz_scan1(u,0);
                if (p2&1){//(4)
                        mpz_mul(temp,v,v);
                        mpz_sub_ui(temp,temp,1);
                        mpz_tdiv_q_2exp(temp,temp,3);
                        if (mpz_odd_p(temp)){
                                t=-1*t;
                        }
                }
                mpz_tdiv_q_2exp(u,u,p2);
                //4:
                if (mpz_cmp(u,v)<0){//(6)
                        mpz_sub_ui(temp,u,1);
                        mpz_tdiv_q_2exp(temp,temp,1);
                        mpz_sub_ui(temp2,v,1);
                        mpz_tdiv_q_2exp(temp2,temp2,1);
                        mpz_mul(temp,temp,temp2);
                        if (mpz_odd_p(temp)){
                                t=-1*t;
                        }
                        mpz_swap(u,v);
                 }
                //5:
                if (mpz_cmp_ui(v,0)==0) return 0;
                mpz_mod(u,u,v);
                //6:
                if (mpz_cmp_ui(u,0)==0){
                        if (mpz_cmp_ui(v,1)==0){
                                return t;
                        }else{
                                return 0;
                        }
                }
        }
	return -1;

}

int jacobiIter(mpz_t u, mpz_t v){
        int t,p2,count =0;
        mpz_t temp,temp2;mpz_init(temp);mpz_init(temp2);
        if (mpz_even_p(v)){
                printf("v must be odd!");
                return 0;
        }
        //1:
        t=1;
        while (mpz_cmp(u,v)!=0){
        count ++;
                //2:
                if (mpz_sgn(u) == -1){
                        mpz_neg(u,u);
                        if (mpz_congruent_ui_p(v,3,4)){
                          t=-1*t;
                        }
                }
                //3:
                p2 = mpz_scan1(u,0);
                if (p2&1){//(4)
                        mpz_mul(temp,v,v);
                        mpz_sub_ui(temp,temp,1);
                        mpz_tdiv_q_2exp(temp,temp,3);
                        if (mpz_odd_p(temp)){
                                t=-1*t;
                        }
                }
                mpz_tdiv_q_2exp(u,u,p2);
                //4:
                if (mpz_cmp(u,v)<0){//(6)
                        mpz_sub_ui(temp,u,1);
                        mpz_tdiv_q_2exp(temp,temp,1);
                        mpz_sub_ui(temp2,v,1);
                        mpz_tdiv_q_2exp(temp2,temp2,1);
                        mpz_mul(temp,temp,temp2);
                        if (mpz_odd_p(temp)){
                                t=-1*t;
                        }
                        mpz_swap(u,v);
                 }
                //5:
				if (mpz_cmp_ui(v,0)==0) {
					return count;
				}
                mpz_mod(u,u,v);
                //6:
                if (mpz_cmp_ui(u,0)==0){
                        if (mpz_cmp_ui(v,1)==0){
                                return count;
                        }else{
                                return count;
                        }
                }
        }
	return -1;
}
