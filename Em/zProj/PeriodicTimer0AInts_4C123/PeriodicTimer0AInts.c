// PeriodicTimer0AInts.c
// Runs on LM4F120/TM4C123
// Use Timer0A in periodic mode to request interrupts at a particular
// period.
// Daniel Valvano
// September 11, 2013

/* This example accompanies the book
   "Embedded Systems: Real Time Interfacing to Arm Cortex M Microcontrollers",
   ISBN: 978-1463590154, Jonathan Valvano, copyright (c) 2014
  Program 7.5, example 7.6

 Copyright 2014 by Jonathan W. Valvano, valvano@mail.utexas.edu
    You may use, edit, run or distribute this file
    as long as the above copyright notice remains
 THIS SOFTWARE IS PROVIDED "AS IS".  NO WARRANTIES, WHETHER EXPRESS, IMPLIED
 OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE APPLY TO THIS SOFTWARE.
 VALVANO SHALL NOT, IN ANY CIRCUMSTANCES, BE LIABLE FOR SPECIAL, INCIDENTAL,
 OR CONSEQUENTIAL DAMAGES, FOR ANY REASON WHATSOEVER.
 For more information about my classes, my research, and my books, see
 http://users.ece.utexas.edu/~valvano/
 */

// oscilloscope or LED connected to PF3-1 for period measurement
// When using the color wheel, the blue LED on PF2 is on for four
// consecutive interrupts then off for four consecutive interrupts.
// Blue is off for: dark, red, yellow, green
// Blue is on for: light blue, blue, purple, white
// Therefore, the frequency of the pulse measured on PF2 is 1/8 of
// the frequency of the Timer0A interrupts.

#include "tm4c123gh6pm.h"
#include <stdint.h>
#include "PLL.h"
#include "Timer0A.h"


#define PF1       (*((volatile uint32_t *)0x40025008))
#define PF2       (*((volatile uint32_t *)0x40025010))
#define PF3       (*((volatile uint32_t *)0x40025020))
#define LEDS      (*((volatile uint32_t *)0x40025038))
	
#define RED       0x02
#define BLUE      0x04
#define GREEN     0x08
#define WHEELSIZE 8           // must be an integer multiple of 2
                              //    red, yellow,    green, light blue, blue, purple,   white,          dark
const long COLORWHEEL[WHEELSIZE] = {RED, RED+GREEN, GREEN, GREEN+BLUE, BLUE, BLUE+RED, RED+GREEN+BLUE, 0};

void DisableInterrupts(void); // Disable interrupts
void EnableInterrupts(void);  // Enable interrupts
long StartCritical (void);    // previous I bit, disable interrupts
void EndCritical(long sr);    // restore I bit to previous value
void WaitForInterrupt(void);  // low power mode

void UserTask(void){
  static int i = 0;
  LEDS = COLORWHEEL[i&(WHEELSIZE-1)];
  i = i + 1;
}
void PortF_Init(void){
	SYSCTL_RCGCGPIO_R |= 0x00000020; 			// 1) activate clock for Port F
	GPIO_PORTF_LOCK_R = 0x4C4F434B; 			// 2) unlock GPIO Port F
  GPIO_PORTF_PUR_R = 0x11; 							// enable pull-up on PF0 and PF4 SW1
  GPIO_PORTF_CR_R = 0x1F; 							// allow changes to PF4-0 SW2
	// only PF0 needs to be unlocked, other bits can't be locked
	GPIO_PORTF_AMSEL_R = 0x00; 						// 3) disable analog on PF
	GPIO_PORTF_DIR_R = 0x0E; 							// 5) PF4,PF0 in, PF3-1 out
	GPIO_PORTF_AFSEL_R = 0x00; 						// 6) disable alt funct on PF7-0
	GPIO_PORTF_DEN_R = 0x1F; 							// 7) enable digital I/O on PF4-0
	
//  GPIO_PORTF_IM_R |= 0x11;      // (f) arm interrupt on PF4 and PF1 *** No IME bit as mentioned in Book ***
  NVIC_PRI7_R = (NVIC_PRI7_R&0xFF00FFFF)|0x00600000; // (g) priority 3
  NVIC_EN0_R = 0x40000000;      // (h) enable interrupt 30 in NVIC
  EnableInterrupts();           // (i) Clears the I bit
}

unsigned long In;
const int PWM =  100000;
const int rate = 1000;
void delay(int time){
	int i=0;
	for(i=0;i<time;i++);
}
void GPIOPortF_Handler(void){
	In = GPIO_PORTF_DATA_R&0x11;   			// read switch status  
	LEDS = 0x00;
	int i=0;
	for(i=0;i<10000000;i++);
	
	if(In == 0x01){					// SW1 pressed
		for(i=0;i<PWM;i+=rate){
				LEDS = RED;
				delay(i);
				LEDS = 0x00;
				delay(PWM-i);
		}
		for(i=PWM;i>0;i-=rate){
				LEDS = RED;
				delay(i);
				LEDS = 0x00;
				delay(PWM-i);
		}
	}
	else{
		for(i=0;i<PWM;i+=rate){
				LEDS = GREEN;
				delay(i);
				LEDS = 0x00;
				delay(PWM-i);
		}
		for(i=PWM;i>0;i-=rate){
				LEDS = GREEN;
				delay(i);
				LEDS = 0x00;
				delay(PWM-i);
		}			
	}
	
	for(i=0;i<10000000;i++){}
  GPIO_PORTF_ICR_R = 0x11;    // acknowledge flag4 FLAG 4
}

// if desired interrupt frequency is f, Timer0A_Init parameter is busfrequency/f
#define F16HZ (50000000/16)
#define F1HZ (50000000/1)
#define F20KHZ (50000000/20000)
//debug code
int main(void){ 
  PLL_Init();                      // bus clock at 50 MHz
	PortF_Init();
  LEDS = 0;                        // turn all LEDs off
  Timer0A_Init(&UserTask, F1HZ);  // initialize timer0A (1 Hz)
  EnableInterrupts();

  while(1){
//    WaitForInterrupt();
		In = GPIO_PORTF_DATA_R&0x11;   			// read switch status  
		if(In == 0x10){		// SW2 pressed
			LEDS = 0x00;
			int i=0;
			for(i=0;i<10000000;i++){}
			for(i=0;i<PWM;i+=rate){
					LEDS = BLUE;
					delay(i);
					LEDS = 0x00;
					delay(PWM-i);
			}
			for(i=PWM;i>0;i-=rate){
					LEDS = BLUE;
					delay(i);
					LEDS = 0x00;
					delay(PWM-i);
			}		
			for(i=0;i<10000000;i++){}
			In = 0x00;
			GPIO_PORTF_IM_R |= 0x11;      // (f) arm interrupt on PF4 and PF1
		}
  }
}
