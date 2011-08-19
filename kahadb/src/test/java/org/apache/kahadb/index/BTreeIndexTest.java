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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|List
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
name|Random
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
name|LongMarshaller
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
name|StringMarshaller
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
name|VariableMarshaller
import|;
end_import

begin_class
specifier|public
class|class
name|BTreeIndexTest
extends|extends
name|IndexTestSupport
block|{
specifier|private
name|NumberFormat
name|nf
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|nf
operator|=
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|()
expr_stmt|;
name|nf
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|createIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|id
init|=
name|tx
operator|.
name|allocate
argument_list|()
operator|.
name|getPageId
argument_list|()
decl_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
operator|new
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|(
name|pf
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|index
operator|.
name|setKeyMarshaller
argument_list|(
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|index
operator|.
name|setValueMarshaller
argument_list|(
name|LongMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
return|return
name|index
return|;
block|}
comment|/**      * Yeah, the current implementation does NOT try to balance the tree.  Here is       * a test case showing that it gets out of balance.        *       * @throws Exception      */
specifier|public
name|void
name|disabled_testTreeBalancing
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|BTreeIndex
name|index
init|=
operator|(
operator|(
name|BTreeIndex
operator|)
name|this
operator|.
name|index
operator|)
decl_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doInsert
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|int
name|minLeafDepth
init|=
name|index
operator|.
name|getMinLeafDepth
argument_list|(
name|tx
argument_list|)
decl_stmt|;
name|int
name|maxLeafDepth
init|=
name|index
operator|.
name|getMaxLeafDepth
argument_list|(
name|tx
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Tree is balanced"
argument_list|,
name|maxLeafDepth
operator|-
name|minLeafDepth
operator|<=
literal|1
argument_list|)
expr_stmt|;
comment|// Remove some of the data
name|doRemove
argument_list|(
literal|16
argument_list|)
expr_stmt|;
name|minLeafDepth
operator|=
name|index
operator|.
name|getMinLeafDepth
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|maxLeafDepth
operator|=
name|index
operator|.
name|getMaxLeafDepth
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"min:"
operator|+
name|minLeafDepth
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"max:"
operator|+
name|maxLeafDepth
argument_list|)
expr_stmt|;
name|index
operator|.
name|printStructure
argument_list|(
name|tx
argument_list|,
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Tree is balanced"
argument_list|,
name|maxLeafDepth
operator|-
name|minLeafDepth
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPruning
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
operator|(
operator|(
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
operator|)
name|this
operator|.
name|index
operator|)
decl_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|int
name|minLeafDepth
init|=
name|index
operator|.
name|getMinLeafDepth
argument_list|(
name|tx
argument_list|)
decl_stmt|;
name|int
name|maxLeafDepth
init|=
name|index
operator|.
name|getMaxLeafDepth
argument_list|(
name|tx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|minLeafDepth
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|maxLeafDepth
argument_list|)
expr_stmt|;
name|doInsert
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|minLeafDepth
operator|=
name|index
operator|.
name|getMinLeafDepth
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|maxLeafDepth
operator|=
name|index
operator|.
name|getMaxLeafDepth
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Depth of tree grew"
argument_list|,
name|minLeafDepth
operator|>
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Depth of tree grew"
argument_list|,
name|maxLeafDepth
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// Remove the data.
name|doRemove
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|minLeafDepth
operator|=
name|index
operator|.
name|getMinLeafDepth
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|maxLeafDepth
operator|=
name|index
operator|.
name|getMaxLeafDepth
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|minLeafDepth
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|maxLeafDepth
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testIteration
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
operator|(
operator|(
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
operator|)
name|this
operator|.
name|index
operator|)
decl_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Insert in reverse order..
name|doInsertReverse
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// BTree should iterate it in sorted order.
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|i
init|=
name|index
operator|.
name|iterator
argument_list|(
name|tx
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|key
argument_list|(
name|counter
argument_list|)
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|counter
argument_list|,
operator|(
name|long
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
name|this
operator|.
name|index
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testVisitor
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
operator|(
operator|(
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
operator|)
name|this
operator|.
name|index
operator|)
decl_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Insert in reverse order..
name|doInsert
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// BTree should iterate it in sorted order.
name|index
operator|.
name|visit
argument_list|(
name|tx
argument_list|,
operator|new
name|BTreeVisitor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|isInterestedInKeysBetween
parameter_list|(
name|String
name|first
parameter_list|,
name|String
name|second
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
name|List
argument_list|<
name|Long
argument_list|>
name|values
parameter_list|)
block|{             }
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testRandomRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
operator|(
operator|(
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
operator|)
name|this
operator|.
name|index
operator|)
decl_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|int
name|count
init|=
literal|4000
decl_stmt|;
name|doInsert
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|prev
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|index
operator|.
name|isEmpty
argument_list|(
name|tx
argument_list|)
condition|)
block|{
name|prev
operator|=
name|i
expr_stmt|;
name|i
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
try|try
block|{
name|index
operator|.
name|remove
argument_list|(
name|tx
argument_list|,
name|key
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"unexpected exception on "
operator|+
name|i
operator|+
literal|", prev: "
operator|+
name|prev
operator|+
literal|", ex: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testRemovePattern
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
operator|(
operator|(
name|BTreeIndex
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
operator|)
name|this
operator|.
name|index
operator|)
decl_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|int
name|count
init|=
literal|4000
decl_stmt|;
name|doInsert
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|index
operator|.
name|remove
argument_list|(
name|tx
argument_list|,
name|key
argument_list|(
literal|3697
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|.
name|remove
argument_list|(
name|tx
argument_list|,
name|key
argument_list|(
literal|1566
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|x_testLargeValue
parameter_list|()
throws|throws
name|Exception
block|{
name|createPageFileAndIndex
argument_list|(
literal|4
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|long
name|id
init|=
name|tx
operator|.
name|allocate
argument_list|()
operator|.
name|getPageId
argument_list|()
decl_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|BTreeIndex
argument_list|<
name|Long
argument_list|,
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|test
init|=
operator|new
name|BTreeIndex
argument_list|<
name|Long
argument_list|,
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|pf
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|test
operator|.
name|setKeyMarshaller
argument_list|(
name|LongMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|test
operator|.
name|setValueMarshaller
argument_list|(
name|HashSetStringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|test
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tx
operator|=
name|pf
operator|.
name|tx
argument_list|()
expr_stmt|;
name|String
name|val
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|93
index|]
argument_list|)
decl_stmt|;
specifier|final
name|long
name|numMessages
init|=
literal|2000
decl_stmt|;
specifier|final
name|int
name|numConsumers
init|=
literal|10000
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|hs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numConsumers
condition|;
name|j
operator|++
control|)
block|{
name|hs
operator|.
name|add
argument_list|(
name|val
operator|+
literal|"SOME TEXT"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|test
operator|.
name|put
argument_list|(
name|tx
argument_list|,
name|i
argument_list|,
name|hs
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|test
operator|.
name|get
argument_list|(
name|tx
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|void
name|doInsertReverse
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
name|count
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|index
operator|.
name|put
argument_list|(
name|tx
argument_list|,
name|key
argument_list|(
name|i
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|i
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Overriding so that this generates keys that are the worst case for the BTree. Keys that      * always insert to the end of the BTree.        */
annotation|@
name|Override
specifier|protected
name|String
name|key
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|"key:"
operator|+
name|nf
operator|.
name|format
argument_list|(
name|i
argument_list|)
return|;
block|}
specifier|static
class|class
name|HashSetStringMarshaller
extends|extends
name|VariableMarshaller
argument_list|<
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|>
block|{
specifier|final
specifier|static
name|HashSetStringMarshaller
name|INSTANCE
init|=
operator|new
name|HashSetStringMarshaller
argument_list|()
decl_stmt|;
specifier|public
name|void
name|writePayload
parameter_list|(
name|HashSet
argument_list|<
name|String
argument_list|>
name|object
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|oout
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|oout
operator|.
name|writeObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|oout
operator|.
name|flush
argument_list|()
expr_stmt|;
name|oout
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|HashSet
argument_list|<
name|String
argument_list|>
name|readPayload
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|dataLen
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dataLen
index|]
decl_stmt|;
name|dataIn
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|ObjectInputStream
name|oin
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|HashSet
argument_list|<
name|String
argument_list|>
operator|)
name|oin
operator|.
name|readObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cfe
parameter_list|)
block|{
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
literal|"Failed to read HashSet<String>: "
operator|+
name|cfe
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|cfe
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

