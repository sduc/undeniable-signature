#include "gmp.h"       
#include "math.h"    
#include <stdlib.h>
#include <stdio.h>


//-------------programmed by Malik Hammoutene---------------

struct cornAlgo{        
        
// structure pour définir les nombres complexes
// (elle porte ce nom simplement parce qu'elle
// apparaît pour la première fois dans Cornacchia

        mpz_t x_sol;
        mpz_t y_sol;
};

int power(int val, int pow){ 
        
// a^b       
        
        int ret_val = 1;
    int i;

    for(i = 0; i < pow; i++) ret_val *= val;

    return(ret_val);
}



struct cornAlgo multComplex(struct cornAlgo x, struct cornAlgo y){

// calcul (a+bi)*(c+di)
// la réponse est un nombre complexe

        struct cornAlgo reponse;
        mpz_t temp1,temp2;

        mpz_init(temp1);
        mpz_init(temp2);
        mpz_init(reponse.x_sol);
        mpz_init(reponse.y_sol);

        mpz_set(temp1,x.x_sol);
        mpz_mul(temp1,temp1,y.x_sol);
        mpz_set(temp2,x.y_sol);
        mpz_mul(temp2,temp2,y.y_sol);
        mpz_neg(temp2,temp2);
        mpz_add(reponse.x_sol,temp1,temp2);

        mpz_set(temp1,x.x_sol);
        mpz_mul(temp1,temp1,y.y_sol);
        mpz_set(temp2,x.y_sol);
        mpz_mul(temp2,temp2,y.x_sol);
        mpz_add(reponse.y_sol,temp1,temp2);

        mpz_clear(temp1);
        mpz_clear(temp2);

        return reponse;
}

struct cornAlgo moduloGauss(struct cornAlgo x, struct cornAlgo y){ 

// calcul (a+bi)modulo(c+di)
// la réponse est un complexe

        struct cornAlgo tempXY;
        mpz_t temp1,temp2,temp3,normY,yReal_q,yImag_q;

        mpz_init(temp1);
        mpz_init(temp2);
        mpz_init(temp3);


        mpz_mul(temp1,y.x_sol,y.x_sol); // c^2 + d^2 
        mpz_mul(temp2,y.y_sol,y.y_sol);

        mpz_init(normY);
        mpz_add(normY,temp1,temp2);
        mpz_init(yReal_q);                              // calcul du quotient réel

        mpz_mul(yReal_q,x.x_sol,y.x_sol);
        mpz_mul(temp3,x.y_sol,y.y_sol);
        mpz_add(yReal_q,yReal_q,temp3); // yReal_q = ac+bd

        if(mpz_sgn(yReal_q) == -1){
                mpz_neg(yReal_q,yReal_q);
                mpz_mod(temp1,yReal_q,normY);
                mpz_add(temp2,temp1,temp1);
                if (mpz_cmp(temp2,normY) > 0){
                        mpz_cdiv_q(yReal_q,yReal_q,normY);
                }
                
                else{
                        mpz_fdiv_q(yReal_q,yReal_q,normY);
                }

                mpz_neg(yReal_q,yReal_q);
        }
        else{
                mpz_mod(temp1,yReal_q,normY);
                mpz_add(temp2,temp1,temp1);
                if (mpz_cmp(temp2,normY) > 0){
                        mpz_cdiv_q(yReal_q,yReal_q,normY);
                }
                
                else{
                        mpz_fdiv_q(yReal_q,yReal_q,normY);
                }
        }

        mpz_init(yImag_q);                              // calcul du quotient imaginaire
        mpz_mul(yImag_q,x.x_sol,y.y_sol);
        mpz_neg(yImag_q,yImag_q);
        mpz_mul(temp3,x.y_sol,y.x_sol);
        mpz_add(yImag_q,yImag_q,temp3);

        if(mpz_sgn(yImag_q) == -1){
                mpz_neg(yImag_q,yImag_q);
                mpz_mod(temp1,yImag_q,normY);
                mpz_add(temp2,temp1,temp1);
                if (mpz_cmp(temp2,normY) > 0){
                        mpz_cdiv_q(yImag_q,yImag_q,normY);      
                }
                else{
                        mpz_fdiv_q(yImag_q,yImag_q,normY);
                }
                mpz_neg(yImag_q,yImag_q);
        }
        else{
                mpz_mod(temp1,yImag_q,normY);
                mpz_add(temp2,temp1,temp1);
                if (mpz_cmp(temp2,normY) > 0){
                        mpz_cdiv_q(yImag_q,yImag_q,normY);
                }
                else{
                        mpz_fdiv_q(yImag_q,yImag_q,normY);
                }
        }

        mpz_init(tempXY.x_sol);
        mpz_init(tempXY.y_sol);
        mpz_set(tempXY.x_sol,yReal_q);
        mpz_set(tempXY.y_sol,yImag_q);

        tempXY = multComplex(tempXY,y); // e+fi = (yReal_q+iYImag_q)(c+di)

        mpz_set(temp1, tempXY.x_sol);
        mpz_set(temp2, tempXY.y_sol);

