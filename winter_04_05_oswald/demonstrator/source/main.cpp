#define _WIN32_WINNT 0x0501
#define IDT_TIMER1   300

#define LKEY 80
#define LSIG 20
// pour rendre le système plus maléable, il faudrait intégrer des constantes de ce type. 

#include <process.h>
#include <shlobj.h>
#include <malloc.h>
#include <math.h>
#include <iostream.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <windows.h>
#include <wincrypt.h>
#include <sys\timeb.h>
#include "dictionnaire.h"
#include "prng.h"
#include "quartic.h"
#include "resource.h"
#include "progressbar.h"
#include "serveur.h"
#include "gmp/gmp.h"
#include "cryptlib/mycrypt.h"
#pragma comment(lib,"wsock32.lib")
#pragma comment(lib, "C:/Documents And Settings/mhammout/Desktop/FINAL MOVA/gmp.lib") // Ne pas oublier de modifier!
#pragma comment(lib, "C:/Documents And Settings/mhammout/Desktop/FINAL MOVA/tomcrypt.lib") // Ne pas oublier de modifier!

// les différentes fenêtres du programme:
// "Generate a signature"
BOOL CALLBACK MainDlgProc(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam);
// "Page of choices"
BOOL CALLBACK ChoiDlgProc(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam);
// "Verifier Page (Conf)"
BOOL CALLBACK ConfDlgProc(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam);
// "Prover Page (Conf)"
BOOL CALLBACK ConfPDlgProc(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam);
// "Prover Page (Denial)"
BOOL CALLBACK DeniDlgProcP(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam);
// "Verifier Page (Denial)"
BOOL CALLBACK DeniDlgProcV(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam);

// fonction pour calculer l'ensemble S
//int computing_the_s_file(HWND hWndconf,char** n_pkref,char** yKeys_pkref,char** seedK_pkref,char concat100[201],char** concat100ref,char** szDataref);
int computing_the_s_file(HWND hWndconf,char** n_pkref,char** yKeys_pkref,char** seedK_pkref,char** concat100ref,char** szDataref);
// fonction pour calculer les u
int getting_the_u_file(HWND hWnd,char** szData2ref, char** szDataref);
// fonction pour signer un fichier
int signOnBOk(HWND hWnd, int idcname, int idcname2, int chrono, char** pwdref);
// fonction Verifier (Conf)
int computeS(HWND hWndconf);
// fonction Verifier (Denial)
int computeSDeni(HWND hWnddeniV);
// fonction Prover (Conf)
int computeH(HWND hWndconfp);
// fonction Prover (Denial)
int computeHDeni(HWND hWndconfpDeni);
// fonctions pour générer des clés
int geneOnBOk(HWND hWnd);
int generateKeys(char* pkname, char* skname, char* bitnb);
// fonction pour générer des Xsigs à partir d'un fichier
int compute_the_x_sigs(HWND hWnd, int path_src, char*** xsigref);
// fonction pour gérer les Browse
void OnBrowse(HWND hParent,bool bOpen, int idc_name, int extension);
// fonction de hashage avec SHA-1
void encode(char* pszData,unsigned char* out, DWORD dwFileSize);
// fonction de hashage avec MD-5
void commit(char* entry, DWORD dwFileSize, DWORD dwFileSize2);
// fonction d'affichage dans une listbox
void afficheMsg(char* msg, int idc_name, HWND hWnd);
// booléin pour afficher la listbox
BOOL EditVisible = FALSE;
// booléin pour savoir si conf ou denial
BOOL Denial = FALSE;
// texte dans les editbox des fenêtres
char* pk_text;
char* ip_text;
char* po_text;
char* sm_text;
char* sf_text;
char* sk_text;
char* s2_text;
// fonction pour conserver le contenu des editbox
char* keepPath(HWND hWnd, int idc_name);

// ###############################################################
// *********** 
// *********** Fonction d'affichage dans une listbox
// *********** 
// ###############################################################

void afficheMsg(char* msg, int idc_name, HWND hWnd){
	
	char* szRes;

	if (EditVisible) {
	szRes = new char[1000];
	sprintf(szRes, msg);
	SendMessage(GetDlgItem(hWnd, idc_name), LB_SETTOPINDEX,(WPARAM)SendMessage(GetDlgItem(hWnd, idc_name), LB_ADDSTRING, (WPARAM)0, (LPARAM)szRes),(LPARAM)0);
	UpdateWindow(hWnd);
	delete[] szRes;
	}
}

// ###############################################################
// *********** 
// *********** Sélection d'une ligne dans un fichier
// *********** 
// ###############################################################

char* GetLineFromBuf(char* lpBuf, int iLine, int iNbLines) {

        if(!lpBuf || (iLine < 0) || (iLine >= iNbLines))
		{MessageBox(NULL, "Error reading file!\nProtocol aborted", "Error", MB_ICONERROR); 
		return NULL;}

        while(iLine--)
                lpBuf += strlen(lpBuf) + 1;

        return lpBuf;
}

// ###############################################################
// *********** 
// *********** fonction de commitment: MD5(msg+r)
// *********** 
// ###############################################################

	void commit(char* entry, unsigned char* truc, DWORD dwFileSize){

	hash_state md;
	md5_init(&md);

	md5_process(&md, (unsigned char*)entry, dwFileSize);
	md5_done(&md, truc);
}

// ###############################################################
// *********** 
// *********** fonction de haschage SHA-1
// *********** 
// ###############################################################

void encode(char* pszData, unsigned char* out, DWORD dwFileSize) 
{	
	hash_state sha;
	sha1_init(&sha);
	sha1_process(&sha, (unsigned char*)pszData, dwFileSize);
	sha1_done(&sha, out);
}

// ###############################################################
// *********** 
// *********** génération d'un grand premier tel que p = 1+4t
// *********** 
// ###############################################################

void genPrime(mpz_t prime_n,mpz_t seedNb){
 
	mpz_t t,t2;
	int d2;
	
	d2=getrand();
	mpz_init(t);
	mpz_mul_ui(t,seedNb,d2);
	mpz_set(t,seedNb);
	mpz_mul_ui(t,t,4);
	mpz_add_ui(t,t,1);

	if (mpz_probab_prime_p(t,20)==0){ 
		while (mpz_probab_prime_p(t,20)==0){
			d2=getrand();
			mpz_mul_ui(t,seedNb,d2);
			mpz_mul_ui(t,t,4);
			mpz_add_ui(t,t,1);
		}
	}

	mpz_init(t2);
	mpz_set(t2,t);
	mpz_set(t,t2);
	mpz_set(prime_n,t);
	mpz_clear(t);
	mpz_clear(t2);
}

// ###############################################################
// *********** 
// *********** génération d'un nombre aléatoire > 2^(d-1)
// *********** 
// ###############################################################

void getrand2(mpz_t randNb1, char* bitnb){

	gmp_randstate_t state;
	mpz_t comp;
	HCRYPTPROV hProv = 0;
	int rnd,d;
	CryptAcquireContext(&hProv, 0, 0, PROV_RSA_FULL, CRYPT_VERIFYCONTEXT);
	CryptGenRandom(hProv, sizeof(rnd), (BYTE*)&rnd);
	CryptReleaseContext(hProv, 0);
	rnd = abs(rnd);

	d=atoi(bitnb);
	
	mpz_init(comp);
	mpz_set_ui(comp,2);
	mpz_pow_ui(comp,comp,d-1); // comp = 2^(d-1)
 
	
	gmp_randinit_default(state);
	gmp_randseed_ui(state,rnd);

	mpz_urandomb(randNb1, state, d-1);// on en prend un au hasard entre 0 et 2^(d-1)-1
	mpz_xor(randNb1, randNb1,comp); // on XOR le tout pour être avec un nb > 2^(d-1)
	mpz_clear(comp);
}

// ###############################################################
// *********** 
// *********** génération d'un élément inversible dans Zn
// *********** 
// ###############################################################

void getrand3(mpz_t randNb1, mpz_t n){

	gmp_randstate_t state;
	HCRYPTPROV hProv = 0;
	unsigned long int rnd;

	CryptAcquireContext(&hProv, 0, 0, PROV_RSA_FULL, CRYPT_VERIFYCONTEXT);
	CryptGenRandom(hProv, sizeof(rnd), (BYTE*)&rnd);
	CryptReleaseContext(hProv, 0);
	// TODO:
	//rnd : en faire 4 et utiliser les 128b comme seed
	// sortir la fabrication du seed, et mettre cela au début du vérifieur

	gmp_randinit_default(state);
	gmp_randseed_ui(state,rnd);

	mpz_urandomm(randNb1, state, n);  
}

HWND hdlgdem;
HINSTANCE hInst;

// ###############################################################
// *********** 
// *********** affichage de l'image "MOVA" du démarrage
// *********** 
// ###############################################################

LRESULT CALLBACK Demarrage (HWND hdlgdem, UINT msg, WPARAM wParam, LPARAM lParam)
{
	switch(msg)
	{
		case WM_INITDIALOG:
             SetTimer(hdlgdem,IDT_TIMER1,2000,(TIMERPROC) NULL);
			 //3e paramètre = tps d'affichage de l'image en msec
			 break;

        case WM_TIMER:  
             switch (wParam)
             {
             case IDT_TIMER1:
             EndDialog(hdlgdem, FALSE);
             break;
             }

        default:
			return FALSE;
	}

return TRUE; 
}

// ###############################################################
// *********** 
// *********** fonction de chemin au bureau
// *********** 
// ###############################################################

char* pathDesktop(char* nomfichier){
	char* szbuff;

	szbuff = new char[255+strlen(nomfichier)];
	SHGetSpecialFolderPath(0, szbuff, 0, 0);
	strcat(szbuff,"\\");
	strcat(szbuff,nomfichier);

	return szbuff;

	}

// ###############################################################
// *********** 
// *********** Point d'entrée du programme
// *********** 
// ###############################################################

int WINAPI WinMain(HINSTANCE hInst,HINSTANCE hPrev,LPSTR szCmdLine,int nCmdShow)
{
	
	DialogBox(hInst, MAKEINTRESOURCE(IDD_DIALOGDEM), NULL, (DLGPROC) Demarrage);
	// création du dossier MovaDir
	if(CreateDirectory(pathDesktop("MovaDir"),NULL) == ERROR_ALREADY_EXISTS){}
	return DialogBox(hInst,MAKEINTRESOURCE(IDD_DIALOG1),NULL, ChoiDlgProc);	
}

// ###############################################################
// *********** 
// *********** Fonction pour conserver les données dans Verbose
// *********** 
// ###############################################################

char* keepPath(HWND hWnd, int idc_name){
	int nLen;
	HWND hCtrl;
	char* szPath;
	char* one_text;

	hCtrl = GetDlgItem(hWnd,idc_name);
	nLen = GetWindowTextLength(hCtrl) + 1;

	szPath=(char *)malloc(nLen);
	one_text=(char *)malloc(nLen);
	
	GetWindowText(hCtrl,szPath,nLen);
	one_text = szPath;

	return one_text;
}

// ###############################################################
// *********** 
// *********** Fenêtre de choix (radios)
// *********** 
// ###############################################################

BOOL CALLBACK ChoiDlgProc (HWND hWndtent, UINT msg, WPARAM wParam, LPARAM lParam)
{
	
	switch(msg)
	{
		case WM_INITDIALOG:
 		return TRUE;

        case WM_COMMAND:
			switch((UINT)wParam)
			{
				case IDOK_RADIO:
					// quit
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO5),BM_GETCHECK,0,0) == BST_CHECKED)
						return EndDialog(hWndtent,0);
					// verifier (deni)
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO4),BM_GETCHECK,0,0) == BST_CHECKED)
						{EndDialog(hWndtent,0);
						Denial = TRUE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_PROTCONF),NULL,DeniDlgProcV);
						}
					// prover (deni)
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO7),BM_GETCHECK,0,0) == BST_CHECKED)
						{EndDialog(hWndtent,0);
						Denial = TRUE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_CONFPROV),NULL,DeniDlgProcP);
						}
					// verifier (conf)
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO3),BM_GETCHECK,0,0) == BST_CHECKED)
						{EndDialog(hWndtent,0);
						Denial = FALSE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_PROTCONF),NULL,ConfDlgProc);
						}
					// email (not implemented)
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO2),BM_GETCHECK,0,0) == BST_CHECKED)
						return EndDialog(hWndtent,0);
					// 	signer un fichier	
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO1),BM_GETCHECK,0,0) == BST_CHECKED)
						{EndDialog(hWndtent,0);
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_MAIN1),NULL,MainDlgProc);
						}
					// générer des clés
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO8),BM_GETCHECK,0,0) == BST_CHECKED)
						{EndDialog(hWndtent,0);
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_MAIN2),NULL,MainDlgProc);
						}
					// prover (conf)
					if(SendMessage(GetDlgItem(hWndtent,IDC_RADIO6),BM_GETCHECK,0,0) == BST_CHECKED)
						{EndDialog(hWndtent,0);
						Denial = FALSE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_CONFPROV),NULL,ConfPDlgProc);
						}
				
				default:
					return FALSE;
			}
			break;
	}
return FALSE; 
}

// ###############################################################
// *********** 
// *********** Fenêtre de GHIProof - The Prover
// *********** 
// ###############################################################

