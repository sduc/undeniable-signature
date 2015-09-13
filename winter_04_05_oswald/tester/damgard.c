#include <stdio.h>           /* printf */
#include "gmp.h"
#include "damgard.h"
#include "quartic2.h"
#include <stdlib.h>


//calculate the Damgard's approximate norm
void norm2(mpz_t norm,const struct gaussInt x){
 	int a;       
	mpz_t temp;
	mpz_set_ui(norm,0);
	a = (int)mpz_sizeinbase(x.a,2)- 9;
	if (a>0){
			mpz_tdiv_q_2exp (temp, x.a, a);
			mpz_mul(temp,temp,temp);
			mpz_mul_2exp(norm,temp,2*(a));
	}   
	a = (int)mpz_sizeinbase(x.b,2) - 9;
	if (a>0) {
        mpz_tdiv_q_2exp (temp, x.b,a);
        mpz_mul(temp,temp,temp);
        mpz_mul_2exp(temp,temp,2*(a));
	}
	mpz_add(norm,norm,temp);
    mpz_clear(temp);
}


//calculate the quartic residue symbol using Damgard's algorithm
struct gaussInt quarticd(struct gaussInt x, struct gaussInt y){
//INPUT   x, y in Z[i] \ {0}, and y is not divisible by (1 + i)
//OUTPUT  c = [x/y] quartic residue symbol in Z[i]
//		1: Let primary x1, y1 in Z[i] be defined by 
//				x = (i)^i1 · (1 + i)^j1 · x1
//				y= (i)^i2 · y1.
//      2: Let m, n in Z be defined by y1 = m + ni.
//      3: t <- (m - n - n^2 - 1)j1  + (m^2 + n^2 - 1)i1 mod 4
//      4: Replace x, y by x1, y1.
//      5: If N(x) < N(y) then interchange x, y  and adjust t
//				t <- t + 2((N(x)-1)(N(y)-1)/16)mod 4.
//      6: while x ¬= y do
//      7: LOOP INVARIANT: x, y are primary and N(x) >= N(y)
//      8: Let primary x1 be defined by x - y= (i)^j · (1 + i)^k · x1
//      9: Let m, n in Z be defined by y = m + ni.
//      10: t <- t + (m - n - n^2 - 1)k  + (m^2 + n^2 - 1)j mod 4
//      11: Replace x with x1.
//      12: If N(x) < N(y) then interchange x, y and adjust t
//				t <- t + 2(((N(x)-1)(N(y)-1)/16))mod 2)
//      13: end while
//      14: If x ¬= 1 then c <- 0 else c <- i^t
		
	//Variables
	int i1,j1,m,n,t;
	struct gaussInt x1;
	mpz_t  Nx, Ny;
	
