#pragma once
#include "gmp.h"
#include "quartic2.h"
#include "help.h"


//calculate the Damgard's approximate norm
extern void norm2(mpz_t norm,const struct gaussInt x);

//calculate the quartic residue symbol using Damgard's algorithm
extern struct gaussInt quarticd(struct gaussInt x, struct gaussInt y);

//calculate the quartic residue symbol using Damgard's algorithm
//with comments printed on console to allow following the algorithm
extern struct gaussInt quarticverbose(struct gaussInt x, struct gaussInt y);

//count the number of iterations Damgard's algorithm needs
extern int quarticdIt(struct gaussInt x, struct gaussInt y);

