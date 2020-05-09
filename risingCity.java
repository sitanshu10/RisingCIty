import java.util.*;
import java.io.*;
class Buildings{
  int buildingNum;
  int executed_time;
  int total_time;
  //The main Data Structure being used to store the Building number, Executed time, Total time
  // It is used in the minHeap
  public Buildings(int buildingNum, int executed_time, int total_time){
    this.buildingNum = buildingNum;
    this.executed_time = executed_time;
    this.total_time = total_time;
  }
}

class risingCity{
  public static void main(String args[]) throws IOException{
        //Global time initialised to 0
        int time = 0;
        //A queue of all the buildings that need to be inserted
        Queue<Buildings> q = new LinkedList<>();
        //The minHeap that decides which building will be worked on
        MinimumHeap minHeap = new MinimumHeap();
        //Red Black Tree which is used for printing the buildings
        RedBlackTree redblacktree = new RedBlackTree();
        //A temp variable is used to store values of buildings
        Buildings b;
        //creating a buffered reader to read from the file
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line = br.readLine(); //stores each line from the file one by one
        String[] contents = line.split("\\W"); //using regex stores the contents of the file
        File file = new File("output_file.txt");
        //new file writer to store the output
        FileWriter fr = new FileWriter(file);
        b = new Buildings(Integer.parseInt(contents[3]),0,Integer.parseInt(contents[4]));
        //inserts the first element into the minHeap
        minHeap.insert(b);
        redblacktree.functionAccess(1, b);
        time = 1;
        line = br.readLine();
        contents = line.split("\\W");
        //keeps running this loop till the file reaches an end
        while(line != null){
          // if the size becomes 0 empties the queue in the heap
          if(minHeap.size == 0){
            while(q.size() != 0){
              minHeap.insert(q.poll());
            }
            minHeap.minHeap();
          }
          //checks if the node has completed execution and removes it
          if(minHeap.size > 0 && minHeap.rootElement().executed_time == minHeap.rootElement().total_time){
              b = minHeap.rootElement();
              minHeap.remove();
              redblacktree.functionAccess(2,b); // remove from rbt as well
              fr.write("("+b.buildingNum+","+(time-1)+")\n");
          }
          //if the building is done with work for 5 days minHeapify
          //and empty the queue of pending inserts into the heap
          if(minHeap.size > 0 && minHeap.rootElement().executed_time % 5 == 0){
            while(q.size() != 0){
              minHeap.insert(q.poll());
            }
            minHeap.minHeap();
          }
          if(minHeap.size > 0){
            //increments the executed time of the root of the heap by 1
            minHeap.minHeap[1].executed_time++;
          }
          contents = line.split("\\W");
          //checks if the global time has reached the time of the next command in the file
          if(Integer.parseInt(contents[0]) == time){
            line = br.readLine(); // read and store next line
            //if the command is to print
            if(contents[2].substring(0,5).equals("Print")){
              if(contents.length == 4){
                //printing of one node
                fr.write(redblacktree.findAndPrint(Integer.parseInt(contents[3])) + "\n");
              } else {
                //printing of nodes between a range
                String w = redblacktree.printRange(Integer.parseInt(contents[3]),Integer.parseInt(contents[4]));
                fr.write(w.substring(0,w.length()-1) + "\n");
              }
            } else {
              //if the command is to insert
              b = new Buildings(Integer.parseInt(contents[3]),0,Integer.parseInt(contents[4]));
              redblacktree.functionAccess(1, b); //insert it in the rbt
              q.add(b); //add it in the queue
            }
          }
          time++;
        }
        time--;
        //if there are any pending nodes to be added they are added after the file is done reading
        while(q.size() != 0){
          minHeap.insert(q.poll());
        }
        minHeap.minHeap();
        //keep constructing and removing the top which completes executio0n
        while(minHeap.size > 0)
  			{
          //if exection is completed remove building
  				if(minHeap.rootElement().executed_time == minHeap.rootElement().total_time)
  				{
  					b = minHeap.rootElement();
            fr.write("("+b.buildingNum+","+time+")" + "\n");
  	        minHeap.remove();
            if(minHeap.size == 0){
              break;
            }
  	      }
          //if it is done executing for 5 days, minHeapify()
  				else if(minHeap.rootElement().executed_time % 5 == 0){
  					minHeap.minHeap();
  				}
  				time++;
  				minHeap.minHeap[1].executed_time++;
  			}
        //close the file
        fr.close();
      } //end of main function
}//end of risingCity

