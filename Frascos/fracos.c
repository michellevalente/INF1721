#include <stdio.h>
#include <stdlib.h>

int ** parsed_data = NULL;

char* FILE_ERROR = "Couldn't read or open the file";

typedef enum{
	SUCCESS,
	ERROR,
	WARNING
} ret;

ret std;


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
		for(j=0; j<bittage/8; j++)
			parsed_data[i][j]=0;
	}

	for(i=0;i<num_elem;i++){

		for(j=0;j<bittage/8;j++){

			for(k=0;k<8;k++){

				if(fscanf(data_file," %c",&buffer[0]) != 1){
					puts(FILE_ERROR);
					return ERROR;
				}
				if(i==0){
					printf("%c=%d ",buffer[0],((int)(buffer[0] - '0')) );
				}
				parsed_data[i][j] += ((int)(buffer[0] - '0'));
				parsed_data[i][j] = parsed_data[i][j] << (k < 7 ? 1 : 0);

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