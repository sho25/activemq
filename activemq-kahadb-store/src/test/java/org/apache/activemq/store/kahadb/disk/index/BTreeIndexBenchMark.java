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
name|text
operator|.
name|NumberFormat
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
name|LongMarshaller
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

begin_class
specifier|public
class|class
name|BTreeIndexBenchMark
extends|extends
name|IndexBenchmark
block|{
specifier|private
name|NumberFormat
name|nf
decl_stmt|;
annotation|@
name|Override
specifier|public
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
literal|10
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
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
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
annotation|@
name|Override
specifier|protected
name|void
name|dumpIndex
parameter_list|(
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
operator|(
operator|(
name|BTreeIndex
operator|)
name|index
operator|)
operator|.
name|printStructure
argument_list|(
name|tx
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**      * Overriding so that this generates keys that are the worst case for the BTree. Keys that      * always insert to the end of the BTree.        */
annotation|@
name|Override
specifier|protected
name|String
name|key
parameter_list|(
name|long
name|i
parameter_list|)
block|{
return|return
literal|"a-long-message-id-like-key:"
operator|+
name|nf
operator|.
name|format
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
end_class

end_unit
