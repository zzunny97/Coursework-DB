//added
#include <iostream>
#include <vector>
#include <cmath>
using namespace std;

// Note: You need to add additional member variables and functions as you need.

#define NUM_KEYS 10 
// NUM_KEYS should be set to make each tree node as large as 4KB. 
// But for this assignment, we will set the degree of node to 10 just to make debugging and grading easy
// Please do not change this number. 
// In practice, DBMS employs 'slotted page structure' to store variable-length records in B+tree.
// But again, the purpose of this assignment is to make students familiar with B-tree itself. 

enum NodeType {
	ROOT,
	INTERNAL,
	LEAF
};

class BTreeNode{   
   protected:
	BTreeNode* parent;
	NodeType type;
	int num_keys;
   public:
	//added 
	virtual BTreeNode* search(long long value) = 0;
	//virtual void goThrough(long long low, long long high) = 0;
	//virtual void insert(long long value) = 0;

	BTreeNode() {
		cout << "root class constructor" << endl;
		num_keys = 0;
		parent = NULL;
	}
	int getNum() { return num_keys; }

	virtual ~BTreeNode() {}
	NodeType getNodeType() {
		return type;
	}
	void setNodeType(NodeType _type) {
		type = _type;
	}
};

class BTreeInternalNode:public BTreeNode{
   private:
	long long keys[NUM_KEYS];
	BTreeNode* child[NUM_KEYS+1];
   public:
	//added
	virtual BTreeNode* search(long long value) {
		cout << "Internal class search" << endl;
		int i;
		for(i=0; i<num_keys; i++) {
			if(value<=keys[i])
				break;
		}
		if(i == NUM_KEYS) {
			int j;
			for(int j=0; j<NUM_KEYS+1; j++) {
				if(child[j] == NULL)
					break;
			}
			return child[j-1];
		}
		else if(value == keys[i]) {
			return child[i+1];
		}
		else { // value < keys[i]
			return child[i];
		}
	}
	
	BTreeInternalNode() {
		type = INTERNAL;
		for(int i=0; i<NUM_KEYS+1; i++) child[i] = NULL;
	}
	~BTreeInternalNode() {}
};

class BTreeLeafNode:public BTreeNode{
   private:
	long long keys[NUM_KEYS];
	BTreeLeafNode* right_sibling;
   public:
	//added
	virtual BTreeNode* search(long long value) {
		cout << "Leaf class search" << endl;
		for(int i=0; i<NUM_KEYS; i++) {
			if(value == keys[i]) return this; 
		}
		return NULL;
	}

	//virtual void goThrough(long long low, long long high) {
	void goThrough(long long low, long long high) {
		int i;
		for(i=0; i<NUM_KEYS; i++) {
			if(keys[i] >= low) break;
		}
		if(i==NUM_KEYS) {
			i = 1 + num_keys;
		}
		bool done = false;
		BTreeLeafNode* cur = this;
		while(!done) {
			int n = cur->num_keys;
			if(i<=n && cur->keys[i] <= high) {
				cout << keys[i] << ",";
				++i;
			}
			else if(i<=n && cur->keys[i] > high) {
				done = true;
			}
			else if(i>n && cur->right_sibling != NULL) {
				cur = cur->right_sibling;
				i=1;
			}
			else {
				done = true;
			}
		}
		cout << endl;
	}

	/*
	void insert_in_leaf(BTreeLeafNode* node, long long value) {
		for(int i=0; i<node->num_keys; i++) {
			if(value < node->keys[i]) {
				for(int j=node->num_keys-1; j>=i; j--) {
					node->keys[j+1] = node->keys[j];	
				}
				node->keys[i] = value;
				++(node->num_keys);
			}
		}
	}

	void insert_in_parent(BTreeLeafNode* n1, long long value, BTreeLeafNode* n2) {
		if(n1->getNodeType() == ROOT) {
			BTreeNode* newroot = new BTreeInternalNode;	
			
			newroot->setNodeType(ROOT);
			n1->setNodeType(INTERNAL);
		}
	}

	virtual void insert(long long value) {
		if(num_keys < NUM_KEYS-1) {
			insert_in_leaf(this, value);
		}
		else {
			BTreeNode* newnode = new BTreeNode;
			BTreeLeafNode tmp;
			for(int i=0; i<num_keys; i++) insert_in_leaf(&tmp, keys[i]);	
			insert_in_leaf(&tmp, value);
			newnode->right_sibling = right_sibling;
			num_keys = 0;
			for(int i=0; i<tmp.num_keys; i++) {
				if(i < ceil((float)NUM_KEYS/2)) {
					keys[num_keys++] = tmp.keys[i];
				}
				else {
					newnode->keys[newnode->num_keys++] = tmp.keys[i];
				}
			}
			insert_in_parent(this, newnode->key[0], newnode);
		}
	}*/

	BTreeLeafNode() {
		cout << "Leaf constructor" << endl;
		type = LEAF;
		right_sibling = NULL;
	}

	~BTreeLeafNode() {}

	void printLeafNode() {
		for(int i=0; i<NUM_KEYS; i++)
			cout << keys[i] << ",";
		cout << endl;
	}// print all keys in the current leaf node, separated by comma.
};


class BTree{  
    private:
	BTreeNode *root;
    public:
	// You are not allowed to change the interfaces of these public methods.
	BTree() {
		root = new BTreeLeafNode;
		root->setNodeType(LEAF);
	}
	
	~BTree() {}
	
	void insert(long long value) {
		if(root->getNodeType() == LEAF) {

		}
		else {

		}
		/*
		// find the leaf node L that should contain key value K
		BTreeNode* cur = root;
		while(cur->getNodeType()!=LEAF) {
			cur = cur->search(value);
		}
		if(cur->search(value)!= NULL) return;
		else {
			cur->insert(value);
		}
		*/
	}
	
	void remove(long long value) {}
	
	void printLeafNode(long long value) {
		BTreeNode* cur = root;
		while(cur->getNodeType()!=LEAF) {
			cur = cur->search(value);
		}
		if(cur->search(value)!=NULL) {
			BTreeLeafNode* cur2 = static_cast<BTreeLeafNode*>(cur);
			cur2->printLeafNode();
		} 
	} // find the leaf node that contains 'value' and print all values in the leaf node.

	void pointQuery(long long value) {
		BTreeNode* cur = root;
		while(cur->getNodeType()!=LEAF) {
			cur = cur->search(value);
		}
		if(cur->search(value)!=NULL) {
			cout << value << endl;
		} else {
			cout << "NOT FOUND" << endl;
		}
	} // print the found value or "NOT FOUND" if there is no value in the index
	
	void rangeQuery(long long low, long long high) {
		BTreeNode* cur = root;
		while(cur->getNodeType()!=LEAF) {
			cur = cur->search(low);
		}
		//cur->goThrough(low, high);
	}
	// print all found keys (low <= keys < high), separated by comma (e.g., 10, 11, 13, 15\n) 
};