	mpz_init(x1.a);
	mpz_init(x1.b);
	mpz_init(Nx);
	mpz_init(Ny);        
	//1: y= (i)^i2 · y1, y = y1, x = (i)^i1 · (1 + i)^j1 · x1   
	if (primaryExp(y.a, y.b) == -1) {
		return x1;
	}
	norm(Nx,x);
	j1 = mpz_scan1(Nx,0);
	x1 = divUnPlusI(x,j1);
	i1 = primaryExp(x1.a, x1.b);          
	//2: Let m, n in Z be defined by y = m + ni mod 16  
	//3: t <- (m - n - n^2 - 1)j1  + (m^2 + n^2 - 1)i1 mod 4    
	m = mpz_fdiv_ui(y.a,16);
	n = mpz_fdiv_ui(y.b,16);
	t = (((m-n-n*n-1)/4)*j1 + ((m*m + n*n-1)/4)*i1 )&3;        
	// 4: Replace x by x1.
	// 5: If N(x) < N(y) then interchange x, y and adjust t.   
	norm(Nx, x1);
	norm(Ny, y);
	if ( mpz_cmp(Nx,Ny) < 0) {
		mpz_set(x.a, y.a);
		mpz_set(x.b, y.b);
		mpz_set(y.a, x1.a);
		mpz_set(y.b, x1.b);
		
		m = mpz_fdiv_ui(Nx,32);
		n = mpz_fdiv_ui(Ny,32);
		t = (t + ((m-1)*(n-1)/8))&3;
	} else {
		mpz_set(x.a, x1.a);
		mpz_set(x.b, x1.b);
	}        
	// 6: while x ¬= y do
	// 7: LOOP INVARIANT: x, y are primary and N(x) >= N(y)   
    while ((mpz_cmp(x.a, y.a) != 0) || (mpz_cmp(x.b, y.b) != 0) ) {  
		// 8: Let primary x1 be defined by x - y= (i)^j1 · (1 + i)^i1 · x1   
		mpz_sub(x1.a, x.a, y.a);
		mpz_sub(x1.b, x.b, y.b);
		norm(Nx, x1);
		i1 = mpz_scan1(Nx,0);
		x1 = divUnPlusI(x1,i1);
		j1 = primaryExp(x1.a, x1.b);   
        //9: Let m, n in Z be defined by y = m + ni.    
		//10: t <- t + (m - n - n^2 - 1)k  + (m^2 + n^2 - 1)j mod 4 
		m = mpz_fdiv_ui(y.a,16);
		n = mpz_fdiv_ui(y.b,16);
		t = (t + ((m-n-n*n-1)/4)*i1 + ((m*m + n*n-1)/4)*j1 )&3; 
        // 11: Replace x with x1.
		// 12: If N(x) < N(y) then interchange x, y. 
		norm(Nx, x1);
		norm(Ny, y);
		if ( mpz_cmp(Nx,Ny) < 0) {                                  
			mpz_set(x.a, y.a);
			mpz_set(x.b, y.b);
			mpz_set(y.a, x1.a);
			mpz_set(y.b, x1.b);

			m = mpz_fdiv_ui(Nx,32);
			n = mpz_fdiv_ui(Ny,32);
			t = (t + ((m-1)*(n-1)/8))&3;
		} else {       
			mpz_set(x.a, x1.a);
			mpz_set(x.b, x1.b);
		}        
	} 
	// 13: end while
	// 14: If x ¬= 1 then  return 0 else return i^t
	if ((mpz_cmp_si(x.a,1) == 0) && (mpz_cmp_si(x.b,0) == 0)){
 		switch (t){
			case 0:  {
				mpz_set_si(x1.a,1);
				mpz_set_si(x1.b,0);
				break;
			} case 1: {
				mpz_set_si(x1.a,0);
				mpz_set_si(x1.b,1);
				break;
			} case 2: {
				mpz_set_si(x1.a,-1);
				mpz_set_si(x1.b,0);
				break;
			} case 3: {
				mpz_set_si(x1.a,0);
				mpz_set_si(x1.b,-1);
				break;
			}
		}
	} else {
		mpz_set_si(x1.a,0);
		mpz_set_si(x1.b,0);
	}
	mpz_clear(Nx);
	mpz_clear(Ny);  
	return x1;
}


//calculate the quartic residue symbol using Damgard's algorithm
//with comments printed on console to allow following the algorithm
struct gaussInt quarticverbose(struct gaussInt x, struct gaussInt y){
//INPUT   x, y in Z[i] \ {0}, and y is not divisible by (1 + i)
//OUTPUT  c = [x/y] quartic residue symbol in Z[i]
        int i1,i2,j1,j,k,t;
        struct gaussInt x1, temp1,temp2;
        mpz_t  Nx, Ny, a,b,c;

        mpz_init(a);mpz_init(b);mpz_init(c);
        mpz_init(x1.a);
        mpz_init(x1.b);
        mpz_init(temp1.a);
        mpz_init(temp1.b);
        mpz_init(temp2.a);
        mpz_init(temp2.b);
        mpz_init(Nx);
        mpz_init(Ny);

        printf("\n\nInput: x: ");
		//1: x = (i)^i1 · (1 + i)^j1 · x1 and y= (i)^i2 · y1.
        printgaussInt(x); printf("  y: "); printgaussInt(y);printf("\n");
        
        norm(Nx,x);
        j1 = mpz_scan1(Nx,0);
        printf("1:   j1: %i   ",j1);
		x1 = divUnPlusI(x,j1);
       
        i1 = primaryExp(x1.a, x1.b);                 
        printf("i1: %i ",i1);

        printf("x1 primarized: "); printgaussInt(x1);printf("\n");


