#include "gmp.h"


//-------------programmed by Malik Hammoutene---------------

extern struct cornAlgo{        
        
// structure pour définir les nombres complexes
// (elle porte ce nom simplement parce qu'elle
// apparaît pour la première fois dans Cornacchia

        mpz_t x_sol;
        mpz_t y_sol;
};
extern struct cornAlgo quarticb1(struct cornAlgo x, struct cornAlgo y);
extern struct cornAlgo transformPrimary(mpz_t inX, mpz_t inY);
extern struct cornAlgo multComplex(struct cornAlgo x, struct cornAlgo y);
extern struct cornAlgo powerUnPlusI(int r);
extern struct cornAlgo divExactComplex(struct cornAlgo x, struct cornAlgo y);
extern struct cornAlgo moduloGauss(struct cornAlgo x, struct cornAlgo y);

//-------------programmed by Malik Hammoutene---------------




//-------------programmed by Yvonne Anne Oswald-------------

//count number of iterations of basic algorithm
extern int quarticbIt(struct cornAlgo x, struct cornAlgo y);

//-------------programmed by Yvonne Anne Oswald-------------

