// *************************************************************************
#include "gmp.h"       
#include "math.h" 
#include "quartic2.h"



//transform (a + bi) into its primary associate
int primaryExp(mpz_t a,mpz_t b){
//find j in {0,1,2,3} such that a + bi = (i)^j · (a' + b'i)
//				a' = 1 mod 4            b' = 0 mod 4  or
//				a' = 3 mod 4            b' = 2 mod 4
//and set  a <- a', b <- b' 
//return -1 in case of failure
		int j = 0;

        if (mpz_congruent_ui_p(b,1,2)) {				// b = 1 mod2 => multiply by i^3
                mpz_neg(b, b);
                mpz_swap(b, a);
                j=3;
        }
        if (mpz_congruent_ui_p (a, 1, 4)) {             // a = 1 mod 4
                if (mpz_congruent_ui_p(b, 0, 4)) {      // b = 0 mod 4 => ok
                        return j;
                } else if (mpz_congruent_ui_p(b, 2, 4)) {// b = 2 mod 4 => multiply by i^2
                        mpz_neg(a, a);
                        mpz_neg(b, b);
                        return (j + 2)&3;
                } else return -1;
        }
        if (mpz_congruent_ui_p (a, 3, 4)) {             // a = 3 mod 4
                if (mpz_congruent_ui_p(b, 2, 4)) {      // b = 2 mod 4 => ok
                        return j;
                } else if (mpz_congruent_ui_p(b, 0, 4)){// b = 0 mod 4 => multiply by i^2
                        mpz_neg(a, a);
                        mpz_neg(b, b);
                        return (j+2)&3;
                } else return -1;
        }
        return -1;
}




//compute (x * y) in Z[i]
struct gaussInt MultComplex(struct gaussInt x, struct gaussInt y){      
		struct gaussInt reponse;
        mpz_init(reponse.a);
        mpz_init(reponse.b);
        mpz_mul(reponse.a,x.a,y.a);
        mpz_submul(reponse.a,x.b,y.b);
        mpz_mul(reponse.b,x.a,y.b);
        mpz_addmul(reponse.b,x.b,y.a);
        return reponse;
}




//compute (x mod y) in Z[i]
struct gaussInt ModuloGauss(struct gaussInt x, struct gaussInt y){ 
        struct gaussInt tempXY;
        mpz_t temp1, normY, yReal_q, yImag_q;
        mpz_init(temp1);
        mpz_init(normY);
        mpz_init(yReal_q);
        mpz_init(yImag_q);
        mpz_init(tempXY.a);        
        mpz_init(tempXY.b);

        mpz_mul(normY,y.a,y.a); // c^2 + d^2 
        mpz_addmul(normY,y.b,y.b);
                                        
        //real part
        mpz_mul(yReal_q,x.a,y.a);
        mpz_addmul(yReal_q,x.b,y.b); // yReal_q = ac+bd
        if(mpz_sgn(yReal_q) == -1){
                mpz_neg(yReal_q,yReal_q);
                mpz_mod(temp1,yReal_q,normY);
                mpz_mul_2exp(temp1,temp1,1);
                if (mpz_cmp(temp1,normY) > 0){
                        mpz_cdiv_q(yReal_q,yReal_q,normY);
                }
                
                else{
                        mpz_fdiv_q(yReal_q,yReal_q,normY);
                }

                mpz_neg(yReal_q,yReal_q);
        }
        else{//not negative
                mpz_mod(temp1,yReal_q,normY);
                mpz_add(temp1,temp1,temp1);
                if (mpz_cmp(temp1,normY) > 0){
                        mpz_cdiv_q(yReal_q,yReal_q,normY);
                }
                
                else{
                        mpz_fdiv_q(yReal_q,yReal_q,normY);
                }
        }

        //imaginary part
        mpz_mul(yImag_q,x.a,y.b);
        mpz_neg(yImag_q,yImag_q);
        mpz_addmul(yImag_q,x.b,y.a);
        if(mpz_sgn(yImag_q) == -1){
                mpz_neg(yImag_q,yImag_q);
                mpz_mod(temp1,yImag_q,normY);
                mpz_add(temp1,temp1,temp1);
                if (mpz_cmp(temp1,normY) > 0){
                        mpz_cdiv_q(yImag_q,yImag_q,normY);      
                }
                else{
                        mpz_fdiv_q(yImag_q,yImag_q,normY);
                }
                mpz_neg(yImag_q,yImag_q);
        }
        else{
                mpz_mod(temp1,yImag_q,normY);
                mpz_add(temp1,temp1,temp1);
                if (mpz_cmp(temp1,normY) > 0){
                        mpz_cdiv_q(yImag_q,yImag_q,normY);
                }
                else{
                        mpz_fdiv_q(yImag_q,yImag_q,normY);
                }
        }
        
