begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Page
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|PageFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Transaction
operator|.
name|Closure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|Marshaller
import|;
end_import

begin_comment
comment|/**  * BTreeIndex represents a Variable Magnitude B+Tree in a Page File.  * A BTree is a bit flexible in that it can be used for set or  * map-based indexing.  Leaf nodes are linked together for faster  * iteration of the values.   *  *<br>  * The Variable Magnitude attribute means that the BTree attempts  * to store as many values and pointers on one page as is possible.  *   *<br>  * The implementation can optionally a be Simple-Prefix B+Tree.  *   *<br>  * For those who don't know how a Simple-Prefix B+Tree works, the primary  * distinction is that instead of promoting actual keys to branch pages,  * when leaves are split, a shortest-possible separator is generated at  * the pivot.  That separator is what is promoted to the parent branch  * (and continuing up the list).  As a result, actual keys and pointers  * can only be found at the leaf level.  This also affords the index the  * ability to ignore costly merging and redistribution of pages when  * deletions occur.  Deletions only affect leaf pages in this  * implementation, and so it is entirely possible for a leaf page to be  * completely empty after all of its keys have been removed.  *  * @version $Revision$, $Date$  */
end_comment

begin_class
specifier|public
class|class
name|BTreeIndex
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
implements|implements
name|Index
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BTreeIndex
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Interface used to determine the simple prefix of two keys.      *      * @version $Revision$, $Date$      */
specifier|static
specifier|public
interface|interface
name|Prefixer
parameter_list|<
name|Key
parameter_list|>
block|{
comment|/**          * This methods should return shortest prefix of value2 where the following still holds:<br/>          * value1<= prefix<= value2.<br/><br/>          *           * When this method is called, the following is guaranteed:<br/>          * value1< value2<br/><br/>          *           *           * @param value1          * @param value2          * @return          */
specifier|public
name|Key
name|getSimplePrefix
parameter_list|(
name|Key
name|value1
parameter_list|,
name|Key
name|value2
parameter_list|)
function_decl|;
block|}
comment|/**      * StringPrefixer is a Prefixer implementation that works on strings.      */
specifier|static
specifier|public
class|class
name|StringPrefixer
implements|implements
name|Prefixer
argument_list|<
name|String
argument_list|>
block|{
comment|/**          * Example:          * If value1 is "Hello World"          * and value 2 is "Help Me"          * then the result will be: "Help"          *           * @see  Prefixer#getSimplePrefix          */
specifier|public
name|String
name|getSimplePrefix
parameter_list|(
name|String
name|value1
parameter_list|,
name|String
name|value2
parameter_list|)
block|{
name|char
index|[]
name|c1
init|=
name|value1
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|c2
init|=
name|value2
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|c1
operator|.
name|length
argument_list|,
name|c2
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|n
condition|)
block|{
if|if
condition|(
name|c1
index|[
name|i
index|]
operator|!=
name|c2
index|[
name|i
index|]
condition|)
block|{
return|return
name|value2
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|==
name|c2
operator|.
name|length
condition|)
block|{
return|return
name|value2
return|;
block|}
return|return
name|value2
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|PageFile
name|pageFile
decl_stmt|;
specifier|private
specifier|final
name|long
name|pageId
decl_stmt|;
specifier|private
name|AtomicBoolean
name|loaded
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BTreeNode
operator|.
name|Marshaller
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|marshaller
init|=
operator|new
name|BTreeNode
operator|.
name|Marshaller
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|keyMarshaller
decl_stmt|;
specifier|private
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|valueMarshaller
decl_stmt|;
specifier|private
name|Prefixer
argument_list|<
name|Key
argument_list|>
name|prefixer
decl_stmt|;
specifier|public
name|BTreeIndex
parameter_list|(
name|PageFile
name|pageFile
parameter_list|,
name|long
name|rootPageId
parameter_list|)
block|{
name|this
operator|.
name|pageFile
operator|=
name|pageFile
expr_stmt|;
name|this
operator|.
name|pageId
operator|=
name|rootPageId
expr_stmt|;
block|}
specifier|public
name|BTreeIndex
parameter_list|(
name|PageFile
name|pageFile
parameter_list|,
name|Page
name|page
parameter_list|)
block|{
name|this
argument_list|(
name|pageFile
argument_list|,
name|page
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|load
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|loaded
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading"
argument_list|)
expr_stmt|;
if|if
condition|(
name|keyMarshaller
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The key marshaller must be set before loading the BTreeIndex"
argument_list|)
throw|;
block|}
if|if
condition|(
name|valueMarshaller
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The value marshaller must be set before loading the BTreeIndex"
argument_list|)
throw|;
block|}
specifier|final
name|Page
argument_list|<
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|p
init|=
name|tx
operator|.
name|load
argument_list|(
name|pageId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Page
operator|.
name|PAGE_FREE_TYPE
condition|)
block|{
comment|// Need to initialize it..
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|root
init|=
name|createNode
argument_list|(
name|p
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|storeNode
argument_list|(
name|tx
argument_list|,
name|root
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|synchronized
specifier|public
name|void
name|unload
parameter_list|(
name|Transaction
name|tx
parameter_list|)
block|{
if|if
condition|(
name|loaded
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{         }
block|}
specifier|private
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getRoot
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|loadNode
argument_list|(
name|tx
argument_list|,
name|pageId
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|contains
argument_list|(
name|tx
argument_list|,
name|key
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Value
name|get
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|get
argument_list|(
name|tx
argument_list|,
name|key
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Value
name|put
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|put
argument_list|(
name|tx
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Value
name|remove
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|remove
argument_list|(
name|tx
argument_list|,
name|key
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|synchronized
specifier|public
name|void
name|clear
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|clear
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|int
name|getMinLeafDepth
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|getMinLeafDepth
argument_list|(
name|tx
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|int
name|getMaxLeafDepth
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|getMaxLeafDepth
argument_list|(
name|tx
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|void
name|printStructure
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|printStructure
argument_list|(
name|tx
argument_list|,
name|out
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|printStructure
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|out
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|printStructure
argument_list|(
name|tx
argument_list|,
name|pw
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|iterator
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|,
name|Key
name|initialKey
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|iterator
argument_list|(
name|tx
argument_list|,
name|initialKey
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|void
name|visit
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|BTreeVisitor
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|visit
argument_list|(
name|tx
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getFirst
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|getFirst
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getLast
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRoot
argument_list|(
name|tx
argument_list|)
operator|.
name|getLast
argument_list|(
name|tx
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Internal implementation methods
comment|///////////////////////////////////////////////////////////////////
specifier|private
name|void
name|assertLoaded
parameter_list|()
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
operator|!
name|loaded
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The BTreeIndex is not loaded"
argument_list|)
throw|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Internal methods made accessible to BTreeNode
comment|///////////////////////////////////////////////////////////////////
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|loadNode
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|long
name|pageId
parameter_list|,
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|Page
argument_list|<
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|page
init|=
name|tx
operator|.
name|load
argument_list|(
name|pageId
argument_list|,
name|marshaller
argument_list|)
decl_stmt|;
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
init|=
name|page
operator|.
name|get
argument_list|()
decl_stmt|;
name|node
operator|.
name|setPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|node
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|createNode
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|Page
argument_list|<
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|p
init|=
name|tx
operator|.
name|allocate
argument_list|()
decl_stmt|;
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
init|=
operator|new
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|node
operator|.
name|setPage
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|node
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|node
operator|.
name|setEmpty
argument_list|()
expr_stmt|;
name|p
operator|.
name|set
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|createNode
parameter_list|(
name|Page
argument_list|<
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|p
parameter_list|,
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
init|=
operator|new
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|node
operator|.
name|setPage
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|node
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|node
operator|.
name|setEmpty
argument_list|()
expr_stmt|;
name|p
operator|.
name|set
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
name|void
name|storeNode
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|BTreeNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
parameter_list|,
name|boolean
name|overflow
parameter_list|)
throws|throws
name|IOException
block|{
name|tx
operator|.
name|store
argument_list|(
name|node
operator|.
name|getPage
argument_list|()
argument_list|,
name|marshaller
argument_list|,
name|overflow
argument_list|)
expr_stmt|;
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Property Accessors
comment|///////////////////////////////////////////////////////////////////
specifier|public
name|PageFile
name|getPageFile
parameter_list|()
block|{
return|return
name|pageFile
return|;
block|}
specifier|public
name|long
name|getPageId
parameter_list|()
block|{
return|return
name|pageId
return|;
block|}
specifier|public
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|getKeyMarshaller
parameter_list|()
block|{
return|return
name|keyMarshaller
return|;
block|}
specifier|public
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|keyMarshaller
parameter_list|)
block|{
name|this
operator|.
name|keyMarshaller
operator|=
name|keyMarshaller
expr_stmt|;
block|}
specifier|public
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|getValueMarshaller
parameter_list|()
block|{
return|return
name|valueMarshaller
return|;
block|}
specifier|public
name|void
name|setValueMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|valueMarshaller
parameter_list|)
block|{
name|this
operator|.
name|valueMarshaller
operator|=
name|valueMarshaller
expr_stmt|;
block|}
specifier|public
name|Prefixer
argument_list|<
name|Key
argument_list|>
name|getPrefixer
parameter_list|()
block|{
return|return
name|prefixer
return|;
block|}
specifier|public
name|void
name|setPrefixer
parameter_list|(
name|Prefixer
argument_list|<
name|Key
argument_list|>
name|prefixer
parameter_list|)
block|{
name|this
operator|.
name|prefixer
operator|=
name|prefixer
expr_stmt|;
block|}
block|}
end_class

end_unit