        mpz_neg(temp1,temp1);
        mpz_neg(temp2,temp2);
        mpz_add(temp1,x.x_sol,temp1);   // calcul du reste
        mpz_add(temp2,x.y_sol,temp2);   // réel et imaginaire

        mpz_set(tempXY.x_sol,temp1);
        mpz_set(tempXY.y_sol,temp2);    // on a x/y = bq + r, donc xmody = r

        mpz_clear(temp1);
        mpz_clear(temp2);
        mpz_clear(temp3);
        mpz_clear(yReal_q);
        mpz_clear(yImag_q);
        mpz_clear(normY);

        return tempXY;                                  // la réponse est le reste, c'est un complexe

}

struct cornAlgo divExactComplex(struct cornAlgo x, struct cornAlgo y){

// calcul (a+bi)/(c+di) sachant que l'on sait d'avance que la division ne donnera pas de reste
// la réponse est un nombre complexe

        struct cornAlgo reponse;
        mpz_t temp1,temp2,temp3;

        mpz_init(temp1);
        mpz_init(temp2);
        mpz_init(temp3);
        mpz_init(reponse.x_sol);
        mpz_init(reponse.y_sol);

        mpz_set(temp1,y.x_sol);                 // méthode classique: x/y = (x*conj(y))/norme(y)
        mpz_mul(temp1,temp1,y.x_sol);
        mpz_set(temp2,y.y_sol);
        mpz_mul(temp2,temp2,y.y_sol);
        mpz_add(temp1,temp1,temp2);
        
        mpz_set(temp2,y.x_sol);
        mpz_mul(temp2,temp2,x.x_sol);
        mpz_set(temp3,y.y_sol);
        mpz_mul(temp3,temp3,x.y_sol);
        mpz_add(temp2,temp2,temp3);
        mpz_div(reponse.x_sol,temp2,temp1);

        mpz_set(temp2,y.x_sol);
        mpz_mul(temp2,temp2,x.y_sol);
        mpz_set(temp3,y.y_sol);
        mpz_mul(temp3,temp3,x.x_sol);
        mpz_neg(temp3,temp3);
        mpz_add(temp2,temp2,temp3);
        mpz_div(reponse.y_sol,temp2,temp1);

        mpz_clear(temp1);
        mpz_clear(temp2);
        mpz_clear(temp3);

        return reponse;
}

struct cornAlgo transformPrimary(mpz_t inX, mpz_t inY){

// test de PRIMARITY
// il s'agit de vérifier qu'un nombre complexe a+bi respecte les deux règles
//suivantes:
// b = 0 mod 2
// a+b = 1 mod 4

        mpz_t tempX1,tempX2,tempY1,tempY2,temp1,temp2;
        struct cornAlgo nbrXY;

        mpz_init(tempX1);
        mpz_init(tempX2);
        mpz_init(tempY1);
        mpz_init(tempY2);
        mpz_init(temp1);
        mpz_init(temp2);
        mpz_init(nbrXY.x_sol);
        mpz_init(nbrXY.y_sol);

        mpz_set(nbrXY.x_sol,inX);
        mpz_set(nbrXY.y_sol,inY);       // au cas où le test n'est pas réussi

        mpz_set(tempX1,inX);            //  a
        mpz_neg(tempX2,tempX1);         // -a
        mpz_set(tempY1,inY);            //  b
        mpz_set(tempY2,tempY1);
        mpz_neg(tempY2,tempY2);         // -b

        // si le nombre c = a+bi ne passe pas le test, on cherche à le modifier pour
        // trouver un nombre qui passe le test.
        // Les nombres possibles sont:
        // a+bi = 1*c, -b+ai = i*c, -a-bi = -1*c, b-ai = -i*c

        mpz_mod_ui(temp1,tempY1,2);
        mpz_add(temp2,tempX1,tempY1);
        mpz_mod_ui(temp2,temp2,4);

        if ((mpz_cmp_ui(temp1,0)==0) && (mpz_cmp_ui(temp2,1)==0)){ // fois 1
                                mpz_set(nbrXY.x_sol,tempX1);
                                mpz_set(nbrXY.y_sol,tempY1);
        }
        else{
                mpz_mod_ui(temp1,tempY2,2);
                mpz_add(temp2,tempX2,tempY2);
                mpz_mod_ui(temp2,temp2,4);

                if ((mpz_cmp_ui(temp1,0)==0) && (mpz_cmp_ui(temp2,1)==0)){ // fois -1
                                        mpz_set(nbrXY.x_sol,tempX2);
                                        mpz_set(nbrXY.y_sol,tempY2); 
                }
                else{           
                        mpz_mod_ui(temp1,tempX1,2);
                        mpz_add(temp2,tempY2,tempX1);
                        mpz_mod_ui(temp2,temp2,4);

                        if ((mpz_cmp_ui(temp1,0)==0) && (mpz_cmp_ui(temp2,1)==0)){ // fois i
                                                mpz_set(nbrXY.x_sol,tempY2);
                                                mpz_set(nbrXY.y_sol,tempX1);
                        }
                        else{
                                mpz_mod_ui(temp1,tempX2,2);
                                mpz_add(temp2,tempY1,tempX2);
                                mpz_mod_ui(temp2,temp2,4);

                                if ((mpz_cmp_ui(temp1,0)==0) && (mpz_cmp_ui(temp2,1)==0)){ // fois-i
                                                        mpz_set(nbrXY.x_sol,tempY1);
                                                        mpz_set(nbrXY.y_sol,tempX2);
                                }
                        }
                }
        }
        mpz_clear(tempX1);
        mpz_clear(tempX2);
        mpz_clear(tempY1);
        mpz_clear(tempY2);
        mpz_clear(temp1);
        mpz_clear(temp2);

