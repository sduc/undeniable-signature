#ifndef __PROGRESSBAR_H
#define __PROGRESSBAR_H

#include <windows.h>
#include <commctrl.h>

class ProgressBar
{
private:
HWND hBar;

public:
int MaxRan;
int MinRan;

BOOL Init(HWND hwndbar); //Initialise le controle
BOOL SetRange(int nMinRange,int nMaxRange); //D�finis la position Minimale et Maximale
void Hide(BOOL bVisible); //Afiche ou Cache le controle
BOOL IsVisible(void); //D�termine si le controle est visible
int SetStep(int nStep); //D�finis le pas
int SetPos(int nPos); //D�finis la valeur du controle
int Increment(int nIncrement);//Incr�mente de nIncrement la valeur du controle
int IncrementStep(void);//Incr�mente d'un pas d�finis par SetStep la valeur du controle
};

BOOL ProgressBar::Init(HWND hwndbar) //Initialise le controle
{
hBar=hwndbar;

if (hBar==NULL)
    {
	     return FALSE;
     }
return TRUE;
}

BOOL ProgressBar::SetRange(int nMinRange,int nMaxRange) //D�finis la position Minimale et Maximale
{
if ((nMinRange<0) || (nMaxRange>65535))
    {
	   return FALSE;
     }

if(SendMessage(hBar,PBM_SETRANGE,0,MAKELPARAM(nMinRange,nMaxRange))==0)
    {
     return FALSE;
     }

MinRan = nMinRange;
MaxRan = nMaxRange;

return TRUE;
}

int ProgressBar::SetStep(int nStep) //D�finis le pas
{
int nOldStep;
nOldStep=SendMessage(hBar,PBM_SETSTEP,(WPARAM)nStep,0);
return nOldStep;
}

int ProgressBar::Increment(int nIncrement) //Incr�mente de nIncrement la valeur du controle
{
int nOldPos;
nOldPos=SendMessage(hBar,PBM_DELTAPOS,(WPARAM)nIncrement,0);
return nOldPos;
}

int ProgressBar::IncrementStep(void) //Incr�mente d'un pas d�finis par SetStep la valeur du controle
{
int nOldPos;
nOldPos=SendMessage(hBar,PBM_STEPIT,0,0);
return nOldPos;
}

int ProgressBar::SetPos(int nPos) //D�finis la valeur du controle
{
int nOldPos;
nOldPos=SendMessage(hBar,PBM_SETPOS,(WPARAM)nPos,0);
return nOldPos;
}

void ProgressBar::Hide(BOOL bVisible) //Afiche ou Cache le controle
{
if (bVisible==TRUE)
    {
        ShowWindow(hBar,SW_SHOW);
     }
else
    {
        ShowWindow(hBar,SW_HIDE);
     }
}

BOOL ProgressBar::IsVisible(void) //D�termine si le controle est visible
{
return IsWindowVisible(hBar);
}
#endif