        i2 = primaryExp(y.a, y.b);                              
        printf("1: i2: %i ", i2);
        if (i2 == -1) {
			printf("y divisible by (1+i), aborted algorithm");
			mpz_set_ui(temp1.a,0);
			mpz_set_ui(temp1.b,0);
			return temp1;
        }

		printf("y primarized: "); printgaussInt(y);printf("\n");
		//2: Let m, n in Z be defined by y = mm + nni mod 16
		// 3: t <- ((mm - nn - nn*nn -1)/4 * j1 + (mm*mm + nn*nn -1)/4 * i1) mod 4;
        mpz_mul(a,y.b,y.b);//a = n*n
        mpz_mul(b,y.a,y.a);//b = m*m
        mpz_sub(c,y.a,y.b);
        mpz_sub(c,c,a);
        mpz_sub_ui(c,c,1); 
        mpz_divexact_ui(c,c,4); //c = (m - n - n*n -1) /4
        printf("3: (1+i) factor: ");
        gmp_printf("%Zi\t",c);

        mpz_mul_si(c,c,j1);//*j1
        gmp_printf(" * : %Zi\t",c);

        mpz_add(a,a,b);
        mpz_sub_ui(a,a,1);
        mpz_divexact_ui(a,a,4);//a=(m*m + n*n -1)/4
        gmp_printf(" i factor: %Zi\t",a);
        mpz_mul_si(a,a,i1);
        gmp_printf(" * : %Zi\t",a);


        mpz_add(c,c,a);
        gmp_printf("before mod: %Zi\n",c);
        t = mpz_fdiv_ui(c,4);

        //t = ((m - n - n*n -1)/4 * j1 + (m*m + n*n -1)/4 * i1)&3;
        printf("3: t:  %i \n",t);
		
		// 4: Replace x by x1.
		// 5: If N(x) < N(y) then interchange x, y and adjust t.
        norm(Nx, x1);
        norm(Ny, y);

        printf("4: norms:");printmpz(Nx);printmpz(Ny);printf("\n");