        mpz_set(tempXY.a,yReal_q);
        mpz_set(tempXY.b,yImag_q);
        tempXY = MultComplex(tempXY,y); // e+fi = (yReal_q + iYImag_q)(c+di)
        mpz_sub(tempXY.a, x.a, tempXY.a); //compute real part of rest
        mpz_sub(tempXY.b, x.b, tempXY.b);//compute imaginary part of rest

        mpz_clear(temp1);
        mpz_clear(yReal_q);
        mpz_clear(yImag_q);
        mpz_clear(normY);
        return tempXY;                                 

}


//compute x/(1+i)^r
struct gaussInt divUnPlusI(struct gaussInt x, int r){
//result = x / (1+i)^r
// x = a + b i
// x / (1+i) = (a+b)/2 + (b-a)/2 i
// r = 2*k + b => result = i^3*k ((a/2^k) + (b/2^k))/(1+i)^b
 		int b = r&1;
        int k = (r >> 1);
        int k4 = (3*k)&3;       
		struct gaussInt result;
        mpz_init(result.a);mpz_init(result.b);
        if (r == 0) {
                mpz_set(result.a,x.a);
                mpz_set(result.b,x.b);
                return result;
        }
        if (b){
                mpz_t temp; mpz_init(temp);//temp = x.a >> k+1 before dividing by 1+i
          switch(k4){
                case(0):{
                        mpz_cdiv_q_2exp(temp,x.a,k);
                        mpz_cdiv_q_2exp(result.b,x.b,k);
                        mpz_add(result.a,temp,result.b);
                        mpz_cdiv_q_2exp(result.a,result.a,1);
                        mpz_sub(result.b,result.a,temp);        
                        return result;
                }case(1):{
                        mpz_cdiv_q_2exp(result.b,x.a,k);
                        mpz_cdiv_q_2exp(result.a,x.b,k);
                        mpz_neg(temp,result.a);
                        mpz_add(result.a,temp,result.b);
                        mpz_cdiv_q_2exp(result.a,result.a,1);
                        mpz_sub(result.b,result.a,temp);                
                        return result;
                }case(2):{
                        mpz_cdiv_q_2exp(result.a,x.a,k);
                        mpz_neg(temp,result.a);
                        mpz_cdiv_q_2exp(result.b,x.b,k);
                        mpz_neg(result.b,result.b);
                        mpz_add(result.a,temp,result.b);
                        mpz_cdiv_q_2exp(result.a,result.a,1);
                        mpz_sub(result.b,result.a,temp);
                        return result;
                }case(3):{
                        mpz_cdiv_q_2exp(temp,x.b,k);
                        mpz_cdiv_q_2exp(result.b,x.a,k);
                        mpz_neg(result.b,result.b);
                        mpz_add(result.a,temp,result.b);
                        mpz_cdiv_q_2exp(result.a,result.a,1);
                        mpz_sub(result.b,result.a,temp);
                        return result;
                }
                }
        }
        else {
          switch(k4){
                case(0):{
                        mpz_cdiv_q_2exp(result.a,x.a,k);
                        mpz_cdiv_q_2exp(result.b,x.b,k);
                        return result;
                }case(1):{
                        mpz_cdiv_q_2exp(result.b,x.a,k);
                        mpz_cdiv_q_2exp(result.a,x.b,k);
                        mpz_neg(result.a,result.a);
                        return result;
                }case(2):{
                        mpz_cdiv_q_2exp(result.a,x.a,k);
                        mpz_neg(result.a,result.a);
                        mpz_cdiv_q_2exp(result.b,x.b,k);
                        mpz_neg(result.b,result.b);
                        return result;
                }case(3):{
                        mpz_cdiv_q_2exp(result.a,x.b,k);
                        mpz_cdiv_q_2exp(result.b,x.a,k);
                        mpz_neg(result.b,result.b);
                        return result;
                }
                }
        }
        return result;
}


//compute the quartic residue symbol using the basic algorithm with the optimized subfunctions
//Malik Hammoutene's code, using Yvonne Anne Oswald's optimized subfunctions
struct gaussInt quarticb2(struct gaussInt x, struct gaussInt y){
        