BOOL CALLBACK ConfPDlgProc (HWND hWndconfp, UINT msg, WPARAM wParam, LPARAM lParam)
{

	switch(msg)
		{
			case WM_INITDIALOG:
				HWND hCtrl;
				WSADATA wsaData;
				int iResult;

				// gestion du contenu des editbox
				hCtrl = GetDlgItem(hWndconfp,IDC_BW_PATH);
				SetWindowText(hCtrl,sk_text);free(sk_text);
				
				hCtrl = GetDlgItem(hWndconfp,IDC_BW_PATH2);
				SetWindowText(hCtrl,s2_text);free(s2_text);

				hCtrl = GetDlgItem(hWndconfp,IDC_PORT);
				if (po_text == NULL) SetWindowText(hCtrl,"4000");
				else SetWindowText(hCtrl,po_text); free(po_text);

				hCtrl = GetDlgItem(hWndconfp,IDC_GHI2);
				SetWindowText(hCtrl,"THIS IS THE GHI-PROOF");

				iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
				if (iResult != NO_ERROR) {return FALSE;}
				
				return TRUE;

			case WM_COMMAND:
	
				switch((UINT)wParam)
				{
					case IDC_CL:
						return EndDialog(hWndconfp,0);

					case IDC_BW2:
						OnBrowse(hWndconfp,true, IDC_BW_PATH2,0);
						return TRUE;

					case IDC_BW:
						OnBrowse(hWndconfp,true, IDC_BW_PATH, 1);
						return TRUE;

										
					case IDC_VERBOSE_P:	// si on voulait le verbose
						sk_text = keepPath(hWndconfp,IDC_BW_PATH);
						s2_text = keepPath(hWndconfp,IDC_BW_PATH2);
						po_text = keepPath(hWndconfp,IDC_PORT);

						if(SendMessage(GetDlgItem(hWndconfp,IDC_VERBOSE_P),BM_GETCHECK,0,0) == BST_CHECKED){
							EndDialog(hWndconfp,0);
							EditVisible = TRUE;
							return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_CONFPROV1),NULL,ConfPDlgProc);	}
						break;

					case IDC_CHECK2:
						sk_text = keepPath(hWndconfp,IDC_BW_PATH);
						s2_text = keepPath(hWndconfp,IDC_BW_PATH2);
						po_text = keepPath(hWndconfp,IDC_PORT);

						if(SendMessage(GetDlgItem(hWndconfp,IDC_CHECK2),BM_GETCHECK,0,0) == BST_CHECKED){
							EndDialog(hWndconfp,0);
							EditVisible = FALSE;
							return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_CONFPROV),NULL,ConfPDlgProc);	}
						break;

					case IDC_LISTEN: // execution

						char tBuff[6];
						int PORT, nLen;
						HWND hCtrl;
						char* szSrc;
						char* getCom;
						char** pwdref;
						int i,x;
						char* szSig;
						char* szFil;
						char* szData;
						char* d;
						char*buffer;
						char denOrnot[100];
						unsigned char out[20];
						HANDLE hSrcFile;
						DWORD dwFileSize;
						DWORD dw;

						szSig = NULL;
						szFil = NULL;
						szData = NULL;
						d = NULL;
						dwFileSize = 0;

						char* szSrc2;
						char* szSrc3;

						GetDlgItemText(hWndconfp,IDC_PORT,tBuff,5);
						
						if (strcmp(tBuff,"") == 0){
							MessageBox(hWndconfp,"unavailable port number",NULL,MB_ICONEXCLAMATION);
							return FALSE;}

						hCtrl = GetDlgItem(hWndconfp,IDC_BW_PATH2);
						nLen = GetWindowTextLength(hCtrl) + 1;
						szSrc2 = new char[nLen];
						GetWindowText(hCtrl,szSrc2,nLen);
						if ((strcmp(szSrc2,"") == 0)){
							MessageBox(hWndconfp,"unavailable signed file path",NULL,MB_ICONEXCLAMATION);
							return FALSE;} 

						hCtrl = GetDlgItem(hWndconfp,IDC_BW_PATH);
						nLen = GetWindowTextLength(hCtrl) + 1;
						szSrc3 = new char[nLen];
						GetWindowText(hCtrl,szSrc3,nLen);
						if ((strcmp(szSrc3,"") == 0) || (strcmp(szSrc3,".smova") == 0)){
							MessageBox(hWndconfp,"unavailable secret key path\nplease, choose a \".smova\" file  ",NULL,MB_ICONEXCLAMATION);
							return FALSE;}

						delete[] szSrc2;
						delete[] szSrc3;

						pwdref = &szFil;
						signOnBOk(hWndconfp,IDC_BW_PATH,IDC_BW_PATH2,1, pwdref);


						// lecture du fichier à signer
						hCtrl = GetDlgItem(hWndconfp,IDC_BW_PATH2);
						nLen = GetWindowTextLength(hCtrl) + 1;
						szSig = new char[nLen];
						GetWindowText(hCtrl,szSig,nLen);

						hSrcFile = CreateFile(szSig,GENERIC_READ,FILE_SHARE_READ,NULL,
								OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
						if(hSrcFile == INVALID_HANDLE_VALUE)
						{
						CloseHandle(hSrcFile);
						return 6;
						}
						delete[] szSig;
						dwFileSize = GetFileSize(hSrcFile,NULL);
						szData = new char[dwFileSize+1];

						if(!ReadFile(hSrcFile,szData,dwFileSize,&dw,NULL))
						{
							delete[] szData;
							CloseHandle(hSrcFile);
							return 6;
						}
						CloseHandle(hSrcFile);

						*(szData + dwFileSize) = '\0';

						d = szData;
						i = (*d ? 1 : 0);

						while(*d){
							switch(*d){
							case '\r':
								memmove(d,d+1,strlen(d+1)+1);
								break;
							case '\n':
								*d='\0';
								d++;
								i++;
								break;
							default:
								d++;
								break;
							}
						}
						d=NULL;
			
						// encodage (SHA-1)
						ZeroMemory(out,20);
						encode(szData,out,dwFileSize);
						for (x=0; x<20;x++)	nLen += strlen((char*)(IntToChar((int)out[x])));

						ZeroMemory(denOrnot,nLen);
						buffer=new char;
						for (x=0; x<20;x++) {
							sprintf(buffer,"%02x",out[x]);
							strcat(denOrnot,buffer);
						}
						strcat(denOrnot,szFil);

						getCom = NULL;
						GetDlgItemText(hWndconfp,IDC_PORT,tBuff,5);
						if (strcmp(tBuff,"") == 0){
							MessageBox(hWndconfp,"unavailable port number",NULL,MB_ICONEXCLAMATION);
							return FALSE;}

						hCtrl = GetDlgItem(hWndconfp,IDC_BW_PATH);
						nLen = GetWindowTextLength(hCtrl) + 1;
						szSrc = new char[nLen];
						GetWindowText(hCtrl,szSrc,nLen);
						if ((strcmp(szSrc,"") == 0) || (strcmp(szSrc,".smova") == 0)){
							MessageBox(hWndconfp,"unavailable secret key path\nplease, choose a \".smova\" file  ",NULL,MB_ICONEXCLAMATION);
							return FALSE;}

						PORT = atoi(tBuff);

						hCtrl = GetDlgItem(hWndconfp,IDC_SL);
						SetWindowText(hCtrl, "Waiting for the verifier...");
						ServerListen(PORT);

						if ((m_socket = accept(m_socket,NULL,NULL)) != INVALID_SOCKET){
							hCtrl = GetDlgItem(hWndconfp,IDC_SL);
							SetWindowText(hCtrl, "Verifier connected...");
						}

						getCom = (char*)malloc(100);

						if (!(csGet(getCom))) return FALSE;
						
						// En enlevant les commentaires qui suivent, cela permettrait
						// de savoir si il serait plus judicieux d'exécuter GHIProof ou
						// coGHIProof. Ceci est un début de l'évolution possible du programme
						// là où il choisit automatiquement quel "proof" exécuter.
						
						//MessageBox(NULL,getCom, denOrnot,NULL);
						//if (!(strcmp(getCom,denOrnot))){

						//	if (!(csSend("0",2))) return FALSE;
						//	MessageBox(0,"you should execute GHIPROOF","proof is...",0);
						//}
						///else{

						//	if (!(csSend("0",2))) return FALSE;
						//	MessageBox(0,"you should execute coGHIPROOF","proof is...",0);
						//}

						i = computeH(hWndconfp);
						if (i == 0){
						MessageBox(hWndconfp,"GHIProof successfully executed","Prover: Success",MB_OK);
						return TRUE;
						}
						if (i == 1){
						MessageBox(hWndconfp,"unavailable signed file path",0,MB_ICONEXCLAMATION);
						return FALSE;
						}
						if (i == 2){
						MessageBox(hWndconfp,"unavailable secret key path",0,MB_ICONEXCLAMATION);
						return FALSE;
						}
						if (i == 3){
						MessageBox(hWndconfp,"unavailable port number",0,MB_ICONEXCLAMATION);
						return FALSE;
						}
						if (i == 6){
						MessageBox(hWndconfp,"File error!",0,MB_ICONERROR);
						return FALSE;
						}
						if (i == 7){
						MessageBox(hWndconfp,"Socket error!",0,MB_ICONERROR);
						return FALSE;
						}
						if (i == 8){
						MessageBox(hWndconfp,"GHIProof failed!",0,MB_ICONERROR);
						return FALSE;
						}

						hardClose(m_socket);
						return TRUE;
				}
	}
	
	return FALSE; 
}

// ###############################################################
// *********** 
// *********** Fenêtre de GHIProof - The Verifier
// *********** 
// ###############################################################

BOOL CALLBACK ConfDlgProc (HWND hWndconf, UINT msg, WPARAM wParam, LPARAM lParam)
{	
	switch(msg)
	{	
		case WM_INITDIALOG:
			HWND hCtrl;
			WSADATA wsaData;
			int iResult;

			hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC);
			SetWindowText(hCtrl,pk_text);free(pk_text);

			hCtrl = GetDlgItem(hWndconf,IDC_IP);
			if (ip_text == NULL) SetWindowText(hCtrl,"lasecpc6.epfl.ch");
			else SetWindowText(hCtrl,ip_text); free(ip_text);
			
			hCtrl = GetDlgItem(hWndconf,IDC_PORT);
			if (po_text == NULL) SetWindowText(hCtrl,"4000");
			else SetWindowText(hCtrl,po_text); free(po_text);
			
			hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC3);
			SetWindowText(hCtrl,sm_text);free(sm_text);
			
			hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC2);
			SetWindowText(hCtrl,sf_text);free(sf_text);
			
			iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
			if (iResult != NO_ERROR) {return FALSE;}

			hCtrl = GetDlgItem(hWndconf,IDC_GHI);
			SetWindowText(hCtrl,"THIS IS THE GHI-PROOF");

			return TRUE;

 		return TRUE;

		case WM_COMMAND:
			
			switch((UINT)wParam)
			{
				case IDC_CHECK1:

					pk_text = keepPath(hWndconf,IDC_PATH_SRC);
					ip_text = keepPath(hWndconf,IDC_IP);
					po_text = keepPath(hWndconf,IDC_PORT);
					sm_text = keepPath(hWndconf,IDC_PATH_SRC3);
					sf_text = keepPath(hWndconf,IDC_PATH_SRC2);

					if(SendMessage(GetDlgItem(hWndconf,IDC_CHECK1),BM_GETCHECK,0,0) == BST_CHECKED){
						EndDialog(hWndconf,0);
						EditVisible = FALSE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_PROTCONF),NULL,ConfDlgProc);	
					}
					break;

				case IDC_VERBOSE_V:
					
					pk_text = keepPath(hWndconf,IDC_PATH_SRC);
					ip_text = keepPath(hWndconf,IDC_IP);
					po_text = keepPath(hWndconf,IDC_PORT);
					sm_text = keepPath(hWndconf,IDC_PATH_SRC3);
					sf_text = keepPath(hWndconf,IDC_PATH_SRC2);

					if(SendMessage(GetDlgItem(hWndconf,IDC_VERBOSE_V),BM_GETCHECK,0,0) == BST_CHECKED){
						EndDialog(hWndconf,0);
						EditVisible = TRUE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_PROTCONF1),NULL,ConfDlgProc);
					}
					break;

				case IDC_CL:
					return EndDialog(hWndconf,0);

				case IDC_PATHPK:
					OnBrowse(hWndconf,true, IDC_PATH_SRC,2);
					return TRUE;
				
				case IDC_PATHPK2:
					OnBrowse(hWndconf,true, IDC_PATH_SRC2,0);
					return TRUE;

				case IDC_COMP:
					int i;
					int x,nLen;
					char* szSig = NULL;
					char* szFil = NULL;
					char* szData = NULL;
					char* d = NULL;
					char*buffer;
					char denOrnot[100] = {0};
					unsigned char out[20];
					HWND hCtrl;
					HANDLE hSrcFile;
					DWORD dwFileSize = 0;
					DWORD dw;

					// fichier
					hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC2);
					nLen = GetWindowTextLength(hCtrl) + 1;
					szFil = new char[nLen];
					GetWindowText(hCtrl,szFil,nLen);
					// signature
					hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC3);
					nLen = GetWindowTextLength(hCtrl) + 1;
					szSig = new char[nLen];
					GetWindowText(hCtrl,szSig,nLen);

					// lecture du fichier à signer
					hSrcFile = CreateFile(szFil,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
					if(hSrcFile == INVALID_HANDLE_VALUE)
					{
					CloseHandle(hSrcFile);
					return 6;
					}
					delete[] szFil;
					dwFileSize = GetFileSize(hSrcFile,NULL);
					szData = new char[dwFileSize+1];

					if(!ReadFile(hSrcFile,szData,dwFileSize,&dw,NULL))
					{
						delete[] szData;
						CloseHandle(hSrcFile);
						return 6;
					}
					CloseHandle(hSrcFile);

					*(szData + dwFileSize) = '\0';

					d = szData;
					i = (*d ? 1 : 0);

					while(*d){
						switch(*d){
						case '\r':
							memmove(d,d+1,strlen(d+1)+1);
							break;
						case '\n':
							*d='\0';
							d++;
							i++;
							break;
						default:
							d++;
							break;
						}
					}
					d=NULL;

				// passer szData dans encode
					ZeroMemory(out,20);
					encode(szData,out,dwFileSize);
					for (x=0; x<20;x++)	nLen += strlen((char*)(IntToChar((int)out[x])));

					ZeroMemory(denOrnot,nLen);
					buffer=new char;
					for (x=0; x<20;x++) {sprintf(buffer,"%02x",out[x]);strcat(denOrnot,buffer);}//strcat(denOrnot,(char*)(IntToChar((int)out[x])));
					strcat(denOrnot,szSig);

					if (!(ClientConnect("lasecpc6.epfl.ch",4000,hWndconf))) return FALSE;
					if (!(csSend(denOrnot,nLen))) return FALSE;


					i = computeS(hWndconf);
					if(i==0){ // gestion des erreurs
						MessageBox(hWndconf,"GHIProof successfully executed","Verifier: Success",MB_OK);
						return TRUE;}
					if(i==1){
						MessageBox(hWndconf,"unavailable public key path",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==2){
						MessageBox(hWndconf,"unavailable signature",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==3){
						MessageBox(hWndconf,"unavailable secret key path",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==4){
						MessageBox(hWndconf,"unavailable port number",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==5){
						MessageBox(hWndconf,"unavailable IP",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==6){
						MessageBox(hWndconf,"File error!",NULL,MB_ICONERROR);
						return FALSE;}
					if(i==7){
						MessageBox(hWndconf,"Socket error!",NULL,MB_ICONERROR);
						return FALSE;}
					if(i==8){
						MessageBox(hWndconf,"GHIProof failed!",NULL,MB_ICONERROR);
						return FALSE;}

						hardClose(m_socket);
						return TRUE;
		}
	}
	return FALSE; 
}

// ###############################################################
// *********** 
// *********** Fenêtre de coGHIProof - PROVER
// *********** 
// ###############################################################

BOOL CALLBACK DeniDlgProcP (HWND hWnddeniP, UINT msg, WPARAM wParam, LPARAM lParam)
{

	switch(msg)
	{
		case WM_INITDIALOG:
			HWND hCtrl;
			WSADATA wsaData;
			int iResult;

			hCtrl = GetDlgItem(hWnddeniP,IDC_BW_PATH);
			SetWindowText(hCtrl,sk_text);free(sk_text);

			hCtrl = GetDlgItem(hWnddeniP,IDC_BW_PATH2);
			SetWindowText(hCtrl,s2_text);free(s2_text);

			hCtrl = GetDlgItem(hWnddeniP,IDC_PORT);
			if (po_text == NULL) SetWindowText(hCtrl,"4000");
			else SetWindowText(hCtrl,po_text); free(po_text);

			hCtrl = GetDlgItem(hWnddeniP,IDC_GHI2);
			SetWindowText(hCtrl,"THIS IS THE coGHI-PROOF");

			iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
			if (iResult != NO_ERROR) {return FALSE;}
	 		return TRUE;

		case WM_COMMAND:
			switch((UINT)wParam)
			{
				case IDC_BW:
					OnBrowse(hWnddeniP,true, IDC_BW_PATH,1);
					return TRUE;

				case IDC_BW2:
						OnBrowse(hWnddeniP,true, IDC_BW_PATH2,0);
						return TRUE;

				case IDC_VERBOSE_P:				
						sk_text = keepPath(hWnddeniP,IDC_BW_PATH);
						s2_text = keepPath(hWnddeniP,IDC_BW_PATH2); 
						po_text = keepPath(hWnddeniP,IDC_PORT);

						if(SendMessage(GetDlgItem(hWnddeniP,IDC_VERBOSE_P),BM_GETCHECK,0,0) == BST_CHECKED){
							EndDialog(hWnddeniP,0);
							EditVisible = TRUE;
							return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_CONFPROV1),NULL,DeniDlgProcP);	}
						break;

				case IDC_CHECK2:
					sk_text = keepPath(hWnddeniP,IDC_BW_PATH);
					s2_text = keepPath(hWnddeniP,IDC_BW_PATH2); 
					po_text = keepPath(hWnddeniP,IDC_PORT);

					if(SendMessage(GetDlgItem(hWnddeniP,IDC_CHECK2),BM_GETCHECK,0,0) == BST_CHECKED){
						EndDialog(hWnddeniP,0);
						EditVisible = FALSE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_CONFPROV),NULL,DeniDlgProcP);	}
					break;
				
				case IDC_CL:
					return EndDialog(hWnddeniP,0);

				case IDC_LISTEN:
					char tBuff[6];
						int PORT, nLen, i;
						HWND hCtrl;
						char* szSrc;
						char* szSrc2;

						GetDlgItemText(hWnddeniP,IDC_PORT,tBuff,5);
						
						if (strcmp(tBuff,"") == 0){
							MessageBox(hWnddeniP,"unavailable port number",NULL,MB_ICONEXCLAMATION);
							return FALSE;}

						hCtrl = GetDlgItem(hWnddeniP,IDC_BW_PATH2);
						nLen = GetWindowTextLength(hCtrl) + 1;
						szSrc2 = new char[nLen];
						GetWindowText(hCtrl,szSrc2,nLen);
						if ((strcmp(szSrc2,"") == 0)){
							MessageBox(hWnddeniP,"unavailable signed file path",NULL,MB_ICONEXCLAMATION);
							return FALSE;} 

						hCtrl = GetDlgItem(hWnddeniP,IDC_BW_PATH);
						nLen = GetWindowTextLength(hCtrl) + 1;
						szSrc = new char[nLen];
						GetWindowText(hCtrl,szSrc,nLen);
						if ((strcmp(szSrc,"") == 0) || (strcmp(szSrc,".smova") == 0)){
							MessageBox(hWnddeniP,"unavailable secret key path\nplease, choose a \".smova\" file  ",NULL,MB_ICONEXCLAMATION);
							return FALSE;}

						PORT = atoi(tBuff);

						hCtrl = GetDlgItem(hWnddeniP,IDC_SL);
						SetWindowText(hCtrl, "Waiting for the verifier...");
						ServerListen(PORT);
						
						if ((m_socket = accept(m_socket,NULL,NULL)) != INVALID_SOCKET){
							hCtrl = GetDlgItem(hWnddeniP,IDC_SL);
							SetWindowText(hCtrl, "Verifier connected...");
						}
						
						i = computeHDeni(hWnddeniP);
						if (i == 0){
						//MessageBox(hWnddeniP,"coGHIProof successfully executed","Prover: Success",MB_OK);
						return TRUE;
						}
						if (i == 1){
						MessageBox(hWnddeniP,"unavailable signed file path",0,MB_ICONEXCLAMATION);
						return FALSE;
						}
						if (i == 2){
						MessageBox(hWnddeniP,"unavailable secret key path",0,MB_ICONEXCLAMATION);
						return FALSE;
						}
						if (i == 3){
						MessageBox(hWnddeniP,"unavailable port number",0,MB_ICONEXCLAMATION);
						return FALSE;
						}
						if (i == 6){
						MessageBox(hWnddeniP,"File error!",0,MB_ICONERROR);
						return FALSE;
						}
						if (i == 7){
						MessageBox(hWnddeniP,"Socket error!",0,MB_ICONERROR);
						return FALSE;
						}
						if (i == 8){
						//MessageBox(hWnddeniP,"coGHIProof failed!",0,MB_ICONERROR);
						return FALSE;
						}
						hardClose(m_socket);
						return TRUE;
			}
	}
	return FALSE; 
}

