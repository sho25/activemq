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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|page
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
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
name|util
operator|.
name|HashSet
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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|util
operator|.
name|StringMarshaller
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
class|class
name|PageFileTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PageFileTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testCRUD
parameter_list|()
throws|throws
name|IOException
block|{
name|PageFile
name|pf
init|=
operator|new
name|PageFile
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test-data"
argument_list|)
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|pf
operator|.
name|delete
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Insert some data into the page file.
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Page
argument_list|<
name|String
argument_list|>
name|page
init|=
name|tx
operator|.
name|allocate
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Page
operator|.
name|PAGE_FREE_TYPE
argument_list|,
name|page
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|t
init|=
literal|"page:"
operator|+
name|i
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|page
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|tx
operator|.
name|store
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// Reload it...
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|tx
operator|=
name|pf
operator|.
name|tx
argument_list|()
expr_stmt|;
comment|// Iterate it to make sure they are still there..
name|HashSet
argument_list|<
name|String
argument_list|>
name|actual
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
name|Page
argument_list|<
name|String
argument_list|>
name|page
range|:
name|tx
control|)
block|{
name|tx
operator|.
name|load
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|actual
operator|.
name|add
argument_list|(
name|page
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
comment|// Remove the odd records..
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|String
name|t
init|=
literal|"page:"
operator|+
name|i
decl_stmt|;
name|expected
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Page
argument_list|<
name|String
argument_list|>
name|page
range|:
name|tx
control|)
block|{
name|tx
operator|.
name|load
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|contains
argument_list|(
name|page
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
name|tx
operator|.
name|free
argument_list|(
name|page
argument_list|)
expr_stmt|;
block|}
block|}
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Reload it...
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|tx
operator|=
name|pf
operator|.
name|tx
argument_list|()
expr_stmt|;
comment|// Iterate it to make sure the even records are still there..
name|actual
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Page
argument_list|<
name|String
argument_list|>
name|page
range|:
name|tx
control|)
block|{
name|tx
operator|.
name|load
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|actual
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|page
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
comment|// Update the records...
name|HashSet
argument_list|<
name|String
argument_list|>
name|t
init|=
name|expected
decl_stmt|;
name|expected
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|t
control|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|s
operator|+
literal|":updated"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Page
argument_list|<
name|String
argument_list|>
name|page
range|:
name|tx
control|)
block|{
name|tx
operator|.
name|load
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|page
operator|.
name|set
argument_list|(
name|page
operator|.
name|get
argument_list|()
operator|+
literal|":updated"
argument_list|)
expr_stmt|;
name|tx
operator|.
name|store
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Reload it...
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|tx
operator|=
name|pf
operator|.
name|tx
argument_list|()
expr_stmt|;
comment|// Iterate it to make sure the updated records are still there..
name|actual
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Page
argument_list|<
name|String
argument_list|>
name|page
range|:
name|tx
control|)
block|{
name|tx
operator|.
name|load
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|actual
operator|.
name|add
argument_list|(
name|page
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testStreams
parameter_list|()
throws|throws
name|IOException
block|{
name|PageFile
name|pf
init|=
operator|new
name|PageFile
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test-data"
argument_list|)
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|pf
operator|.
name|delete
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
name|Page
name|page
init|=
name|tx
operator|.
name|allocate
argument_list|()
decl_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|OutputStream
name|pos
init|=
name|tx
operator|.
name|openOutputStream
argument_list|(
name|page
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|pos
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|os
operator|.
name|writeUTF
argument_list|(
literal|"Test string:"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Reload the page file.
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|tx
operator|=
name|pf
operator|.
name|tx
argument_list|()
expr_stmt|;
name|InputStream
name|pis
init|=
name|tx
operator|.
name|openInputStream
argument_list|(
name|page
argument_list|)
decl_stmt|;
name|DataInputStream
name|is
init|=
operator|new
name|DataInputStream
argument_list|(
name|pis
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Test string:"
operator|+
name|i
argument_list|,
name|is
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|is
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAddRollback
parameter_list|()
throws|throws
name|IOException
block|{
name|PageFile
name|pf
init|=
operator|new
name|PageFile
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test-data"
argument_list|)
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|pf
operator|.
name|delete
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Insert some data into the page file.
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Page
argument_list|<
name|String
argument_list|>
name|page
init|=
name|tx
operator|.
name|allocate
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Page
operator|.
name|PAGE_FREE_TYPE
argument_list|,
name|page
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|t
init|=
literal|"page:"
operator|+
name|i
decl_stmt|;
name|page
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|tx
operator|.
name|store
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Rollback every other insert.
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tx
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Reload it...
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
name|tx
operator|=
name|pf
operator|.
name|tx
argument_list|()
expr_stmt|;
comment|// Iterate it to make sure they are still there..
name|HashSet
argument_list|<
name|String
argument_list|>
name|actual
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
name|Page
argument_list|<
name|String
argument_list|>
name|page
range|:
name|tx
control|)
block|{
name|tx
operator|.
name|load
argument_list|(
name|page
argument_list|,
name|StringMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|actual
operator|.
name|add
argument_list|(
name|page
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
comment|//Test for AMQ-6590
specifier|public
name|void
name|testFreePageRecoveryUncleanShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|PageFile
name|pf
init|=
operator|new
name|PageFile
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test-data"
argument_list|)
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|pf
operator|.
name|delete
argument_list|()
expr_stmt|;
name|pf
operator|.
name|setEnableRecoveryFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
comment|//Allocate 10 free pages
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
name|tx
operator|.
name|allocate
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|pf
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|//Load a second instance on the same directory fo the page file which
comment|//simulates an unclean shutdown from the previous run
name|PageFile
name|pf2
init|=
operator|new
name|PageFile
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test-data"
argument_list|)
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|pf2
operator|.
name|setEnableRecoveryFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pf2
operator|.
name|load
argument_list|()
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
literal|"We have 10 free pages"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|pf2
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|freePages
init|=
name|pf2
operator|.
name|getFreePageCount
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"free page count: "
operator|+
name|freePages
argument_list|)
expr_stmt|;
return|return
name|freePages
operator|==
literal|10l
return|;
block|}
block|}
argument_list|,
literal|12000000
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pf
operator|.
name|unload
argument_list|()
expr_stmt|;
name|pf2
operator|.
name|unload
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