    struct gaussInt temp,tempX,tempY,tempNeg;
    mpz_t temp1,temp2,temp3;
    int r=0,s,resp,a,b,c;

    mpz_init(tempX.a);
    mpz_init(tempX.b);
    mpz_init(tempY.a);
    mpz_init(tempY.b);
    mpz_init(temp.a);
    mpz_init(temp.b);
    mpz_init(temp1);
    mpz_init(temp2);
    mpz_init(temp3);
    mpz_init(tempNeg.a);
    mpz_init(tempNeg.b);
    mpz_set(tempX.a,x.a);
    mpz_set(tempX.b,x.b);
    mpz_set(tempY.a,y.a);
    mpz_set(tempY.b,y.b);
    
    
    mpz_set(tempNeg.a,tempX.a);
    mpz_neg(tempNeg.a,tempNeg.a);
    mpz_set(tempNeg.b,tempX.b);
    mpz_neg(tempNeg.b,tempNeg.b);

        resp = 0;

    if (((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))){
       // MessageBox(NULL,"Entrées non conformes...alpha est nulle!\n",NULL,NULL);
        //_beep(200,300);
        //exit(1);
    }
		
    tempX = ModuloGauss(tempX,tempY);
    mpz_set(temp1,tempX.a);
    mpz_set(temp2,tempX.b);

    if (((mpz_cmp_ui(temp1,0) == 0) && (mpz_cmp_ui(temp2,0) == 0))){
        //MessageBox(NULL,"Entrees non conformes...alpha est un Multiple de delta!\n",NULL,NULL);
        //_beep(200,300);
        //exit(1);
    }

// 0, PRIMARY test sur le delta. On effectue une transformation si nécessaire
// IL EST ESSENTIEL QUE DELTA SORTE DU TEST EN ETANT PRIMARY POUR QUE L'ALGO FONCTIONNE!

    //tempY= transformPrimary(tempY.a,tempY.b);malik zum loeschen
        primaryExp(tempY.a,tempY.b);

    while (!(
        ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,0) == 0)) 
||
        ((mpz_cmp_ui(tempX.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) == 0)) 
||
        ((mpz_cmp_ui(tempNeg.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))||
        ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,1) == 0)) 
||
        ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempNeg.b,1) ==
