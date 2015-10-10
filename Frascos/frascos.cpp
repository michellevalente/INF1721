#include <stdio.h>
#include <stdlib.h>
#include "CPUTimer.h"
#include <string.h>
#define INTBIT 32

int ** parsed_data = NULL;
int bittage, num_elem;
char FILE_ERROR[] = "Couldn't read or open the file";

typedef enum{
	SUCCESS,
	ERROR,
	WARNING
} ret;

//ret stand;

/******************************
 retorna >0 se a maior que b
 		  0 se a igual a b
 		 <0 se a menor que b
*******************************/
int compare(int * a, int *b, int byte){
	int i,sum;
	for(i=0;i<byte;i++){
		sum = a[i] - b[i];
		if(sum != 0)
			return sum;
	}
	return sum;
}

ret parser_whole_file(char* fileName){

	char buffer[5];
	int i,j,k; 
	FILE * data_file = fopen(fileName,"r");
	#if defined VERBOSE
	printf("Parsing the file:%s\n",fileName );
	#endif
	if(data_file == NULL){
		puts(FILE_ERROR);
		return ERROR;
	}
	if (fscanf(data_file,"%d %d",&bittage,&num_elem) != 2){
		puts(FILE_ERROR);
		return ERROR;
	}
	//Capture \n
	if(fscanf(data_file,"%c%c",buffer,&buffer[1]) != 2){
		puts(FILE_ERROR);
		return ERROR;
	}

	parsed_data = (int**) malloc (sizeof(int*)*num_elem);

	for(i=0;i<num_elem;i++){
		parsed_data[i] = (int*) malloc( bittage );
		for(j=0; j<bittage/INTBIT; j++)
			parsed_data[i][j]=0;
	}

	for(i=0;i<num_elem;i++){

		for(j=0;j<bittage/INTBIT;j++){

			for(k=0;k<INTBIT;k++){

				if(fscanf(data_file," %c",&buffer[0]) != 1){
					puts(FILE_ERROR);
					return ERROR;
				}
				parsed_data[i][j] += ((int)(buffer[0] - '0'));
				parsed_data[i][j] = parsed_data[i][j] << (k < INTBIT-1 ? 1 : 0);

			}
			
		}
		//Capture \n
		if(fscanf(data_file,"%c",buffer) != 1){
				puts(FILE_ERROR);
				return ERROR;
		}
		if(buffer[0]== EOF || buffer[0] =='\0'){
			puts("Unexpected end");
			return WARNING;
		}
	}
	#if defined VERBOSE
	puts("File succefully parsed");
	#endif
	return SUCCESS;
}

void move_to(int *num, int size, int bits){
	int byte, rest,i;
	byte = bits/INTBIT;
	rest = bits%INTBIT;
	#if defined VERBOSE
	printf("%d %d\n",byte,rest );
	#endif
	for(i=0;i<size;i++){
		num[i]=0;
	}
	num[byte] = 1<<(rest==0?0:rest);
}

void sumBig(unsigned int *num, int size){
	int i, carry;
	carry = 0;
	for (i=0;i<size;i++){
		if(carry == 0){
			if (num[i]==0xFFFFFFFF){
				num[i] = 0;
				carry =1;
			}
			else{
				num[i] += 1;
			}
		}
		else{
			if (num[i]==0xFFFFFFFF){
				num[i] = 0;
				carry = 1;
			}
			else{
				num[i] += carry;
				carry = 0;
			}
			if (num[i]==0xFFFFFFFF){
				num[i] = 0;
				carry = 1;
			}
			else{
				num[i] += 1;
			}
		}
	}
}

void print(int *num, int size){
	int i;
	for(i=0;i<size;i++)
		printf(" _%x_",num[i]);
}

ret drop(int n_bits, int k, int * data ){

	int step,stepping,i,
	 answer[8], temp[8],
	 size, base, roof;
	size = n_bits/INTBIT;

	for(i=0;i<8;i++)
		answer[i]=0;

	
	base = 0;
	roof = n_bits;
	while(k >= 2){	
		step = (roof - base)/k;

		//go steping += step; (until steping == n_bits)
		stepping = base;

		while((stepping<roof)&& stepping<n_bits){

			move_to(answer,size,stepping);

			if(compare(answer,data,size)>=0){ //We've passed
				if(compare(answer,data,size)==0){
					printf("ACHEI\n"); //Do something
					return SUCCESS;
				}
				else{	
					break;
				}
			}
			stepping +=step;
		}
		//when it does break, shortens the scope and wides up the step
		base = stepping - step;
		roof = stepping; 
		k -=2;

	}

	// answer from base until roof ++
	//printf("===base %d roof %d\n",base,roof );
	move_to(answer,size,base-1);
	//puts("answer:");
	//print(answer,size);

	move_to(temp,size,roof-1);
	//puts("\ntemp:");
	//print(temp,size);
	//printf("\n\n\n\n\n\n");

	while(compare(answer,temp,size)<=0){

		sumBig((unsigned int*)answer,size);

		if(compare(answer,data,size)==0){
			#if defined VERBOSE

			puts("the result is:");
			print(answer,size);
			printf("\n");

			#endif
			return SUCCESS;
		}
		//printf("algo\n");
		//print(answer,size);

	}

	printf("ERROR _%d_\n",compare(answer,temp,size));
	puts("the result was wrong:");
	print(answer,size);
	printf("\n");
	return ERROR;

}
ret drop_all(int k){
	int i = 0, count =0;
	double file_avg=0;
	CPUTimer timer;
	for (i=0; i<num_elem; i++){
		count = 0;
		timer.reset();
		while(timer.getCPUTotalSecs() < 5.0){
				timer.start();
				drop(bittage,k,parsed_data[i]);
				timer.stop();
				count++;
			}
		file_avg += (timer.getCPUTotalSecs()/count)/num_elem;
	}
	ret SUCCESS;
}

int main(int argc, char *argv[]){
	CPUTimer timer;
	double average = 0;
	int count = 0;
	if (argc == 3 && strcmp("--file",argv[1]) == 0){
		parser_whole_file(argv[2]);

		average = drop_all(8);
		printf("Averarge time for %s: %gs\n",argv[2],average );
		printf("Size in bits:%d amount: %d k used:8\n",bittage,num_elem );

		average = drop_all(4);
		printf("Averarge time for %s: %gs\n",argv[2],average );
		printf("Size in bits:%d amount: %d k used:4\n",bittage,num_elem );

	}
	else{
		puts("Wrong parameters! \nUsage: --file fileName");
	}
	return 0;
}