#include "Object.h"

Object::Object() {
    weight = value = elem = 0;
    frequency = 0.0;
}

Object::Object(int p, int v, int n) {
    weight = p;
    value = v;
    elem = n;
    density = value * 1.0  / weight;
    frequency = 0.0;
}

Object::Object(const Object &obj ){
    weight = obj.weight;
    value = obj.value;
    elem = obj.elem;
    density =  obj.density;
    frequency = obj.frequency;
}

bool operator< (const Object& lhs, const Object& rhs){
	return lhs.density < rhs.density; 
}

bool operator> (const Object& lhs, const Object& rhs){
	return rhs < lhs;
}

bool operator<=(const Object& lhs, const Object& rhs){
	return !(lhs > rhs);
}

bool operator>=(const Object& lhs, const Object& rhs){
	return !(lhs < rhs);
}

bool operator==(const Object& lhs, const Object& rhs){
	return lhs.density == rhs.density;
}

bool operator!=(const Object& lhs, const Object& rhs){
	return !(lhs == rhs);
}

const Object& Object::operator=(const Object& obj){
    weight = obj.weight;
    value = obj.value;
    elem = obj.elem;
    density =  obj.density;
    frequency = obj.frequency;

    return *this;
}


bool object_compare (Object obj1, Object obj2) { 
    return obj1.density > obj2.density; 
}