0))
        )){
        
        tempX = ModuloGauss(tempX,tempY); // on réduit si c'est possible                
        mpz_set(tempNeg.a,tempX.a);
        mpz_neg(tempNeg.a,tempNeg.a);
        mpz_set(tempNeg.b,tempX.b);
        mpz_neg(tempNeg.b,tempNeg.b);
        
    // 2, LE PARASITE 1+i
        if (!(
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))  ||
            ((mpz_cmp_ui(tempX.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))  ||
            ((mpz_cmp_ui(tempNeg.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))||
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,1) ==
0))  ||
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempNeg.b,1) ==
0))
            )){ 

            mpz_mul(temp1,tempX.a,tempX.a);
            mpz_addmul(temp1,tempX.b, tempX.b);
        
            r = mpz_scan1(temp1,0);

                        a = mpz_fdiv_ui(tempY.a,16);/////
                        b = mpz_fdiv_ui(tempY.b,16);

                        c = ((a - b - b*b - 1)/4)*r;

         
                        if (c<0){
                                c = abs(c);
                                c = c&3;


                                if (c == 3){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 1){resp = resp +  3;}

                }

            else{
                                c = c&3;

                                if (c == 1){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 3){resp = resp +  3;}
               
            }

            tempX = divUnPlusI(tempX,r);

            mpz_set(tempNeg.a,tempX.a);
            mpz_neg(tempNeg.a,tempNeg.a);
            mpz_set(tempNeg.b,tempX.b);
            mpz_neg(tempNeg.b,tempNeg.b);
        }

        if (!(
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))  ||
            ((mpz_cmp_ui(tempX.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))  ||
            ((mpz_cmp_ui(tempNeg.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))||
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,1) ==
0))  ||
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempNeg.b,1) ==
0)))){

    // 3, PRIMARIYsation
                        
                        s = primaryExp(tempX.a, tempX.b);
    // calcul de (i/delta):
                        
                        
                        a = mpz_fdiv_ui(tempY.a,16);
                        b = mpz_fdiv_ui(tempY.b,16);
                        

                        c = ((a*a + b*b - 1)/4)*s;
                                c = c&3;

                if (c == 1){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 3){resp = resp +  3;}

                                
        }

    // A ce stade, les deux éléments de l'équations sont PRIMARY
    // On peut donc appliquer la loi de réciprocité:
    // 4, Loi de réciprocité

        mpz_set(tempNeg.a,tempX.a);
        mpz_neg(tempNeg.a,tempNeg.a);
        mpz_set(tempNeg.b,tempX.b);
        mpz_neg(tempNeg.b,tempNeg.b);

        if (!(
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))  ||
            ((mpz_cmp_ui(tempX.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))  ||
            ((mpz_cmp_ui(tempNeg.a,1) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0))||
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,1) ==
0))  ||
            ((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempNeg.b,1) ==
0)))){

            a = mpz_fdiv_ui(tempY.a,32);
                        b = mpz_fdiv_ui(tempY.b,32);

                        c = a*a + b*b - 1;

                        a = mpz_fdiv_ui(tempX.a,32);
                        b = mpz_fdiv_ui(tempX.b,32);

                        c = c * (a*a + b*b - 1);
                        c /= 16;
                        c = c&1;

                        if ( c== 1) resp += 2;   

         
            // mise à jour
                        mpz_swap(tempX.a,tempY.a);
                        mpz_swap(tempX.b,tempY.b);
                
            mpz_set(tempNeg.a,tempX.a);
            mpz_neg(tempNeg.a,tempNeg.a);
            mpz_set(tempNeg.b,tempX.b);
            mpz_neg(tempNeg.b,tempNeg.b);
        }

    } // le grand while

    if (!(((mpz_cmp_ui(tempX.a,0) == 0) && (mpz_cmp_ui(tempX.b,0) ==
0)))){
        

        if (mpz_cmp_ui(tempX.a,1)   == 0) {r=0;}
        if (mpz_cmp_ui(tempNeg.a,1) == 0) {r=2;}
        if (mpz_cmp_ui(tempX.b,1)   == 0) {r=1;}
        if (mpz_cmp_ui(tempNeg.b,1) == 0) {r=3;}

                        a = mpz_fdiv_ui(tempY.a,16);
                b = mpz_fdiv_ui(tempY.b,16);

                c = ((a*a + b*b -1)/4)*r;

                        c = c&3;

                        if (c == 1){resp = resp + 1;}
                        if (c == 2){resp = resp + 2;}
                        if (c == 3){resp = resp + 3;}

        }
        resp = resp&3;

        if (resp == 0){
            mpz_set_ui(temp.a,1);mpz_set_ui(temp.b,0);
                } 

        if (resp == 1){
            mpz_set_ui(temp.a,0);mpz_set_ui(temp.b,1);
                }

        if (resp == 2){
            mpz_set_ui(temp.a,1);mpz_neg(temp.a,temp.a);mpz_set_ui(temp.b,0);
                }

        if (resp == 3){
            mpz_set_ui(temp.a,0);mpz_set_ui(temp.b,1);mpz_neg(temp.b,temp.b);
                
                }

        mpz_clear(temp1);
        mpz_clear(temp2);
        mpz_clear(temp3);
        mpz_clear(tempX.a);
        mpz_clear(tempX.b);
        mpz_clear(tempY.a);
        mpz_clear(tempY.b);
    return temp;

}

//compute the quartic residue symbol using the basic algorithm with the optimized subfunctions
//written by Yvonne Anne Oswald
struct gaussInt quarticb3(struct gaussInt x, struct gaussInt y){
//INPUT:
//	x,y: gaussian integers
//OUTPUT:
//	quartic residue [x/y]
//ALGORITHM
//      Compute quartic residuosity in Z[i]
//      Require: x, y in Z[i] \ {0}, and y is not divisible by (1 + i)
//      Ensure: c = [x/y]
//	1: x <- x mod y
//	   if x = 0 return c = 0;
//	2: let primary x1, y1 in Z[i] be defined by
//	    x = (i)^i1 · (1 + i)^j1 · x1
//	    y= (i)^i2 · y1.
//      and let m, n in Z be defined by
//	    y1 = 1 + (2 + 2i)(m + ni).
//  3: t <- (-n-(m+n)^2)j1  + (n-m)i1 mod 4
//	4: replace x with y1, y with x1 and adjust t
//	   t <- t + 2((N(x)-1)(N(y)-1)/16)mod 4.
//  6: while N(x) > 1  do
//  7: LOOP INVARIANT: x, y are primary
//  8: Let primary x1 be defined by x mod y = (i)^j · (1 + i)^k · x1
//  9: Let m, n in Z be defined by y = 1 + (2 + 2i)(m + ni).
//  10: t <- (-n-(m+n)^2)j1  + (n-m)i1 mod 4
//  11: Replace x with y and y with x1 and adjust t
//       t <- t + 2(((N(x)-1)(N(y)-1)/16))mod 2)
//  13: end while
//  14: If x ¬= 1 then c <- 0 else c <- i^t

