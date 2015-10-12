#include <stdio.h>
#include <stdlib.h>
#include "CPUTimer.h"
#include <string.h>
#define INTBIT 32


#define index(bit_pos, base) ((int) bit_pos/base)
#define bit_select(value, bit_pos, base) (int)((value[index(bit_pos,base)]>>bit_pos%base) & 1)
#define bit_set(value,bit_pos,base, bit) value[index(bit_pos,base)] ^= (-bit ^value[index(bit_pos,base)]) & (1<<(bit_pos%base))

int ** parsed_data = NULL;
int bittage, num_elem; // base_num is used to memorize progress on finding the answer
char FILE_ERROR[] = "Couldn't read or open the file";

typedef enum{
	SUCCESS,
	ERROR,
	WARNING
} ret;




/********************************************
			compare
	@num bits array address
	@start bit position where starts
	@end bit position where end

 @Return >0 if @a greater than @b
 		  0 if equal
 		 <0 otherwise
********************************************/

int compare(int * a, int *b, int start, int end, int base){
	int bit_pos,sum;

	for(bit_pos=end-1;bit_pos>=start;bit_pos--){

		sum = bit_select(a,bit_pos,base) - bit_select(b,bit_pos,base);

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


/********************************************
			add
	@carry -1 or +1
	@num bits array address
	@start bit position where starts
	@end bit position where end

	@Return 0 if sum was ok,
			1 or -1 if there was  overflow 


********************************************/

int add (int carry, int *num, int start, int end, int base){
	int sum, bit_pos, temp_bit;

	bit_pos = start;
	
	do{
		temp_bit = bit_select(num,bit_pos,base);
		temp_bit += carry;
		switch(temp_bit){
			case -1:
				temp_bit = 0;
				carry = -1;
				break;
			case 0:
				carry = 0;
				break;
			case 1:
				carry = 0;
				break;
			case 2:
				temp_bit = 0;
				carry = 1;
				break;
			default:
				puts("Something went wrong on increment");
		}
		bit_set(num,bit_pos,base, temp_bit);
		bit_pos++;
	}while(carry != 0 && bit_pos < end );

	return carry;
}


void print(int *num, int size){
	int i;
	for(i=0;i<size;i++)
		printf(" _%x_",num[i]);
}

ret drop(int n_bits, int k, int * data ){

	int step,i, answer[8], start, end, result;

	 for(i =0; i<8;i++)
	 	answer[i]=0;

	 step = n_bits/k;
	 start = 0;
	 end = step;
	while(end <= n_bits){
		
		while(1){

			add(1,answer,start,end,INTBIT);
			result = compare(answer,data,start,end,INTBIT);

			if (result == 0 )
				break;
			else if (result >0){
				add(-1,answer,start,end,INTBIT);
				break;
			}
		}

		start +=step;
		end += step;
	}
	if( compare(answer,data,start,end,INTBIT)==0){

		#ifdef VERBOSE
			puts("the result is:");
			print(answer,n_bits/INTBIT);
			printf("\n");
		#endif
		return SUCCESS;
	}
	printf("ERROR _%d_\n", compare(answer,data,start,end,INTBIT));

	puts("the result was wrong:");
	print(answer,n_bits/INTBIT);
	printf("\n");
	return ERROR;

}
double drop_all(int k){
	int i = 0, count =0;
	double file_avg=0;
	CPUTimer timer;
	for (i=0; i<num_elem; i++){
		count = 0;
		timer.reset();
		while(timer.getCPUTotalSecs() < 1.0){
				timer.start();
				//printf("aaaa k:%d\n",k);
				drop(bittage,k,parsed_data[i]);
			//	printf("bbbb\n");
				timer.stop();
				count++;
			}
		file_avg += (timer.getCPUTotalSecs()/count)/num_elem;
	}
	return file_avg;
}

int main(int argc, char *argv[]){
	CPUTimer timer;
	double average = 0;
	int count = 0;
	if (argc == 3 && strcmp("--file",argv[1]) == 0){
		parser_whole_file(argv[2]);

		// average = drop_all(16);
		// printf("Averarge time for %s: %gs\n",argv[2],average );
		// printf("Size in bits:%d amount: %d k_used:16\n",bittage,num_elem );

		// average = drop_all(8);
		// printf("Averarge time for %s: %gs\n",argv[2],average );
		// printf("Size in bits:%d amount: %d k used:8\n",bittage,num_elem );

		// average = drop_all(4);
		// printf("Averarge time for %s: %gs\n",argv[2],average );
		// printf("Size in bits:%d amount: %d k used:4\n",bittage,num_elem );

		average = drop_all(2);
		printf("Averarge time for %s: %gs\n",argv[2],average );
		printf("Size in bits:%d amount: %d k used:2\n",bittage,num_elem );

		average = drop_all(1);
		printf("Averarge time for %s: %gs\n",argv[2],average );
		printf("Size in bits:%d amount: %d k used:1\n",bittage,num_elem );

	}
	else{
		puts("Wrong parameters! \nUsage: --file fileName");
	}
	return 0;
}