#include <vector>       
#include <iostream>
#include <fstream>

class Object{
public:
	int peso, valor, elem;
	double densidade;

	Object(int p, int v, int n)
	{
		peso = p;
		valor = v;
		elem = n;
		densidade = valor *1.0  / peso;
	}

	Object(){
		peso = valor = densidade = 0;
	}
	
} ;

bool object_compare(Object obj1, Object obj2) { return obj1.densidade > obj2.densidade; }

Object * objetos = NULL;
int W;
int num_elem;

void parser(std::string fileName)
{
	int valor, peso, num;
	std::ifstream infile(fileName);

	infile >> num_elem;

	objetos = new Object[num_elem];

	for(int i = 0 ; i < num_elem; i++)
	{
		infile >> num >> valor >> peso;
		Object obj(peso, valor, num);
		objetos[i] = obj;
	}
	infile >> W;

}

void kpfrac(Object obj[])
{
	int current_w = 0, i;
	int peso = 0;
	double frac[num_elem];
	std::sort(obj, obj+num_elem, object_compare);

	for(i = 0; i < num_elem ; i++)
		frac[i] = 0;
	
	i = 0;
	while(peso < W)
	{	
		if(peso + obj[i].peso <= W)
		{
			std::cout << "Elemento colocado na mochila: " << obj[i].elem << std::endl;
			frac[i] = 1;
			peso += obj[i].peso;
		}
		else
		{
			std::cout << "Elemento colocado parcialmente na mochila: " <<  obj[i].elem << std::endl;

			frac[i] = (W - peso) *1.0 / obj[i].peso;
			peso = W;
		}
		i++;
	}
}

int main()
{
	parser("dummy.in");

	kpfrac(objetos);

	
}