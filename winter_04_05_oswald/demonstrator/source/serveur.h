#include <winsock2.h>		// Socket 
#pragma comment(lib,"WS2_32.lib") // Librairie Associé au Socket

#define WM_SOCKET	WM_USER+100
int ClientConnect(char *IP,int PORT);
SOCKET m_socket;
sockaddr_in clientService;
char* IP;
char** a;
hostent* host;

// ###############################################################
// *********** 
// *********** Connection du client
// *********** 
// ###############################################################

BOOL ClientConnect(char req_host[255],int PORT, HWND hWndconf)
{
	if (host = gethostbyname(req_host))
		IP = inet_ntoa(**((struct in_addr **)host->h_addr_list));

	else return FALSE;

    clientService.sin_family = AF_INET;
    clientService.sin_addr.s_addr = inet_addr(IP);
    clientService.sin_port = htons(PORT);

    m_socket = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );
    if (m_socket == INVALID_SOCKET) return FALSE;

    if (connect(m_socket,(SOCKADDR*) &clientService,sizeof(clientService)) == SOCKET_ERROR) return FALSE;
	else return TRUE;

//	return TRUE;
}

// ###############################################################
// *********** 
// *********** Envoi d'un fichier
// *********** 
// ###############################################################

BOOL csSendFile(char* fileName)
{
	FILE *fich;
	char *buffer = NULL;	
	int TailleFichier1;
	DWORD dwFileSize;
	HANDLE hSrcFile = INVALID_HANDLE_VALUE;

	hSrcFile = CreateFile(fileName,GENERIC_READ,FILE_SHARE_READ,NULL,
							OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,NULL);
	
	if(hSrcFile == INVALID_HANDLE_VALUE) {CloseHandle(hSrcFile);return FALSE;}

	dwFileSize = GetFileSize(hSrcFile,NULL) + 1;
	CloseHandle(hSrcFile);

	if ((buffer=(char *)malloc(dwFileSize))==NULL) return FALSE;
	if ((fich=fopen(fileName,"rb"))==NULL) return FALSE;

    if(TailleFichier1=fread(buffer,1,dwFileSize,fich)<0) return FALSE;

    if (send(m_socket,buffer,dwFileSize,0)<0) {return FALSE;}
    fclose(fich);
	free(buffer);

	return TRUE;
}

// ###############################################################
// *********** 
// *********** Réception d'un fichier
// *********** 
// ###############################################################

BOOL csGetFile(char* fileName)
{
	FILE *fich;
	char *buffer = NULL;
	int TailleFichier;

	if ((fich=fopen(fileName,"w"))==NULL) {_beep(400,600);return FALSE;}

    if ((buffer=(char *)malloc(200000))== NULL) {_beep(400,600);return FALSE;}

	TailleFichier=recv(m_socket,buffer,200000,0);
	buffer[TailleFichier]='\0';
	fwrite(buffer,1,TailleFichier,fich);
	fclose(fich);
	free(buffer);
	return TRUE;
}

// ###############################################################
// *********** 
// *********** Envoi d'un char
// *********** 
// ###############################################################

BOOL csSend(char* szData, DWORD msgSize)
{
	if(!(send(m_socket,szData,msgSize,0))) return FALSE;
	return TRUE;
}

// ###############################################################
// *********** 
// *********** Réception d'un buffer 
// *********** 
// ###############################################################

BOOL csGet(char* getData)
{	
	if(!(recv(m_socket,getData,strlen(getData)+1,0))) return FALSE;
	return TRUE;
}

// ###############################################################
// *********** 
// *********** Attente d'un client
// *********** 
// ###############################################################

BOOL ServerListen(int PORT)
{
	int iResult;

	sockaddr_in service;
	service.sin_family = AF_INET;
	service.sin_addr.S_un.S_addr = INADDR_ANY;
	service.sin_port = htons(PORT);

	m_socket = socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
	if(m_socket == INVALID_SOCKET) return FALSE;

	iResult = bind(m_socket,(SOCKADDR*) &service, sizeof(service));
	if(iResult == SOCKET_ERROR)	return FALSE;

	listen(m_socket,0);
	
	return TRUE;
}

void hardClose(SOCKET m_socket){
	shutdown(m_socket,SD_BOTH);
	closesocket(m_socket);
	WSACleanup();
}