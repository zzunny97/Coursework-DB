#include <iostream>

using namespace std;

class A {
	public:
		virtual void print() = 0; 
};

class B : public A {
	public:
		virtual void print() {
			cout << "class b" << endl;
		}
};

class C: public A {
	public:
		virtual void print() {
			cout << "class c" << endl;
		}
};

int main() {

	b.print();
}
