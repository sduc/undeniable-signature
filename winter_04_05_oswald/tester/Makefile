CC = gcc
CFLAGS = -Wall  -g 

program: compare.o quartic1.o quartic2.o damgard.o jacobi.o discreteLogarithm.o rsa.o help.o frequence_cpu.o
	$(CC) -lgmp $(CFLAGS) compare.o quartic1.o quartic2.o damgard.o jacobi.o discreteLogarithm.o rsa.o frequence_cpu.o help.o -o program


compare.o: compare.c quartic1.h quartic2.h damgard.h jacobi.h discreteLogarithm.h help.h rsa.h
	$(CC) $(CFLAGS) -c compare.c

discreteLogarithm.o: discreteLogarithm.h discreteLogarithm.c
	$(CC) $(CFLAGS) -c discreteLogarithm.c

quartic1.o: quartic1.c quartic1.h
	$(CC) $(CFLAGS) -c quartic1.c

quartic2.o: quartic2.c quartic2.h
	$(CC) $(CFLAGS) -c quartic2.c

damgard.o: damgard.c quartic2.h damgard.h
	$(CC) $(CFLAGS) -c damgard.c
        
jacobi.o: jacobi.c jacobi.h
	$(CC) $(CFLAGS) -c jacobi.c
	
help.o: help.c help.h
	$(CC) $(CFLAGS) -c help.c

frequence_cpu.o: frequence_cpu.c frequence_cpu.h
	$(CC) $(CFLAGS) -c frequence_cpu.c

rsa.o: rsa.c rsa.h
	$(CC) $(CFLAGS) -c rsa.c
clean:
	rm -f *.o program
	