class MinimumHeap{
     Buildings[] minHeap;
     int maxSize = 2000; // initialised the max size possible
     int size;
     /*This is the Constructor that initialises
     min heap which will be used to store the buildings being executed*/
     public MinimumHeap(){
       this.size = 0;
       minHeap = new Buildings[maxSize+1];
       //Element at 0 has a garbage value and is never considered minheap starts from index 1
       minHeap[0] = new Buildings(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
     }
     //Returns the root of the Heap
     public Buildings rootElement(){
       return minHeap[1];
     }
     //Builds a minheap at the position entered
     public void minHeapify(int pos)
    {   //only performs for non leaf buildingas
        if (!isLeaf(pos)) {
            if(lChild(pos) <= size && rChild(pos) <= size){
              if (minHeap[pos].executed_time > minHeap[lChild(pos)].executed_time ||
                        minHeap[pos].executed_time > minHeap[rChild(pos)].executed_time) {
                  if (minHeap[lChild(pos)].executed_time < minHeap[rChild(pos)].executed_time) {
                      swap(pos, lChild(pos));
                      minHeapify(lChild(pos));
                  }
                  else if(lChild(pos) <= size && rChild(pos) <= size &&
                  minHeap[lChild(pos)].executed_time == minHeap[rChild(pos)].executed_time){
                    if(minHeap[lChild(pos)].buildingNum < minHeap[rChild(pos)].buildingNum){
                      swap(pos, lChild(pos));
                      minHeapify(lChild(pos));
                    }else{
                      swap(pos, rChild(pos));
                      minHeapify(rChild(pos));
                    }
                  }
                  else{
                      swap(pos, rChild(pos));
                      minHeapify(rChild(pos));
                  }
              }
              else if(minHeap[pos].executed_time == minHeap[rChild(pos)].executed_time
                     && minHeap[rChild(pos)].executed_time == minHeap[lChild(pos)].executed_time){
                       if(minHeap[rChild(pos)].buildingNum < minHeap[lChild(pos)].buildingNum
                              && minHeap[rChild(pos)].buildingNum < minHeap[pos].buildingNum){
                                swap(pos, rChild(pos));
                                minHeapify(rChild(pos));
                       }
                       else if(minHeap[lChild(pos)].buildingNum < minHeap[rChild(pos)].buildingNum
                              && minHeap[lChild(pos)].buildingNum < minHeap[pos].buildingNum){
                                swap(pos, lChild(pos));
                                minHeapify(lChild(pos));
                              }
                     }
              else if(minHeap[pos].executed_time == minHeap[lChild(pos)].executed_time){
                if (minHeap[lChild(pos)].buildingNum < minHeap[pos].buildingNum) {
                    swap(pos, lChild(pos));
                    minHeapify(lChild(pos));
                }
              }
              else if(minHeap[pos].executed_time == minHeap[rChild(pos)].executed_time){
                if (minHeap[rChild(pos)].buildingNum < minHeap[pos].buildingNum) {
                    swap(pos, rChild(pos));
                    minHeapify(rChild(pos));
                }
              }
            }
            else if(lChild(pos) <= size && rChild(pos) > size){
              if(minHeap[lChild(pos)].executed_time < minHeap[pos].executed_time){
                swap(pos, lChild(pos));
                minHeapify(lChild(pos));
              }else if(minHeap[lChild(pos)].executed_time == minHeap[pos].executed_time){
                if(minHeap[lChild(pos)].buildingNum < minHeap[pos].buildingNum){
                  swap(pos, lChild(pos));
                  minHeapify(lChild(pos));
                }
              }
            }
        }
    }
    //removes the first element and converts the tree back to a min heap
    public Buildings remove()
    {
        Buildings popped = minHeap[1];
        minHeap[1] = minHeap[size];
        minHeap[size] = null;
        size--;
        minHeapify(1);
        return popped;
    }
    //swaps the two Buildings at the two positions given as parameters
    private void swap(int fpos, int spos)
    {
        Buildings tmp;
        tmp = minHeap[fpos];
        minHeap[fpos] = minHeap[spos];
        minHeap[spos] = tmp;
    }
     //returns the p building of the position given as parameter
     public int parent(int p){
        return p/2;
     }
     //returns the left child of the position given as parameter
     public int lChild(int p){
       return 2*p;
     }
     //returns the left child of the position given as parameter
     public int rChild(int p){
       return (2*p)+1;
     }
     //Checks if the Building at the parameter position
     public boolean isLeaf(int pos)
     {
        if (pos > (size / 2) && pos <= size) {
            return true;
        }
        return false;
     }
     //Calls minHeapify on all the buildings that are not leafs
     public void minHeap()
    {
        for (int pos = (size / 2); pos >= 1; pos--) {
            minHeapify(pos);
        }
    }
    //Inserts a new Building into the minheap and then puts it in the right position
     public void insert(Buildings element){
       //Will not insert if the size has exceeded the limit
       if (size >= maxSize) {
            return;
        }
        //increment size and add element
        minHeap[++size] = element;
     }
} // End of minHeap
class RedBlackTree {

