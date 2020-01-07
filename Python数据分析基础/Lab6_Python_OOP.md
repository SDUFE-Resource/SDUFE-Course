### 编写类,实现二叉树,并实现二叉树的相关方法(包括但不限于以下方法)。
+ a) 判断二叉树是否为空
+ b) 结点插入和删除
+ c) 返回二叉树的长度(结点个数)

```python
#Leave
class Tree_Node:
    def __init__(self,value,index):
        self.value=value
        self.index=index
        self.right=None
        self.left=None

#Binary Tree
class Binary_Tree:
    def __init__(self, leaves):
        self.leaves=leaves
        self.head=Tree_Node(leaves[0],1)
        self.height=1
        self.node_num=len(leaves)
        for i in range(1,len(self.leaves)):
            temp=Tree_Node(leaves[i],i+1)
            original=int((i+2)/2)
            bias=i+1-original




x=input("Please input the leaves of binary tree with list format:")
x=eval(x)
print(x)
c1=Binary_Tree(x)
```
### 实现有序二叉树。并构造数据,利用有序二叉树存储,并分别按照前序、中序和后序打印输出二叉树。
```python

```
### 实现完全二叉树。构造数据,利用完全二叉树存储,并分别按照深度优先和广度优先打印输出二叉树。
```python

```
