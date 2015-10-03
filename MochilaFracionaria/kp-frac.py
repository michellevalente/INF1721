KEY = 0

def mergesort(v):

	if(len(v) == 1):
		return v

	else:
		mid = len(v)/2
		left = mergesort(v[:mid])
		right = mergesort(v[mid:])
		res = []
		i = 0
		j = 0

		while(len(res) < len(right) + len(left)):          
			if left[i] < right[j]:  
				res.append(left[i])  
				i += 1  

			else:  
				res.append(right[j])  
				j += 1         

			if i == len(left) or j == len(right):              
				res.extend(left[i:] or right[j:])  
				break  

	return res

def throw(step):
"""
	Encapsulates the answer 
"""
	if step >= KEY:
		return True
	else:
		return False

def simpleSolve(start = 0):
	while throw(start) == False:
		start += 1
	return start