	//Variables
	int t,i1,j1,m,n,it;
	struct gaussInt res;
	mpz_t t1,t2;
	mpz_init(res.a); mpz_init(res.b);
	mpz_init(t1); mpz_init(t2);
	it = 0;
	//1: x <- x mod y
	//	   if x = 0 return c = 0;
	x = ModuloGauss(x,y);
	if(!mpz_cmp_ui(x.a,0) && !mpz_cmp_ui(x.b,0)){ return res; }

	//2: let primary x1, y1 in Z[i] be defined by
	//	    x = (i)^i1 · (1 + i)^j1 · x1
	//	    y= (i)^i2 · y1.
	if (primaryExp(y.a, y.b) == -1) {
		return res;
	}
   	norm(t1,x);
	j1 = mpz_scan1(t1,0);
	x = divUnPlusI(x,j1);
	i1 = primaryExp(x.a, x.b);
	//3: Let m, n in Z be defined by y = m + ni mod 16
	//4: t <- ((m - n - n*n -1)/4 * j1 + (m*m + n*n -1)/4 * i1) mod 4;
    m = mpz_fdiv_ui(y.a,16);
    n = mpz_fdiv_ui(y.b,16);
    t = (((m-n-n*n-1)/4)*j1 + ((m*m + n*n-1)/4)*i1 )&3;
    //5: replace x with y1, y with x1 and adjust t
	//	   t <- t + 2((N(x)-1)(N(y)-1)/16)mod 4.
	mpz_swap(x.a,y.a);
	mpz_swap(x.b,y.b);
	norm(t1,x);
	norm(t2,y);
    m = mpz_fdiv_ui(t1,32);
    n = mpz_fdiv_ui(t2,32);
    t = (t + ((m-1)*(n-1)/8))&3;
	//6: while norm(x) > 1
	//7: LOOP INVARIANT: x, y are primary
	while ((mpz_cmp_ui(t1,1)> 0) && (mpz_cmp_ui(t2,1)>0)){
		it++;
		//8: Let primary x1 be defined by x mod y = (i)^j · (1 + i)^k · x1
		x = ModuloGauss(x,y);
		norm(t1,x);
		if (mpz_cmp_ui(t1,0)> 0 ){
			j1 = mpz_scan1(t1,0);
			x = divUnPlusI(x,j1);
			i1 = primaryExp(x.a, x.b);
			//9: Let m, n in Z be defined by y = m + ni mod 16
			//10: t <- t + ((m - n - n*n -1)/4 * j1 + (m*m + n*n -1)/4 * i1) mod 4;
        	m = mpz_fdiv_ui(y.a,16);
			n = mpz_fdiv_ui(y.b,16);
			t = t + (((m-n-n*n-1)/4)*j1 + ((m*m + n*n-1)/4)*i1 )&3;
			//11: Replace x with y and y with x1 and adjust t
			//          t <- t + 2(((N(x)-1)(N(y)-1)/16))mod 2)
			//13: end while
			mpz_swap(x.a,y.a);
			mpz_swap(x.b,y.b);
			norm(t1,x);
			norm(t2,y);
			m = mpz_fdiv_ui(t1,32);
			n = mpz_fdiv_ui(t2,32);
			t = (t + ((m-1)*(n-1)/8))&3;
		}
	}
    //14: If x ¬= 1 then c <- 0 else c <- i^t
	if ((mpz_cmp_ui(t1,0)>0)&&(mpz_cmp_ui(t2,0)>0)){
		switch (t){          
			case 0:  {               
				mpz_set_si(res.a,1);             
				mpz_set_si(res.b,0);break; 
			} case 1: {
				mpz_set_si(res.a,0);
				mpz_set_si(res.b,1);break;
			} case 2: {
				mpz_set_si(res.a,-1);                
				mpz_set_si(res.b,0);break;
			} case 3: {
				mpz_set_si(res.a,0);                    
				mpz_set_si(res.b,-1);break;            
			}
		}
	}
	mpz_clear(t1);
	mpz_clear(t2);
	return res;
}