        // si ce n'est pas transformable ou si c'est inutile de transformer,
        // on ne change rien
        
        return nbrXY;
}


struct cornAlgo powerUnPlusI(int r){

// calcul (1+i)^r
// la réponse est un nombre complexe, r est positif ou nul

    int k,kp,b,t;
    struct cornAlgo reponse;
    mpz_t temp;

    mpz_init(temp);
    mpz_init(reponse.x_sol);
    mpz_init(reponse.y_sol);
        
        if (r==1){
                mpz_set_ui(reponse.x_sol,1);
                mpz_set_ui(reponse.y_sol,1);
        }

    if (r==0){
        mpz_set_ui(reponse.x_sol,1);
        mpz_set_ui(reponse.y_sol,0);
        }

        if (r>1){
                b = r&1;
                k = (r-b)/2;
                kp = k&3;

                t = power(2,k);

                if (kp == 0){
                        mpz_set_ui(reponse.x_sol,t);
                        mpz_set_ui(reponse.y_sol,0);
                }
                if (kp == 1){
                        mpz_set_ui(reponse.x_sol,0);
                        mpz_set_ui(reponse.y_sol,t);
                }
                if (kp == 2){
                        mpz_set_ui(reponse.x_sol,t);
                        mpz_neg(reponse.x_sol,reponse.x_sol);
                        mpz_set_ui(reponse.y_sol,0);
                }
                if (kp == 3){
                        mpz_set_ui(reponse.x_sol,0);
                        mpz_set_ui(reponse.y_sol,t);
                        mpz_neg(reponse.y_sol,reponse.y_sol);
                }

                if (b == 1){
                        mpz_mul_ui(temp,reponse.y_sol,2);
                        mpz_neg(reponse.y_sol,reponse.y_sol);
                        mpz_add(reponse.x_sol,reponse.x_sol,reponse.y_sol);
                        mpz_add(reponse.y_sol,reponse.x_sol,temp);
                }
        }

    
    mpz_clear(temp);  
    
    return reponse;
}

struct cornAlgo quarticb1(struct cornAlgo x, struct cornAlgo y){

    struct cornAlgo temp,tempX,tempY,unPlusI,tempNeg;
    mpz_t temp1,temp2,temp3;
    int r=0, s=0, resp=0, a=0, b=0, c=0;
        

    mpz_init(tempX.x_sol);
    mpz_init(tempX.y_sol);
    mpz_init(tempY.x_sol);
    mpz_init(tempY.y_sol);
    mpz_init(temp.x_sol);
    mpz_init(temp.y_sol);

    mpz_set(tempX.x_sol,x.x_sol);
    mpz_set(tempX.y_sol,x.y_sol);

    mpz_set(tempY.x_sol,y.x_sol);
    mpz_set(tempY.y_sol,y.y_sol);
    
    mpz_init(temp1);
    mpz_init(temp2);
    mpz_init(temp3);

    mpz_init(tempNeg.x_sol);
    mpz_init(tempNeg.y_sol);
    mpz_set(tempNeg.x_sol,tempX.x_sol);
    mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
    mpz_set(tempNeg.y_sol,tempX.y_sol);
    mpz_neg(tempNeg.y_sol,tempNeg.y_sol);

    mpz_init(unPlusI.x_sol);
    mpz_init(unPlusI.y_sol);

        resp = 0;

    if (((mpz_cmp_ui(tempX.x_sol,0) == 0) && (mpz_cmp_ui(tempX.y_sol,0)
== 0))){
       // MessageBox(NULL,"Entrées non conformes...alpha est
	//nulle!\n",NULL,NULL);
        //_beep(200,300);
        //exit(1);
                printf("Entrées non conformes...alpha est nulle!\n");
                return temp;
    }

    tempX = moduloGauss(tempX,tempY);
    mpz_set(temp1,tempX.x_sol);
    mpz_set(temp2,tempX.y_sol);

    if (((mpz_cmp_ui(temp1,0) == 0) && (mpz_cmp_ui(temp2,0) == 0))){
       // MessageBox(NULL,"Entrees non conformes...alpha est un multiple de
	//delta!\n",NULL,NULL);
        //_beep(200,300);
        //exit(1);
                printf("Entrées non conformes...alpha est un multiple de delta!\n");
                return temp;
    }

// 0, PRIMARY test sur le delta. On effectue une transformation si nécessaire
// IL EST ESSENTIEL QUE DELTA SORTE DU TEST EN ETANT PRIMARY POUR QUE L'ALGO
//FONCTIONNE!

    tempY= transformPrimary(tempY.x_sol,tempY.y_sol);

