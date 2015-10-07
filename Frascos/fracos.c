#include <stdio.h>
#include <stdlib.h>

int ** parsed_data = NULL;
#define INTBIT 32
char* FILE_ERROR = "Couldn't read or open the file";

typedef enum{
	SUCCESS,
	ERROR,
	WARNING
} ret;

ret std;

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
	int bittage, num_elem;
	int i,j,k; 
	FILE * data_file = fopen(fileName,"r");

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
			
			printf("%d_%X ",parsed_data[i][j],parsed_data[i][j] );

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
		printf("_%c\n", buffer[0] );
	}

	return SUCCESS;
}


int main(){

	parser_whole_file("bignum_32_02.dat");

	return 0;
}