// ###############################################################
// *********** 
// *********** Fenêtre de coGHIProof - VERIFIER
// *********** 
// ###############################################################

BOOL CALLBACK DeniDlgProcV (HWND hWnddeniV, UINT msg, WPARAM wParam, LPARAM lParam)
{
	
	switch(msg)
	{
		case WM_INITDIALOG:
 		HWND hCtrl;
			WSADATA wsaData;
			int iResult;

			hCtrl = GetDlgItem(hWnddeniV,IDC_PATH_SRC);
			SetWindowText(hCtrl,pk_text);free(pk_text);

			hCtrl = GetDlgItem(hWnddeniV,IDC_IP);
			if (ip_text == NULL) SetWindowText(hCtrl,"lasecpc6.epfl.ch");
			else SetWindowText(hCtrl,ip_text); free(ip_text);
			
			hCtrl = GetDlgItem(hWnddeniV,IDC_PORT);
			if (po_text == NULL) SetWindowText(hCtrl,"4000");
			else SetWindowText(hCtrl,po_text); free(po_text);
			
			hCtrl = GetDlgItem(hWnddeniV,IDC_PATH_SRC3);
			SetWindowText(hCtrl,sm_text);free(sm_text);
			
			hCtrl = GetDlgItem(hWnddeniV,IDC_PATH_SRC2);
			SetWindowText(hCtrl,sf_text);free(sf_text);

			hCtrl = GetDlgItem(hWnddeniV,IDC_GHI);
			SetWindowText(hCtrl,"THIS IS THE coGHI-PROOF");
			
			iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
			if (iResult != NO_ERROR) {return FALSE;}
			return TRUE;

 		return TRUE;
		case WM_COMMAND:
			switch((UINT)wParam)
			{
				case IDC_CHECK1:

					pk_text = keepPath(hWnddeniV,IDC_PATH_SRC);
					ip_text = keepPath(hWnddeniV,IDC_IP);
					po_text = keepPath(hWnddeniV,IDC_PORT);
					sm_text = keepPath(hWnddeniV,IDC_PATH_SRC3);
					sf_text = keepPath(hWnddeniV,IDC_PATH_SRC2);

					if(SendMessage(GetDlgItem(hWnddeniV,IDC_CHECK1),BM_GETCHECK,0,0) == BST_CHECKED){
						EndDialog(hWnddeniV,0);
						EditVisible = FALSE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_PROTCONF),NULL,DeniDlgProcV);	}
					break;

				case IDC_VERBOSE_V:
					
					pk_text = keepPath(hWnddeniV,IDC_PATH_SRC);
					ip_text = keepPath(hWnddeniV,IDC_IP);
					po_text = keepPath(hWnddeniV,IDC_PORT);
					sm_text = keepPath(hWnddeniV,IDC_PATH_SRC3);
					sf_text = keepPath(hWnddeniV,IDC_PATH_SRC2);

					if(SendMessage(GetDlgItem(hWnddeniV,IDC_VERBOSE_V),BM_GETCHECK,0,0) == BST_CHECKED){
						EndDialog(hWnddeniV,0);
						EditVisible = TRUE;
						return (int)DialogBox(hInst,MAKEINTRESOURCE(IDD_PROTCONF1),NULL,DeniDlgProcV);	}
					break;
				
				case IDC_PATHPK:
					OnBrowse(hWnddeniV,true, IDC_PATH_SRC,2);
					return TRUE;
				
				case IDC_PATHPK2:
					OnBrowse(hWnddeniV,true, IDC_PATH_SRC2,0);
					return TRUE;

				case IDC_CL:
					return EndDialog(hWnddeniV,0);

				case IDC_COMP:
					int i;
					i = computeSDeni(hWnddeniV);
					if(i==0){
						//MessageBox(hWnddeniV,"coGHIProof successfully executed","Verifier: Success",MB_OK);
						return TRUE;}
					if(i==1){
						MessageBox(hWnddeniV,"unavailable public key path",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==2){
						MessageBox(hWnddeniV,"unavailable signature",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==3){
						MessageBox(hWnddeniV,"unavailable secret key path",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==4){
						MessageBox(hWnddeniV,"unavailable port number",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==5){
						MessageBox(hWnddeniV,"unavailable IP",NULL,MB_ICONEXCLAMATION);
						return FALSE;}
					if(i==6){
						MessageBox(hWnddeniV,"File error!",NULL,MB_ICONERROR);
						return FALSE;}
					if(i==7){
						MessageBox(hWnddeniV,"Socket error!",NULL,MB_ICONERROR);
						return FALSE;}
					if(i==8){
						//MessageBox(hWnddeniV,"coGHIProof failed!",NULL,MB_ICONERROR);
						return FALSE;}
					
						hardClose(m_socket);
						return TRUE;
			}
	}
	return FALSE; 
}

// ###############################################################
// *********** 
// *********** Génération des clés
// *********** 
// ###############################################################

ProgressBar Bar1;
HWND hwndbar2;

BOOL CALLBACK MainDlgProc(HWND hWnd,UINT uMsg,WPARAM wParam,LPARAM lParam)
{
	switch(uMsg)
	{
		case WM_INITDIALOG:
		
			hwndbar2 = GetDlgItem(hWnd,IDC_PDL2);
			Bar1.Init(hwndbar2);
			Bar1.IsVisible();
			Bar1.SetRange(0,100);
			Bar1.SetStep(0);

			return TRUE;

		case WM_COMMAND:
			switch((UINT)wParam)
			{
				case IDCANCEL:
					return EndDialog(hWnd,0);

				case IDC_B_OK:
					char** pwdref;
					pwdref=NULL;
					if(signOnBOk(hWnd, IDC_PATH_SRC6, IDC_PATH_SRC, 0, pwdref) == 0) 
						return TRUE;

					if(signOnBOk(hWnd, IDC_PATH_SRC6, IDC_PATH_SRC, 0, pwdref) == 1){
						MessageBox(hWnd,"unavailable file name",NULL,MB_ICONEXCLAMATION);
						return FALSE;
					}

					if(signOnBOk(hWnd, IDC_PATH_SRC6, IDC_PATH_SRC, 0, pwdref) == 2){
						MessageBox(hWnd,"unavailable secret key path",NULL,MB_ICONEXCLAMATION);
						return FALSE;
					}

					if(signOnBOk(hWnd, IDC_PATH_SRC6, IDC_PATH_SRC, 0, pwdref) == 3){
						MessageBox(hWnd,"Error computing homomorphism!",NULL,MB_ICONERROR);
						return FALSE;
					}

					if(signOnBOk(hWnd, IDC_PATH_SRC6, IDC_PATH_SRC, 0, pwdref) == 4){
						MessageBox(hWnd,"File Error!",NULL,MB_ICONERROR);
						return FALSE;
					}

					Bar1.SetPos(0);
					Bar1.Hide(false);
					return TRUE;

				case IDC_B_OK2:
					if(geneOnBOk(hWnd)==0) return TRUE;
					if(geneOnBOk(hWnd)==1)
					{
						MessageBox(hWnd,"Unavailable number size","Error",MB_ICONEXCLAMATION); 
						return FALSE;
					}
					if(geneOnBOk(hWnd)==2)
					{
						MessageBox(hWnd,"Unavailable secret key name","Error",MB_ICONEXCLAMATION); 
						return FALSE;
					}
					if(geneOnBOk(hWnd)==3)
					{
						MessageBox(hWnd,"Unavailable public key name","Error",MB_ICONEXCLAMATION); 
						return FALSE;
					}
					if(geneOnBOk(hWnd)==4)
					{
						MessageBox(hWnd,"File Error!","Error",MB_ICONERROR); 
						return FALSE;
					}

				case IDC_B_BROWSE_SRC4:
					OnBrowse(hWnd,false, IDC_PATH_SRC7,1);
					return TRUE;

				case IDC_B_BROWSE_SRC3:
					OnBrowse(hWnd,true,IDC_PATH_SRC6,1);
					return TRUE;

				case IDC_B_BROWSE_SRC2:
					OnBrowse(hWnd,false, IDC_PATH_SRC5,2);
					return TRUE;

				case IDC_B_BROWSE_SRC:
					OnBrowse(hWnd,true, IDC_PATH_SRC,0);
					return TRUE;
			}

			break;
	}

	return FALSE;
}

// ###############################################################
// *********** 
// *********** bouton "..." (Browse)
// *********** 
// ###############################################################

void OnBrowse(HWND hParent,bool bOpen, int idc_name, int extension)
{
	OPENFILENAME sOpenFile;
	char szPath[MAX_PATH + 1];
	BOOL bRet;
	HWND hCtrl;
	int i;

	szPath[0] = '\0';
	
	//Initialise la structure
	ZeroMemory(&sOpenFile,sizeof(OPENFILENAME));
	sOpenFile.lStructSize = sizeof(OPENFILENAME);
	sOpenFile.hwndOwner = hParent;
	
	// choix des extensions
	if(extension == 0)
	sOpenFile.lpstrFilter = "File (*.*)\0*.*\0\0";
	if(extension == 1)
	sOpenFile.lpstrFilter = "MOVA File (*.smova)\0*.smova\0\0";
	if(extension == 2)
	sOpenFile.lpstrFilter = "MOVA File (*.pmova)\0*.pmova\0\0";
	
	
	sOpenFile.nFilterIndex = 1;
	sOpenFile.lpstrFile = szPath;
	sOpenFile.nMaxFile = MAX_PATH + 1;
	sOpenFile.Flags = OFN_EXPLORER | OFN_PATHMUSTEXIST | 
		((bOpen)? OFN_FILEMUSTEXIST : OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT);

	if(bOpen)
	{//Dialogue open
		hCtrl = GetDlgItem(hParent,idc_name);
		bRet = GetOpenFileName(&sOpenFile);
		SetWindowText(hCtrl,szPath);
	}
	else
	{//Dialogue save
		hCtrl = GetDlgItem(hParent,idc_name);
		bRet = GetSaveFileName(&sOpenFile);

		i=0;
		while ((!(szPath[i] == '.')) && (i < MAX_PATH+1)){i++;}

		if (i == MAX_PATH+1){
		if(extension == 0) SetWindowText(hCtrl,szPath);
		if(extension == 1){strcat(szPath,".smova");SetWindowText(hCtrl,szPath);}
		if(extension == 2){strcat(szPath,".pmova");SetWindowText(hCtrl,szPath);}
		}
		else {SetWindowText(hCtrl,szPath);}
		
	}

}

// ###############################################################
// *********** 
// *********** CONFIRMATION PROTOCOL - VERIFIER
// *********** 
// ###############################################################

int computeS(HWND hWndconf){

	BOOL bRet;
	HWND hCtrl;
	int x,PORT;
	int i,e,w				= 0;
	char* szSrc				= NULL;
	char* szData			= NULL;
	char* d					= NULL;
	HANDLE hSrcFile			= INVALID_HANDLE_VALUE;
	HANDLE hDestFile2		= INVALID_HANDLE_VALUE;
	HANDLE hDestFile3		= INVALID_HANDLE_VALUE;
	DWORD dwFileSize		= 0;
	DWORD dw;
	char* seedK_pk			= NULL;
	char* n_pk				= NULL;
	char** n_pkref			= NULL;
	char** yKeys_pkref		= NULL;
	char** seedK_pkref		= NULL;
	char** szDataref		= NULL;
	char* yKeys_pk			= NULL;
	char concat2[101]        ={0};
	char r_stock[20][1025]	= {0};
	int w_list[20]			= {0};
	int a_stock[20][100]	= {0};
	unsigned char com2[17]	= {0};
	int inite[16]			= {0};
	int s,a,i2;
	mpz_t zn,r,gt2;
	char* gtemp = NULL;
	char tempQ20c[21][1025]	= {0};
	char IP[16];
	char req_host[255];
	char* concat100 = NULL;
	char** concat100ref = NULL;

	n_pkref		= &n_pk;
	yKeys_pkref = &yKeys_pk;
	seedK_pkref = &seedK_pk;
	szDataref	= &szData;
	concat100ref = &concat100;

	hCtrl = GetDlgItem(hWndconf,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting");

	a = computing_the_s_file(hWndconf,n_pkref,yKeys_pkref,seedK_pkref,concat100ref,szDataref);
	if (a != 0) return a;
	
	afficheMsg("S has been recalculated...",IDC_EDIT1,hWndconf);
	hDestFile2 = CreateFile(pathDesktop("MovaDir/u_conffile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile2 == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hDestFile2);
		return 6;
	}

	mpz_init(gt2);
	mpz_init(zn);
	mpz_init(r);
	mpz_set_str(zn,n_pk,10);

	afficheMsg("picking r[i]'s and a[i][j]'s and computing u[i]'s and w[i]'s...",IDC_EDIT1,hWndconf);
	// choix de r, de a et calcul des u et des w, ensuite sauvés dans u_conffile
	for(i=0;i<20;i++){
		
		getrand3(r,zn); // tirage d'un r[i]
		mpz_get_str(r_stock[i],10,r);

		mpz_pow_ui(r,r,4);
		mpz_mod(r,r,zn); // r puissance 4 modulo n

		w=0;
		for(s=0;s<80;s++){

			a = getrand()&3; // a = 0,1,2 ou 3
			a_stock[i][s] = a;

			i2 = 2*s;
			if ((concat100[i2] == '0') && (concat100[i2+1] == '0')){e = 0;} //  1
			if ((concat100[i2] == '0') && (concat100[i2+1] == '1')){e = 1;} //  i
			if ((concat100[i2] == '1') && (concat100[i2+1] == '0')){e = 2;} // -1
			if ((concat100[i2] == '1') && (concat100[i2+1] == '1')){e = 3;} // -i

			w = w+e*a;

			gtemp=GetLineFromBuf(szData, s, 102); 

			mpz_set_str(gt2,gtemp,10);// gt2 est g
			mpz_pow_ui(gt2,gt2,a);
			mpz_mod(gt2,gt2,zn);
			mpz_mul(r,r,gt2);
			mpz_mod(r,r,zn);
			}

		i2=0;
		for(s=81;s<101;s++){

			a = getrand()&3;
			a_stock[i][s-1] = a; // s-1 pour être dans la continuïté de a_stock

			i2 = 2*(s-1); // pour la même raison que ci-dessus
			if ((concat100[i2] == '0') && (concat100[i2+1] == '0')){e = 0;} //  1
			if ((concat100[i2] == '0') && (concat100[i2+1] == '1')){e = 1;} //  i
			if ((concat100[i2] == '1') && (concat100[i2+1] == '0')){e = 2;} // -1
			if ((concat100[i2] == '1') && (concat100[i2+1] == '1')){e = 3;} // -i

			w = w+e*a;

			gtemp=GetLineFromBuf(szData, s, 102);

			mpz_set_str(gt2,gtemp,10);// gt2 est g
			mpz_powm_ui(gt2,gt2,a,zn);
			mpz_mul(r,r,gt2);
			mpz_mod(r,r,zn);
			}

		w_list[i]=w&3;					// w[i]
		mpz_get_str(tempQ20c[i],10,r);	// u[i]	
	}

	mpz_clear(gt2);
	mpz_clear(zn);
	mpz_clear(r);

	afficheMsg("u[i]'s and w[i]'s computed...",IDC_EDIT1,hWndconf);
	for(i=0;i<20;i++){ // le verbose
	
		afficheMsg(tempQ20c[i],IDC_EDIT1,hWndconf);
		
		bRet = WriteFile(hDestFile2,tempQ20c[i],strlen(tempQ20c[i]),&dw,0);
		bRet = WriteFile(hDestFile2,"\r\n",2, &dw,0);
	}

	CloseHandle(hDestFile2);

	// serveur (IP,PORT)
	GetDlgItemText(hWndconf,IDC_PORT,IP,15);
	PORT= atoi(IP);
	if (PORT == 0) return 4;
	GetDlgItemText(hWndconf,IDC_IP,req_host,255);
	if (strcmp(req_host,"") == 0) return 5;	
	
	// envoi du fichier u_file.crypt au Prover 

	hCtrl = GetDlgItem(hWndconf,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting\n2 - Sending u[i]'s");

	afficheMsg("sending u[i]'s...",IDC_EDIT1,hWndconf);
	if (!csSendFile(pathDesktop("MovaDir/u_conffile.crypt"))) return 7;

	// réception du commit envoyé par le Prover (commit_conffile2.crypt)
	hCtrl = GetDlgItem(hWndconf,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting\n2 - Sending u[i]'s\n3 - Receiving Commit");

	afficheMsg("receiving commit from the server...",IDC_EDIT1,hWndconf);
	if (!csGetFile(pathDesktop("MovaDir/commit_conffile2.crypt"))) return 7;
	

	afficheMsg("computing r[i]'s...",IDC_EDIT1,hWndconf);
	// il faut encore créer un fichier ra_file.crypt qui sera envoyé au prover le moment voulu
	hDestFile3 = CreateFile(pathDesktop("MovaDir/ra_conffile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile3 == INVALID_HANDLE_VALUE) return 6;
	
	for (i=0;i<20;i++){
		bRet = WriteFile(hDestFile3,r_stock[i],strlen(r_stock[i]), &dw,0);
		bRet = WriteFile(hDestFile3,"\r\n",2, &dw,0);

		afficheMsg(r_stock[i],IDC_EDIT1,hWndconf);
		for (x=0;x<100;x++){
			bRet = WriteFile(hDestFile3,IntToChar(a_stock[i][x]),strlen(IntToChar(a_stock[i][x])), &dw,0);
		}
		bRet = WriteFile(hDestFile3,"\r\n",2, &dw,0);
	}

	CloseHandle(hDestFile3);

	// envoi du fichier ra_file.crypt au Prover 	
	afficheMsg("sending r[i]'s and a[i][j]'s...",IDC_EDIT1,hWndconf);
	if (!csSendFile(pathDesktop("MovaDir/ra_conffile.crypt"))) return 7;

	// w[i] = v[i] ?
	hCtrl = GetDlgItem(hWndconf,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting\t\t4 - Sending R's and a's\n2 - Sending u[i]'s\n3 - Receiving Commit");
	afficheMsg("receiving commitment...",IDC_EDIT1,hWndconf);
	szSrc = (char*)malloc(100);
	// reception de l'ouverture du commit
	if (!csGet(szSrc))return 7;

	for (i=0;i<20;i++){strcat(concat2,IntToChar(w_list[i]));}

	strcat(concat2,szSrc);
	free(szSrc);
	// fonction de commit
	commit(concat2,com2,strlen(concat2)+1);

	// pour comparer la sortie avec le commit, il faut ouvrir le fichier commit_file.crypt
	// et lire la ligne 1.
	hSrcFile = CreateFile(pathDesktop("MovaDir/commit_conffile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFile);
		return 6;
	}

	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFile,NULL) + 1;
	szData = new char[dwFileSize];

	//Lire le fichier source
	if(!ReadFile(hSrcFile,szData,dwFileSize,&dw,0))
	{
		delete[] szData;
		CloseHandle(hSrcFile);
		return 6;
	}
	
	CloseHandle(hSrcFile);
	*(szData + dwFileSize) = '\0';
	d = szData;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;
	
	ZeroMemory(inite,16);
	
	i=0;
	hCtrl = GetDlgItem(hWndconf,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting\t\t4 - Sending R's and a's\n2 - Sending u[i]'s\t\t5 - Opening Commit\n3 - Receiving Commit\t6 - Checking v's and w's");
	
	for (x=0;x<16;x++){
		inite[x]=(int)com2[x];
	// comparaison des w et des v
	if (inite[x] != atoi(GetLineFromBuf(szData,x,16))) i=i+1;
	}
	// en cas de réussite...
	if (i == 0)
	{	afficheMsg(" ", IDC_LIST1, hWndconf);
		afficheMsg("******************** GHIProof is a success!",IDC_EDIT1,hWndconf);
		csSend("0",2); 
		return 0;}
	else
	// en cas d'échec...
	{	afficheMsg(" ", IDC_LIST1, hWndconf);
		afficheMsg("******************** GHIProof failed!",IDC_EDIT1,hWndconf);

		csSend("1",2); 
		return 8;
	}
}

// ###############################################################
// *********** 
// *********** CONFIRMATION PROTOCOL - PROVER
// *********** 
// ###############################################################

int computeH(HWND hWndconfp){

	mpz_t u_t,rz,rz2,npkz,real,imag;
	struct cornAlgo alphaXY,u_part,rep;

	BOOL bRet;
	char* szData;
	char* szData1;
	char* szData3;
	char* lastData;
	char* d;
	char* n_pk				= NULL;
	char* compare			= NULL;
	HWND hCtrl;
	int i,x,a,a2;
	DWORD dwFileSize,dw;
	HANDLE hSrcFile,hDestFile,hSrcFileRA;
	char* temp_u			= NULL;
	char concat[27]         ={0};
	char concat100[101]		={0};
	char concat80[81]		={0};
	char concatTrue[41];
	unsigned char com[17]	={0};
	int initc[16]			={0};
	char** szDataref		= NULL;
	char** szData2ref		= NULL;
	char*** xsigref = NULL;
	char tempQ80[81][1025]={0};
	char** xsig = NULL;
	int i_prng ;
	mpz_t tempQ_prng;
	int x_prng;
	long xl_prng  = 0;
	int iBit_prng = 0;
	char *work_prng = 0;

	szDataref	= &szData;
	szData2ref	= &szData1;
	xsigref		= &xsig;

	a = getting_the_u_file(hWndconfp,szData2ref,szDataref);
	if(a != 0) return a;

	mpz_init(real);
	mpz_init(imag);

	mpz_set_str(real,GetLineFromBuf(szData1, 0, 4),10);
	mpz_set_str(imag,GetLineFromBuf(szData1, 1, 4),10);

	mpz_init(alphaXY.x_sol);
	mpz_init(alphaXY.y_sol);
	
	mpz_set(alphaXY.x_sol,real);
	mpz_set(alphaXY.y_sol,imag);

	afficheMsg("Computing n...", IDC_LIST1, hWndconfp);

	// calcul de n, pour plus tard
	mpz_mul(real,alphaXY.x_sol,alphaXY.x_sol);
	mpz_mul(imag,alphaXY.y_sol,alphaXY.y_sol);

	mpz_init(npkz);
	mpz_add(npkz,imag,real); // <- a^2 + b^2

	mpz_clear(real);
	mpz_clear(imag);

	afficheMsg("Computing f(u[i])'s...", IDC_LIST1, hWndconfp);

	// Calcul de v = f(u) à partir des u envoyés par le vérifieur
	hCtrl = GetDlgItem(hWndconfp,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s\n2 - Computing f(u)");

	mpz_init(rep.x_sol);
	mpz_init(rep.y_sol);
	mpz_init(u_part.x_sol);
	mpz_init(u_part.y_sol);

	mpz_set_ui(u_part.y_sol,0);

	mpz_init(u_t);

	ZeroMemory(concatTrue,41);
	ZeroMemory(concat,27);
	ZeroMemory(concat100,101);

	for (i=0;i<20;i++){
		temp_u	= GetLineFromBuf(szData, i, 22);
		mpz_set_str(u_t,temp_u,10);
		mpz_set(u_part.x_sol,u_t);

		rep = quartic(u_part,alphaXY);
	
		if ((mpz_cmp_ui(rep.y_sol, 0) == 0) && (mpz_cmp_ui(rep.x_sol, 0) == 0)){return 8;}
			else{
					if (mpz_cmp_ui(rep.y_sol, 0) == 0){// s'il n'y a pas de i
						if (mpz_cmp_si(rep.x_sol, 1) == 0){
							strcat(concatTrue,"00");
							strcat(concat,"0");}

						else{
							strcat(concatTrue,"10");strcat(concat,"2");}
					}
			
					else{
						if (mpz_cmp_si(rep.y_sol, 1) == 0){
							strcat(concatTrue,"01");strcat(concat,"1");}

						else{
							strcat(concatTrue,"11");strcat(concat,"3");}
				}
		}
	// à ce stade, on a calculé f(u) à partir de u_file et de secret_key
	// c'est les 20 v[i] du point 2 de GHIProofL(S) (on est prover)
	}
	afficheMsg(concatTrue, IDC_LIST1, hWndconfp);

	mpz_clear(u_part.x_sol);mpz_clear(u_part.y_sol);
	mpz_clear(alphaXY.x_sol);mpz_clear(alphaXY.y_sol);

	ZeroMemory(com,17);
	dwFileSize = GetFileSize(concat,NULL) + 1;
	
	strcat(concat100,concat);

	a2 = 0;
	while (a2 <80){
	a = getrand()&1;
	strcat(concat80,IntToChar(a));
	a2++;
	}

	strcat(concat100,concat80);
	commit(concat100,com, strlen(concat100)+1);

	afficheMsg("Computing commitment...", IDC_LIST1, hWndconfp);

	// créer le fichier commit_file.crypt et y stocker le résultat
	hDestFile = CreateFile(pathDesktop("MovaDir/commit_conffile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hDestFile);
		return 6;
	}

	ZeroMemory(initc,16);
	for (x=0; x<16;x++){
		initc[x]=(int)com[x]; // INITC est le COMMITMENT qui utilise un random (ici: dico)
		afficheMsg(IntToChar(initc[x]), IDC_LIST1, hWndconfp);
		bRet = WriteFile(hDestFile,IntToChar(initc[x]),strlen(IntToChar(initc[x])), &dw,0);
		bRet = WriteFile(hDestFile,"\r\n",2, &dw,0);
	}
	CloseHandle(hDestFile);

	// Le COMMIT est stocké. Il reste à l'envoyer
	if (!csSendFile(pathDesktop("MovaDir/commit_conffile.crypt"))) return 7;	
	afficheMsg("Commit sent...", IDC_LIST1, hWndconfp);
	hCtrl = GetDlgItem(hWndconfp,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s\n2 - Computing f(u)\n3 - Sending Commit");

// *********** Point 4 de GHIProof
	if (!csGetFile(pathDesktop("MovaDir/ra_conffile2.crypt"))) return 7;	
	afficheMsg("Receiving R[i]'s and a[i]'s...", IDC_LIST1, hWndconfp);
	
	hCtrl = GetDlgItem(hWndconfp,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s\t4 - Receiving R's and a's\n2 - Computing f(u)\n3 - Sending Commit");

	hSrcFileRA = CreateFile(pathDesktop("MovaDir/ra_conffile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFileRA == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFileRA);
		return 6;
	}

	dwFileSize = GetFileSize(hSrcFileRA,NULL);
	szData = new char[dwFileSize+1];
	if(!ReadFile(hSrcFileRA,szData,dwFileSize,&dw,NULL))
	{
		delete[] szData;
		CloseHandle(hSrcFileRA);
		return 6;
	}
	CloseHandle(hSrcFileRA);

	// récupérer la ligne i et i+1
	*(szData + dwFileSize) = '\0';

	d = szData;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;
	afficheMsg("Computing f(u)...", IDC_LIST1, hWndconfp);
	hCtrl = GetDlgItem(hWndconfp,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s\t4 - Receiving R's and a's\n2 - Computing f(u)\t5 - Checking u[i]'s\n3 - Sending Commit");

	hSrcFile = CreateFile(pathDesktop("MovaDir/u_conffile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{

		CloseHandle(hSrcFile);
		return 6;
	}

	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFile,NULL);// + 1;
	szData3 = new char[dwFileSize+1];

	//Lire le fichier source
	if(!ReadFile(hSrcFile,szData3,dwFileSize,&dw,NULL))
	{
		delete[] szData3;
		CloseHandle(hSrcFile);
		return 6;
	}
	CloseHandle(hSrcFile);

	*(szData3 + dwFileSize) = '\0';

	d = szData3;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	init_genrand(atoi(GetLineFromBuf(szData1, 3, 4)));
	mpz_init(tempQ_prng);
	for(x_prng = 0; x_prng < 80; x_prng++) {
		work_prng = (char*)tempQ80[x_prng];
		
		for(i_prng = 0; i_prng < 32; i_prng++) {
			xl_prng = (long)genrand_int32();

			for(iBit_prng = 0; iBit_prng < 32; iBit_prng++, xl_prng <<= 1) {
				*work_prng++ = (xl_prng & 0x80000000) ? '1' : '0';
			}
		}
		mpz_set_str(tempQ_prng, tempQ80[x_prng],2);
		mpz_get_str(tempQ80[x_prng],10,tempQ_prng);

	}

	mpz_init(rz);mpz_init(rz2);

	compute_the_x_sigs(hWndconfp,IDC_BW_PATH2, xsigref);

	for (i=0;i<20;i++){
		
		// SzData  -> pour les r et les a
		// SzData2 -> pour les x
		// SzData3 -> c'est u_file.crypt

		mpz_set_str(rz, GetLineFromBuf(szData, 2*i, 41), 10);
		mpz_pow_ui(rz,rz,4); // r^d
		mpz_mod(rz,rz,npkz);

		for (x=0;x<80;x++){
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '0'){ a=0;}
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '1'){ a=1;}
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '2'){ a=2;}
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '3'){ a=3;}

			mpz_set_str(rz2,tempQ80[x],10);	
			
			mpz_pow_ui(rz2,rz2,a); // g^a
			mpz_mod(rz2,rz2,npkz); // g^a mod n
			mpz_mul(rz,rz,rz2);
			mpz_mod(rz,rz,npkz);
		}

		for (x=80;x<100;x++){
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '0'){ a=0;}
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '1'){ a=1;}
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '2'){ a=2;}
			if (GetLineFromBuf(szData, 2*i + 1, 42)[x] == '3'){ a=3;}

			mpz_set_str(rz2,xsig[x-80],10);
			
			mpz_pow_ui(rz2,rz2,a);
			mpz_mod(rz2,rz2,npkz);
			mpz_mul(rz,rz,rz2);
			mpz_mod(rz,rz,npkz);
		}

		mpz_get_str(temp_u,10,rz); // tempu = r^d * g1^a1 * ... * gs^as = u

		afficheMsg(temp_u, IDC_LIST1, hWndconfp);
		compare = GetLineFromBuf(szData3, i, 21);
		afficheMsg(">> u is a good one!", IDC_LIST1, hWndconfp);

		if (strcmp(temp_u,compare) != 0){
			MessageBox(NULL,"Wrong u[i]'s!!!","STOP",MB_OK);
			return 8;
		}
	}
	afficheMsg("All u[i]'s are right", IDC_LIST1, hWndconfp);

	mpz_clear(tempQ_prng);
	mpz_clear(rz);mpz_clear(rz2);mpz_clear(npkz);	
	
	hCtrl = GetDlgItem(hWndconfp,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s\t4 - Receiving R's and a's\n2 - Computing f(u)\t5 - Checking u[i]'s\n3 - Sending Commit\t6 - Opening Commit");

// ouverture du commit
	if (!csSend(concat80,81)) return 7;
	afficheMsg("Opening commit...", IDC_LIST1, hWndconfp);

	lastData = new char[2];
	if (!csGet(lastData)) return 7;

	afficheMsg("Getting \"End of protocol\" confirmation...", IDC_LIST1, hWndconfp);
	if (atoi(lastData) == 0){
		afficheMsg(" ", IDC_LIST1, hWndconfp);
		afficheMsg("******************** GHIProof is a success!", IDC_LIST1, hWndconfp);	
		return 0;}

	else{
		afficheMsg(" ", IDC_LIST1, hWndconfp);
		afficheMsg("******************** GHIProof failed!", IDC_LIST1, hWndconfp);
		return 8;
	}
}

// ###############################################################
// *********** 
// *********** DENIAL PROTOCOL - VERIFIER
// *********** 
// ###############################################################

int computeSDeni(HWND hWnddeniV){
	
	BOOL bRet;
	HWND hCtrl;
	int i,k,e,w				= 0;
	char* szSrc				= NULL;
	char* szData			= NULL;
	char* szAck				= NULL;
	char* ack2				= NULL;
	char* szData2			= NULL;
	char* d					= NULL;
	HANDLE hSrcFile			= INVALID_HANDLE_VALUE;
	HANDLE hDestFile2		= INVALID_HANDLE_VALUE;
	DWORD dwFileSize		= 0;
	DWORD dw;
	char* seedK_pk			= NULL;
	char* n_pk				= NULL;
	char** n_pkref			= NULL;
	char** yKeys_pkref		= NULL;
	char** seedK_pkref		= NULL;
	char** szDataref		= NULL;
	char* yKeys_pk			= NULL;
	char concat2[101]       = {0};
	unsigned char com2[17]	= {0};
	int inite[16]			= {0};
	char lamb[20]			= {0};
	int s,a,j;
	mpz_t zn,r,gt2;
	char IP[16];
	int PORT;
	char req_host[255];
	int lambda[20]			= {0};
	int aijk[21][21][81]	= {0};
	int wik[21][21]			= {0};
	char* rik[21][21]		= {0};
	char* uik[21][21]		= {0};
	char* xk[21]			= {0};
	char* gj[81]			= {0};
	int ej[80]				= {0};
	char** concat100ref = NULL;
	char* concat100 = NULL;

	n_pkref		= &n_pk;
	yKeys_pkref = &yKeys_pk;
	seedK_pkref = &seedK_pk;
	szDataref	= &szData;
	concat100ref = &concat100;

	afficheMsg("computing S...",IDC_EDIT1,hWnddeniV);
	a = computing_the_s_file(hWnddeniV,n_pkref,yKeys_pkref,seedK_pkref,concat100ref,szDataref);

	if (a != 0) return a;

	// connexion et envoi de S
	GetDlgItemText(hWnddeniV,IDC_PORT,IP,15);
	PORT= atoi(IP);
	if (PORT == 0) return 4;
	
	GetDlgItemText(hWnddeniV,IDC_IP,req_host,255);
	
	if (strcmp(req_host,"") == 0) return 5;	
	
	hCtrl = GetDlgItem(hWnddeniV,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting");
	
	if (!(ClientConnect(req_host,PORT,hWnddeniV))) return 7;

	afficheMsg("client Connected to the server...",IDC_EDIT1,hWnddeniV);

	hDestFile2 = CreateFile(pathDesktop("MovaDir/u_denifile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile2 == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hDestFile2);
		return 6;
	}

	mpz_init(gt2);
	mpz_init(zn);
	mpz_init(r);
	mpz_set_str(zn,n_pk,10);
	
	ZeroMemory(lamb,20);

	afficheMsg("picking r[i][k]'s, a[i][j][k]'s and lambda[i]'s...",IDC_EDIT1,hWnddeniV);

	for (i=0;i<20;i++) lambda[i] = getrand()&1; 
	
	for (j=0;j<80;j++){ // les g entre 0 et 79
	gj[j] = (char*)malloc(sizeof(char)*1025);
	gj[j] = GetLineFromBuf(szData, j, 102);// szData est l'ensemble S
	}

	for (k=0;k<20;k++){
		xk[k] = (char*)malloc(sizeof(char)*1025);
		xk[k] = GetLineFromBuf(szData, 81+k, 102);
	}

	for (k=0;k<20;k++){
		for (i=0;i<20;i++){
			getrand3(r,zn);
			rik[k][i] = (char*)malloc(sizeof(char)*1025);
			mpz_get_str(rik[k][i],10,r);
		
			for (j=0;j<80;j++){
				aijk[k][i][j] = getrand()&3;
			}
		}
	}

	for(j=0;j<80;j++){

		if ((concat100[2*j] == '0') && (concat100[2*j+1] == '0')){ej[j] = 0;} //  1
		if ((concat100[2*j] == '0') && (concat100[2*j+1] == '1')){ej[j] = 1;} //  i
		if ((concat100[2*j] == '1') && (concat100[2*j+1] == '0')){ej[j] = 2;} // -1
		if ((concat100[2*j] == '1') && (concat100[2*j+1] == '1')){ej[j] = 3;} // -i
		w = w + ej[j];
			}

	for (k=0;k<20;k++){
		for (i=0;i<20;i++){
						
			mpz_set_str(r,rik[k][i],10);
			mpz_pow_ui(r,r,4);
			mpz_mod(r,r,zn);

			wik[k][i] = 0;
			for(j=0;j<80;j++){

				wik[k][i] = (wik[k][i]+ej[j]*aijk[k][i][j]);
				
				mpz_set_str(gt2,gj[j],10);
				mpz_powm_ui(gt2,gt2,aijk[k][i][j],zn);
				mpz_mul(r,r,gt2);
				mpz_mod(r,r,zn);
			}

			j = 2*(80+k);
			if ((concat100[j] == '0') && (concat100[j+1] == '0')){e = 0;} //  1
			if ((concat100[j] == '0') && (concat100[j+1] == '1')){e = 1;} //  i
			if ((concat100[j] == '1') && (concat100[j+1] == '0')){e = 2;} // -1
			if ((concat100[j] == '1') && (concat100[j+1] == '1')){e = 3;} // -i

			mpz_set_str(gt2,xk[k],10);
			mpz_powm_ui(gt2,gt2,lambda[i],zn);
			mpz_mul(r,r,gt2);
			mpz_mod(r,r,zn);

			wik[k][i] = (wik[k][i]+e*lambda[i])&3;
			uik[k][i] = (char*)malloc(sizeof(char)*1025);
			mpz_get_str(uik[k][i],10,r);
		
			bRet = WriteFile(hDestFile2,uik[k][i],strlen(uik[k][i]),&dw,0);
			bRet = WriteFile(hDestFile2,"\r\n",2, &dw,0);
		}
	}

	mpz_clear(gt2);
	mpz_clear(zn);
	mpz_clear(r);
	
	afficheMsg("u[i][k]'s and w[i][k]'s computed...",IDC_EDIT1,hWnddeniV);

	for(k=0;k<20;k++) {
		for(i=0;i<20;i++) {
			bRet = WriteFile(hDestFile2,IntToChar(wik[k][i]),strlen(IntToChar(wik[k][i])),&dw,0);
		}
	}
	bRet = WriteFile(hDestFile2,"\r\n",2, &dw,0);

	CloseHandle(hDestFile2);

	hDestFile2 = CreateFile(pathDesktop("MovaDir/ra_denifile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile2 == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hDestFile2);
		return 6;
	}
	for(k=0;k<20;k++) {
		for(i=0;i<20;i++) {
			bRet = WriteFile(hDestFile2,rik[k][i],strlen(rik[k][i]),&dw,0);
			bRet = WriteFile(hDestFile2,"\r\n",2, &dw,0);
	
			for (j=0;j<80;j++){// Il ne doit pas envoyer lambda! d'où 80 plutôt que 81!!
				bRet = WriteFile(hDestFile2,IntToChar(aijk[k][i][j]),strlen(IntToChar(aijk[k][i][j])),&dw,0);
			}
			bRet = WriteFile(hDestFile2,"\r\n",2, &dw,0);
		}
	}
	CloseHandle(hDestFile2);

// Envoi du fichier u_file.crypt au Prover 
	hCtrl = GetDlgItem(hWnddeniV,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting\n2 - Sending u's & w's");

	if (!csSendFile(pathDesktop("MovaDir/u_denifile.crypt"))) return 7;
	afficheMsg("u[i][k]'s and w[i][k]'s sent to the server...",IDC_EDIT1,hWnddeniV);

// Envoi du fichier s_file.crypt au Prover 
	szAck = (char*)malloc(2);
	if (!(csGet(szAck))) return 7; // ack pour éviter un mélange
	free(szAck);

	if (!csSendFile(pathDesktop("MovaDir/s_denifile.crypt"))) return 7;
	afficheMsg("Sending S...",IDC_EDIT1,hWnddeniV);

	ack2 = (char*)malloc(2);
	if (!csGet(ack2)) return 7;	
	if (strcmp(ack2,"1") == 0) {
		MessageBox(hWnddeniV,"Program can't prove the invalidity of the signature!",0,0);
		return 8;
	}
	if (!csSend(ack2,2)) return 7;
	free(ack2);

// Réception du commit sur les lambdas
	hCtrl = GetDlgItem(hWnddeniV,IDC_VS);
	SetWindowText(hCtrl, "1 - Connecting\n2 - Sending u's & w's\n3 - Receiving Commit");
	afficheMsg("receiving commitment...",IDC_EDIT1,hWnddeniV);
	if(!csGetFile(pathDesktop("MovaDir/commit_denifile2.crypt"))) return 7;

// Envoi du fichier ra_file.crypt au Prover
	if (!csSendFile(pathDesktop("MovaDir/ra_denifile.crypt"))) return 7;
	afficheMsg("r[i][k]'s and a[i][j][k]'s sent to the server...",IDC_EDIT1,hWnddeniV);
	
	// réception de l'ouverture du commit
	szSrc = (char*)malloc(81);

	if (!(csGet(szSrc))) return 7;

	ZeroMemory(concat2,101);
	for (i=0;i<20;i++) strcat(concat2,IntToChar(lambda[i]));

	strcat(concat2,szSrc);
	commit(concat2,com2,strlen(concat2)+1);
	
	free(szSrc);
	hSrcFile = CreateFile(pathDesktop("MovaDir/commit_denifile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFile);
		return 6;
	}

	dwFileSize = GetFileSize(hSrcFile,NULL) + 1;
	szData2 = new char[dwFileSize];

	if(!ReadFile(hSrcFile,szData2,dwFileSize,&dw,0))
	{
		delete[] szData2;
		CloseHandle(hSrcFile);
		return 6;
	}

	CloseHandle(hSrcFile);
	*(szData2 + dwFileSize) = '\0';
	d = szData2;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;
	
	ZeroMemory(inite,16);
	
	i=0;
	
	for (s=0;s<16;s++){
		inite[s]=(int)com2[s];

	if (inite[s] != atoi(GetLineFromBuf(szData2,s,16))) i=i+1;
	}

	if (i == 0)
	{
		csSend("0",2); 
		MessageBox(hWnddeniV,"The signature is not valid","coGHIProof", MB_OK);
		afficheMsg("**************** The signature is not valid !!!",IDC_EDIT1,hWnddeniV);
		return 0;}// LE PROVER A PU RETROUVER LES LAMBDAS
	else
	{
		csSend("1",2); 
		MessageBox(hWnddeniV,"The invalidity of the signature remains undetermined","coGHIProof", MB_OK);
		afficheMsg("**************** The invalidity of the signature remains undetermined!",IDC_EDIT1,hWnddeniV);
		return 8;// LE PROVER N'A PAS PU RETROUVER LES LAMBDAS
	}
}

// ###############################################################
// *********** 
// *********** DENIAL PROTOCOL - PROVER
// *********** 
// ###############################################################
int computeHDeni(HWND hWnddeniP){

	mpz_t u_t,npkz,real,imag,tempQ;
	mpz_t tempmpz,r,gt2;
	struct cornAlgo alphaXY,piXY,u_part,rep;
	BOOL bRet;
	char* szData;
	char* szData1;
	char* szData4;
	char* szDataRA;
	char tempQ20[21][1025]	= {0};
	char bidule	 = NULL;
	char bidule2 = NULL;
	char bidule3 = NULL;
	char* lastData;
	char* d;
	HWND hCtrl;
	int i,x,a,k,s,w,w1,w2;
	DWORD dwFileSize,dw;
	HANDLE hSrcFile,hDestFile;
	char concat[400]         ={0};
	char concatd[21]         ={0};
	char concatFull_ZK[21]	= {0};
	char concatFull_YK[21]	= {0};
	char concat40[41]		={0};
	char concat80[81]		={0};
	char concej80[81]		={0};
	char concat100[101]		={0};
	char concatTrue[801];
	char concatTrued[40];
	unsigned char com[17]	= {0};
	int initc[16]			= {0};
	int al[20]				= {0};
	int bl[20]				= {0};
	char** szDataref		= NULL;
	char** szData2ref		= NULL;
	int lambda2[21];
	char lamb[21] = {0};
	char*** xsigref = NULL;
	char** xsig = NULL;
	int wik[20][20] = {0};
	int vik[20][20] = {0};
	int zk[20] = {0};
	int yk[20] = {0};
	int jac[20] = {0};
	int jac80[160] = {0};
	mpz_t jac1;
	char* ack = NULL;

	szDataref	= &szData;
	szData2ref	= &szData1;
	xsigref		= &xsig;

	hCtrl = GetDlgItem(hWnddeniP,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s");
	a = getting_the_u_file(hWnddeniP,szData2ref,szDataref);
	if(a != 0) return a;

	if (!csSend("0",2)) return 7; // pour pas que 2 send se mangent entre eux!

	if (!csGetFile(pathDesktop("MovaDir/s_denifile2.crypt"))) return 7;	

	mpz_init(real);
	mpz_init(imag);

	mpz_set_str(real,GetLineFromBuf(szData1, 0, 4),10);
	mpz_set_str(imag,GetLineFromBuf(szData1, 1, 4),10);

	mpz_init(alphaXY.x_sol);
	mpz_init(alphaXY.y_sol);

	mpz_set(alphaXY.x_sol,real);
	mpz_set(alphaXY.y_sol,imag);

	afficheMsg("computing n...", IDC_LIST1, hWnddeniP);

	// calcul de n, pour plus tard
	mpz_mul(real,alphaXY.x_sol,alphaXY.x_sol);
	mpz_mul(imag,alphaXY.y_sol,alphaXY.y_sol);

	mpz_init(npkz);
	mpz_add(npkz,imag,real); // <- a^2 + b^2

	mpz_clear(real);
	mpz_clear(imag);
	delete[] szData1;

	afficheMsg("computing f(u[i])'s...", IDC_LIST1, hWnddeniP);

	hCtrl = GetDlgItem(hWnddeniP,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s\n2 - Computing f(u)");

	mpz_init(rep.x_sol);
	mpz_init(rep.y_sol);
	mpz_init(u_part.x_sol);
	mpz_init(u_part.y_sol);

	mpz_set_ui(u_part.y_sol,0);

	mpz_init(u_t);

	ZeroMemory(concatTrue,801);

	ZeroMemory(concat,400);
	ZeroMemory(concat40,40);

	for (k=0;k<20;k++){
		for (i=0;i<20;i++){

			mpz_set_str(u_t,GetLineFromBuf(szData, 20*k+i, 402),10);// vérifié ok
			mpz_set(u_part.x_sol,u_t);

			rep = quartic(u_part,alphaXY);

			if ((mpz_cmp_ui(rep.y_sol, 0) == 0) && (mpz_cmp_ui(rep.x_sol, 0) == 0)) return 8;
			else{
					if (mpz_cmp_ui(rep.y_sol, 0) == 0){// s'il n'y a pas de i
						if (mpz_cmp_si(rep.x_sol, 1) == 0){
							strcat(concatTrue,"00");
							strcat(concat,"0");}

						else{
							strcat(concatTrue,"10");strcat(concat,"2");}
					}
			
					else{
						if (mpz_cmp_si(rep.y_sol, 1) == 0){
							strcat(concatTrue,"01");strcat(concat,"1");}

						else{
							strcat(concatTrue,"11");strcat(concat,"3");}
					}
			}
		}
	}
	mpz_clear(u_part.x_sol);
	mpz_clear(u_part.y_sol);

	afficheMsg(concatTrue, IDC_LIST1, hWnddeniP);

	hSrcFile = CreateFile(pathDesktop("MovaDir/s_denifile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFile);
		return 6;
	}

	dwFileSize = GetFileSize(hSrcFile,NULL);
	szData4 = new char[dwFileSize+1];

	//Lire le fichier source
	if(!ReadFile(hSrcFile,szData4,dwFileSize,&dw,NULL))
	{
		delete[] szData4;
		CloseHandle(hSrcFile);
		return 6;
	}
	CloseHandle(hSrcFile);
	
	*(szData4 + dwFileSize) = '\0';
	d = szData4;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	mpz_init(piXY.x_sol);
	mpz_init(piXY.y_sol);
	mpz_init(tempQ);

	ZeroMemory(concatTrued,41);
	ZeroMemory(concatd,21);

	mpz_set_ui(piXY.y_sol,0);

	afficheMsg("computing XSigs...", IDC_LIST1, hWnddeniP);
	a = compute_the_x_sigs(hWnddeniP,IDC_BW_PATH2,xsigref);
	if (a != 0) return a;

	for (k=0; k<20;k++){

		mpz_set_str(tempQ,xsig[k],10);// vérifié ok
		mpz_set(piXY.x_sol,tempQ);

		rep = quartic(piXY,alphaXY);

		if ((mpz_cmp_ui(rep.y_sol, 0) == 0) && (mpz_cmp_ui(rep.x_sol, 0) == 0)) return 8;
		else{
				if (mpz_cmp_ui(rep.y_sol, 0) == 0){
					if (mpz_cmp_si(rep.x_sol, 1) == 0){strcat(concatd,"0");
					strcat(concatTrued,"00");
					}
					else{
						strcat(concatd,"1");
						strcat(concatTrued,"10");
					}
				}
		
				else{
					if (mpz_cmp_si(rep.y_sol, 1) == 0){
						strcat(concatd,"0");
						strcat(concatTrued,"01");
					}
					else{strcat(concatd,"1");
					strcat(concatTrued,"11");
					}
				}
		}

	}
	mpz_clear(tempQ);
	mpz_clear(rep.x_sol);mpz_clear(rep.y_sol);
	mpz_clear(piXY.x_sol);mpz_clear(piXY.y_sol);
	mpz_clear(alphaXY.x_sol);mpz_clear(alphaXY.y_sol);

	// décompression
	mpz_init(jac1);
	
	for (k=0;k<20;k++){
		mpz_set_str(jac1,GetLineFromBuf(szData4, 81+k, 102),10);
		jac[k] = mpz_jacobi(jac1,npkz);
	}

	for (k=0;k<80;k++){
		mpz_set_str(jac1,GetLineFromBuf(szData4, k, 102),10);
		jac80[k] = mpz_jacobi(jac1,npkz);
	}
	mpz_clear(jac1);
 
	// si Jacobi(x/N(alpha)) = 1  et Quart(x) = 0, x = 00 -> 0
    // si Jacobi(x/N(alpha)) = -1 et Quart(x) = 0, x = 01 -> 1
    // si Jacobi(x/N(alpha)) = 1  et Quart(x) = 1, x = 10 -> 2
    // si Jacobi(x/N(alpha)) = -1 et Quart(x) = 1, x = 11 -> 3

	ZeroMemory(concatFull_YK,21);
	ZeroMemory(concatFull_ZK,21);

	afficheMsg("comparing y[k]'s and z[k]'s...", IDC_LIST1, hWnddeniP);

	for (k=0;k<20;k++){
		
		if ((jac[k] == 1)  && (*(concatd+k) == '0')){ strcat(concatFull_YK,"0");}
		if ((jac[k] == -1) && (*(concatd+k) == '0')){ strcat(concatFull_YK,"1");}
		if ((jac[k] == 1)  && (*(concatd+k) == '1')){ strcat(concatFull_YK,"2");}
		if ((jac[k] == -1) && (*(concatd+k) == '1')){ strcat(concatFull_YK,"3");}
		if ((jac[k] == 1)  && (*(GetLineFromBuf(szData4, 101, 102)+k) == '0')){ strcat(concatFull_ZK,"0");}
		if ((jac[k] == -1) && (*(GetLineFromBuf(szData4, 101, 102)+k) == '0')){ strcat(concatFull_ZK,"1");}
		if ((jac[k] == 1)  && (*(GetLineFromBuf(szData4, 101, 102)+k) == '1')){ strcat(concatFull_ZK,"2");}
		if ((jac[k] == -1) && (*(GetLineFromBuf(szData4, 101, 102)+k) == '1')){ strcat(concatFull_ZK,"3");}
	}

	ZeroMemory(concej80,81);

	for (k=0;k<80;k++){
		if ((jac80[k] == 1)  && (*(GetLineFromBuf(szData4, 80, 102)+k) == '0')){ strcat(concej80,"0");}
		if ((jac80[k] == -1) && (*(GetLineFromBuf(szData4, 80, 102)+k) == '0')){ strcat(concej80,"1");}
		if ((jac80[k] == 1)  && (*(GetLineFromBuf(szData4, 80, 102)+k) == '1')){ strcat(concej80,"2");}
		if ((jac80[k] == -1) && (*(GetLineFromBuf(szData4, 80, 102)+k) == '1')){ strcat(concej80,"3");}
	}	

	if (strcmp(concatFull_YK,concatFull_ZK) == 0){
		if (!csSend("1",2)) return 7; 
		afficheMsg("y[k] = z[k] --> Program can't prove the invalidity of the signature!", IDC_LIST1, hWnddeniP);
		MessageBox(hWnddeniP,"Program can't prove the invalidity of the signature!","STOP",NULL);
		return 8;
	} 
	
	if (!csSend("0",2)) return 7; 
	ack = (char*)malloc(2);
	if (!csGet(ack)) return 7;
	free(ack);

	for (i=0;i<20;i++){ 
		al[i]=0;
		bl[i]=0;
	}

	afficheMsg("looking for lambda[i]'s...", IDC_LIST1, hWnddeniP);
	for (k=0;k<20;k++){

		bidule  = *(concatFull_YK+k);
		bidule2 = *(concatFull_ZK+k);

		yk[k] = atoi(&bidule);
		zk[k] = atoi(&bidule2);

		w1 = zk[k] - yk[k];

			for (i=0;i<20;i++){

				bidule  = *(GetLineFromBuf(szData, 400, 402)+(20*k+i));
				bidule2 = *(concat+(20*k+i));
				// si la signature est valide,  bidule = bidule2
				wik[k][i] = atoi(&bidule);
				vik[k][i] = atoi(&bidule2); 
				
				w2 = wik[k][i] - vik[k][i];
				if ((w1 == 0) && (w2 != 0)) {MessageBox(0,"Cheater!",0,0);return 8;}
				if (w1!=0){
					if (w2 == w1) bl[i]++;
					if (w2 == 0)  al[i]++;
				}
			}
	}

	ZeroMemory(lamb,21);

	for (i=0;i<20;i++){

		if ((al[i] != 0) && (bl[i] == 0)) lambda2[i] = 0;
		if ((al[i] == 0) && (bl[i] != 0)) lambda2[i] = 1;
		if ((al[i] != 0) && (bl[i] != 0)) lambda2[i] = getrand()&1;
			
	strcat(lamb,IntToChar(lambda2[i]));
	}

	afficheMsg("lambda[i]'s built...", IDC_LIST1, hWnddeniP);
	ZeroMemory(concat100,101);
	ZeroMemory(concat80,81); // CONCAT80 est un random ajouté

	for (i=0;i<20;i++)	strcat(concat100,IntToChar(lambda2[i]));
	k = 0;
	while (k <80){
	strcat(concat80,IntToChar(getrand()&1));
	k++;
	}

	strcat(concat100,concat80);
	commit(concat100,com,strlen(concat100)+1);

	// créer le fichier commit_denifile.crypt et y stocker le résultat
	hDestFile = CreateFile(pathDesktop("MovaDir/commit_denifile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hDestFile);
		return 6;
	}
	afficheMsg("commitment...", IDC_LIST1, hWnddeniP);
	ZeroMemory(initc,16);
	for (x=0; x<16;x++){
		initc[x]=(int)com[x]; // INITC est le COMMITMENT qui utilise un random (ici: dico)
		afficheMsg(IntToChar(initc[x]), IDC_LIST1, hWnddeniP);
		bRet = WriteFile(hDestFile,IntToChar(initc[x]),strlen(IntToChar(initc[x])), &dw,0);
		bRet = WriteFile(hDestFile,"\r\n",2, &dw,0);
	}
	CloseHandle(hDestFile);

// Le COMMIT est stocké. Il reste à l'envoyer
	afficheMsg("sending commit...", IDC_LIST1, hWnddeniP);
	if (!csSendFile(pathDesktop("MovaDir/commit_denifile.crypt"))) 
		return 7;

	afficheMsg("receiving r[i][k]'s and a[i][j][k]'s...", IDC_LIST1, hWnddeniP);
	if (!csGetFile(pathDesktop("MovaDir/ra_denifile2.crypt"))) 
		return 7;	

	hCtrl = GetDlgItem(hWnddeniP,IDC_SL);SetWindowText(hCtrl, "1 - receiving u[i][k]'s\t4 - Receiving r's and a's\n2 - Computing f(u)\n3 - sending commit");

// contrôle des u et des w
	hSrcFile = CreateFile(pathDesktop("MovaDir/ra_denifile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFile);
		return 6;
	}

	dwFileSize = GetFileSize(hSrcFile,NULL) + 1;
	szDataRA = new char[dwFileSize];

	if(!ReadFile(hSrcFile,szDataRA,dwFileSize,&dw,0))
	{
		delete[] szDataRA;
		CloseHandle(hSrcFile);
		return 6;
	}

	CloseHandle(hSrcFile);
	*(szDataRA + dwFileSize) = '\0';
	d = szDataRA;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	mpz_init(r);mpz_init(gt2);mpz_init(tempmpz);
	   
	for (k=0;k<20;k++){

		for (i=0;i<20;i++){
			mpz_set_str(r,GetLineFromBuf(szDataRA, 2*(20*k+i), 801),10); // r
			mpz_pow_ui(r,r,4);
			mpz_mod(r,r,npkz); // r puissance d mod n
	
			w = 0;
			for (s=0;s<80;s++){
	
				bidule2 = *(concej80+s);
				bidule3 = *(GetLineFromBuf(szDataRA, 2*(20*k+i)+1, 801)+s); // a
				a = atoi(&bidule3);
				mpz_set_str(gt2,GetLineFromBuf(szData4, s, 103),10);
				mpz_pow_ui(gt2,gt2,a);
				mpz_mod(gt2,gt2,npkz);
				mpz_mul(r,r,gt2);
				mpz_mod(r,r,npkz);

				w = w+a*atoi(&bidule2);
				w = w&3;
			}

			bidule2 = *(concatFull_ZK+k);
		
			a = lambda2[i];
			mpz_set_str(gt2,GetLineFromBuf(szData4, 81+k, 103),10);
			mpz_pow_ui(gt2,gt2,a);
			mpz_mod(gt2,gt2,npkz);
			mpz_mul(r,r,gt2);
			mpz_mod(r,r,npkz);
			w += a*atoi(&bidule2);
			w = w&3;

			mpz_set_str(tempmpz,GetLineFromBuf(szData, (20*k)+i, 401),10);

			if (mpz_cmp(r,tempmpz) != 0) {
				MessageBox(NULL,"u was not correctly computed!","Error",MB_ICONEXCLAMATION);
				return 8;}

			bidule2 = *(GetLineFromBuf(szData,400,401)+20*k+i);
			a = atoi(&bidule2);

			if ((w-a) !=0){
				MessageBox(NULL,"w was not correctly computed!","Error",MB_ICONEXCLAMATION);
				return 8;
			}
		}
	}

	mpz_clear(r);
	mpz_clear(tempmpz);
	mpz_clear(gt2);
	mpz_clear(npkz);
	mpz_clear(u_t);

	delete[] szData;
	delete[] szData4;

	afficheMsg("opening commit...", IDC_LIST1, hWnddeniP);
	if (!csSend(concat80,81)) return 7; // opening the commit	
	
	lastData = new char[2];

	if (!csGet(lastData))  return 7;

	afficheMsg("Getting \"End of protocol\" confirmation...", IDC_LIST1, hWnddeniP);
	if (atoi(lastData) == 0) {
		MessageBox(hWnddeniP,"The signature is not valid","coGHIProof", MB_OK);
		afficheMsg("********** coGHIProof was a success : the signature is NOT valid", IDC_LIST1, hWnddeniP);
		return 0;
	}

	else {
		MessageBox(hWnddeniP,"The invalidity of the signature remains undetermined","coGHIProof", MB_OK);
		afficheMsg("********** coGHIProof failed : unable to say if the signature is valid or not", IDC_LIST1, hWnddeniP);
		return 8;
	}

}

// ###############################################################
// *********** 
// *********** bouton "Ok" (Génération des clés)
// *********** 
// ###############################################################
int geneOnBOk(HWND hWnd)
{
	int nLen;
	HWND hCtrl;
	char* pkname;
	char* skname;
	char* bitnb;
	struct timeb TempsDepart, TempsArrive;
	long converti,converti2,Temps;
	char* temps;

	// début chrono:
	ftime(&TempsDepart);
	converti = (((TempsDepart.time-1069000000)*1000)+ TempsDepart.millitm);

	hCtrl = GetDlgItem(hWnd,IDC_PATH_SRC5);
	nLen = GetWindowTextLength(hCtrl) + 1;
	pkname = (char*)malloc(nLen);
	GetWindowText(hCtrl,pkname,nLen);

	if((strcmp(pkname,".pmova")==0) || (strcmp(pkname,"")==0)) return 3;

	hCtrl = GetDlgItem(hWnd,IDC_PATH_SRC7);
	nLen = GetWindowTextLength(hCtrl) + 1;
	skname = (char*)malloc(nLen);
	GetWindowText(hCtrl,skname,nLen);

	if((strcmp(skname,".smova")==0) || (strcmp(skname,"")==0)) return 2;

	hCtrl = GetDlgItem(hWnd,IDC_BITS);
	nLen = GetWindowTextLength(hCtrl) + 1;
	bitnb = (char*)malloc(nLen);
	GetWindowText(hCtrl,bitnb,nLen);

	if(strcmp(bitnb,"")==0) return 1;
	
	generateKeys(pkname,skname,bitnb);

	//fin chrono
	ftime(&TempsArrive);
	converti2= (((TempsArrive.time-1069000000)*1000)+ TempsArrive.millitm);	
	Temps= converti2-converti;

	temps = (char*)malloc(strlen(IntToChar(Temps))+21);

	sprintf(temps,"Processing time: %d ms",Temps);

	hCtrl = GetDlgItem(hWnd,IDC_TEMPS);
	SetWindowText(hCtrl, temps);

	free(temps);
	free(pkname);
	free(skname);
	free(bitnb);

	return 0;
}

int generateKeys(char* pkname, char* skname, char* bitnb){

	// Les variables...
	char tempQ80[81][1025]={0};
	int i_prng;
	DWORD dwO;
	HANDLE Public_Key,Secret_Key;
	BOOL bRet;
	struct cornAlgo piXY,sigmaXY,alphaXY,rep2;
	mpz_t prime_number1,prime_number2,n,randNb0,prime_n,randNb1,randNbX,tempQ_prng;
	int x_prng;
	double l_prng;
	char concat2[81];
	char concatTrue2[161];
	long xl_prng  = 0;
	int iBit_prng = 0;
	char *work_prng = 0;
	char conv[2049]={0};
	char conv_prng[2049]={0};
	char convAlpha[2049]={0};

	mpz_init(randNbX);
	getrand2(randNbX,bitnb);
	mpz_init(randNb0);mpz_init(randNb1);
	mpz_set(randNb0,randNbX);
	getrand2(randNbX,bitnb);mpz_set(randNb1,randNbX);

	Bar1.SetRange(0,19);
	Bar1.Hide(true);

// création du fichier Public_Key.crypt
	Public_Key = CreateFile(pkname,GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(Public_Key == INVALID_HANDLE_VALUE)
	{
		CloseHandle(Public_Key);
		return 4;
	}

// création du fichier Secret_Key.crypt
	Secret_Key = CreateFile(skname,GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(Secret_Key == INVALID_HANDLE_VALUE)
	{

		CloseHandle(Public_Key);
		CloseHandle(Secret_Key);
		return 4;
	}
	Bar1.SetPos(1);
	Sleep(10);

	mpz_init(prime_n);
	mpz_init(prime_number1);genPrime(prime_n,randNb0);
	mpz_set(prime_number1,prime_n);

	Bar1.SetPos(3);
	Sleep(10);

	mpz_init(prime_number2);genPrime(prime_n,randNb1);
	mpz_set(prime_number2,prime_n);

	piXY = cornacchia(prime_number1);
	piXY = transformPrimary(piXY.x_sol,piXY.y_sol);

	Bar1.SetPos(5);
	Sleep(10);

	sigmaXY = cornacchia(prime_number2);
	sigmaXY = transformPrimary(sigmaXY.x_sol,sigmaXY.y_sol);

// alpha représente l'homomorphisme Xgroup -> Ygroup
	alphaXY = multComplex(piXY,sigmaXY);
	alphaXY = transformPrimary(alphaXY.x_sol,alphaXY.y_sol);

	Bar1.SetPos(7);
	Sleep(10);
	
	mpz_get_str(convAlpha,10,alphaXY.x_sol);
	bRet = WriteFile(Secret_Key,convAlpha,strlen(convAlpha),&dwO,0);
	bRet = WriteFile(Secret_Key,"\r\n",2, &dwO,0);
	mpz_get_str(convAlpha,10,alphaXY.y_sol);
	bRet = WriteFile(Secret_Key,convAlpha,strlen(convAlpha),&dwO,0);
	bRet = WriteFile(Secret_Key,"\r\n",2, &dwO,0);
	bRet = WriteFile(Secret_Key,"\r\n",2, &dwO,0);

// l_prng représente seedK dans la version 1

	l_prng=getrand(); // A TRAITER: SEED DE 512 BITS ==> MODIF DE PRNG.H

	bRet = WriteFile(Public_Key,IntToChar(l_prng),strlen(IntToChar(l_prng)),&dwO,0);
	bRet = WriteFile(Public_Key,"\r\n",2, &dwO,0);

// n est le nombre qui représente l'ensemble Xgroup

	mpz_init(n);
	mpz_mul(n, prime_number1, prime_number2);
	mpz_get_str(conv,10,n);
	bRet = WriteFile(Public_Key,conv,strlen(conv),&dwO,0);
	bRet = WriteFile(Public_Key,"\r\n",2, &dwO,0);

	Bar1.SetPos(9);
	Sleep(10);

// tempQ80 représente les 80 Xkey dans la version 1
	init_genrand(l_prng);
	mpz_init(tempQ_prng);
	for(x_prng = 0; x_prng < 80; x_prng++) {
		work_prng = (char*)tempQ80[x_prng];
		
		for(i_prng = 0; i_prng < 32; i_prng++) {
			xl_prng = (long)genrand_int32();

			for(iBit_prng = 0; iBit_prng < 32; iBit_prng++, xl_prng <<= 1) {
				*work_prng++ = (xl_prng & 0x80000000) ? '1' : '0';
			}
		}
		mpz_set_str(tempQ_prng, tempQ80[x_prng],2); 
		mpz_get_str(conv_prng,10,tempQ_prng);
		
		if (x_prng == 40){Bar1.SetPos(11);Sleep(10);}	
		
	}
	Bar1.SetPos(13);
	Sleep(10);

// rep2 représente les 80 Ykey dans la version 1
	mpz_set_ui(piXY.y_sol,0);

	ZeroMemory(concat2,81);
	ZeroMemory(concatTrue2,161);

	for (x_prng=0; x_prng<80;x_prng++){
		mpz_set_str(tempQ_prng, tempQ80[x_prng],2);
		mpz_set(piXY.x_sol,tempQ_prng);

		rep2 = quartic(piXY,alphaXY);
	
		if (x_prng == 30){Bar1.SetPos(15);Sleep(10);}
		if (x_prng == 60){Bar1.SetPos(17);Sleep(10);}

	if ((mpz_cmp_ui(rep2.y_sol, 0) == 0) && (mpz_cmp_ui(rep2.x_sol, 0) == 0)){return FALSE;}
		else{
				if (mpz_cmp_ui(rep2.y_sol, 0) == 0){// s'il n'y a pas de i
					if (mpz_cmp_ui(rep2.x_sol, 1) == 0){
					
						strcat(concat2,"0");strcat(concatTrue2,"00");}
					else{
						strcat(concat2,"1");strcat(concatTrue2,"10");}
				}
		
				else{
					if (mpz_cmp_ui(rep2.y_sol, 1) == 0){
						strcat(concat2,"0");strcat(concatTrue2,"01");}
					else{
						strcat(concat2,"1");strcat(concatTrue2,"11");}
				}
		}
	}
		mpz_clear(tempQ_prng);
	
		bRet = WriteFile(Public_Key,concat2,strlen(concat2),&dwO,0);
		bRet = WriteFile(Public_Key,"\r\n",2, &dwO,0);

		bRet = WriteFile(Secret_Key,IntToChar(l_prng),strlen(IntToChar(l_prng)),&dwO,0);
		bRet = WriteFile(Secret_Key,"\r\n",2, &dwO,0);
		
		Bar1.SetPos(19);Sleep(10);
		
		CloseHandle(Public_Key);
		CloseHandle(Secret_Key);

		return 0;
}

// ###############################################################
// *********** 
// *********** bouton "Ok" (SIGNATURE)
// *********** 
// ###############################################################
int signOnBOk(HWND hWnd, int idcname, int idcname2, int chrono, char** pwdref)
{
	// Les variables...
	char* szSrc;
	char* szData;
	HWND hCtrl;
	int nLen,i;
	DWORD dwFileSize,dwO;
	HANDLE hSrcFileSC;
	struct cornAlgo piXY,alphaXY,rep;
	mpz_t tempQ;
	int x,y,z1,z2;
	char concat[21];
	char concatTrue[41];
	char* pwd1 = NULL;
	char* pwd2 = NULL;
	char pwd[12] = {0};
	char* d;
	char concatA[12];char concatB[12];
	char*** xsigref = NULL;
	char** xsig = NULL;
	char* temps;
	struct timeb TempsDepart, TempsArrive;
	long converti,converti2,Temps;

	xsigref = &xsig;
	
	// début chrono
	if (chrono == 0) {
		ftime(&TempsDepart);
		converti= (((TempsDepart.time-1069000000)*1000)+ TempsDepart.millitm);	
	}

// ouvrir Secret_Key et récupérer alphaXY
	hCtrl = GetDlgItem(hWnd,IDC_PATH_SRC6);
	nLen = GetWindowTextLength(hCtrl) + 1;
	szSrc = new char[nLen];
	GetWindowText(hCtrl,szSrc,nLen);

	if (strcmp(szSrc,"") == 0) return 2;

   hSrcFileSC = CreateFile(szSrc,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFileSC == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFileSC);
		return 4;
	}	

	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFileSC,NULL);// + 1;
	szData = new char[dwFileSize+1];

	if(!ReadFile(hSrcFileSC,szData,dwFileSize,&dwO,NULL))
	{
		delete[] szData;
		delete[] szSrc;

		CloseHandle(hSrcFileSC);
		return 4;
	}
	CloseHandle(hSrcFileSC);

	*(szData + dwFileSize) = '\0';

	d = szData;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	mpz_init(alphaXY.x_sol);mpz_init(alphaXY.y_sol);

	mpz_set_str(alphaXY.x_sol,GetLineFromBuf(szData, 0, 4),10);
	mpz_set_str(alphaXY.y_sol,GetLineFromBuf(szData, 1, 4),10);

	mpz_init(piXY.x_sol);mpz_init(piXY.y_sol);
	mpz_set_ui(piXY.y_sol,0);

	compute_the_x_sigs(hWnd,idcname2, xsigref);

	mpz_init(tempQ);
	ZeroMemory(concat,21);ZeroMemory(concatTrue,41);
	for (x=0; x<20;x++){
		mpz_set_str(tempQ, xsig[x],10);
		mpz_set(piXY.x_sol,tempQ);

		rep = quartic(piXY,alphaXY);

		if ((mpz_cmp_ui(rep.y_sol, 0) == 0) && (mpz_cmp_ui(rep.x_sol, 0) == 0)){return 3;}
		else{
				if (mpz_cmp_ui(rep.y_sol, 0) == 0){
					if (mpz_cmp_si(rep.x_sol, 1) == 0){strcat(concat,"0");strcat(concatTrue,"00");}
					else{
						strcat(concat,"1");strcat(concatTrue,"10");}
				}
		
				else{
					if (mpz_cmp_si(rep.y_sol, 1) == 0){
						strcat(concat,"0");strcat(concatTrue,"01");}
					else{strcat(concat,"1");strcat(concatTrue,"11");}
				}
		}
	}

	mpz_clear(tempQ);
	
	ZeroMemory(concatA,11);
	ZeroMemory(concatB,11);
	for(x=0;x<10;x++){
		concatA[x]=concat[x];
		concatB[x]=concat[x+10];}
	
	strcat(concatA,"1");strcat(concatB,"1"); // un checksum pas vraiment checksumique ;o)
	
	z1=0;y=1;
	for(x=strlen(concatA)-1;x>=0;x--)
	{
		if (concatA[x]=='1') z1+=y;
		y=2*y;
	}

	z2=0;y=1;
	for(x=strlen(concatB)-1;x>=0;x--)
	{
		if (concatB[x]=='1') z2+=y;
		y=2*y;
	}

	pwd1 = dico[z1];pwd2 = dico[z2];

	ZeroMemory(pwd,11);
	strcat(pwd,pwd1);strcat(pwd," ");strcat(pwd,pwd2);

	if (chrono == 0) SetDlgItemText(hWnd,IDC_EDIT1,pwd);

	if (chrono == 1) *pwdref = pwd;

	delete[] szSrc;
	delete[] szData;

	//fin chrono
	if (chrono == 0) {
		ftime(&TempsArrive);
		converti2= (((TempsArrive.time-1069000000)*1000)+ TempsArrive.millitm);	
		Temps= converti2-converti;

		temps = (char*)malloc(strlen(IntToChar(Temps))+21);

		sprintf(temps,"Processing time: %d ms",Temps);
		hCtrl = GetDlgItem(hWnd,IDC_TEMPS2);
		SetWindowText(hCtrl, temps);
		free(temps);
	}

	return 0;
}

// ###############################################################
// *********** 
// *********** compute_the_x_sigs
// *********** 
// ###############################################################

int compute_the_x_sigs(HWND hWnd, int path_src, char*** xsigref){

	char* szSrcS;
	char* szData;
	HWND hCtrl;
	int nLen,i;
	DWORD dwFileSize,dwO;
	HANDLE hSrcFile;
	unsigned char out[20];
	mpz_t tempQ;
	int x;
	unsigned long init[20], length=20;
	long xl       = 0;
	int  iBit     = 0;
	char *work    = 0;
	char** xsig = NULL;
	char tempQ20[21][1025] = {0};

	//Récupérer le path du fichier source
	hCtrl = GetDlgItem(hWnd,path_src);
	nLen = GetWindowTextLength(hCtrl) + 1;
	
	szSrcS = new char[nLen];
	GetWindowText(hCtrl,szSrcS,nLen);

	if (strcmp(szSrcS,"") == 0) return 1;
	
	//Ouverture du fichier source 
	hSrcFile = CreateFile(szSrcS,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		delete[] szSrcS;
		CloseHandle(hSrcFile);
		return 6;
	}

	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFile,NULL) + 1;
	szData = new char[dwFileSize];

	//Lire le fichier source
	if(!ReadFile(hSrcFile,szData,dwFileSize,&dwO,0))
	{
		delete[] szSrcS;
		delete[] szData;
		CloseHandle(hSrcFile);
		return 6;
	}

	// haschage (SHA-1) du fichier source
	ZeroMemory(out,20,);
	ZeroMemory(init,20);

	encode(szData,out,dwFileSize);

	xsig = (char**)malloc(sizeof(char*)*20);

	for (x=0;x<20;x++)
		xsig[x] =(char*)malloc(sizeof(char)*1025);	

	for (x=0; x<20;x++)
		init[x]=(int)out[x];

	mpz_init(tempQ);
	init_by_array(init,length);

	for(x = 0; x < 20; x++) {
		work = (char*)tempQ20[x];
		for(i = 0; i < 32; i++) {
			xl = (long)genrand_int32();
			for(iBit = 0; iBit < 32; iBit++, xl <<= 1) {
				*work++ = (xl & 0x80000000) ? '1' : '0';
			}
		}
	mpz_set_str(tempQ,tempQ20[x],2);
	mpz_get_str(xsig[x],10,tempQ);
	}

	mpz_clear(tempQ);

	*xsigref = xsig;

	return 0;

}

// ###############################################################
// *********** 
// *********** computing_the_s_file
// *********** 
// ###############################################################

int computing_the_s_file(HWND hWndconf,char** n_pkref,char** yKeys_pkref,char** seedK_pkref,char** concat100ref,char** szDataref){

	mpz_t convXKeys,convXSigs;

	BOOL bRet;
	HWND hCtrl;
	int nLen,x_prng,x;
	int i,jac,w			= 0;
	char* szSrc				= NULL;
	char* szData			= NULL;
	char* d					= NULL;
	HANDLE hSrcFile			= INVALID_HANDLE_VALUE;
	HANDLE hDestFile		= INVALID_HANDLE_VALUE;
	HANDLE hSrcFile2		= INVALID_HANDLE_VALUE;
	DWORD dwFileSize		= 0;
	DWORD dw;
	char* mot1_sig			= NULL;
	char* mot2_sig			= NULL;
	char tempQ80[81][1025]	= {0};
	char tempQ20b[21][1025]	= {0};
	int tempQ80Int[100]		= {0};
	char concat100[201]		= {0};
	long xl_prng			= 0;
	int iBit_prng			= 0;
	char *work_prng			= 0;
	char convb[2049]		= {0};
	char conv[2049]			= {0};
	int binInv[11]			= {0};
	char bin[20]			= {0};
	unsigned long init[20],	length = 20;
	unsigned char out[20];
	mpz_t az,bz;

// ouverture du fichier Public_Key.crypt
	hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC);
	nLen = GetWindowTextLength(hCtrl) + 1;
	szSrc = new char[nLen];
	GetWindowText(hCtrl,szSrc,nLen);

	if ((strcmp(szSrc,"") == 0) || (strcmp(szSrc,".pmova") == 0)) return 1;
	
	//Ouverture du fichier Public_Key.crypt
	hSrcFile = CreateFile(szSrc,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		delete[] szSrc;
		CloseHandle(hSrcFile);
		return 6;
	}
	
// création du fichier s_file.crypt
// ligne 01->80 : XKeys		ligne 81 : YKeys
// ligne 82->101: XSigs		ligne 102: YSigs

	if(Denial)
	hDestFile = CreateFile(pathDesktop("MovaDir/s_denifile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	else
	hDestFile = CreateFile(pathDesktop("MovaDir/s_conffile.crypt"),GENERIC_WRITE,NULL,NULL,
							CREATE_ALWAYS,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hDestFile == INVALID_HANDLE_VALUE)
	{
		delete[] szSrc;
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		return 6;
	}

	afficheMsg("Creation of S...",IDC_EDIT1,hWndconf);

// lecture du fichier Public_key.crypt
	dwFileSize = GetFileSize(hSrcFile,NULL);
	szData = new char[dwFileSize+1];

	if(!ReadFile(hSrcFile,szData,dwFileSize,&dw,NULL))
	{
		delete[] szSrc;
		delete[] szData;
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		return 6;
	}
	CloseHandle(hSrcFile);

	*(szData + dwFileSize) = '\0';

	d = szData;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	*seedK_pkref	= GetLineFromBuf(szData, 0, i);// ligne no 1, le seed
	*n_pkref		= GetLineFromBuf(szData, 1, i);// ligne no 2, le n
	*yKeys_pkref	= GetLineFromBuf(szData, 2, i);// ligne no 3, les YKeys
	
	afficheMsg("Recovery of the seed...",IDC_EDIT1,hWndconf);
	afficheMsg((char*)*seedK_pkref,IDC_EDIT1,hWndconf);
	afficheMsg("Recovery of n...",IDC_EDIT1,hWndconf);
	afficheMsg((char*)*n_pkref,IDC_EDIT1,hWndconf);
	afficheMsg("YKeys...",IDC_EDIT1,hWndconf);
	afficheMsg((char*)*yKeys_pkref,IDC_EDIT1,hWndconf);

// calcul des XKeys à partir du seedK et stockage des YKeys
	afficheMsg("XKeys...",IDC_EDIT1,hWndconf);

	i = atoi(*seedK_pkref);
	init_genrand(i);
	
	mpz_init(az);mpz_init(bz);

	mpz_set_str(bz,*n_pkref,10);
	mpz_init(convXKeys);
	for(i = 0; i < 80; i++) {
		work_prng = (char*)tempQ80[i];
		
		for(x_prng = 0; x_prng < 32; x_prng++) {
			xl_prng = (long)genrand_int32();

			for(iBit_prng = 0; iBit_prng < 32; iBit_prng++, xl_prng <<= 1) {
				*work_prng++ = (xl_prng & 0x80000000) ? '1' : '0';
			}
		}
		
	mpz_set_str(convXKeys, tempQ80[i],2);
	mpz_get_str(conv,10,convXKeys);

	afficheMsg(conv,IDC_EDIT1,hWndconf);

		bRet = WriteFile(hDestFile,conv,strlen(conv),&dw,0);
		bRet = WriteFile(hDestFile,"\r\n",2, &dw,0);

		mpz_set_str(convXKeys, tempQ80[i],10);
		mpz_get_str(conv,10,convXKeys);
		
		mpz_set_str(az,tempQ80[i],2);
		mpz_get_str(tempQ80[i],10,az);
		mpz_set_str(az,tempQ80[i],10); // Voilà, on est en base 10

		tempQ80Int[i] = mpz_jacobi(az,bz);// 1 ou -1, Jacobi de Xkeys par n
	}
	
	bRet = WriteFile(hDestFile,(char*)*yKeys_pkref,strlen((char*)*yKeys_pkref),&dw,0);
	bRet = WriteFile(hDestFile,"\r\n",2, &dw,0);
		
// Récupération de la signature et traitement
	hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC3);
	nLen = GetWindowTextLength(hCtrl) + 1;
	szSrc = new char[nLen];
	GetWindowText(hCtrl,szSrc,nLen);

	if (strcmp(szSrc,"") == 0) {
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		return 2;}

	*(szSrc + nLen) = '\0';

	d = szSrc;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
	
		case ' ':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	mot1_sig	= GetLineFromBuf(szSrc, 0, 2);
	mot2_sig	= GetLineFromBuf(szSrc, 1, 2);

	ZeroMemory(bin,21);

	x=0;
	while ((strcmp(dico[x],mot1_sig)) & (x < 2049)){x++;}

	// Recherche de la position du mot dans dico
	if (x == 2048) {
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		return 2;}

	i=0;
	while (x != 0){
		binInv[10-i] = x%2;
		x /= 2;
		i++;
	}

	for (i=0;i<10;i++){strcat(bin,(char*)IntToChar(binInv[i]));}

	x=0;
	while ((strcmp(dico[x],mot2_sig))& (x < 2049)){x++;}
	if (x == 2048) {
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		return 2;}

	for (i=0;i<11;i++){binInv[i]=0;}
	i=0;	
	while (x != 0){
		binInv[10-i] = x%2;
		x /= 2;
		i++;
	}
	
	for (i=0;i<10;i++){strcat(bin,(char*)IntToChar(binInv[i]));}
	
// calcul des XSigs à partir du fichier source
	hCtrl = GetDlgItem(hWndconf,IDC_PATH_SRC2);
	nLen = GetWindowTextLength(hCtrl) + 1;
	szSrc = new char[nLen];
	GetWindowText(hCtrl,szSrc,nLen);
	if (strcmp(szSrc,"") == 0) {
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		return 3;}

	//Ouverture du fichier source 
	hSrcFile2 = CreateFile(szSrc,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile2 == INVALID_HANDLE_VALUE)
	{
		delete[] szSrc;
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		CloseHandle(hSrcFile2);
		return 6;
	}
	
	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFile2,NULL) + 1;
	szData = new char[dwFileSize];

	//Lire le fichier source
	if(!ReadFile(hSrcFile,szData,dwFileSize,&dw,0))
	{
		delete[] szSrc;
		delete[] szData;
		CloseHandle(hSrcFile);
		CloseHandle(hDestFile);
		CloseHandle(hSrcFile2);
		return 6;
	}

	//Encrypter le fichier source, cela donnera le seed du PRNG
	encode(szData,out,dwFileSize);

	for (x=0; x<20;x++){
			init[x]=(int)out[x];
		}

  // les 20 Xsig 
	mpz_init(convXSigs);
	init_by_array(init,length);
	for(i = 0; i < 20; i++) {
		work_prng = (char*)tempQ20b[i];
		
		for(x_prng = 0; x_prng < 32; x_prng++) {
			xl_prng = (long)genrand_int32();

			for(iBit_prng = 0; iBit_prng < 32; iBit_prng++, xl_prng <<= 1) {
				*work_prng++ = (xl_prng & 0x80000000) ? '1' : '0';
			}
		}
		
		mpz_set_str(convXSigs, tempQ20b[i],2);
		mpz_get_str(convb,10,convXSigs);

		afficheMsg(convb,IDC_EDIT1,hWndconf);


		bRet = WriteFile(hDestFile,convb,strlen(convb),&dw,0);
		bRet = WriteFile(hDestFile,"\r\n",2, &dw,0);

		mpz_set_str(az, tempQ20b[i],2);
		mpz_get_str(tempQ20b[i],10,az);
		mpz_set_str(az,tempQ20b[i],10); // Voilà, on est en base 10
		tempQ80Int[i+80] = mpz_jacobi(az,bz);	
	}
	mpz_clear(az);mpz_clear(bz);
	
	afficheMsg("S has been built...",IDC_EDIT1,hWndconf);

	bRet = WriteFile(hDestFile,(char*)bin,strlen((char*)bin),&dw,0);
	bRet = WriteFile(hDestFile,"\r\n",2, &dw,0);

	CloseHandle(hSrcFile);
	CloseHandle(hDestFile);
	CloseHandle(hSrcFile2);
	delete[] szData;
	delete[] szSrc;

	if (Denial)
	hSrcFile2 = CreateFile(pathDesktop("MovaDir/s_denifile.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	else
		hSrcFile2 = CreateFile(pathDesktop("MovaDir/s_conffile.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);

	if(hSrcFile2 == INVALID_HANDLE_VALUE)
	{

		CloseHandle(hSrcFile2);
		return 6;
	}
	
	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFile2,NULL) + 1;
	szData = new char[dwFileSize];

	//Lire le fichier source
	if(!ReadFile(hSrcFile2,szData,dwFileSize,&dw,0))
	{

		delete[] szData;
		CloseHandle(hSrcFile2);
		return 6;
	}

	CloseHandle(hSrcFile2);

	*(szData + dwFileSize) = '\0';

	d = szData;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}

	*szDataref = szData;

	d=NULL;


	// reconstruction des Ykeys et Ysigs:
	// 
	// récupérer les y: avec y1 et y2, on a les bits de
	// poids fort... il faut donc récupérer le bit de 
	// poids faible en utilisant deux points:
	// 1) 1,i -> 0; -1,-i -> 1
	// 2) si (x/alpha)4 = 1/-1, Jacobi(x/N(alpha)) = 1
	//    si (x/alpha)4 = i/-i, Jacobi(x/N(alpha)) = -1
	// 
	// si Jacobi(x/N(alpha)) = 1  et Quart(x) = 0, x = 00
	// si Jacobi(x/N(alpha)) = -1 et Quart(x) = 0, x = 01
	// si Jacobi(x/N(alpha)) = 1  et Quart(x) = 1, x = 10
	// si Jacobi(x/N(alpha)) = -1 et Quart(x) = 1, x = 11
	//
	// Puis: Calcul des u[i] et des w[i]
	afficheMsg("computing full Y...",IDC_EDIT1,hWndconf);

	ZeroMemory(concat100,201);

	for (i=0;i<80;i++){
		
		jac = tempQ80Int[i];

		if ((jac == 1) && ((*yKeys_pkref)[i] == '0')){
			strcat(concat100,"00");}

		if ((jac == 1) && ((*yKeys_pkref)[i] == '1')){
			strcat(concat100,"10");}
	
		if ((jac == -1) && ((*yKeys_pkref)[i] == '0')){
			strcat(concat100,"01");}

		if ((jac == -1) && ((*yKeys_pkref)[i] == '1')){
			strcat(concat100,"11");}
	}

	for (i=0;i<20;i++){
	
		jac = tempQ80Int[i+80];
	
		if ((jac == 1) && (bin[i] == '0')){
			strcat(concat100,"00");}

		if ((jac == 1) && (bin[i] == '1')){
			strcat(concat100,"10");}
	
		if ((jac == -1) && (bin[i] == '0')){
			strcat(concat100,"01");}

		if ((jac == -1) && (bin[i] == '1')){
			
			strcat(concat100,"11");}
	}

	*concat100ref = concat100;

	afficheMsg(concat100,IDC_EDIT1,hWndconf);
	afficheMsg("Computing u and w... ",IDC_EDIT1,hWndconf);

	return 0;
}

// ###############################################################
// *********** 
// *********** getting_the_u_file
// *********** 
// ###############################################################

int getting_the_u_file(HWND hWnd, char** szData2ref, char** szDataref){
	
	char* szData;
	char* szData1;
	char* szSrc;
	char* d;
	HWND hCtrl;
	int nLen,i;
	DWORD dwFileSize,dw;
	HANDLE hSrcFile;

	if (Denial){
		if (!csGetFile(pathDesktop("MovaDir/u_denifile2.crypt")))
			return 7;
	}
	else{
		if (!csGetFile(pathDesktop("MovaDir/u_conffile2.crypt"))) 
			return 7;
		}
			
	afficheMsg("Receiving u[i]'s...", IDC_LIST1, hWnd);

	hCtrl = GetDlgItem(hWnd,IDC_SL);
	SetWindowText(hCtrl, "1 - Receiving u[i]'s");
						
// récupération de la clé secrète
	hCtrl = GetDlgItem(hWnd,IDC_BW_PATH);
	nLen = GetWindowTextLength(hCtrl) + 1;
	szSrc = new char[nLen];
	GetWindowText(hCtrl,szSrc,nLen);
	
	hSrcFile = CreateFile(szSrc,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		delete[] szSrc;
		CloseHandle(hSrcFile);
		return 6;
	}
	afficheMsg("Opening secret key", IDC_LIST1, hWnd);

	//Création du buffer pour lire le fichier source
	dwFileSize = GetFileSize(hSrcFile,NULL);// + 1;
	szData1 = new char[dwFileSize+1];

	if(!ReadFile(hSrcFile,szData1,dwFileSize,&dw,NULL))
	{
		delete[] szData1;
		CloseHandle(hSrcFile);
		return 6;
	}
	delete[] szSrc;
	CloseHandle(hSrcFile);

	*(szData1 + dwFileSize) = '\0';

	d = szData1;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;
	*szData2ref = szData1;
	
// récupération du fichier u_file.crypt
	if (Denial) 
		hSrcFile = CreateFile(pathDesktop("MovaDir/u_denifile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	else 
		hSrcFile = CreateFile(pathDesktop("MovaDir/u_conffile2.crypt"),GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	if(hSrcFile == INVALID_HANDLE_VALUE)
	{
		CloseHandle(hSrcFile);
		return 6;
	}

	afficheMsg("Reading u[i]'s...", IDC_LIST1, hWnd);

// lecture du fichier u_file.crypt
	dwFileSize = GetFileSize(hSrcFile,NULL);
	szData = new char[dwFileSize+1];

	//Lire le fichier source
	if(!ReadFile(hSrcFile,szData,dwFileSize,&dw,NULL))
	{
		delete[] szData;
		delete[] szData1;
		CloseHandle(hSrcFile);
		return 6;
	}
	CloseHandle(hSrcFile);

	*(szData + dwFileSize) = '\0';

	d = szData;
	i = (*d ? 1 : 0);

	while(*d){
		switch(*d){
		case '\r':
			memmove(d,d+1,strlen(d+1)+1);
			break;
		case '\n':
			*d='\0';
			d++;
			i++;
			break;
		default:
			d++;
			break;
		}
	}
	d=NULL;

	*szDataref = szData;

	return 0;
}

// FIN DU PROGRAMME ********************************************************