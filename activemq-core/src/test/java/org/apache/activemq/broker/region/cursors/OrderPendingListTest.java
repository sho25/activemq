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
name|broker
operator|.
name|region
operator|.
name|cursors
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|LinkedList
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
name|broker
operator|.
name|region
operator|.
name|Destination
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|command
operator|.
name|ConsumerId
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
name|command
operator|.
name|Message
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
name|command
operator|.
name|MessageId
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
name|IdGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|OrderPendingListTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAddMessageFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|lastId
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|lastId
operator|--
argument_list|,
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMessageLast
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|lastId
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|lastId
operator|++
argument_list|,
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClear
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSize
parameter_list|()
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|TestMessageReference
name|toRemove
init|=
operator|new
name|TestMessageReference
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|lastId
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|lastId
operator|--
argument_list|,
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|remove
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContains
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|TestMessageReference
name|toRemove
init|=
operator|new
name|TestMessageReference
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|contains
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|getAsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|TestMessageReference
name|toRemove
init|=
operator|new
name|TestMessageReference
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|MessageReference
argument_list|>
name|values
init|=
name|list
operator|.
name|values
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageReference
name|msg
range|:
name|values
control|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|values
operator|.
name|contains
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addMessageLast
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|values
operator|=
name|list
operator|.
name|values
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageReference
name|msg
range|:
name|values
control|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|values
operator|.
name|contains
argument_list|(
name|toRemove
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddAll
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPendingList
name|list
init|=
operator|new
name|OrderedPendingList
argument_list|()
decl_stmt|;
name|TestPendingList
name|source
init|=
operator|new
name|TestPendingList
argument_list|()
decl_stmt|;
name|source
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|addMessageFirst
argument_list|(
operator|new
name|TestMessageReference
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|source
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageReference
name|message
range|:
name|source
control|)
block|{
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|addAll
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|TestPendingList
implements|implements
name|PendingList
block|{
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|theList
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|theList
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|theList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PendingNode
name|addMessageFirst
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|theList
operator|.
name|addFirst
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|PendingNode
argument_list|(
literal|null
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PendingNode
name|addMessageLast
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|theList
operator|.
name|addLast
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|PendingNode
argument_list|(
literal|null
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PendingNode
name|remove
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
if|if
condition|(
name|theList
operator|.
name|remove
argument_list|(
name|message
argument_list|)
condition|)
block|{
return|return
operator|new
name|PendingNode
argument_list|(
literal|null
argument_list|,
name|message
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|theList
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|theList
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
return|return
name|theList
operator|.
name|contains
argument_list|(
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|MessageReference
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|theList
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addAll
parameter_list|(
name|PendingList
name|pendingList
parameter_list|)
block|{
for|for
control|(
name|MessageReference
name|messageReference
range|:
name|pendingList
control|)
block|{
name|theList
operator|.
name|add
argument_list|(
name|messageReference
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|static
class|class
name|TestMessageReference
implements|implements
name|MessageReference
block|{
specifier|private
specifier|static
specifier|final
name|IdGenerator
name|id
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|private
name|MessageId
name|messageId
decl_stmt|;
specifier|private
name|int
name|referenceCount
init|=
literal|0
decl_stmt|;
specifier|public
name|TestMessageReference
parameter_list|(
name|int
name|sequenceId
parameter_list|)
block|{
name|messageId
operator|=
operator|new
name|MessageId
argument_list|(
name|id
operator|.
name|generateId
argument_list|()
operator|+
literal|":1"
argument_list|,
name|sequenceId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MessageId
name|getMessageId
parameter_list|()
block|{
return|return
name|messageId
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|getMessageHardRef
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|getMessage
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|getRegionDestination
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRedeliveryCounter
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|incrementRedeliveryCounter
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|referenceCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|incrementReferenceCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|referenceCount
operator|++
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|decrementReferenceCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|referenceCount
operator|--
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConsumerId
name|getTargetConsumerId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getExpiration
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getGroupID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getGroupSequence
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExpired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDropped
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAdvisory
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