        if ( mpz_cmp(Nx,Ny) < 0) {       
			printf("5: swap");
			mpz_set(x.a, y.a);
			mpz_set(x.b, y.b);
			mpz_set(y.a, x1.a);
			mpz_set(y.b, x1.b);
            
			mpz_sub_ui(Nx,Nx,1);
			mpz_sub_ui(Ny,Ny,1);
			mpz_mul(temp1.a,Nx,Ny);
			mpz_divexact_ui(temp1.a, temp1.a, 8);
			t= (t + mpz_fdiv_ui(temp1.a,4))&3;
            printf("5: swapped and adjusted t: %i\n",t); 
        } else {
			printf("5: no swap");
			mpz_set(x.a, x1.a);
			mpz_set(x.b, x1.b);
        }
		printf("\n5: new values x, y: ");
        printgaussInt(x);
        printgaussInt(y);
		// 6: while x ¬= y do
		// 7: LOOP INVARIANT: x, y are primary and N(x) >= N(y)
        while ((mpz_cmp(x.a, y.a) != 0) || (mpz_cmp(x.b, y.b) != 0) ) {
            printf("\n7: in while\n");
			// 8: Let primary x1 be defined by x - y= (i)^j · (1 + i)^k · x1
			mpz_sub(x1.a, x.a, y.a);
			mpz_sub(x1.b, x.b, y.b);
			printf("8: x - y: "); printgaussInt(x1);
			norm(Nx, x1);
			k = mpz_scan1(Nx,0);
			x1 = divUnPlusI(x1,k);
			j = primaryExp(x1.a, x1.b);              
			printf("\n8: x - y without (1+i):  "); printgaussInt(x1); printf(" j : %i,  k:  %i",j,k); printf("\n");
            printf("8: x1 primarized: "); printgaussInt(x1); printf("\n");
			//9: Let m, n in Z be defined by y = mm + nni mod 16.
			// 10: t <- (t + (mm - nn - nn*nn -1)/4 * k + (mm*mm + nn*nn -1)/4 * j) mod 4
			mpz_mul(a,y.b,y.b);//a = n*n
			mpz_mul(b,y.a,y.a);//b = m*m
			mpz_sub(c,y.a,y.b);
			mpz_sub(c,c,a);
			mpz_sub_ui(c,c,1); //temp1 = m - n - n*n -1
			mpz_divexact_ui(c,c,4);
			gmp_printf(" i+1 factor: %Zi\t",c);
			mpz_mul_si(c,c,k);//*j1
			gmp_printf(" * : %Zi\t",c);
            
			mpz_add(a,a,b);
			mpz_sub_ui(a,a,1);//a=(m*m + n*n -1)
			mpz_divexact_ui(a,a,4);
			gmp_printf(" i factor: %Zi\t",a);
			mpz_mul_si(a,a,j);
            
			gmp_printf(" * : %Zi\t",a);
			mpz_add(c,c,a);
			gmp_printf("before mod: %Zi\n",c);
			t = t + mpz_fdiv_ui(c,4);
			printf("10: t:  %i \n",t);
			// 11: Replace x with x1.
			// 12: If N(x) < N(y) then interchange x, y.
			norm(Nx, x1);
			norm(Ny, y);
			printf("11: norms:"); printmpz(Nx); printmpz(Ny);printf("\n");
            if ( mpz_cmp(Nx,Ny) < 0) {                
				printf("12: swap");
				mpz_set(x.a, y.a);
				mpz_set(x.b, y.b);
				mpz_set(y.a, x1.a);
				mpz_set(y.b, x1.b);
				mpz_sub_ui(Nx,Nx,1);
				mpz_sub_ui(Ny,Ny,1);
				mpz_mul(temp1.a,Nx,Ny);
				mpz_divexact_ui(temp1.a, temp1.a, 8);
				t= (t + mpz_fdiv_ui(temp1.a,4))&3;
				printf("12: swaped and adjusted t: %i\n",t);                
			} else {               
                printf("12: no swap");
				mpz_set(x.a, x1.a);
				mpz_set(x.b, x1.b);
			}
            printf("\n 12: neue werte x, y: ");
			printgaussInt(x);
			printgaussInt(y);
        }
		// 13: end while
		// 14: If x ¬= 1 then  return 0 else return i^t
        printf("\n13: out of while\n");
        if ((mpz_cmp_si(x.a,1) == 0) && (mpz_cmp_si(x.b,0) == 0)){
			printf("14: x == 1            t: %i \n",t);
			switch (t){
				case 0:  {
					mpz_set_si(temp1.a,1);
					mpz_set_si(temp1.b,0);break;
				} case 1: {
					mpz_set_si(temp1.a,0);
					mpz_set_si(temp1.b,1);break;
				} case 2: {
					mpz_set_si(temp1.a,-1);
					mpz_set_si(temp1.b,0);break;
				} case 3: {
					mpz_set_si(temp1.a,0);
					mpz_set_si(temp1.b,-1);break;
				}
			}
        } else {
			printf("14: x != 1\n");
			mpz_set_si(temp1.a,0);
			mpz_set_si(temp1.b,0);
        }
        
        mpz_clear(x1.a);
        mpz_clear(x1.b);
        mpz_clear(Nx);
        mpz_clear(Ny);  

        return temp1;
}

