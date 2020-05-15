// Please implement most of your source codes here. 
#include "BTree.h"

BTreeNode::BTreeNode(NodeType _type) {
	num_keys = 0;	
	parent = NULL;
	type = _type;
	for(int i=0; i<NUM_KEYS+1; i++) child[i] = NULL;		
}

NodeType BTreeNode::getNodeType() {
	return type;
}

string BTreeNode::getNodeType2() {
	if(type == ROOT) return "ROOT";
	else if(type == INTERNAL) return "INTERNAL";
	else if(type == LEAF) return "LEAF";
	return "ERROR";
}

bool BTreeNode::can_merge(BTreeNode* sibling) {
	if(sibling == NULL) return false;
	int sum_of_keys= num_keys + sibling->num_keys;
	if(sum_of_keys >= ceil(NUM_KEYS/2) && sum_of_keys <= NUM_KEYS)
		return true;
	return false;
}

bool BTreeNode::can_borrow(BTreeNode* sibling) {
	if(sibling==NULL) return false;
	if(sibling->type == LEAF) {
		// leaf
		int num_val = sibling->num_keys-1;
		if(num_val < ceil((float)(NUM_KEYS)/2)) return false;
	}
	else {
		// nonleaf
		int num_ptr = sibling->num_keys;
		if(num_ptr < ceil((float)(NUM_KEYS+1)/2)) return false;
	}
	return true;
}