    while (!(
        ((mpz_cmp_ui(tempX.x_sol,0) == 0) && (mpz_cmp_ui(tempX.y_sol,0)
== 0))  ||
        ((mpz_cmp_ui(tempX.x_sol,1) == 0) && (mpz_cmp_ui(tempX.y_sol,0)
== 0))  ||
        ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
        ((mpz_cmp_ui(tempX.x_sol,0) == 0) && (mpz_cmp_ui(tempX.y_sol,1)
== 0))  ||
        ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0))
        )){

        tempX = moduloGauss(tempX,tempY); // on réduit si c'est possible

        mpz_set(tempNeg.x_sol,tempX.x_sol);
        mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
        mpz_set(tempNeg.y_sol,tempX.y_sol);
        mpz_neg(tempNeg.y_sol,tempNeg.y_sol);
        
    // 2, LE PARASITE 1+i
        if (!(
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,1) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0))
            )){

            mpz_mul(temp1,tempX.x_sol,tempX.x_sol);
            mpz_mul(temp2,tempX.y_sol,tempX.y_sol);
            mpz_add(temp1,temp1,temp2);
        
            r = mpz_scan1(temp1,0);

                        a = mpz_fdiv_r_ui(temp1,tempY.x_sol,16);
                        b = mpz_fdiv_r_ui(temp1,tempY.y_sol,16);

                        c = ((a - b - b*b - 1)/4)*r;

         //   mpz_mul(temp3,tempY.y_sol,tempY.y_sol);
         //   mpz_add(temp3,temp3,tempY.y_sol);
         //   mpz_add_ui(temp3,temp3,1);
                //      mpz_sub(temp3,tempY.x_sol,temp3);
          //  mpz_div_ui(temp3,temp3,4); // a-b-b^2-1 / 4
           // mpz_mul_ui(temp3,temp3,r); // * r

            //if (mpz_sgn(temp3) == -1){
                        if (c<0){
                                c = abs(c);
                                c = c&3;

                //mpz_neg(temp3,temp3);
                //mpz_mod_ui(temp3,temp3,4);

                                if (c == 3){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 1){resp = resp +  3;}

               // if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  1;}
               // if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
               // if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  3;}
                }

            else{
                //mpz_mod_ui(temp3,temp3,4);
                                c = c&3;

                                if (c == 1){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 3){resp = resp +  3;}
                //if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  1;}
                //if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
                //if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  3;}
            }

           // mpz_set_ui(unPlusI.x_sol,1);
           // mpz_set_ui(unPlusI.y_sol,1);
            //unPlusI = powerComplex(unPlusI,r);
                        unPlusI = powerUnPlusI(r);
            tempX = divExactComplex(tempX,unPlusI);

            mpz_set(tempNeg.x_sol,tempX.x_sol);
            mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
            mpz_set(tempNeg.y_sol,tempX.y_sol);
            mpz_neg(tempNeg.y_sol,tempNeg.y_sol);
        }

        if (!(
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,1) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0)))){

    // 3, PRIMARIYsation
            temp = transformPrimary(tempX.x_sol,tempX.y_sol);

            if ((mpz_cmp(tempX.x_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempX.y_sol,temp.y_sol) == 0)){
                s=0;} // *1
            if ((mpz_cmp(tempNeg.y_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempX.x_sol,temp.y_sol) == 0)){
                s=1;} // *i
            if ((mpz_cmp(tempNeg.x_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempNeg.y_sol,temp.y_sol) == 0)){
                s=2;} // *-1
            if ((mpz_cmp(tempX.y_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempNeg.x_sol,temp.y_sol) == 0)){
                s=3;} // *-i

    // calcul de i/delta:

                        a = mpz_fdiv_r_ui(temp1,tempY.x_sol,16);
                        b = mpz_fdiv_r_ui(temp1,tempY.y_sol,16);

                        c = ((a*a + b*b - 1)/4)*3*s;


         /*   mpz_set(temp2,tempY.x_sol);
            mpz_mul(temp2,temp2,tempY.x_sol);// a^2
            mpz_set(temp3,tempY.y_sol);
            mpz_mul(temp3,temp3,tempY.y_sol); // b^2
            mpz_add(temp3,temp3,temp2); // a^2+b^2
            mpz_neg(temp3,temp3); // -(a^2+b-2)
            mpz_add_ui(temp3,temp3,1); // 1-(a^2+b^2)
            mpz_neg(temp3,temp3); // a^2+b^2-1
            mpz_div_ui(temp3,temp3,4);
            s=3*s; // (-i)^s = i^3s
            mpz_mul_ui(temp3,temp3,s);
                */
          //  if (mpz_sgn(temp3) == -1){

            //    mpz_neg(temp3,temp3);
              //  mpz_mod_ui(temp3,temp3,4);

                /*      if (c<0){
                                
                                c = abs(c); c = c&3;

                if (c == 3){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 1){resp = resp +  3;}
                        //      if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  1;}
             //   if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
             //   if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  3;}
                }

            else{*/
              //  mpz_mod_ui(temp3,temp3,4);
                                c = c&3;

                if (c == 1){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 3){resp = resp +  3;}
              //  if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  1;}
              //  if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
              //  if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  3;}
           // }
    // mise à jour de x
            mpz_set(tempX.x_sol,temp.x_sol);
            mpz_set(tempX.y_sol,temp.y_sol);
        }

    // A ce stade, les deux éléments de l'équations sont PRIMARY
    // On peut donc appliquer la loi de réciprocité:
    // 4, Loi de réciprocité

        mpz_set(tempNeg.x_sol,tempX.x_sol);
        mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
        mpz_set(tempNeg.y_sol,tempX.y_sol);
        mpz_neg(tempNeg.y_sol,tempNeg.y_sol);

        if (!(
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,1) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0)))){

            a = mpz_fdiv_r_ui(temp1,tempY.x_sol,32);
                        b = mpz_fdiv_r_ui(temp1,tempY.y_sol,32);

                        c = a*a + b*b - 1;

                        a = mpz_fdiv_r_ui(temp1,tempX.x_sol,32);
                        b = mpz_fdiv_r_ui(temp1,tempX.y_sol,32);

                        c = c * (a*a + b*b - 1);
                        c /= 16;
                        c = c&1;

                        if ( c== 1) resp += 2;   

         /*   mpz_mul(temp1,tempY.x_sol,tempY.x_sol);
            mpz_mul(temp2,tempY.y_sol,tempY.y_sol);
            mpz_add(temp1,temp1,temp2); // norme de delta
            mpz_neg(temp1,temp1);
            mpz_add_ui(temp1,temp1,1);
            mpz_neg(temp1,temp1); // a^2 + b^2 - 1

            mpz_mul(temp2,tempX.x_sol,tempX.x_sol);
            mpz_mul(temp3,tempX.y_sol,tempX.y_sol);
            mpz_add(temp2,temp2,temp3); // norme de alpha
            mpz_neg(temp2,temp2);
            mpz_add_ui(temp2,temp2,1);
            mpz_neg(temp2,temp2);

            mpz_mul(temp1,temp1,temp2);
            mpz_div_ui(temp1,temp1,16);
            mpz_mod_ui(temp1,temp1,2);

            if (!(mpz_cmp_ui(temp1,0) == 0)){resp = resp + 2;}
*/             
            // mise à jour
            mpz_set(tempX.x_sol,tempY.x_sol);
            mpz_set(tempX.y_sol,tempY.y_sol);
            mpz_set(tempY.x_sol,temp.x_sol);
            mpz_set(tempY.y_sol,temp.y_sol);

            mpz_set(tempNeg.x_sol,tempX.x_sol);
            mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
            mpz_set(tempNeg.y_sol,tempX.y_sol);
            mpz_neg(tempNeg.y_sol,tempNeg.y_sol);
        }

    } // le grand while

    if (!(((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0)))){
        

        if (mpz_cmp_ui(tempX.x_sol,1)   == 0) {r=0;}//4;}
        if (mpz_cmp_ui(tempNeg.x_sol,1) == 0) {r=2;}
        if (mpz_cmp_ui(tempX.y_sol,1)   == 0) {r=1;}
        if (mpz_cmp_ui(tempNeg.y_sol,1) == 0) {r=3;}

                        a = mpz_fdiv_r_ui(temp1,tempY.x_sol,16);
                b = mpz_fdiv_r_ui(temp1,tempY.y_sol,16);

                c = ((a*a + b*b -1)/4)*r;
/*
        mpz_set(temp2,tempY.x_sol);
        mpz_mul(temp2,temp2,tempY.x_sol);
        mpz_set(temp3,tempY.y_sol);
        mpz_mul(temp3,temp3,tempY.y_sol);
        mpz_add(temp3,temp3,temp2);
        mpz_neg(temp3,temp3);
        mpz_add_ui(temp3,temp3,1);
        mpz_neg(temp3,temp3);
        mpz_div_ui(temp3,temp3,4);
        mpz_mul_ui(temp3,temp3,r);
*/
        /*      if (c<0){
                        c = abs(c);
                        c = c&3;

                        if (c == 3){resp = resp + 1;}
                        if (c == 2){resp = resp + 2;}
                        if (c == 1){resp = resp + 3;}
                }
                else{*/
                        c = c&3;

                        if (c == 1){resp = resp + 1;}
                        if (c == 2){resp = resp + 2;}
                        if (c == 3){resp = resp + 3;}
        //      }

  /*      if (mpz_sgn(temp3) == -1){

            mpz_neg(temp3,temp3);
            mpz_mod_ui(temp3,temp3,4);

            if (mpz_cmp_ui(temp3,3) == 0){resp = resp + 1;}
            if (mpz_cmp_ui(temp3,2) == 0){resp = resp + 2;}
            if (mpz_cmp_ui(temp3,1) == 0){resp = resp + 3;}
            }

        else{
            mpz_mod_ui(temp3,temp3,4);

            if (mpz_cmp_ui(temp3,1) == 0){resp = resp + 1;}
            if (mpz_cmp_ui(temp3,2) == 0){resp = resp + 2;}
            if (mpz_cmp_ui(temp3,3) == 0){resp = resp + 3;}
        }
*/
        }////////////////////////////
        resp = resp&3;

        if (resp == 0){
            mpz_set_ui(temp.x_sol,1);mpz_set_ui(temp.y_sol,0);
                } else if (resp == 1){
            mpz_set_ui(temp.x_sol,0);mpz_set_ui(temp.y_sol,1);
                } else if (resp == 2){
           
mpz_set_ui(temp.x_sol,1);mpz_neg(temp.x_sol,temp.x_sol);mpz_set_ui(temp.y_sol,0);
                        
                } else if (resp == 3){
           
mpz_set_ui(temp.x_sol,0);mpz_set_ui(temp.y_sol,1);mpz_neg(temp.y_sol,temp.y_sol);
                        
                } 

        mpz_clear(temp1);
        mpz_clear(temp2);
        mpz_clear(temp3);
        mpz_clear(tempX.x_sol);
        mpz_clear(tempX.y_sol);
        mpz_clear(tempY.x_sol);
        mpz_clear(tempY.y_sol);
        mpz_clear(unPlusI.x_sol);
        mpz_clear(unPlusI.y_sol);
        
    //}

    return temp;

}