    int R = 0; //Red colour is value 0
    int B = 1; //Black colour is value 1

    //The node of the Red Black Tree
    class RedOrBlackNode {

        Buildings value;
        int shade = B;
        RedOrBlackNode left = garbage, right = garbage, p = garbage;
        RedOrBlackNode(Buildings value) {
            this.value = value;
        }
    }
    //A garbage value used for detecting when you fall off the tree
    RedOrBlackNode garbage = new RedOrBlackNode(new Buildings(-1,-1,-1));
    //initaise the root as garbage
    RedOrBlackNode root = garbage;
    //finds specific node in the tree and returns it
    public String findAndPrint(int b){
      RedOrBlackNode temp = root; //begin the search at root
      //recurse untill you find the value or fall off the tree
      while(temp != garbage){
        if(temp.value.buildingNum == b){
          //the building was found
          return "("+b+","+temp.value.executed_time+","+temp.value.total_time+")";
        }else if(temp.value.buildingNum > b){
          //value is lesser so check left subtree
          temp = temp.left;
        }
        else{
          //value is more then check right subtree
          temp = temp.right;
        }
      }
      return "(0,0,0)";
    }
    //inserts a new node in the Red Black Tree
    public void insert(RedOrBlackNode node) {
        //starts at the root node
        RedOrBlackNode temp = root;
        if (root == garbage) {
            //if the tree is empty initialise the element as the root
            root = node;
            node.shade = B; //root will be black
            node.p = garbage;
        } else {
            //before inserting a node make it red
            node.shade = R;
            while (true) {
                if (node.value.buildingNum < temp.value.buildingNum) {
                    //if building number is lesser than root go in left subtree
                    if (temp.left == garbage) {
                        //if left is garbage initalise the left node as new node
                        temp.left = node;
                        node.p = temp;
                        break;
                    } else {
                        //recurse in left if it is not empty
                        temp = temp.left;
                    }
                    //do the same for right subtree
                } else if (node.value.buildingNum > temp.value.buildingNum) {
                    if (temp.right == garbage) {
                        temp.right = node;
                        node.p = temp;
                        break;
                    } else {
                        temp = temp.right;
                    }
                }
            }
            correctRbt(node);
        }
    }
    //finds whether a particular node is present in the red black tree
    public RedOrBlackNode findRedOrBlackNode(RedOrBlackNode findRedOrBlackNode, RedOrBlackNode node) {
        if (root == garbage) {
            return null;
        }
        //if the value is less recurse in the left subtree
        if (findRedOrBlackNode.value.buildingNum < node.value.buildingNum) {
            if (node.left != garbage) {
                return findRedOrBlackNode(findRedOrBlackNode, node.left);
            }
        //if the value is more recurse in the right subtree
        } else if (findRedOrBlackNode.value.buildingNum > node.value.buildingNum) {
            if (node.right != garbage) {
                return findRedOrBlackNode(findRedOrBlackNode, node.right);
            }
        //value found
        } else if (findRedOrBlackNode.value.buildingNum == node.value.buildingNum) {
            return node;
        }
        //return if not found
        return null;
    }