//count the number of iterations Damgard's algorithm needs
int quarticdIt(struct gaussInt x, struct gaussInt y){
//INPUT   x, y in Z[i] \ {0}, and y is not divisible by (1 + i)
//OUTPUT  count = numbers of times the main loop is executed when
//		  computing [x/y], the quartic residue symbol in Z[i]
//return -1 in case of failure
        int i1,i2,j1,j,k,t;
        int count=0;
        struct gaussInt x1, temp1;
        mpz_t  Nx, Ny, a,b,c;

        mpz_init(a);mpz_init(b);mpz_init(c);
        mpz_init(x1.a);
        mpz_init(x1.b);
        mpz_init(temp1.a);
        mpz_init(temp1.b);
        mpz_init(Nx);
        mpz_init(Ny);
        norm(Nx,x);

        j1 = mpz_scan1(Nx,0);
        x1 = divUnPlusI(x,j1);
        i1 = primaryExp(x1.a, x1.b);  
        i2 = primaryExp(y.a, y.b);   
        if (i2 == -1) {                        
			mpz_set_ui(temp1.a,0);                       
			mpz_set_ui(temp1.b,0);                        
			return -1;
        }

        mpz_mul(a,y.b,y.b);//a = n*n
        mpz_mul(b,y.a,y.a);//b = m*m
        mpz_sub(c,y.a,y.b);
        mpz_sub(c,c,a);
        mpz_sub_ui(c,c,1); 
        mpz_divexact_ui(c,c,4); //c = (m - n - n*n -1) /4     
		
		mpz_mul_si(c,c,j1);//*j1

        mpz_add(a,a,b);
        mpz_sub_ui(a,a,1);
        mpz_divexact_ui(a,a,4);//a=(m*m + n*n -1)/4
        
        mpz_mul_si(a,a,i1);
        mpz_add(c,c,a);
        t = mpz_fdiv_ui(c,4);

        norm(Nx, x1);
        norm(Ny, y);

        if ( mpz_cmp(Nx,Ny) < 0) {
                mpz_set(x.a, y.a);
                mpz_set(x.b, y.b);
                mpz_set(y.a, x1.a);
                mpz_set(y.b, x1.b);

                mpz_sub_ui(Nx,Nx,1);
                mpz_sub_ui(Ny,Ny,1);
                mpz_mul(temp1.a,Nx,Ny);
                mpz_divexact_ui(temp1.a, temp1.a, 8);
                t= (t + mpz_fdiv_ui(temp1.a,4))&3;                       
        } else {
                mpz_set(x.a, x1.a);
                mpz_set(x.b, x1.b);
        }


        while ((mpz_cmp(x.a, y.a) != 0) || (mpz_cmp(x.b, y.b) != 0) ) {

			count++;
                
			mpz_sub(x1.a, x.a, y.a);                
			mpz_sub(x1.b, x.b, y.b);
			norm(Nx, x1);
			k = mpz_scan1(Nx,0);
			x1 = divUnPlusI(x1,k);
			j = primaryExp(x1.a, x1.b);                 
			mpz_mul(a,y.b,y.b);//a = n*n                
			mpz_mul(b,y.a,y.a);//b = m*m                
			mpz_sub(c,y.a,y.b);                
			mpz_sub(c,c,a);                
			mpz_sub_ui(c,c,1); //temp1 = m - n - n*n -1                
			mpz_divexact_ui(c,c,4);         
			mpz_mul_si(c,c,k);//*j1                
			mpz_add(a,a,b);                
			mpz_sub_ui(a,a,1);//a=(m*m + n*n -1)                
			mpz_divexact_ui(a,a,4);                
			mpz_mul_si(a,a,j);               
			mpz_add(c,c,a);                
			t = t + mpz_fdiv_ui(c,4);                
			norm(Nx, x1);                
			norm(Ny, y);                
			if ( mpz_cmp(Nx,Ny) < 0) {  
				mpz_set(x.a, y.a);                        
				mpz_set(x.b, y.b);                        
				mpz_set(y.a, x1.a);                        
				mpz_set(y.b, x1.b);                        
				mpz_sub_ui(Nx,Nx,1);                        
				mpz_sub_ui(Ny,Ny,1);                        
				mpz_mul(temp1.a,Nx,Ny);                        
				mpz_divexact_ui(temp1.a, temp1.a, 8);                        
				t= (t + mpz_fdiv_ui(temp1.a,4))&3;
			} else {
				mpz_set(x.a, x1.a);
			mpz_set(x.b, x1.b);
			}
	}
        if ((mpz_cmp_si(x.a,1) == 0) && (mpz_cmp_si(x.b,0) == 0)){
                switch (t){
                        case 0:  {
                                mpz_set_si(temp1.a,1);
                                mpz_set_si(temp1.b,0);break;
                        } case 1: {
                                mpz_set_si(temp1.a,0);
                                mpz_set_si(temp1.b,1);break;
                        } case 2: {
                                mpz_set_si(temp1.a,-1);
                                mpz_set_si(temp1.b,0);break;
                        } case 3: {
                                mpz_set_si(temp1.a,0);
                                mpz_set_si(temp1.b,-1);break;
                        }
                }
        } else {
                mpz_set_si(temp1.a,0);
                mpz_set_si(temp1.b,0);
				return -1;
        }
        mpz_clear(x1.a);
        mpz_clear(x1.b);
        mpz_clear(Nx);
        mpz_clear(Ny);  
        return count;

}