int quarticbIt(struct cornAlgo x, struct cornAlgo y){

    struct cornAlgo temp,tempX,tempY,unPlusI,tempNeg;
    mpz_t temp1,temp2,temp3;
    int r=0,s=0,resp,a,b,c;

    int count=0;
        

    mpz_init(tempX.x_sol);
    mpz_init(tempX.y_sol);
    mpz_init(tempY.x_sol);
    mpz_init(tempY.y_sol);
    mpz_init(temp.x_sol);
    mpz_init(temp.y_sol);

    mpz_set(tempX.x_sol,x.x_sol);
    mpz_set(tempX.y_sol,x.y_sol);

    mpz_set(tempY.x_sol,y.x_sol);
    mpz_set(tempY.y_sol,y.y_sol);
    
    mpz_init(temp1);
    mpz_init(temp2);
    mpz_init(temp3);

    mpz_init(tempNeg.x_sol);
    mpz_init(tempNeg.y_sol);
    mpz_set(tempNeg.x_sol,tempX.x_sol);
    mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
    mpz_set(tempNeg.y_sol,tempX.y_sol);
    mpz_neg(tempNeg.y_sol,tempNeg.y_sol);

    mpz_init(unPlusI.x_sol);
    mpz_init(unPlusI.y_sol);

        resp = 0;

    if (((mpz_cmp_ui(tempX.x_sol,0) == 0) && (mpz_cmp_ui(tempX.y_sol,0) == 0))){
       // MessageBox(NULL,"Entrées non conformes...alpha est nulle!\n",NULL,NULL);
        //_beep(200,300);
        //exit(1);
                printf("Entrées non conformes...alpha est nulle!\n");
                return -1;
    }
    
    tempX = moduloGauss(tempX,tempY);
    mpz_set(temp1,tempX.x_sol);
    mpz_set(temp2,tempX.y_sol);
    
    
    if (((mpz_cmp_ui(temp1,0) == 0) && (mpz_cmp_ui(temp2,0) == 0))){
       // MessageBox(NULL,"Entrees non conformes...alpha est un multiple de delta!\n",NULL,NULL);
        //_beep(200,300);
        //exit(1);
                printf("Entrées non conformes...alpha est un multiple de delta!\n");
                return -1;
    }

// 0, PRIMARY test sur le delta. On effectue une transformation si nécessaire
// IL EST ESSENTIEL QUE DELTA SORTE DU TEST EN ETANT PRIMARY POUR QUE L'ALGO
//FONCTIONNE!

    tempY= transformPrimary(tempY.x_sol,tempY.y_sol);

    while (!(
        ((mpz_cmp_ui(tempX.x_sol,0) == 0) && (mpz_cmp_ui(tempX.y_sol,0)
== 0))  ||
        ((mpz_cmp_ui(tempX.x_sol,1) == 0) && (mpz_cmp_ui(tempX.y_sol,0)
== 0))  ||
        ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
        ((mpz_cmp_ui(tempX.x_sol,0) == 0) && (mpz_cmp_ui(tempX.y_sol,1)
== 0))  ||
        ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0))
        )){

        tempX = moduloGauss(tempX,tempY); // on réduit si c'est possible

        mpz_set(tempNeg.x_sol,tempX.x_sol);
        mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
        mpz_set(tempNeg.y_sol,tempX.y_sol);
        mpz_neg(tempNeg.y_sol,tempNeg.y_sol);
        
    // 2, LE PARASITE 1+i
        if (!(
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,1) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0))
            )){

            mpz_mul(temp1,tempX.x_sol,tempX.x_sol);
            mpz_mul(temp2,tempX.y_sol,tempX.y_sol);
            mpz_add(temp1,temp1,temp2);
        
            r = mpz_scan1(temp1,0);

                        a = mpz_fdiv_r_ui(temp1,tempY.x_sol,16);
                        b = mpz_fdiv_r_ui(temp1,tempY.y_sol,16);

                        c = ((a - b - b*b - 1)/4)*r;

         //   mpz_mul(temp3,tempY.y_sol,tempY.y_sol);
         //   mpz_add(temp3,temp3,tempY.y_sol);
         //   mpz_add_ui(temp3,temp3,1);
                //      mpz_sub(temp3,tempY.x_sol,temp3);
          //  mpz_div_ui(temp3,temp3,4); // a-b-b^2-1 / 4
           // mpz_mul_ui(temp3,temp3,r); // * r

            //if (mpz_sgn(temp3) == -1){
                        if (c<0){
                                c = abs(c);
                                c = c&3;

                //mpz_neg(temp3,temp3);
                //mpz_mod_ui(temp3,temp3,4);

                                if (c == 3){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 1){resp = resp +  3;}

               // if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  1;}
               // if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
               // if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  3;}
                }

            else{
                //mpz_mod_ui(temp3,temp3,4);
                                c = c&3;

                                if (c == 1){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 3){resp = resp +  3;}
                //if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  1;}
                //if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
                //if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  3;}
            }

           // mpz_set_ui(unPlusI.x_sol,1);
           // mpz_set_ui(unPlusI.y_sol,1);
            //unPlusI = powerComplex(unPlusI,r);
                        unPlusI = powerUnPlusI(r);
            tempX = divExactComplex(tempX,unPlusI);

            mpz_set(tempNeg.x_sol,tempX.x_sol);
            mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
            mpz_set(tempNeg.y_sol,tempX.y_sol);
            mpz_neg(tempNeg.y_sol,tempNeg.y_sol);
        }

        if (!(
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,1) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0)))){

    // 3, PRIMARIYsation
            temp = transformPrimary(tempX.x_sol,tempX.y_sol);

            if ((mpz_cmp(tempX.x_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempX.y_sol,temp.y_sol) == 0)){
                s=0;} // *1
            if ((mpz_cmp(tempNeg.y_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempX.x_sol,temp.y_sol) == 0)){
                s=1;} // *i
            if ((mpz_cmp(tempNeg.x_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempNeg.y_sol,temp.y_sol) == 0)){
                s=2;} // *-1
            if ((mpz_cmp(tempX.y_sol,temp.x_sol) == 0) &&
(mpz_cmp(tempNeg.x_sol,temp.y_sol) == 0)){
                s=3;} // *-i

    // calcul de i/delta:

                        a = mpz_fdiv_r_ui(temp1,tempY.x_sol,16);
                        b = mpz_fdiv_r_ui(temp1,tempY.y_sol,16);

                        c = ((a*a + b*b - 1)/4)*3*s;


         /*   mpz_set(temp2,tempY.x_sol);
            mpz_mul(temp2,temp2,tempY.x_sol);// a^2
            mpz_set(temp3,tempY.y_sol);
            mpz_mul(temp3,temp3,tempY.y_sol); // b^2
            mpz_add(temp3,temp3,temp2); // a^2+b^2
            mpz_neg(temp3,temp3); // -(a^2+b-2)
            mpz_add_ui(temp3,temp3,1); // 1-(a^2+b^2)
            mpz_neg(temp3,temp3); // a^2+b^2-1
            mpz_div_ui(temp3,temp3,4);
            s=3*s; // (-i)^s = i^3s
            mpz_mul_ui(temp3,temp3,s);
                */
          //  if (mpz_sgn(temp3) == -1){

            //    mpz_neg(temp3,temp3);
              //  mpz_mod_ui(temp3,temp3,4);

                /*      if (c<0){
                                
                                c = abs(c); c = c&3;

                if (c == 3){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 1){resp = resp +  3;}
                        //      if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  1;}
             //   if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
             //   if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  3;}
                }

            else{*/
              //  mpz_mod_ui(temp3,temp3,4);
                                c = c&3;

                if (c == 1){resp = resp +  1;}
                                if (c == 2){resp = resp +  2;}
                                if (c == 3){resp = resp +  3;}
              //  if (mpz_cmp_ui(temp3,1) == 0){resp = resp +  1;}
              //  if (mpz_cmp_ui(temp3,2) == 0){resp = resp +  2;}
              //  if (mpz_cmp_ui(temp3,3) == 0){resp = resp +  3;}
           // }
    // mise à jour de x
            mpz_set(tempX.x_sol,temp.x_sol);
            mpz_set(tempX.y_sol,temp.y_sol);
        }

    // A ce stade, les deux éléments de l'équations sont PRIMARY
    // On peut donc appliquer la loi de réciprocité:
    // 4, Loi de réciprocité

        mpz_set(tempNeg.x_sol,tempX.x_sol);
        mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
        mpz_set(tempNeg.y_sol,tempX.y_sol);
        mpz_neg(tempNeg.y_sol,tempNeg.y_sol);

        if (!(
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))  ||
            ((mpz_cmp_ui(tempNeg.x_sol,1) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0))||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,1) == 0))  ||
            ((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempNeg.y_sol,1) == 0)))){

            a = mpz_fdiv_r_ui(temp1,tempY.x_sol,32);
                        b = mpz_fdiv_r_ui(temp1,tempY.y_sol,32);

                        c = a*a + b*b - 1;

                        a = mpz_fdiv_r_ui(temp1,tempX.x_sol,32);
                        b = mpz_fdiv_r_ui(temp1,tempX.y_sol,32);

                        c = c * (a*a + b*b - 1);
                        c /= 16;
                        c = c&1;

                        if ( c== 1) resp += 2;   

         /*   mpz_mul(temp1,tempY.x_sol,tempY.x_sol);
            mpz_mul(temp2,tempY.y_sol,tempY.y_sol);
            mpz_add(temp1,temp1,temp2); // norme de delta
            mpz_neg(temp1,temp1);
            mpz_add_ui(temp1,temp1,1);
            mpz_neg(temp1,temp1); // a^2 + b^2 - 1

            mpz_mul(temp2,tempX.x_sol,tempX.x_sol);
            mpz_mul(temp3,tempX.y_sol,tempX.y_sol);
            mpz_add(temp2,temp2,temp3); // norme de alpha
            mpz_neg(temp2,temp2);
            mpz_add_ui(temp2,temp2,1);
            mpz_neg(temp2,temp2);

            mpz_mul(temp1,temp1,temp2);
            mpz_div_ui(temp1,temp1,16);
            mpz_mod_ui(temp1,temp1,2);

            if (!(mpz_cmp_ui(temp1,0) == 0)){resp = resp + 2;}
*/             
            // mise à jour
            mpz_set(tempX.x_sol,tempY.x_sol);
            mpz_set(tempX.y_sol,tempY.y_sol);
            mpz_set(tempY.x_sol,temp.x_sol);
            mpz_set(tempY.y_sol,temp.y_sol);

            mpz_set(tempNeg.x_sol,tempX.x_sol);
            mpz_neg(tempNeg.x_sol,tempNeg.x_sol);
            mpz_set(tempNeg.y_sol,tempX.y_sol);
            mpz_neg(tempNeg.y_sol,tempNeg.y_sol);count++;
        }

    } // le grand while

    if (!(((mpz_cmp_ui(tempX.x_sol,0) == 0) &&
(mpz_cmp_ui(tempX.y_sol,0) == 0)))){
        

        if (mpz_cmp_ui(tempX.x_sol,1)   == 0) {r=0;}//4;}
        if (mpz_cmp_ui(tempNeg.x_sol,1) == 0) {r=2;}
        if (mpz_cmp_ui(tempX.y_sol,1)   == 0) {r=1;}
        if (mpz_cmp_ui(tempNeg.y_sol,1) == 0) {r=3;}

                        a = mpz_fdiv_r_ui(temp1,tempY.x_sol,16);
                b = mpz_fdiv_r_ui(temp1,tempY.y_sol,16);

                c = ((a*a + b*b -1)/4)*r;
/*
        mpz_set(temp2,tempY.x_sol);
        mpz_mul(temp2,temp2,tempY.x_sol);
        mpz_set(temp3,tempY.y_sol);
        mpz_mul(temp3,temp3,tempY.y_sol);
        mpz_add(temp3,temp3,temp2);
        mpz_neg(temp3,temp3);
        mpz_add_ui(temp3,temp3,1);
        mpz_neg(temp3,temp3);
        mpz_div_ui(temp3,temp3,4);
        mpz_mul_ui(temp3,temp3,r);
*/
        /*      if (c<0){
                        c = abs(c);
                        c = c&3;

                        if (c == 3){resp = resp + 1;}
                        if (c == 2){resp = resp + 2;}
                        if (c == 1){resp = resp + 3;}
                }
                else{*/
                        c = c&3;

                        if (c == 1){resp = resp + 1;}
                        if (c == 2){resp = resp + 2;}
                        if (c == 3){resp = resp + 3;}
        //      }

  /*      if (mpz_sgn(temp3) == -1){

            mpz_neg(temp3,temp3);
            mpz_mod_ui(temp3,temp3,4);

            if (mpz_cmp_ui(temp3,3) == 0){resp = resp + 1;}
            if (mpz_cmp_ui(temp3,2) == 0){resp = resp + 2;}
            if (mpz_cmp_ui(temp3,1) == 0){resp = resp + 3;}
            }

        else{
            mpz_mod_ui(temp3,temp3,4);

            if (mpz_cmp_ui(temp3,1) == 0){resp = resp + 1;}
            if (mpz_cmp_ui(temp3,2) == 0){resp = resp + 2;}
            if (mpz_cmp_ui(temp3,3) == 0){resp = resp + 3;}
        }
*/
        }////////////////////////////
        resp = resp&3;

        if (resp == 0){
            mpz_set_ui(temp.x_sol,1);mpz_set_ui(temp.y_sol,0);
                } else if (resp == 1){
            mpz_set_ui(temp.x_sol,0);mpz_set_ui(temp.y_sol,1);
                } else if (resp == 2){
           
mpz_set_ui(temp.x_sol,1);mpz_neg(temp.x_sol,temp.x_sol);mpz_set_ui(temp.y_sol,0);
                        
                } else if (resp == 3){
           
mpz_set_ui(temp.x_sol,0);mpz_set_ui(temp.y_sol,1);mpz_neg(temp.y_sol,temp.y_sol);
                        
                } 

        mpz_clear(temp1);
        mpz_clear(temp2);
        mpz_clear(temp3);
        mpz_clear(tempX.x_sol);
        mpz_clear(tempX.y_sol);
        mpz_clear(tempY.x_sol);
        mpz_clear(tempY.y_sol);
        mpz_clear(unPlusI.x_sol);
        mpz_clear(unPlusI.y_sol);
        
    //}

    return count;

}