    public void correctRbt(RedOrBlackNode node) {
        //keep running the loop till p is red since two consecutive nodes are not red
        while (node.p.shade == R) {
            RedOrBlackNode aunt = garbage;
            if (node.p == node.p.p.left) {
                aunt = node.p.p.right;
                //checks if aunt node is not null and is Red
                //perform the necessary change in colour
                if (aunt != garbage && aunt.shade == R) {
                    node.p.shade = B;
                    aunt.shade = B;
                    node.p.p.shade = R;
                    node = node.p.p;
                    continue;
                }
                //checks if the sibling of the node
                // performs the necessary rotations
                if (node == node.p.right) {
                    node = node.p;
                    rleft(node);
                }
                node.p.shade = B;
                node.p.p.shade = R;
                rRight(node.p.p);
            } else {
              //checks if aunt node is not null and is Red
              //perform the necessary change in colour
                aunt = node.p.p.left;
                 if (aunt != garbage && aunt.shade == R) {
                    node.p.shade = B;
                    aunt.shade = B;
                    node.p.p.shade = R;
                    node = node.p.p;
                    continue;
                }
                //if node is on the left of the parent
                if (node == node.p.left) {
                    node = node.p;
                    rRight(node);
                }
                //set nodes parent as black and grandparent as red and rotate left
                node.p.shade = B;
                node.p.p.shade = R;
                rleft(node.p.p);
            }
        }
        //root will always be black
        root.shade = B;
    }
    //rotate the whole subtree/tree at the node to its left
    public void rleft(RedOrBlackNode node) {
        // checks if the node is the root
        if (node.p != garbage) {
           //if node is in the left of the parent
            if (node == node.p.left) {
                node.p.left = node.right;
            } else {
                //if node is in the right of the parent
                node.p.right = node.right;
            }
            node.right.p = node.p;
            node.p = node.right;
            if (node.right.left != garbage) {
                node.right.left.p = node;
            }
            node.right = node.right.left;
            node.p.left = node;
        } else {
            //this is when the rotation is at the root
            RedOrBlackNode right = root.right;
            root.right = right.left;
            right.left.p = root;
            root.p = right;
            right.left = root;
            right.p = garbage;
            root = right;
        }
    }
    //rotate the whole subtree/tree at the node to its right
    public void rRight(RedOrBlackNode node) {
        // checks if the node is the root
        if (node.p != garbage) {
            //if node is in the left of the parent
            if (node == node.p.left) {
                node.p.left = node.left;
            } else {
                //if node is in the right of the parent
                node.p.right = node.left;
            }
            node.left.p = node.p;
            node.p = node.left;
            if (node.left.right != garbage) {
                node.left.right.p = node;
            }
            node.left = node.left.right;
            node.p.right = node;
        } else {
            //rotations is at the left of root
            RedOrBlackNode left = root.left;
            root.left = root.left.right;
            left.right.p = root;
            root.p = left;
            left.right = root;
            left.p = garbage;
            root = left;
        }
    }
    //checks and prints the nodes in the given range
    public String printRange(int i,int j)
 	 {
    //starts the check from root
 		RedOrBlackNode flow = root;
 		String s= "(0,0,0),";
    //checks the range till it fallse off the tree
 		while(flow!=garbage){
 			if(flow.value.buildingNum>=i && flow.value.buildingNum<=j){
 				s = printRBTinRange(flow,i,j);
 				return s;
 			}
      //check in the left tree if the buildernumber is less than the range
 			else if(flow.value.buildingNum > i){
 				flow=flow.left;
 			}
      //check in the right tree if the buildernumber is greater than the range
 			else{
 				flow=flow.right;
 			}
 		}
 		return s;
 	}

 	public String printRBTinRange(RedOrBlackNode flow,int b1,int b2){
 		if(flow== garbage ) return "";
    //print b1 since its found
 		if(flow.value.buildingNum == b1){
 			return(printRBTinRange(flow.right,b1,b2)+"("+flow.value.buildingNum+","+ flow.value.executed_time+","+ flow.value.total_time+"),");
 		}
    //print b2 since its found
 		else if(flow.value.buildingNum == b2){
 			return(printRBTinRange(flow.left,b1,b2)+"("+flow.value.buildingNum+","+ flow.value.executed_time+","+ flow.value.total_time+"),");
 		}
 		else
 		{
 			String ans = "";
      //check if the left of the tree is not garbage and recurse
 			if(flow.left!= garbage && inRange(flow.left.value.buildingNum, b1,b2)){
 				ans += printRBTinRange(flow.left,b1,b2);
 			}
      if(inRange(flow.value.buildingNum,b1,b2)){
        ans += "("+flow.value.buildingNum+","+ flow.value.executed_time+","+ flow.value.total_time+"),";
      }
      //check if the right of the tree is not garbage and recurse
 			if(flow.right!= garbage && inRange(flow.right.value.buildingNum,b1,b2))
 				ans += printRBTinRange(flow.right,b1,b2);
 			return ans;
 		}
 	}

