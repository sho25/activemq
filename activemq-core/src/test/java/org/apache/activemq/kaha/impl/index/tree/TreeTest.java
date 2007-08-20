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
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|tree
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|Store
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|IndexItem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|IndexManager
import|;
end_import

begin_comment
comment|/**  * Test a TreeIndex  */
end_comment

begin_class
specifier|public
class|class
name|TreeTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|int
name|COUNT
init|=
literal|55
decl_stmt|;
specifier|private
name|TreeIndex
name|tree
decl_stmt|;
specifier|private
name|File
name|directory
decl_stmt|;
specifier|private
name|IndexManager
name|indexManager
decl_stmt|;
specifier|private
name|boolean
name|dumpTree
decl_stmt|;
comment|/**      * @throws java.lang.Exception      * @see junit.framework.TestCase#setUp()      */
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
name|directory
operator|=
operator|new
name|File
argument_list|(
literal|"activemq-data"
argument_list|)
expr_stmt|;
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|indexManager
operator|=
operator|new
name|IndexManager
argument_list|(
name|directory
argument_list|,
literal|"im-test"
argument_list|,
literal|"rw"
argument_list|,
literal|null
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|tree
operator|=
operator|new
name|TreeIndex
argument_list|(
name|directory
argument_list|,
literal|"testTree"
argument_list|,
name|indexManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|tree
operator|.
name|setKeyMarshaller
argument_list|(
name|Store
operator|.
name|STRING_MARSHALLER
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTreeWithCaching
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|tree
operator|.
name|setEnablePageCaching
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// doTest();
block|}
specifier|public
name|void
name|testTreeWithoutCaching
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|tree
operator|.
name|setEnablePageCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// doTest();
block|}
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// doTest(300);
comment|// tree.clear();
comment|// tree.unload();
comment|// count = 55 - this fails
name|doTest
argument_list|(
literal|600
argument_list|)
expr_stmt|;
comment|// tree.clear();
comment|// tree.unload();
comment|// doTest(1024*16);
block|}
specifier|public
name|void
name|doTest
parameter_list|(
name|int
name|pageSize
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|keyRoot
init|=
literal|"key:"
decl_stmt|;
name|tree
operator|.
name|setPageSize
argument_list|(
name|pageSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|tree
operator|.
name|load
argument_list|()
expr_stmt|;
comment|// doInsert(keyRoot);
comment|// checkRetrieve(keyRoot);
comment|// doRemove(keyRoot);
name|doInsert
argument_list|(
name|keyRoot
argument_list|)
expr_stmt|;
name|doRemoveBackwards
argument_list|(
name|keyRoot
argument_list|)
expr_stmt|;
block|}
name|void
name|doInsert
parameter_list|(
name|String
name|keyRoot
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|IndexItem
name|value
init|=
name|indexManager
operator|.
name|createNewIndex
argument_list|()
decl_stmt|;
name|indexManager
operator|.
name|storeIndex
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|tree
operator|.
name|store
argument_list|(
name|keyRoot
operator|+
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|checkRetrieve
parameter_list|(
name|String
name|keyRoot
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|tree
operator|.
name|get
argument_list|(
name|keyRoot
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|doRemove
parameter_list|(
name|String
name|keyRoot
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|tree
operator|.
name|remove
argument_list|(
name|keyRoot
operator|+
name|i
argument_list|)
expr_stmt|;
comment|// System.out.println("Removed " + keyRoot+i);
comment|// tree.getRoot().dump();
comment|// System.out.println("");
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|tree
operator|.
name|get
argument_list|(
name|keyRoot
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|doRemoveBackwards
parameter_list|(
name|String
name|keyRoot
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
name|COUNT
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
name|tree
operator|.
name|remove
argument_list|(
name|keyRoot
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"BACK Removed "
operator|+
name|keyRoot
operator|+
name|i
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getRoot
argument_list|()
operator|.
name|dump
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|tree
operator|.
name|get
argument_list|(
name|keyRoot
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @throws java.lang.Exception      * @see junit.framework.TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

