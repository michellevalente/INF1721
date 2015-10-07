#include <vector>       
#include <iostream>

class Object{
public:
	int peso, valor;
	double densidade;

	Object(int p, int v)
	{
		peso = p;
		valor = v;
		densidade = peso *1.0  / valor;
	}
	
} ;

bool object_compare(Object obj1, Object obj2) { return obj1.densidade > obj2.densidade; }

void kpfrac(Object obj[], int n, int W, double frac[])
{
	int current_w = 0, i;
	int peso = 0;
	std::sort(obj, obj+n, object_compare);

	for(i = 0; i < n ; i++)
		frac[i] = 0;
	
	i = 0;
	while(peso < W)
	{	
		if(peso + obj[i].peso <= W)
		{
			frac[i] = 1;
			peso += obj[i].peso;
		}
		else
		{
			frac[i] = (W - peso) *1.0 / obj[i].peso;
			peso = W;
		}
		i++;
	}
}

int main()
{
	Object obj1(3,1);
	Object obj2(1,2);
	Object obj3(4,2);
	Object obj[3] = {obj1,obj2,obj3};
	double frac[3];
	
	kpfrac(obj, 3, 5, frac);

	for(int i = 0 ; i < 3; i++)
		std::cout << frac[i] << std::endl;
}