 	public boolean inRange(int target,int l,int h){
 		if(target >= l && target <= h)
 			return true;
 		return false;
 	}

    //Deletion Code .

    //This operation doesn't care about the new RedOrBlackNode's connections
    //with previous node's left and right. The caller has to take care
    //of that.
    public void deleteAssist(RedOrBlackNode target, RedOrBlackNode with){
          if(target.p == garbage){
              root = with;
          }else if(target == target.p.left){
              target.p.left = with;
          }else
              target.p.right = with;
          with.p = target.p;
    }
    //deletes the node passed as a parameter
    public boolean delete(RedOrBlackNode target){
        if((target = findRedOrBlackNode(target, root))==null)return false;
        RedOrBlackNode i;
        RedOrBlackNode j = target; // temporary reference y
        int j_original_shade = j.shade;
        //if the left of the node to be deleted is garbage then the right node becomes the node
        if(target.left == garbage){
            i = target.right;
            deleteAssist(target, target.right);
        }else if(target.right == garbage){
            //if the right of the node to be deleted is garbage then the left node becomes the node
            i = target.left;
            deleteAssist(target, target.left);
        }else{
            //minimum value along the right subtree is found and made the "root"
            RedOrBlackNode temp = target.right;
            while(temp.left != garbage){
              temp = temp.left;
            }
            j = temp;
            j_original_shade = j.shade;
            i = j.right;
            if(j.p == target)
                i.p = j;
            else{
                deleteAssist(j, j.right);
                j.right = target.right;
                j.right.p = j;
            }
            deleteAssist(target, j);
            j.left = target.left;
            j.left.p = j;
            j.shade = target.shade;
        }
        if(j_original_shade == B)
            deleteFixup(i);
        return true;
    }
    //performs the necessary changes in colour of the tree
    public void deleteFixup(RedOrBlackNode x){
        while(x!=root && x.shade == B){
            if(x == x.p.left){
                RedOrBlackNode w = x.p.right;
                if(w.shade == R){
                    w.shade = B;
                    x.p.shade = R;
                    rleft(x.p);
                    w = x.p.right;
                }
                if(w.left.shade == B && w.right.shade == B){
                    w.shade = R;
                    x = x.p;
                    continue;
                }
                else if(w.right.shade == B){
                    w.left.shade = B;
                    w.shade = R;
                    rRight(w);
                    w = x.p.right;
                }
                if(w.right.shade == R){
                    w.shade = x.p.shade;
                    x.p.shade = B;
                    w.right.shade = B;
                    rleft(x.p);
                    x = root;
                }
            }else{
                RedOrBlackNode w = x.p.left;
                if(w.shade == R){
                    w.shade = B;
                    x.p.shade = R;
                    rRight(x.p);
                    w = x.p.left;
                }
                if(w.right.shade == B && w.left.shade == B){
                    w.shade = R;
                    x = x.p;
                    continue;
                }
                else if(w.left.shade == B){
                    w.right.shade = B;
                    w.shade = R;
                    rleft(w);
                    w = x.p.left;
                }
                if(w.left.shade == R){
                    w.shade = x.p.shade;
                    x.p.shade = B;
                    w.left.shade = B;
                    rRight(x.p);
                    x = root;
                }
            }
        }
        x.shade = B;
    }
    //Chooses what function needs to be performed on the Red Black Tree
    public void functionAccess(int choice,Buildings b) {
        RedOrBlackNode node;
        switch (choice) {
            //inserts a node into the Red Black Tree
            case 1:node = new RedOrBlackNode(b);
                   insert(node);
                   break;
            //deletes a node from the Red Black Tree
            case 2:node = new RedOrBlackNode(b);
                   delete(node);
                   break;
        }
    }

}//end of rbt
